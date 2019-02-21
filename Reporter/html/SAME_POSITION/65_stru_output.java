package org.apache.curator.framework.recipes.nodes;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.WatcherRemoveCuratorFramework;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.CreateBuilderMain;
import org.apache.curator.framework.api.CreateModable;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.utils.ThreadUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class PersistentNode implements Closeable {
  private final AtomicReference<CountDownLatch> initialCreateLatch = new AtomicReference<CountDownLatch>(new CountDownLatch(1));
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final WatcherRemoveCuratorFramework client;
  private final AtomicReference<CreateModable<ACLBackgroundPathAndBytesable<String>>> createMethod = new AtomicReference<CreateModable<ACLBackgroundPathAndBytesable<String>>>(null);
  private final AtomicReference<String> nodePath = new AtomicReference<String>(null);
  private final String basePath;
  private final CreateMode mode;
  private final AtomicReference<byte[]> data = new AtomicReference<byte[]>();
  private final AtomicReference<State> state = new AtomicReference<State>(State.LATENT);
  private final AtomicBoolean authFailure = new AtomicBoolean(false);
  private final BackgroundCallback backgroundCallback;
  private final boolean useProtection;
  private final CuratorWatcher watcher = new CuratorWatcher() {
      @Override public void process(WatchedEvent event) throws Exception {
        if (isActive()) {
          if (event.getType() == EventType.NodeDeleted) {
            createNode();
          }
          else 
            if (event.getType() == EventType.NodeDataChanged) {
              watchNode();
            }
        }
      }
  };
  private final BackgroundCallback checkExistsCallback = new BackgroundCallback() {
      @Override public void processResult(CuratorFramework dummy, CuratorEvent event) throws Exception {
        if (isActive()) {
          if (event.getResultCode() == KeeperException.Code.NONODE.intValue()) {
            createNode();
          }
          else {
            boolean isEphemeral = event.getStat().getEphemeralOwner() != 0;
            if (isEphemeral != mode.isEphemeral()) {
              log.warn("Existing node ephemeral state doesn\'t match requested state. Maybe the node was created outside of PersistentNode? " + basePath);
            }
          }
        }
        else {
          client.removeWatchers();
        }
      }
  };
  private final BackgroundCallback setDataCallback = new BackgroundCallback() {
      @Override public void processResult(CuratorFramework dummy, CuratorEvent event) throws Exception {
        if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
          initialisationComplete();
        }
        else 
          if (event.getResultCode() == KeeperException.Code.NOAUTH.intValue()) {
            log.warn("Client does not have authorisation to write node at path {}", event.getPath());
            authFailure.set(true);
          }
      }
  };
  private final ConnectionStateListener connectionStateListener = new ConnectionStateListener() {
      @Override public void stateChanged(CuratorFramework dummy, ConnectionState newState) {
        if ((newState == ConnectionState.RECONNECTED) && isActive()) {
          createNode();
        }
      }
  };
  @VisibleForTesting volatile CountDownLatch debugCreateNodeLatch = null;
  private enum State {
    LATENT(),

    STARTED(),

    CLOSED(),

  ;
  }
  public PersistentNode(CuratorFramework givenClient, final CreateMode mode, boolean useProtection, final String basePath, byte[] initData) {
    this(givenClient, mode, useProtection, basePath, initData, -1);
  }
  public PersistentNode(CuratorFramework givenClient, final CreateMode mode, boolean useProtection, final String basePath, byte[] initData, long ttl) {
    this.useProtection = useProtection;
    this.client = Preconditions.checkNotNull(givenClient, "client cannot be null").newWatcherRemoveCuratorFramework();
    this.basePath = PathUtils.validatePath(basePath);
    this.mode = Preconditions.checkNotNull(mode, "mode cannot be null");
    final byte[] data = Preconditions.checkNotNull(initData, "data cannot be null");
    backgroundCallback = new BackgroundCallback() {
        @Override public void processResult(CuratorFramework dummy, CuratorEvent event) throws Exception {
          if (isActive()) {
            processBackgroundCallback(event);
          }
          else {
            processBackgroundCallbackClosedState(event);
          }
        }
    };
    CreateBuilderMain createBuilder = mode.isTTL() ? client.create().withTtl(ttl) : client.create();
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_e7d57ec_b3939ac\rev_left_e7d57ec\curator-recipes\src\main\java\org\apache\curator\framework\recipes\nodes\PersistentNode.java
createMethod = useProtection ? createBuilder.creatingParentContainersIfNeeded().withProtection() : createBuilder.creatingParentContainersIfNeeded();
=======
this.data.set(Arrays.copyOf(data, data.length));
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_e7d57ec_b3939ac\rev_right_b3939ac\curator-recipes\src\main\java\org\apache\curator\framework\recipes\nodes\PersistentNode.java

    this.data.set(Arrays.copyOf(data, data.length));
  }
  private void processBackgroundCallbackClosedState(CuratorEvent event) {
    String path = null;
    if (event.getResultCode() == KeeperException.Code.NODEEXISTS.intValue()) {
      path = event.getPath();
    }
    else 
      if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
        path = event.getName();
      }
    if (path != null) {
      try {
        client.delete().guaranteed().inBackground().forPath(path);
      }
      catch (Exception e) {
        log.error("Could not delete node after close", e);
      }
    }
  }
  private void processBackgroundCallback(CuratorEvent event) throws Exception {
    String path = null;
    boolean nodeExists = false;
    if (event.getResultCode() == KeeperException.Code.NODEEXISTS.intValue()) {
      path = event.getPath();
      nodeExists = true;
    }
    else 
      if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
        path = event.getName();
      }
      else 
        if (event.getResultCode() == KeeperException.Code.NOAUTH.intValue()) {
          log.warn("Client does not have authorisation to create node at path {}", event.getPath());
          authFailure.set(true);
          return ;
        }
    if (path != null) {
      authFailure.set(false);
      nodePath.set(path);
      watchNode();
      if (nodeExists) {
        client.setData().inBackground(setDataCallback).forPath(getActualPath(), getData());
      }
      else {
        initialisationComplete();
      }
    }
    else {
      createNode();
    }
  }
  private void initialisationComplete() {
    CountDownLatch localLatch = initialCreateLatch.getAndSet(null);
    if (localLatch != null) {
      localLatch.countDown();
    }
  }
  public void start() {
    Preconditions.checkState(state.compareAndSet(State.LATENT, State.STARTED), "Already started");
    client.getConnectionStateListenable().addListener(connectionStateListener);
    createNode();
  }
  public boolean waitForInitialCreate(long timeout, TimeUnit unit) throws InterruptedException {
    Preconditions.checkState(state.get() == State.STARTED, "Not started");
    CountDownLatch localLatch = initialCreateLatch.get();
    return (localLatch == null) || localLatch.await(timeout, unit);
  }
  @VisibleForTesting final AtomicLong debugWaitMsForBackgroundBeforeClose = new AtomicLong(0);
  @Override public void close() throws IOException {
    if (debugWaitMsForBackgroundBeforeClose.get() > 0) {
      try {
        Thread.sleep(debugWaitMsForBackgroundBeforeClose.get());
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    if (!state.compareAndSet(State.STARTED, State.CLOSED)) {
      return ;
    }
    client.getConnectionStateListenable().removeListener(connectionStateListener);
    try {
      deleteNode();
    }
    catch (Exception e) {
      ThreadUtils.checkInterrupted(e);
      throw new IOException(e);
    }
    client.removeWatchers();
  }
  public String getActualPath() {
    return nodePath.get();
  }
  public void setData(byte[] data) throws Exception {
    data = Preconditions.checkNotNull(data, "data cannot be null");
    Preconditions.checkState(nodePath.get() != null, "initial create has not been processed. Call waitForInitialCreate() to ensure.");
    this.data.set(Arrays.copyOf(data, data.length));
    if (isActive()) {
      client.setData().inBackground(setDataCallback).forPath(getActualPath(), getData());
    }
  }
  public byte[] getData() {
    return this.data.get();
  }
  protected void deleteNode() throws Exception {
    String localNodePath = nodePath.getAndSet(null);
    if (localNodePath != null) {
      try {
        client.delete().guaranteed().forPath(localNodePath);
      }
      catch (KeeperException.NoNodeException ignore) {
      }
    }
  }
  private void createNode() {
    if (!isActive()) {
      return ;
    }
    if (debugCreateNodeLatch != null) {
      try {
        debugCreateNodeLatch.await();
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return ;
      }
    }
    try {
      String existingPath = nodePath.get();
      String createPath = (existingPath != null && !useProtection) ? existingPath : basePath;
      CreateModable<ACLBackgroundPathAndBytesable<String>> localCreateMethod = createMethod.get();
      if (localCreateMethod == null) {
        CreateModable<ACLBackgroundPathAndBytesable<String>> tempCreateMethod = useProtection ? client.create().creatingParentContainersIfNeeded().withProtection() : client.create().creatingParentContainersIfNeeded();
        if (createMethod.compareAndSet(null, tempCreateMethod)) {
          localCreateMethod = tempCreateMethod;
        }
      }
      localCreateMethod.withMode(getCreateMode(existingPath != null)).inBackground(backgroundCallback).forPath(createPath, data.get());
    }
    catch (Exception e) {
      ThreadUtils.checkInterrupted(e);
      throw new RuntimeException("Creating node. BasePath: " + basePath, e);
    }
  }
  private CreateMode getCreateMode(boolean pathIsSet) {
    if (pathIsSet) {
      switch (mode){
        default:
        {
          break ;
        }
        case EPHEMERAL_SEQUENTIAL:
        {
          return CreateMode.EPHEMERAL;
        }
        case PERSISTENT_SEQUENTIAL:
        {
          return CreateMode.PERSISTENT;
        }
        case PERSISTENT_SEQUENTIAL_WITH_TTL:
        {
          return CreateMode.PERSISTENT_WITH_TTL;
        }
      }
    }
    return mode;
  }
  private void watchNode() throws Exception {
    if (!isActive()) {
      return ;
    }
    String localNodePath = nodePath.get();
    if (localNodePath != null) {
      client.checkExists().usingWatcher(watcher).inBackground(checkExistsCallback).forPath(localNodePath);
    }
  }
  private boolean isActive() {
    return (state.get() == State.STARTED);
  }
  @VisibleForTesting boolean isAuthFailure() {
    return authFailure.get();
  }
}

