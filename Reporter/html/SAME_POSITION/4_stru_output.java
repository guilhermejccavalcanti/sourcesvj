package com.github.ambry.network;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.config.NetworkConfig;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Time;
import com.github.ambry.utils.Utils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Iterator;
import java.util.Set;

public class SocketServer implements NetworkServer {
  private final String host;
  private final int port;
  private final int numProcessorThreads;
  private final int maxQueuedRequests;
  private final int sendBufferSize;
  private final int recvBufferSize;
  private final int maxRequestSize;
  private final ArrayList<Processor> processors;
  private volatile ArrayList<Acceptor> acceptors;
  private final SocketRequestResponseChannel requestResponseChannel;
  private Logger logger = LoggerFactory.getLogger(getClass());
  private final NetworkMetrics metrics;
  private final HashMap<PortType, Port> ports;
  public SocketServer(NetworkConfig config, MetricRegistry registry, ArrayList<Port> portList) {
    this.host = config.hostName;
    this.port = config.port;
    this.numProcessorThreads = config.numIoThreads;
    this.maxQueuedRequests = config.queuedMaxRequests;
    this.sendBufferSize = config.socketSendBufferBytes;
    this.recvBufferSize = config.socketReceiveBufferBytes;
    this.maxRequestSize = config.socketRequestMaxBytes;
    processors = new ArrayList<Processor>(numProcessorThreads);
    requestResponseChannel = new SocketRequestResponseChannel(numProcessorThreads, maxQueuedRequests);
    metrics = new NetworkMetrics(requestResponseChannel, registry, processors);
    this.acceptors = new ArrayList<Acceptor>();
    this.ports = new HashMap<PortType, Port>();
    this.validatePorts(portList);
  }
  public String getHost() {
    return host;
  }
  public int getPort() {
    return port;
  }
  public int getSSLPort() {
    Port sslPort = ports.get(PortType.SSL);
    if (sslPort != null) {
      return sslPort.getPort();
    }
    throw new IllegalStateException("No SSL Port Exists for Server " + host + ":" + port);
  }
  public int getNumProcessorThreads() {
    return numProcessorThreads;
  }
  public int getMaxQueuedRequests() {
    return maxQueuedRequests;
  }
  public int getSendBufferSize() {
    return sendBufferSize;
  }
  public int getRecvBufferSize() {
    return recvBufferSize;
  }
  public int getMaxRequestSize() {
    return maxRequestSize;
  }
  @Override public RequestResponseChannel getRequestResponseChannel() {
    return requestResponseChannel;
  }
  private void validatePorts(ArrayList<Port> portList) {
    HashSet<PortType> portTypeSet = new HashSet<PortType>();
    for (Port port : portList) {
      if (portTypeSet.contains(port.getPortType())) {
        throw new IllegalArgumentException("Not more than one port of same type is allowed : " + port.getPortType());
      }
      else {
        portTypeSet.add(port.getPortType());
        this.ports.put(port.getPortType(), port);
      }
    }
  }
  public void start() throws IOException, InterruptedException {
    logger.info("Starting {} processor threads", numProcessorThreads);
    for (int i = 0; i < numProcessorThreads; i++) {
      processors.add(i, new Processor(i, maxRequestSize, requestResponseChannel, metrics));
      Utils.newThread("ambry-processor-" + port + " " + i, processors.get(i), false).start();
    }
    requestResponseChannel.addResponseListener(new ResponseListener() {
        @Override public void onResponse(int processorId) {
          processors.get(processorId).wakeup();
        }
    });
    logger.info("Starting acceptor threads");
    Acceptor plainTextAcceptor = new Acceptor(host, port, processors, sendBufferSize, recvBufferSize);
    
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_3ee53a1_d5fbd56\rev_left_3ee53a1\ambry-network\src\main\java\com.github.ambry.network\SocketServer.java
this.acceptors.add(plainTextAcceptor)
=======
this.acceptor = new Acceptor(host, port, processors, sendBufferSize, recvBufferSize, metrics)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_3ee53a1_d5fbd56\rev_right_d5fbd56\ambry-network\src\main\java\com.github.ambry.network\SocketServer.java
;
    Utils.newThread("ambry-acceptor", plainTextAcceptor, false).start();
    Port sslPort = ports.get(PortType.SSL);
    if (sslPort != null) {
      SSLAcceptor sslAcceptor = new SSLAcceptor(host, sslPort.getPort(), processors, sendBufferSize, recvBufferSize);
      acceptors.add(sslAcceptor);
      Utils.newThread("ambry-sslacceptor", sslAcceptor, false).start();
    }
    for (Acceptor acceptor : acceptors) {
      acceptor.awaitStartup();
    }
    logger.info("Started server");
  }
  public void shutdown() {
    try {
      logger.info("Shutting down server");
      for (Acceptor acceptor : acceptors) {
        if (acceptor != null) {
          acceptor.shutdown();
        }
      }
      for (Processor processor : processors) {
        processor.shutdown();
      }
      logger.info("Shutdown completed");
    }
    catch (Exception e) {
      logger.error("Error shutting down socket server {}", e);
    }
  }
}

abstract class AbstractServerThread implements Runnable {
  private final CountDownLatch startupLatch;
  private final CountDownLatch shutdownLatch;
  private final AtomicBoolean alive;
  protected Logger logger = LoggerFactory.getLogger(getClass());
  public AbstractServerThread() throws IOException {
    startupLatch = new CountDownLatch(1);
    shutdownLatch = new CountDownLatch(1);
    alive = new AtomicBoolean(false);
  }
  public void shutdown() throws InterruptedException {
    alive.set(false);
    shutdownLatch.await();
  }
  public void awaitStartup() throws InterruptedException {
    startupLatch.await();
  }
  protected void startupComplete() {
    alive.set(true);
    startupLatch.countDown();
  }
  protected void shutdownComplete() {
    shutdownLatch.countDown();
  }
  protected boolean isRunning() {
    return alive.get();
  }
}

class SSLAcceptor extends Acceptor {
  public SSLAcceptor(String host, int port, ArrayList<Processor> processors, int sendBufferSize, int recvBufferSize) throws IOException {
    super(host, port, processors, sendBufferSize, recvBufferSize);
  }
}

class Acceptor extends AbstractServerThread {
  private final String host;
  private final int port;
  private final ArrayList<Processor> processors;
  private final int sendBufferSize;
  private final int recvBufferSize;
  private final ServerSocketChannel serverChannel;
  private final java.nio.channels.Selector nioSelector;
  private static final long selectTimeOutMs = 500;
  private final NetworkMetrics metrics;
  protected Logger logger = LoggerFactory.getLogger(getClass());
  public Acceptor(String host, int port, ArrayList<Processor> processors, int sendBufferSize, int recvBufferSize, NetworkMetrics metrics) throws IOException {
    this.host = host;
    this.port = port;
    this.processors = processors;
    this.sendBufferSize = sendBufferSize;
    this.recvBufferSize = recvBufferSize;
    this.serverChannel = openServerSocket(this.host, this.port);
    this.nioSelector = java.nio.channels.Selector.open();
    this.metrics = metrics;
  }
  public void run() {
    try {
      serverChannel.register(nioSelector, SelectionKey.OP_ACCEPT);
      startupComplete();
      int currentProcessor = 0;
      while (isRunning()){
        int ready = nioSelector.select(selectTimeOutMs);
        if (ready > 0) {
          Set<SelectionKey> keys = nioSelector.selectedKeys();
          Iterator<SelectionKey> iter = keys.iterator();
          while (iter.hasNext() && isRunning()){
            SelectionKey key = null;
            try {
              key = iter.next();
              iter.remove();
              if (key.isAcceptable()) {
                accept(key, processors.get(currentProcessor));
              }
              else {
                throw new IllegalStateException("Unrecognized key state for acceptor thread.");
              }
              currentProcessor = (currentProcessor + 1) % processors.size();
            }
            catch (Exception e) {
              key.cancel();
              metrics.acceptConnectionErrorCount.inc();
              logger.debug("Error in accepting new connection", e);
            }
          }
        }
      }
      logger.debug("Closing server socket and selector.");
      serverChannel.close();
      nioSelector.close();
      shutdownComplete();
    }
    catch (Exception e) {
      metrics.acceptorShutDownErrorCount.inc();
      logger.error("Error during shutdown of acceptor thread", e);
    }
  }
  private ServerSocketChannel openServerSocket(String host, int port) throws IOException {
    InetSocketAddress address = null;
    if (host == null || host.trim().isEmpty()) {
      address = new InetSocketAddress(port);
    }
    else {
      address = new InetSocketAddress(host, port);
    }
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.configureBlocking(false);
    serverChannel.socket().bind(address);
    logger.info("Awaiting socket connections on {}:{}", address.getHostName(), port);
    return serverChannel;
  }
  private void accept(SelectionKey key, Processor processor) throws SocketException, IOException {
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
    serverSocketChannel.socket().setReceiveBufferSize(recvBufferSize);
    SocketChannel socketChannel = serverSocketChannel.accept();
    socketChannel.configureBlocking(false);
    socketChannel.socket().setTcpNoDelay(true);
    socketChannel.socket().setSendBufferSize(sendBufferSize);
    logger.trace("Accepted connection from {} on {}. sendBufferSize " + "[actual|requested]: [{}|{}] recvBufferSize [actual|requested]: [{}|{}]", socketChannel.socket().getInetAddress(), socketChannel.socket().getLocalSocketAddress(), socketChannel.socket().getSendBufferSize(), sendBufferSize, socketChannel.socket().getReceiveBufferSize(), recvBufferSize);
    processor.accept(socketChannel);
  }
  public void shutdown() throws InterruptedException {
    nioSelector.wakeup();
    super.shutdown();
  }
}

class Processor extends AbstractServerThread {
  private final int maxRequestSize;
  private final SocketRequestResponseChannel channel;
  private final int id;
  private final Time time;
  private final ConcurrentLinkedQueue<SocketChannel> newConnections = new ConcurrentLinkedQueue<SocketChannel>();
  private final Selector selector;
  private final NetworkMetrics metrics;
  private static final long pollTimeoutMs = 300;
  Processor(int id, int maxRequestSize, RequestResponseChannel channel, NetworkMetrics metrics) throws IOException {
    this.maxRequestSize = maxRequestSize;
    this.channel = (SocketRequestResponseChannel)channel;
    this.id = id;
    this.time = SystemTime.getInstance();
    selector = new Selector(metrics, time);
    this.metrics = metrics;
  }
  public void run() {
    try {
      startupComplete();
      while (isRunning()){
        configureNewConnections();
        processNewResponses();
        selector.poll(pollTimeoutMs);
        List<NetworkReceive> completedReceives = selector.completedReceives();
        for (NetworkReceive networkReceive : completedReceives) {
          String connectionId = networkReceive.getConnectionId();
          SocketServerRequest req = new SocketServerRequest(id, connectionId, new ByteBufferInputStream(networkReceive.getReceivedBytes().getPayload()));
          channel.sendRequest(req);
        }
      }
    }
    catch (Exception e) {
      logger.error("Error in processor thread", e);
    }
    finally {
      logger.debug("Closing server socket and selector.");
      closeAll();
      shutdownComplete();
    }
  }
  private void processNewResponses() throws InterruptedException, IOException {
    SocketServerResponse curr = (SocketServerResponse)channel.receiveResponse(id);
    while (curr != null){
      curr.onDequeueFromResponseQueue();
      SocketServerRequest request = (SocketServerRequest)curr.getRequest();
      String connectionId = request.getConnectionId();
      try {
        if (curr.getPayload() == null) {
          logger.trace("Socket server received no response and hence closing the connection");
          selector.close(connectionId);
        }
        else {
          logger.trace("Socket server received response to send, registering for write: {}", curr);
          NetworkSend networkSend = new NetworkSend(connectionId, curr.getPayload(), curr.getMetrics(), time);
          selector.send(networkSend);
        }
      }
      catch (IllegalStateException e) {
        metrics.processNewResponseErrorCount.inc();
        logger.debug("Error in processing new responses", e);
      }
      finally {
        curr = (SocketServerResponse)channel.receiveResponse(id);
      }
    }
  }
  public void accept(SocketChannel socketChannel) {
    newConnections.add(socketChannel);
    wakeup();
  }
  private void closeAll() {
    selector.close();
  }
  private void configureNewConnections() throws ClosedChannelException {
    while (newConnections.size() > 0){
      SocketChannel channel = newConnections.poll();
      logger.debug("Processor {} listening to new connection from {}", id, channel.socket().getRemoteSocketAddress());
      selector.register(channel);
    }
  }
  public void shutdown() throws InterruptedException {
    selector.wakeup();
    super.shutdown();
  }
  public void wakeup() {
    selector.wakeup();
  }
}

