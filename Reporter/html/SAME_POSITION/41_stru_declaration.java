  public void run() {
    try {
      startupComplete();
      while (isRunning()){
        configureNewConnections();
        processNewResponses();
        long startSelectTime = SystemTime.getInstance().milliseconds();
        int ready = selector.select(300);
        logger.trace("Processor id {} selection time = {} ms", id, (SystemTime.getInstance().milliseconds() - startSelectTime));
        if (ready > 0) {
          Set<SelectionKey> keys = selector.selectedKeys();
          Iterator<SelectionKey> iter = keys.iterator();
          while (iter.hasNext() && isRunning()){
            SelectionKey key = null;
            try {
              key = iter.next();
              iter.remove();
              if (key.isReadable()) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_707fe30_1c9f597\rev_left_707fe30\ambry-network\src\main\java\com.github.ambry.network\SocketServer.java
int bytesRead = read(key);
=======
read(key);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_707fe30_1c9f597\rev_right_1c9f597\ambry-network\src\main\java\com.github.ambry.network\SocketServer.java

                if (bytesRead == -1) 
                  close(key);
              }
            }
          }
        }
      }
      logger.debug("Closing server socket and selector.");
      closeAll();
      selector.close();
      shutdownComplete();
    }
  }


