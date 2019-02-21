package org.apache.curator.framework.recipes.locks;
import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.WatcherRemoveCuratorFramework;
import java.util.concurrent.TimeUnit;

public class InterProcessSemaphoreMutex implements InterProcessLock {
  private final InterProcessSemaphoreV2 semaphore;
  private final WatcherRemoveCuratorFramework watcherRemoveClient;
  private volatile Lease lease;
  public InterProcessSemaphoreMutex(CuratorFramework client, String path) {
    watcherRemoveClient = client.newWatcherRemoveCuratorFramework();
    this.semaphore = new InterProcessSemaphoreV2(watcherRemoveClient, path, 1);
  }
  @Override public void acquire() throws Exception {
    lease = semaphore.acquire();
  }
  @Override public boolean acquire(long time, TimeUnit unit) throws Exception {
    Lease acquiredLease = semaphore.acquire(time, unit);
    if (acquiredLease == null) {
      return false;
    }
    lease = acquiredLease;
    return true;
  }
  @Override public void release() throws Exception {
    Lease lease = this.lease;
    Preconditions.checkState(lease != null, "Not acquired");
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_e683264_d3bccce\rev_left_e683264\curator-recipes\src\main\java\org\apache\curator\framework\recipes\locks\InterProcessSemaphoreMutex.java
try {
      lease.close();
      watcherRemoveClient.removeWatchers();
    }
    finally {
      lease = null;
    }
=======
this.lease = null;
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_e683264_d3bccce\rev_right_d3bccce\curator-recipes\src\main\java\org\apache\curator\framework\recipes\locks\InterProcessSemaphoreMutex.java

    lease.close();
  }
  @Override public boolean isAcquiredInThisProcess() {
    return (lease != null);
  }
}

