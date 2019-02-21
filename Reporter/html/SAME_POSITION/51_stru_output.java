package org.apache.curator.framework.imps;
import org.apache.curator.TimeTrace;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.SyncBuilder;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.zookeeper.AsyncCallback;
import java.util.concurrent.Executor;

public class SyncBuilderImpl implements SyncBuilder, BackgroundOperation<String>, ErrorListenerPathable<Void> {
  private final CuratorFrameworkImpl client;
  private Backgrounding backgrounding = new Backgrounding();
  public SyncBuilderImpl(CuratorFrameworkImpl client) {
    this.client = client;
  }
  @Override public ErrorListenerPathable<Void> inBackground() {
    return this;
  }
  @Override public ErrorListenerPathable<Void> inBackground(Object context) {
    backgrounding = new Backgrounding(context);
    return this;
  }
  @Override public ErrorListenerPathable<Void> inBackground(BackgroundCallback callback) {
    backgrounding = new Backgrounding(callback);
    return this;
  }
  @Override public ErrorListenerPathable<Void> inBackground(BackgroundCallback callback, Object context) {
    backgrounding = new Backgrounding(callback, context);
    return this;
  }
  @Override public ErrorListenerPathable<Void> inBackground(BackgroundCallback callback, Executor executor) {
    backgrounding = new Backgrounding(client, callback, executor);
    return this;
  }
  @Override public ErrorListenerPathable<Void> inBackground(BackgroundCallback callback, Object context, Executor executor) {
    backgrounding = new Backgrounding(client, callback, context, executor);
    return this;
  }
  @Override public Pathable<Void> withUnhandledErrorListener(UnhandledErrorListener listener) {
    backgrounding = new Backgrounding(backgrounding, listener);
    return this;
  }
  @Override public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
    try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("SyncBuilderImpl-Background");
      final String path = operationAndData.getData();
      String adjustedPath = client.fixForNamespace(path);
      AsyncCallback.VoidCallback voidCallback = new AsyncCallback.VoidCallback() {
          @Override public void processResult(int rc, String path, Object ctx) {
            trace.commit();
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.SYNC, rc, path, path, ctx, null, null, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      client.getZooKeeper().sync(adjustedPath, voidCallback, backgrounding.getContext());
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\SyncBuilderImpl.java
AsyncCallback.VoidCallback voidCallback = new AsyncCallback.VoidCallback() {
        @Override public void processResult(int rc, String path, Object ctx) {
          trace.commit();
          CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.SYNC, rc, path, path, ctx, null, null, null, null, null, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("SyncBuilderImpl-Background");
      final String path = operationAndData.getData();
      String adjustedPath = client.fixForNamespace(path);
      AsyncCallback.VoidCallback voidCallback = new AsyncCallback.VoidCallback() {
          @Override public void processResult(int rc, String path, Object ctx) {
            trace.commit();
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.SYNC, rc, path, path, ctx, null, null, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      client.getZooKeeper().sync(adjustedPath, voidCallback, backgrounding.getContext());
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\SyncBuilderImpl.java

    String adjustedPath = client.fixForNamespace(path);
    AsyncCallback.VoidCallback voidCallback = new AsyncCallback.VoidCallback() {
        @Override public void processResult(int rc, String path, Object ctx) {
          trace.commit();
          CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.SYNC, rc, path, path, ctx, null, null, null, null, null, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
    client.getZooKeeper().sync(adjustedPath, voidCallback, backgrounding.getContext());
  }
  @Override public Void forPath(String path) throws Exception {
    OperationAndData<String> operationAndData = new OperationAndData<String>(this, path, backgrounding.getCallback(), null, backgrounding.getContext());
    client.processBackgroundOperation(operationAndData, null);
    return null;
  }
}

