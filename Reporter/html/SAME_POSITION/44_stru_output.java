package org.apache.curator.framework.imps;
import org.apache.curator.RetryLoop;
import org.apache.curator.TimeTrace;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.*;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

class ExistsBuilderImpl implements ExistsBuilder, BackgroundOperation<String>, ErrorListenerPathable<Stat> {
  private final CuratorFrameworkImpl client;
  private Backgrounding backgrounding;
  private Watching watching;
  private boolean createParentContainersIfNeeded;
  ExistsBuilderImpl(CuratorFrameworkImpl client) {
    this.client = client;
    backgrounding = new Backgrounding();
    watching = new Watching();
    createParentContainersIfNeeded = false;
  }
  @Override public ExistsBuilderMain creatingParentContainersIfNeeded() {
    createParentContainersIfNeeded = true;
    return this;
  }
  @Override public BackgroundPathable<Stat> watched() {
    watching = new Watching(true);
    return this;
  }
  @Override public BackgroundPathable<Stat> usingWatcher(Watcher watcher) {
    watching = new Watching(watcher);
    return this;
  }
  @Override public BackgroundPathable<Stat> usingWatcher(CuratorWatcher watcher) {
    watching = new Watching(watcher);
    return this;
  }
  @Override public ErrorListenerPathable<Stat> inBackground(BackgroundCallback callback, Object context) {
    backgrounding = new Backgrounding(callback, context);
    return this;
  }
  @Override public ErrorListenerPathable<Stat> inBackground(BackgroundCallback callback, Object context, Executor executor) {
    backgrounding = new Backgrounding(client, callback, context, executor);
    return this;
  }
  @Override public ErrorListenerPathable<Stat> inBackground(BackgroundCallback callback) {
    backgrounding = new Backgrounding(callback);
    return this;
  }
  @Override public ErrorListenerPathable<Stat> inBackground(BackgroundCallback callback, Executor executor) {
    backgrounding = new Backgrounding(client, callback, executor);
    return this;
  }
  @Override public ErrorListenerPathable<Stat> inBackground() {
    backgrounding = new Backgrounding(true);
    return this;
  }
  @Override public ErrorListenerPathable<Stat> inBackground(Object context) {
    backgrounding = new Backgrounding(context);
    return this;
  }
  @Override public Pathable<Stat> withUnhandledErrorListener(UnhandledErrorListener listener) {
    backgrounding = new Backgrounding(backgrounding, listener);
    return this;
  }
  @Override public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\ExistsBuilderImpl.java
AsyncCallback.StatCallback callback = new AsyncCallback.StatCallback() {
        @Override public void processResult(int rc, String path, Object ctx, Stat stat) {
          trace.commit();
          CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.EXISTS, rc, path, null, ctx, stat, null, null, null, null, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("ExistsBuilderImpl-Background");
      AsyncCallback.StatCallback callback = new AsyncCallback.StatCallback() {
          @Override public void processResult(int rc, String path, Object ctx, Stat stat) {
            trace.commit();
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.EXISTS, rc, path, null, ctx, stat, null, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      if (watching.isWatched()) {
        client.getZooKeeper().exists(operationAndData.getData(), true, callback, backgrounding.getContext());
      }
      else {
        client.getZooKeeper().exists(operationAndData.getData(), watching.getWatcher(), callback, backgrounding.getContext());
      }
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\ExistsBuilderImpl.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\ExistsBuilderImpl.java
if (watching.isWatched()) {
      client.getZooKeeper().exists(operationAndData.getData(), true, callback, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().exists(operationAndData.getData(), watching.getWatcher(client, operationAndData.getData()), callback, backgrounding.getContext());
    }
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("ExistsBuilderImpl-Background");
      AsyncCallback.StatCallback callback = new AsyncCallback.StatCallback() {
          @Override public void processResult(int rc, String path, Object ctx, Stat stat) {
            trace.commit();
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.EXISTS, rc, path, null, ctx, stat, null, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      if (watching.isWatched()) {
        client.getZooKeeper().exists(operationAndData.getData(), true, callback, backgrounding.getContext());
      }
      else {
        client.getZooKeeper().exists(operationAndData.getData(), watching.getWatcher(), callback, backgrounding.getContext());
      }
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\ExistsBuilderImpl.java

    if (watching.isWatched()) {
      client.getZooKeeper().exists(operationAndData.getData(), true, callback, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().exists(operationAndData.getData(), watching.getWatcher(client, operationAndData.getData()), callback, backgrounding.getContext());
    }
  }
  @Override public Stat forPath(String path) throws Exception {
    path = client.fixForNamespace(path);
    Stat returnStat = null;
    if (backgrounding.inBackground()) {
      OperationAndData<String> operationAndData = new OperationAndData<String>(this, path, backgrounding.getCallback(), null, backgrounding.getContext());
      if (createParentContainersIfNeeded) {
        CreateBuilderImpl.backgroundCreateParentsThenNode(client, operationAndData, operationAndData.getData(), backgrounding, true);
      }
      else {
        client.processBackgroundOperation(operationAndData, null);
      }
    }
    else {
      returnStat = pathInForeground(path);
    }
    return returnStat;
  }
  private Stat pathInForeground(final String path) throws Exception {
    if (createParentContainersIfNeeded) {
      final String parent = ZKPaths.getPathAndNode(path).getPath();
      if (!parent.equals(ZKPaths.PATH_SEPARATOR)) {
        TimeTrace trace = client.getZookeeperClient().startTracer("ExistsBuilderImpl-Foreground-CreateParents");
        RetryLoop.callWithRetry(client.getZookeeperClient(), new Callable<Void>() {
            @Override public Void call() throws Exception {
              try {
                ZKPaths.mkdirs(client.getZooKeeper(), parent, true, client.getAclProvider(), true);
              }
              catch (KeeperException e) {
              }
              return null;
            }
        });
        trace.commit();
      }
    }
    return pathInForegroundStandard(path);
  }
  private Stat pathInForegroundStandard(final String path) throws Exception {
    TimeTrace trace = client.getZookeeperClient().startTracer("ExistsBuilderImpl-Foreground");
    Stat returnStat = RetryLoop.callWithRetry(client.getZookeeperClient(), new Callable<Stat>() {
        @Override public Stat call() throws Exception {
          Stat returnStat;
          if (watching.isWatched()) {
            returnStat = client.getZooKeeper().exists(path, true);
          }
          else {
            returnStat = client.getZooKeeper().exists(path, watching.getWatcher(client, path));
          }
          return returnStat;
        }
    });
    trace.commit();
    return returnStat;
  }
}

