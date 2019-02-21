package com.github.ambry.network;
import com.github.ambry.utils.Time;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Selector implements Selectable {
  private static final Logger logger = LoggerFactory.getLogger(Selector.class);
  private final java.nio.channels.Selector nioSelector;
  private final Map<String, SelectionKey> keyMap;
  private final List<NetworkSend> completedSends;
  private final List<NetworkReceive> completedReceives;
  private final List<String> disconnected;
  private final List<String> connected;
  private final Time time;
  private final NetworkMetrics metrics;
  private final AtomicLong IdGenerator;
  private AtomicLong activeConnections;
  public Selector(NetworkMetrics metrics, Time time) throws IOException {
    this.nioSelector = java.nio.channels.Selector.open();
    this.time = time;
    this.keyMap = new HashMap<String, SelectionKey>();
    this.completedSends = new ArrayList<NetworkSend>();
    this.completedReceives = new ArrayList<NetworkReceive>();
    this.connected = new ArrayList<String>();
    this.disconnected = new ArrayList<String>();
    this.metrics = metrics;
    this.IdGenerator = new AtomicLong(0);
    this.activeConnections = new AtomicLong(0);
    this.metrics.initializeSelectorMetricsIfRequired(activeConnections);
  }
  private String generateConnectionId(SocketChannel channel) {
    Socket socket = channel.socket();
    String localHost = socket.getLocalAddress().getHostAddress();
    int localPort = socket.getLocalPort();
    String remoteHost = socket.getInetAddress().getHostAddress();
    int remotePort = socket.getPort();
    long connectionIdSuffix = IdGenerator.getAndIncrement();
    StringBuilder connectionIdBuilder = new StringBuilder();
    connectionIdBuilder.append(localHost).append(":").append(localPort).append("-").append(remoteHost).append(":").append(remotePort).append("_").append(connectionIdSuffix);
    return connectionIdBuilder.toString();
  }
  @Override public String connect(InetSocketAddress address, int sendBufferSize, int receiveBufferSize) throws IOException {
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking(false);
    Socket socket = channel.socket();
    socket.setKeepAlive(true);
    socket.setSendBufferSize(sendBufferSize);
    socket.setReceiveBufferSize(receiveBufferSize);
    socket.setTcpNoDelay(true);
    try {
      channel.connect(address);
    }
    catch (UnresolvedAddressException e) {
      channel.close();
      throw new IOException("Can\'t resolve address: " + address, e);
    }
    catch (IOException e) {
      channel.close();
      throw e;
    }
    String connectionId = generateConnectionId(channel);
    SelectionKey key = channel.register(this.nioSelector, SelectionKey.OP_CONNECT);
    key.attach(new Transmissions(connectionId, address.getHostName(), address.getPort()));
    this.keyMap.put(connectionId, key);
    activeConnections.set(this.keyMap.size());
    return connectionId;
  }
  public String register(SocketChannel channel) throws ClosedChannelException {
    Socket socket = channel.socket();
    String remoteHost = socket.getInetAddress().getHostAddress();
    int remotePort = socket.getPort();
    String connectionId = generateConnectionId(channel);
    SelectionKey key = channel.register(nioSelector, SelectionKey.OP_READ);
    key.attach(new Transmissions(connectionId, remoteHost, remotePort));
    this.keyMap.put(connectionId, key);
    activeConnections.set(this.keyMap.size());
    return connectionId;
  }
  @Override public void disconnect(String connectionId) {
    SelectionKey key = this.keyMap.get(connectionId);
    if (key != null) {
      key.cancel();
    }
  }
  @Override public void wakeup() {
    nioSelector.wakeup();
  }
  @Override public void close() {
    for (SelectionKey key : this.nioSelector.keys()) {
      close(key);
    }
    try {
      this.nioSelector.close();
    }
    catch (IOException e) {
      metrics.selectorNioCloseErrorCount.inc();
      logger.error("Exception closing nioSelector:", e);
    }
  }
  public void send(NetworkSend networkSend) {
    SelectionKey key = keyForId(networkSend.getConnectionId());
    if (key == null) {
      throw new IllegalStateException("Attempt to send data to a null key");
    }
    Transmissions transmissions = transmissions(key);
    if (transmissions.hasSend()) {
      throw new IllegalStateException("Attempt to begin a send operation with prior send operation still in progress.");
    }
    transmissions.send = networkSend;
    metrics.sendInFlight.inc();
    try {
      key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }
    catch (CancelledKeyException e) {
      logger.debug("Ignoring response for closed socket.");
      close(key);
    }
  }
  @Override public void poll(long timeoutMs) throws IOException {
    poll(timeoutMs, null);
  }
  @Override public void poll(long timeoutMs, List<NetworkSend> sends) throws IOException {
    clear();
    if (sends != null) {
      for (NetworkSend networkSend : sends) {
        send(networkSend);
      }
    }
    long startSelect = time.milliseconds();
    int readyKeys = select(timeoutMs);
    long endSelect = time.milliseconds();
    this.metrics.selectorSelectTime.update(endSelect - startSelect);
    this.metrics.selectorSelectRate.inc();
    if (readyKeys > 0) {
      Set<SelectionKey> keys = nioSelector.selectedKeys();
      Iterator<SelectionKey> iter = keys.iterator();
      while (iter.hasNext()){
        SelectionKey key = iter.next();
        iter.remove();
        Transmissions transmissions = transmissions(key);
        try {
          if (key.isConnectable()) {
            handleConnect(key, transmissions);
          }
          else 
            if (key.isReadable()) {
              read(key, transmissions);
            }
            else 
              if (key.isWritable()) {
                write(key, transmissions);
              }
              else 
                if (!key.isValid()) {
                  close(key);
                }
                else {
                  throw new IllegalStateException("Unrecognized key state for processor thread.");
                }
        }
        catch (IOException e) {
          String socketDescription = socketDescription(channel(key));
          if (e instanceof EOFException || e instanceof ConnectException) {
            metrics.selectorDisconnectedErrorCount.inc();
            logger.error("Connection {} disconnected", socketDescription, e);
          }
          else {
            metrics.selectorIOErrorCount.inc();
            logger.warn("Error in I/O with connection to {}", socketDescription, e);
          }
          close(key);
        }
        catch (Exception e) {
          metrics.selectorKeyOperationErrorCount.inc();
          logger.error("closing key on exception remote host {}", channel(key).socket().getRemoteSocketAddress(), e);
          close(key);
        }
      }
      this.metrics.selectorIORate.inc();
    }
    long endIo = time.milliseconds();
    this.metrics.selectorIOTime.update(endIo - endSelect);
  }
  private String socketDescription(SocketChannel channel) {
    Socket socket = channel.socket();
    if (socket == null) {
      return "[unconnected socket]";
    }
    else 
      if (socket.getInetAddress() != null) {
        return socket.getInetAddress().toString();
      }
      else {
        return socket.getLocalAddress().toString();
      }
  }
  @Override public List<NetworkSend> completedSends() {
    return this.completedSends;
  }
  @Override public List<NetworkReceive> completedReceives() {
    return this.completedReceives;
  }
  @Override public List<String> disconnected() {
    return this.disconnected;
  }
  @Override public List<String> connected() {
    return this.connected;
  }
  public long getActiveConnections() {
    return activeConnections.get();
  }
  private void clear() {
    this.completedSends.clear();
    this.completedReceives.clear();
    this.connected.clear();
    this.disconnected.clear();
  }
  private int select(long ms) throws IOException {
    if (ms == 0L) {
      return this.nioSelector.selectNow();
    }
    else 
      if (ms < 0L) {
        return this.nioSelector.select();
      }
      else {
        return this.nioSelector.select(ms);
      }
  }
  @Override public void close(String connectionId) {
    SelectionKey key = keyForId(connectionId);
    if (key == null) {
      metrics.selectorCloseKeyErrorCount.inc();
      logger.error("Attempt to close socket for which there is no open connection. Connection id {}", connectionId);
    }
    else {
      close(key);
    }
  }
  private void close(SelectionKey key) {
    SocketChannel socketChannel = channel(key);
    Transmissions transmissions = transmissions(key);
    if (transmissions != null) {
      logger.debug("Closing connection from {}", transmissions.connectionId);
      this.disconnected.add(transmissions.connectionId);
      this.keyMap.remove(transmissions.connectionId);
      activeConnections.set(this.keyMap.size());
      transmissions.clearReceive();
      transmissions.clearSend();
    }
    key.attach(null);
    key.cancel();
    try {
      socketChannel.socket().close();
      socketChannel.close();
    }
    catch (IOException e) {
      metrics.selectorCloseSocketErrorCount.inc();
      logger.error("Exception closing connection to node {}:", transmissions.connectionId, e);
    }
    this.metrics.selectorConnectionClosed.inc();
  }
  private SelectionKey keyForId(String id) {
    return this.keyMap.get(id);
  }
  private void handleConnect(SelectionKey key, Transmissions transmissions) throws IOException {
    SocketChannel socketChannel = channel(key);
    socketChannel.finishConnect();
    key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
    this.connected.add(transmissions.getConnectionId());
    this.metrics.selectorConnectionCreated.inc();
  }
  private void read(SelectionKey key, Transmissions transmissions) throws IOException {
    long startTimeToReadInMs = time.milliseconds();
    try {
      if (!transmissions.hasReceive()) {
        transmissions.receive = new NetworkReceive(transmissions.getConnectionId(), new BoundedByteBufferReceive(), time);
      }
      SocketChannel socketChannel = channel(key);
      long bytesRead = transmissions.receive.getReceivedBytes().readFrom(socketChannel);
      if (bytesRead == -1) {
        close(key);
        return ;
      }
      metrics.selectorBytesReceived.update(bytesRead);
      metrics.selectorBytesReceivedCount.inc(bytesRead);
      if (transmissions.receive.getReceivedBytes().isReadComplete()) {
        this.completedReceives.add(transmissions.receive);
        transmissions.clearReceive();
      }
    }
    finally {
      long readTime = time.milliseconds() - startTimeToReadInMs;
      logger.trace("SocketServer time spent on read per key {} = {}", transmissions.connectionId, readTime);
    }
  }
  private void write(SelectionKey key, Transmissions transmissions) throws IOException {
    long startTimeToWriteInMs = time.milliseconds();
    try {
      SocketChannel socketChannel = channel(key);
      NetworkSend networkSend = transmissions.send;
      Send send = networkSend.getPayload();
      if (send == null) {
        throw new IllegalStateException("Registered for write interest but no response attached to key.");
      }
      send.writeTo(socketChannel);
      logger.trace("Bytes written to {} using key {}", socketChannel.socket().getRemoteSocketAddress(), transmissions.connectionId);
      if (send.isSendComplete()) {
        logger.trace("Finished writing, registering for read on connection {}", socketChannel.socket().getRemoteSocketAddress());
        networkSend.onSendComplete();
        this.completedSends.add(networkSend);
        metrics.sendInFlight.dec();
        transmissions.clearSend();
        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE | SelectionKey.OP_READ);
      }
    }
    finally {
      long writeTime = time.milliseconds() - startTimeToWriteInMs;
      logger.trace("SocketServer time spent on write per key {} = {}", transmissions.connectionId, writeTime);
    }
  }
  private Transmissions transmissions(SelectionKey key) {
    return (Transmissions)key.attachment();
  }
  private SocketChannel channel(SelectionKey key) {
    return (SocketChannel)key.channel();
  }
  
  private static class Transmissions {
    private String connectionId;
    private String remoteHostName;
    private int remotePort;
    private NetworkSend send = null;
    private NetworkReceive receive = null;
    private Transmissions(String connectionId, String remoteHostName, int remotePort) {
      this.connectionId = connectionId;
      this.remoteHostName = remoteHostName;
      this.remotePort = remotePort;
    }
    private String getConnectionId() {
      return connectionId;
    }
    private boolean hasSend() {
      return send != null;
    }
    private void clearSend() {
      send = null;
    }
    private boolean hasReceive() {
      return receive != null;
    }
    private void clearReceive() {
      receive = null;
    }
  }
}

