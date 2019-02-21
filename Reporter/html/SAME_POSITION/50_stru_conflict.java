<<<<<<< MINE
if (watching.isWatched()) {
      client.getZooKeeper().getData(operationAndData.getData(), true, callback, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().getData(operationAndData.getData(), watching.getWatcher(client, operationAndData.getData()), callback, backgrounding.getContext());
    }
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("GetDataBuilderImpl-Background");
      AsyncCallback.DataCallback callback = new AsyncCallback.DataCallback() {
          @Override public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            trace.commit();
            if (decompress && (data != null)) {
              try {
                data = client.getCompressionProvider().decompress(path, data);
              }
              catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                log.error("Decompressing for path: " + path, e);
                rc = KeeperException.Code.DATAINCONSISTENCY.intValue();
              }
            }
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.GET_DATA, rc, path, null, ctx, stat, data, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      if (watching.isWatched()) {
        client.getZooKeeper().getData(operationAndData.getData(), true, callback, backgrounding.getContext());
      }
      else {
        client.getZooKeeper().getData(operationAndData.getData(), watching.getWatcher(), callback, backgrounding.getContext());
      }
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> YOURS

