<<<<<<< MINE
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
>>>>>>> YOURS

