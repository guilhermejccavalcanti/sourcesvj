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
>>>>>>> YOURS

