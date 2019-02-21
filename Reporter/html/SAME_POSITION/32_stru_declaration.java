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

  }


