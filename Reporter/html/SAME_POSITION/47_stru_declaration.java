  @Override public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\GetChildrenBuilderImpl.java
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
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\GetChildrenBuilderImpl.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\GetChildrenBuilderImpl.java
if (watching.isWatched()) {
      client.getZooKeeper().getChildren(operationAndData.getData(), true, callback, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().getChildren(operationAndData.getData(), watching.getWatcher(client, operationAndData.getData()), callback, backgrounding.getContext());
    }
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
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\GetChildrenBuilderImpl.java

    if (watching.isWatched()) {
      client.getZooKeeper().getChildren(operationAndData.getData(), true, callback, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().getChildren(operationAndData.getData(), watching.getWatcher(client, operationAndData.getData()), callback, backgrounding.getContext());
    }
  }


