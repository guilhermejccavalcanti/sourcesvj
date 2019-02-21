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


