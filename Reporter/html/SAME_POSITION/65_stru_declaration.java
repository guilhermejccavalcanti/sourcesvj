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


