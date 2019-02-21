<<<<<<< MINE
AsyncCallback.Children2Callback callback = new AsyncCallback.Children2Callback() {
        @Override public void processResult(int rc, String path, Object o, List<String> strings, Stat stat) {
          trace.commit();
          if (strings == null) {
            strings = Lists.newArrayList();
          }
          CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.CHILDREN, rc, path, null, o, stat, null, strings, null, null, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("GetChildrenBuilderImpl-Background");
      AsyncCallback.Children2Callback callback = new AsyncCallback.Children2Callback() {
          @Override public void processResult(int rc, String path, Object o, List<String> strings, Stat stat) {
            trace.commit();
            if (strings == null) {
              strings = Lists.newArrayList();
            }
            CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.CHILDREN, rc, path, null, o, stat, null, strings, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      if (watching.isWatched()) {
        client.getZooKeeper().getChildren(operationAndData.getData(), true, callback, backgrounding.getContext());
      }
      else {
        client.getZooKeeper().getChildren(operationAndData.getData(), watching.getWatcher(), callback, backgrounding.getContext());
      }
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> YOURS

