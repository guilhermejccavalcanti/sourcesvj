  public void update(final String path, final byte[] bytes) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\druid\revisions\rev_be2f153_adda148\rev_left_be2f153\client\src\main\java\com\metamx\druid\curator\announcement\Announcer.java
final ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode(path);
=======
synchronized(lock) {
      final ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode(path);
      final String parentPath = pathAndNode.getPath();
      final String nodePath = pathAndNode.getNode();
      ConcurrentMap<String, byte[]> subPaths = announcements.get(parentPath);
      if (subPaths == null || subPaths.get(nodePath) == null) {
        announce(path, bytes);
        return ;
      }
      try {
        byte[] oldBytes = subPaths.get(nodePath);
        if (!Arrays.equals(oldBytes, bytes)) {
          subPaths.put(nodePath, bytes);
          updateAnnouncement(path, bytes);
        }
      }
      catch (Exception e) {
        throw Throwables.propagate(e);
      }
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\druid\revisions\rev_be2f153_adda148\rev_right_adda148\client\src\main\java\com\metamx\druid\curator\announcement\Announcer.java

    final String parentPath = pathAndNode.getPath();
    final String nodePath = pathAndNode.getNode();
    ConcurrentMap<String, byte[]> subPaths = announcements.get(parentPath);
    if (subPaths == null || subPaths.get(nodePath) == null) {
      announce(path, bytes);
      return ;
    }
    try {
      byte[] oldBytes = subPaths.get(nodePath);
      if (!Arrays.equals(oldBytes, bytes)) {
        subPaths.put(nodePath, bytes);
        updateAnnouncement(path, bytes);
      }
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }


