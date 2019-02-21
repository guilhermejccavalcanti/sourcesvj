<<<<<<< MINE
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
>>>>>>> YOURS

