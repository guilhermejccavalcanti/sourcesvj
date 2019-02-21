<<<<<<< MINE
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
>>>>>>> YOURS

