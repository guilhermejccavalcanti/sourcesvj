  private InternalAcquireResult internalAcquire1Lease(ImmutableList.Builder<Lease> builder, long startMs, boolean hasWait, long waitMs) throws Exception {
    if (client.getState() != CuratorFrameworkState.STARTED) {
      return InternalAcquireResult.RETURN_NULL;
    }
    if (hasWait) {
      long thisWaitMs = getThisWaitMs(startMs, waitMs);
      if (!lock.acquire(thisWaitMs, TimeUnit.MILLISECONDS)) {
        return InternalAcquireResult.RETURN_NULL;
      }
    }
    else {
      lock.acquire();
    }
    Lease lease = null;
    try {
      PathAndBytesable<String> createBuilder = client.create().creatingParentContainersIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL);
      String path = (nodeData != null) ? createBuilder.forPath(ZKPaths.makePath(leasesPath, LEASE_BASE_NAME), nodeData) : createBuilder.forPath(ZKPaths.makePath(leasesPath, LEASE_BASE_NAME));
      String nodeName = ZKPaths.getNodeFromPath(path);
      lease = makeLease(path);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_a0dd0c9_8dc0283\rev_left_a0dd0c9\curator-recipes\src\main\java\org\apache\curator\framework\recipes\locks\InterProcessSemaphoreV2.java
try {
        synchronized(this) {
          for (; true; ) {
            List<String> children = client.getChildren().usingWatcher(watcher).forPath(leasesPath);
            if (!children.contains(nodeName)) {
              log.error("Sequential path not found: " + path);
              returnLease(lease);
              return InternalAcquireResult.RETRY_DUE_TO_MISSING_NODE;
            }
            if (children.size() <= maxLeases) {
              break ;
            }
            if (hasWait) {
              long thisWaitMs = getThisWaitMs(startMs, waitMs);
              if (thisWaitMs <= 0) {
                returnLease(lease);
                return InternalAcquireResult.RETURN_NULL;
              }
              wait(thisWaitMs);
            }
            else {
              wait();
            }
          }
        }
      }
      finally {
        client.removeWatchers();
      }
=======
if (debugAcquireLatch != null) {
        debugAcquireLatch.await();
      }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_a0dd0c9_8dc0283\rev_right_8dc0283\curator-recipes\src\main\java\org\apache\curator\framework\recipes\locks\InterProcessSemaphoreV2.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_a0dd0c9_8dc0283\rev_left_a0dd0c9\curator-recipes\src\main\java\org\apache\curator\framework\recipes\locks\InterProcessSemaphoreV2.java
try {
        synchronized(this) {
          for (; true; ) {
            List<String> children = client.getChildren().usingWatcher(watcher).forPath(leasesPath);
            if (!children.contains(nodeName)) {
              log.error("Sequential path not found: " + path);
              returnLease(lease);
              return InternalAcquireResult.RETRY_DUE_TO_MISSING_NODE;
            }
            if (children.size() <= maxLeases) {
              break ;
            }
            if (hasWait) {
              long thisWaitMs = getThisWaitMs(startMs, waitMs);
              if (thisWaitMs <= 0) {
                returnLease(lease);
                return InternalAcquireResult.RETURN_NULL;
              }
              wait(thisWaitMs);
            }
            else {
              wait();
            }
          }
        }
      }
      finally {
        client.removeWatchers();
      }
=======
synchronized(this) {
        for (; true; ) {
          List<String> children;
          try {
            children = client.getChildren().usingWatcher(watcher).forPath(leasesPath);
          }
          catch (Exception e) {
            if (debugFailedGetChildrenLatch != null) {
              debugFailedGetChildrenLatch.countDown();
            }
            returnLease(lease);
            throw e;
          }
          if (!children.contains(nodeName)) {
            log.error("Sequential path not found: " + path);
            returnLease(lease);
            return InternalAcquireResult.RETRY_DUE_TO_MISSING_NODE;
          }
          if (children.size() <= maxLeases) {
            break ;
          }
          if (hasWait) {
            long thisWaitMs = getThisWaitMs(startMs, waitMs);
            if (thisWaitMs <= 0) {
              returnLease(lease);
              return InternalAcquireResult.RETURN_NULL;
            }
            wait(thisWaitMs);
          }
          else {
            wait();
          }
        }
      }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_a0dd0c9_8dc0283\rev_right_8dc0283\curator-recipes\src\main\java\org\apache\curator\framework\recipes\locks\InterProcessSemaphoreV2.java

    }
    builder.add(Preconditions.checkNotNull(lease));
    return InternalAcquireResult.CONTINUE;
  }


