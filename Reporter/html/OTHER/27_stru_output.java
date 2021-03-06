package org.nutz.lang.socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.born.Borning;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public abstract class Sockets {
  private static final Log log = Logs.get();
  public static void send(String host, int port, InputStream ins, OutputStream ops) {
    Socket socket = null;
    try {
      socket = new Socket(InetAddress.getByName(host), port);
      OutputStream sOut = socket.getOutputStream();
      Streams.write(sOut, ins);
      sOut.flush();
      if (!socket.isClosed()) {
        InputStream sReturn = socket.getInputStream();
        Streams.write(ops, sReturn);
      }
    }
    catch (IOException e) {
      throw Lang.wrapThrow(e);
    }
    finally {
      Streams.safeClose(ins);
      Streams.safeClose(ops);
      safeClose(socket);
    }
  }
  public static String sendText(String host, int port, String text) {
    StringBuilder sb = new StringBuilder();
    send(host, port, Lang.ins(text), Lang.ops(sb));
    return sb.toString();
  }
  public static void localListenOneAndStop(int port, String line, SocketAction action) {
    Map<String, SocketAction> actions = createActions();
    actions.put(line, action);
    actions.put("$:^(close|stop|bye|exit)$", doClose());
    localListenByLine(port, actions);
  }
  public static void localListenOne(int port, String line, SocketAction action) {
    Map<String, SocketAction> actions = createActions();
    actions.put(line, action);
    localListenByLine(port, actions);
  }
  private static final int DEFAULT_POOL_SIZE = 10;
  public static void localListenByLine(int port, Map<String, SocketAction> actions) {
    Sockets.localListenByLine(port, actions, DEFAULT_POOL_SIZE);
  }
  public static void localListenByLine(int port, Map<String, SocketAction> actions, int poolSize) {
    Sockets.localListenByLine(port, actions, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * poolSize));
  }
  public static void localListenByLine(int port, Map<String, SocketAction> actions, ExecutorService service) {
    localListen(port, actions, service, SocketAtom.class);
  }
  @SuppressWarnings(value = {"rawtypes", }) public static void localListen(int port, Map<String, SocketAction> actions, ExecutorService service, Class<? extends SocketAtom> klass) {
    try {
      SocketActionTable saTable = new SocketActionTable(actions);
      final ServerSocket server;
      try {
        server = new ServerSocket(port);
      }
      catch (IOException e1) {
        throw Lang.wrapThrow(e1);
      }
      if (log.isInfoEnabled()) 
        log.infof("Local socket is up at :%d with %d action ready", port, actions.size());
      final Context context = Lang.context();
      context.set("stop", false);
      (new Thread() {
          @Override public void run() {
            setName("Nutz.Sockets monitor thread");
            while (true){
              try {
                Thread.sleep(1000);
                if (context.getBoolean("stop")) {
                  try {
                    server.close();
                    server.close();
                  }
                  catch (Throwable e) {
                  }
                  return ;
                }
              }
              catch (Throwable e) {
              }
            }
          }
      }).start();
      Borning borning = Mirror.me(klass).getBorningByArgTypes(Context.class, Socket.class, SocketActionTable.class);
      if (borning == null) {
        log.error("boring == null !!!!");
        return ;
      }
      while (!context.getBoolean("stop")){
        try {
          if (log.isDebugEnabled()) 
            log.debug("Waiting for new socket");
          Socket socket = server.accept();
          if (context.getBoolean("stop")) {
            Sockets.safeClose(socket);
            break ;
          }
          if (log.isDebugEnabled()) 
            log.debug("accept a new socket, create new SocketAtom to handle it ...");
          Runnable runnable = (Runnable)borning.born(new Object[]{ context, socket, saTable } );
          service.execute(runnable);
        }
        catch (Throwable e) {
          log.info("Throwable catched!! maybe ask to exit", e);
        }
        if (log.isDebugEnabled()) 
          log.debugf("next loop \'%s\'", context.getBoolean("stop"));
      }
      if (!server.isClosed()) {
        try {
          server.close();
        }
        catch (Throwable e) {
        }
      }
      log.info("Seem stop signal was got, all running thread to exit in 60s");
      try {
        service.shutdown();
        service.awaitTermination(15, TimeUnit.SECONDS);
      }
      catch (InterruptedException e) {
      }
      try {
        service.shutdownNow();
      }
      catch (Throwable e2) {
      }
    }
    catch (RuntimeException e) {
      throw e;
    }
    finally {
      if (log.isInfoEnabled()) 
        log.info("Stop services ...");
      service.shutdown();
    }
    if (log.isInfoEnabled()) 
      log.infof("Local socket is down for :%d", port);
  }
  public static Socket safeClose(Socket socket) {
    if (null != socket) 
      try {
        socket.close();
        socket = null;
      }
      catch (IOException e) {
        throw Lang.wrapThrow(e);
      }
    return null;
  }
  public static SocketAction doClose() {
    return new SocketAction() {
        public void run(SocketContext context) {
          throw new CloseSocketException();
        }
    };
  }
  public static void close() {
    throw new CloseSocketException();
  }
  public static Map<String, SocketAction> createActions() {
    Map<String, SocketAction> actions = new HashMap<String, SocketAction>();
    return actions;
  }
}

