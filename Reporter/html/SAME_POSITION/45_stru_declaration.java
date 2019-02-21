  @Override public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\ExistsBuilderImpl.java
AsyncCallback.StatCallback callback = new AsyncCallback.StatCallback() {
        @Override public void processResult(int rc, String path, Object ctx, Stat stat) {
          trace.commit();
          CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.EXISTS, rc, path, null, ctx, stat, null, null, null, null, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("ExistsBuilderImpl-Background");
      AsyncCallback.StatCallback callback = new AsyncCallback.StatCallback() {
          @Override public void processResult(int rc, String path, Object ctx, Stat stat) {
            trace.commit();
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.EXISTS, rc, path, null, ctx, stat, null, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      if (watching.isWatched()) {
        client.getZooKeeper().exists(operationAndData.getData(), true, callback, backgrounding.getContext());
      }
      else {
        client.getZooKeeper().exists(operationAndData.getData(), watching.getWatcher(), callback, backgrounding.getContext());
      }
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\ExistsBuilderImpl.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\ExistsBuilderImpl.java
if (watching.isWatched()) {
      client.getZooKeeper().exists(operationAndData.getData(), true, callback, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().exists(operationAndData.getData(), watching.getWatcher(client, operationAndData.getData()), callback, backgrounding.getContext());
    }
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("ExistsBuilderImpl-Background");
      AsyncCallback.StatCallback callback = new AsyncCallback.StatCallback() {
          @Override public void processResult(int rc, String path, Object ctx, Stat stat) {
            trace.commit();
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.EXISTS, rc, path, null, ctx, stat, null, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      if (watching.isWatched()) {
        client.getZooKeeper().exists(operationAndData.getData(), true, callback, backgrounding.getContext());
      }
      else {
        client.getZooKeeper().exists(operationAndData.getData(), watching.getWatcher(), callback, backgrounding.getContext());
      }
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\ExistsBuilderImpl.java

    if (watching.isWatched()) {
      client.getZooKeeper().exists(operationAndData.getData(), true, callback, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().exists(operationAndData.getData(), watching.getWatcher(client, operationAndData.getData()), callback, backgrounding.getContext());
    }
  }


