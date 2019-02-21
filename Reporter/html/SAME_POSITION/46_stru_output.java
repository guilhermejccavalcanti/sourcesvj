package org.apache.curator.framework.imps;
import org.apache.curator.RetryLoop;
import org.apache.curator.TimeTrace;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

class GetACLBuilderImpl implements GetACLBuilder, BackgroundOperation<String>, ErrorListenerPathable<List<ACL>> {
  private final CuratorFrameworkImpl client;
  private Backgrounding backgrounding;
  private Stat responseStat;
  GetACLBuilderImpl(CuratorFrameworkImpl client) {
    this.client = client;
    backgrounding = new Backgrounding();
    responseStat = new Stat();
  }
  @Override public ErrorListenerPathable<List<ACL>> inBackground(BackgroundCallback callback, Object context) {
    backgrounding = new Backgrounding(callback, context);
    return this;
  }
  @Override public ErrorListenerPathable<List<ACL>> inBackground(BackgroundCallback callback, Object context, Executor executor) {
    backgrounding = new Backgrounding(client, callback, context, executor);
    return this;
  }
  @Override public ErrorListenerPathable<List<ACL>> inBackground() {
    backgrounding = new Backgrounding(true);
    return this;
  }
  @Override public ErrorListenerPathable<List<ACL>> inBackground(Object context) {
    backgrounding = new Backgrounding(context);
    return this;
  }
  @Override public ErrorListenerPathable<List<ACL>> inBackground(BackgroundCallback callback) {
    backgrounding = new Backgrounding(callback);
    return this;
  }
  @Override public ErrorListenerPathable<List<ACL>> inBackground(BackgroundCallback callback, Executor executor) {
    backgrounding = new Backgrounding(client, callback, executor);
    return this;
  }
  @Override public Pathable<List<ACL>> withUnhandledErrorListener(UnhandledErrorListener listener) {
    backgrounding = new Backgrounding(backgrounding, listener);
    return this;
  }
  @Override public Pathable<List<ACL>> storingStatIn(Stat stat) {
    responseStat = stat;
    return this;
  }
  @Override public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\GetACLBuilderImpl.java
AsyncCallback.ACLCallback callback = new AsyncCallback.ACLCallback() {
        @Override public void processResult(int rc, String path, Object ctx, List<ACL> acl, Stat stat) {
          trace.commit();
          CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.GET_ACL, rc, path, null, ctx, stat, null, null, null, acl, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("GetACLBuilderImpl-Background");
      AsyncCallback.ACLCallback callback = new AsyncCallback.ACLCallback() {
          @Override public void processResult(int rc, String path, Object ctx, List<ACL> acl, Stat stat) {
            trace.commit();
            CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.GET_ACL, rc, path, null, ctx, stat, null, null, null, acl);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      client.getZooKeeper().getACL(operationAndData.getData(), responseStat, callback, backgrounding.getContext());
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\GetACLBuilderImpl.java

    AsyncCallback.ACLCallback callback = new AsyncCallback.ACLCallback() {
        @Override public void processResult(int rc, String path, Object ctx, List<ACL> acl, Stat stat) {
          trace.commit();
          CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.GET_ACL, rc, path, null, ctx, stat, null, null, null, acl, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
    client.getZooKeeper().getACL(operationAndData.getData(), responseStat, callback, backgrounding.getContext());
  }
  @Override public List<ACL> forPath(String path) throws Exception {
    path = client.fixForNamespace(path);
    List<ACL> result = null;
    if (backgrounding.inBackground()) {
      client.processBackgroundOperation(new OperationAndData<String>(this, path, backgrounding.getCallback(), null, backgrounding.getContext()), null);
    }
    else {
      result = pathInForeground(path);
    }
    return result;
  }
  private List<ACL> pathInForeground(final String path) throws Exception {
    TimeTrace trace = client.getZookeeperClient().startTracer("GetACLBuilderImpl-Foreground");
    List<ACL> result = RetryLoop.callWithRetry(client.getZookeeperClient(), new Callable<List<ACL>>() {
        @Override public List<ACL> call() throws Exception {
          return client.getZooKeeper().getACL(path, responseStat);
        }
    });
    trace.commit();
    return result;
  }
}

