package org.apache.curator.framework.recipes.cache;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.utils.CloseableExecutorService;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;
import java.util.concurrent.Exchanger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings(value = {"NullableProblems", }) public class PathChildrenCache implements Closeable {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final CuratorFramework client;
  private final String path;
  private final CloseableExecutorService executorService;
  private final boolean cacheData;
  private final boolean dataIsCompressed;
  private final EnsurePath ensurePath;
  private final ListenerContainer<PathChildrenCacheListener> listeners = new ListenerContainer<PathChildrenCacheListener>();
  private final ConcurrentMap<String, ChildData> currentData = Maps.newConcurrentMap();
  private final AtomicReference<Map<String, ChildData>> initialSet = new AtomicReference<Map<String, ChildData>>();
  private final Set<Operation> operationsQuantizer = Sets.newSetFromMap(Maps.<Operation, Boolean>newConcurrentMap());
  private final AtomicReference<State> state = new AtomicReference<State>(State.LATENT);
  private enum State {
    LATENT(),

    STARTED(),

    CLOSED(),

  ;
  }
  private static final ChildData NULL_CHILD_DATA = new ChildData(null, null, null);
  private final Watcher childrenWatcher = new Watcher() {
      @Override public void process(WatchedEvent event) {
        offerOperation(new RefreshOperation(PathChildrenCache.this, RefreshMode.STANDARD));
      }
  };
  private final Watcher dataWatcher = new Watcher() {
      @Override public void process(WatchedEvent event) {
        try {
          if (event.getType() == Event.EventType.NodeDeleted) {
            remove(event.getPath());
          }
          else 
            if (event.getType() == Event.EventType.NodeDataChanged) {
              offerOperation(new GetDataOperation(PathChildrenCache.this, event.getPath()));
            }
        }
        catch (Exception e) {
          handleException(e);
        }
      }
  };
  @VisibleForTesting volatile Exchanger<Object> rebuildTestExchanger;
  private final ConnectionStateListener connectionStateListener = new ConnectionStateListener() {
      @Override public void stateChanged(CuratorFramework client, ConnectionState newState) {
        handleStateChange(newState);
      }
  };
  private static final ThreadFactory defaultThreadFactory = ThreadUtils.newThreadFactory("PathChildrenCache");
  @SuppressWarnings(value = {"deprecation", }) public PathChildrenCache(CuratorFramework client, String path, PathChildrenCacheMode mode) {
    this(client, path, mode != PathChildrenCacheMode.CACHE_PATHS_ONLY, false, Executors.newSingleThreadExecutor(defaultThreadFactory));
  }
  @SuppressWarnings(value = {"deprecation", }) public PathChildrenCache(CuratorFramework client, String path, PathChildrenCacheMode mode, ThreadFactory threadFactory) {
    this(client, path, mode != PathChildrenCacheMode.CACHE_PATHS_ONLY, false, Executors.newSingleThreadExecutor(threadFactory));
  }
  public PathChildrenCache(CuratorFramework client, String path, boolean cacheData) {
    this(client, path, cacheData, false, Executors.newSingleThreadExecutor(defaultThreadFactory));
  }
  public PathChildrenCache(CuratorFramework client, String path, boolean cacheData, ThreadFactory threadFactory) {
    this(client, path, cacheData, false, Executors.newSingleThreadExecutor(threadFactory));
  }
  public PathChildrenCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, ThreadFactory threadFactory) {
    this(client, path, cacheData, dataIsCompressed, Executors.newSingleThreadExecutor(threadFactory));
  }
  public PathChildrenCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, final ExecutorService executorService) {
    this.client = client;
    this.path = path;
    this.cacheData = cacheData;
    this.dataIsCompressed = dataIsCompressed;
    this.executorService = new CloseableExecutorService(executorService);
    ensurePath = client.newNamespaceAwareEnsurePath(path);
  }
  public void start() throws Exception {
    start(StartMode.NORMAL);
  }
  public void start(boolean buildInitial) throws Exception {
    start(buildInitial ? StartMode.BUILD_INITIAL_CACHE : StartMode.NORMAL);
  }
  public enum StartMode {
    NORMAL(),

    BUILD_INITIAL_CACHE(),

    POST_INITIALIZED_EVENT(),

  ;
  }
  public void start(StartMode mode) throws Exception {
    Preconditions.checkState(state.compareAndSet(State.LATENT, State.STARTED), "already started");
    mode = Preconditions.checkNotNull(mode, "mode cannot be null");
    client.getConnectionStateListenable().addListener(connectionStateListener);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_11ae23a_601bc4c\rev_left_11ae23a\curator-recipes\src\main\java\org\apache\curator\framework\recipes\cache\PathChildrenCache.java
switch (mode){
      case NORMAL:
      {
        offerOperation(new RefreshOperation(this, RefreshMode.STANDARD));
        break ;
      }
      case BUILD_INITIAL_CACHE:
      {
        rebuild();
        break ;
      }
      case POST_INITIALIZED_EVENT:
      {
        initialSet.set(Maps.<String, ChildData>newConcurrentMap());
        offerOperation(new RefreshOperation(this, RefreshMode.POST_INITIALIZED));
        break ;
      }
    }
=======
executorService.submit(new Runnable() {
        @Override public void run() {
          mainLoop();
        }
    });
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_11ae23a_601bc4c\rev_right_601bc4c\curator-recipes\src\main\java\org\apache\curator\framework\recipes\cache\PathChildrenCache.java

  }
  public void rebuild() throws Exception {
    Preconditions.checkState(!executorService.isShutdown(), "cache has been closed");
    ensurePath.ensure(client.getZookeeperClient());
    clear();
    List<String> children = client.getChildren().forPath(path);
    for (String child : children) {
      String fullPath = ZKPaths.makePath(path, child);
      internalRebuildNode(fullPath);
      if (rebuildTestExchanger != null) {
        rebuildTestExchanger.exchange(new Object());
      }
    }
    offerOperation(new RefreshOperation(this, RefreshMode.FORCE_GET_DATA_AND_STAT));
  }
  public void rebuildNode(String fullPath) throws Exception {
    Preconditions.checkArgument(ZKPaths.getPathAndNode(fullPath).getPath().equals(path), "Node is not part of this cache: " + fullPath);
    Preconditions.checkState(!executorService.isShutdown(), "cache has been closed");
    ensurePath.ensure(client.getZookeeperClient());
    internalRebuildNode(fullPath);
    offerOperation(new RefreshOperation(this, RefreshMode.FORCE_GET_DATA_AND_STAT));
  }
  @Override public void close() throws IOException {
    if (state.compareAndSet(State.STARTED, State.CLOSED)) {
      client.getConnectionStateListenable().removeListener(connectionStateListener);
      executorService.shutdownNow();
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_11ae23a_601bc4c\rev_left_11ae23a\curator-recipes\src\main\java\org\apache\curator\framework\recipes\cache\PathChildrenCache.java
if (state.compareAndSet(State.STARTED, State.CLOSED)) {
      client.getConnectionStateListenable().removeListener(connectionStateListener);
      executorService.shutdownNow();
    }
=======
executorService.close();
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_11ae23a_601bc4c\rev_right_601bc4c\curator-recipes\src\main\java\org\apache\curator\framework\recipes\cache\PathChildrenCache.java

  }
  public ListenerContainer<PathChildrenCacheListener> getListenable() {
    return listeners;
  }
  public List<ChildData> getCurrentData() {
    return ImmutableList.copyOf(Sets.<ChildData>newTreeSet(currentData.values()));
  }
  public ChildData getCurrentData(String fullPath) {
    return currentData.get(fullPath);
  }
  public void clearDataBytes(String fullPath) {
    clearDataBytes(fullPath, -1);
  }
  public boolean clearDataBytes(String fullPath, int ifVersion) {
    ChildData data = currentData.get(fullPath);
    if (data != null) {
      if ((ifVersion < 0) || (ifVersion == data.getStat().getVersion())) {
        data.clearData();
        return true;
      }
    }
    return false;
  }
  public void clearAndRefresh() throws Exception {
    currentData.clear();
    offerOperation(new RefreshOperation(this, RefreshMode.STANDARD));
  }
  public void clear() {
    currentData.clear();
  }
  enum RefreshMode {
    STANDARD(),

    FORCE_GET_DATA_AND_STAT(),

    POST_INITIALIZED(),

  ;
  }
  void refresh(final RefreshMode mode) throws Exception {
    ensurePath.ensure(client.getZookeeperClient());
    final BackgroundCallback callback = new BackgroundCallback() {
        @Override public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
          processChildren(event.getChildren(), mode);
        }
    };
    client.getChildren().usingWatcher(childrenWatcher).inBackground(callback).forPath(path);
  }
  void callListeners(final PathChildrenCacheEvent event) {
    listeners.forEach(new Function<PathChildrenCacheListener, Void>() {
        @Override public Void apply(PathChildrenCacheListener listener) {
          try {
            listener.childEvent(client, event);
          }
          catch (Exception e) {
            handleException(e);
          }
          return null;
        }
    });
  }
  void getDataAndStat(final String fullPath) throws Exception {
    BackgroundCallback existsCallback = new BackgroundCallback() {
        @Override public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
          applyNewData(fullPath, event.getResultCode(), event.getStat(), null);
        }
    };
    BackgroundCallback getDataCallback = new BackgroundCallback() {
        @Override public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
          applyNewData(fullPath, event.getResultCode(), event.getStat(), event.getData());
        }
    };
    if (cacheData) {
      if (dataIsCompressed) {
        client.getData().decompressed().usingWatcher(dataWatcher).inBackground(getDataCallback).forPath(fullPath);
      }
      else {
        client.getData().usingWatcher(dataWatcher).inBackground(getDataCallback).forPath(fullPath);
      }
    }
    else {
      client.checkExists().usingWatcher(dataWatcher).inBackground(existsCallback).forPath(fullPath);
    }
  }
  protected void handleException(Throwable e) {
    log.error("", e);
  }
  @VisibleForTesting protected void remove(String fullPath) {
    ChildData data = currentData.remove(fullPath);
    if (data != null) {
      offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CHILD_REMOVED, data)));
    }
    Map<String, ChildData> localInitialSet = initialSet.get();
    if (localInitialSet != null) {
      localInitialSet.remove(fullPath);
      maybeOfferInitializedEvent(localInitialSet);
    }
  }
  private void internalRebuildNode(String fullPath) throws Exception {
    if (cacheData) {
      try {
        Stat stat = new Stat();
        byte[] bytes = dataIsCompressed ? client.getData().decompressed().storingStatIn(stat).forPath(fullPath) : client.getData().storingStatIn(stat).forPath(fullPath);
        currentData.put(fullPath, new ChildData(fullPath, stat, bytes));
      }
      catch (KeeperException.NoNodeException ignore) {
        currentData.remove(fullPath);
      }
    }
    else {
      Stat stat = client.checkExists().forPath(fullPath);
      if (stat != null) {
        currentData.put(fullPath, new ChildData(fullPath, stat, null));
      }
      else {
        currentData.remove(fullPath);
      }
    }
  }
  private void handleStateChange(ConnectionState newState) {
    switch (newState){
      case SUSPENDED:
      {
        offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED, null)));
        break ;
      }
      case LOST:
      {
        offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CONNECTION_LOST, null)));
        break ;
      }
      case RECONNECTED:
      {
        try {
          offerOperation(new RefreshOperation(this, RefreshMode.FORCE_GET_DATA_AND_STAT));
          offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED, null)));
        }
        catch (Exception e) {
          handleException(e);
        }
        break ;
      }
    }
  }
  private void processChildren(List<String> children, RefreshMode mode) throws Exception {
    List<String> fullPaths = Lists.newArrayList(Lists.transform(children, new Function<String, String>() {
        @Override public String apply(String child) {
          return ZKPaths.makePath(path, child);
        }
    }));
    Set<String> removedNodes = Sets.newHashSet(currentData.keySet());
    removedNodes.removeAll(fullPaths);
    for (String fullPath : removedNodes) {
      remove(fullPath);
    }
    for (String name : children) {
      String fullPath = ZKPaths.makePath(path, name);
      if ((mode == RefreshMode.FORCE_GET_DATA_AND_STAT) || !currentData.containsKey(fullPath)) {
        getDataAndStat(fullPath);
      }
      updateInitialSet(name, NULL_CHILD_DATA);
    }
    maybeOfferInitializedEvent(initialSet.get());
  }
  private void applyNewData(String fullPath, int resultCode, Stat stat, byte[] bytes) {
    if (resultCode == KeeperException.Code.OK.intValue()) {
      ChildData data = new ChildData(fullPath, stat, bytes);
      ChildData previousData = currentData.put(fullPath, data);
      if (previousData == null) {
        offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CHILD_ADDED, data)));
      }
      else 
        if (previousData.getStat().getVersion() != stat.getVersion()) {
          offerOperation(new EventOperation(this, new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.CHILD_UPDATED, data)));
        }
      updateInitialSet(ZKPaths.getNodeFromPath(fullPath), data);
    }
  }
  private void updateInitialSet(String name, ChildData data) {
    Map<String, ChildData> localInitialSet = initialSet.get();
    if (localInitialSet != null) {
      localInitialSet.put(name, data);
      maybeOfferInitializedEvent(localInitialSet);
    }
  }
  private void maybeOfferInitializedEvent(Map<String, ChildData> localInitialSet) {
    if (!hasUninitialized(localInitialSet)) {
      if (initialSet.getAndSet(null) != null) {
        final List<ChildData> children = ImmutableList.copyOf(localInitialSet.values());
        PathChildrenCacheEvent event = new PathChildrenCacheEvent(PathChildrenCacheEvent.Type.INITIALIZED, null) {
            @Override public List<ChildData> getInitialData() {
              return children;
            }
        };
        offerOperation(new EventOperation(this, event));
      }
    }
  }
  private boolean hasUninitialized(Map<String, ChildData> localInitialSet) {
    if (localInitialSet == null) {
      return false;
    }
    Map<String, ChildData> uninitializedChildren = Maps.filterValues(localInitialSet, new Predicate<ChildData>() {
        @Override public boolean apply(ChildData input) {
          return (input == NULL_CHILD_DATA);
        }
    });
    return (uninitializedChildren.size() != 0);
  }
  private void offerOperation(final Operation operation) {
    if (operationsQuantizer.add(operation)) {
      submitToExecutor(new Runnable() {
          @Override public void run() {
            try {
              operationsQuantizer.remove(operation);
              operation.invoke();
            }
            catch (Exception e) {
              handleException(e);
            }
          }
      });
    }
  }
  private synchronized void submitToExecutor(final Runnable command) {
    if (state.get() == State.STARTED) {
      executorService.execute(command);
    }
  }
}

