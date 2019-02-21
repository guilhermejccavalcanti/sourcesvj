<<<<<<< MINE
AsyncCallback.VoidCallback voidCallback = new AsyncCallback.VoidCallback() {
        @Override public void processResult(int rc, String path, Object ctx) {
          trace.commit();
          CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.SYNC, rc, path, path, ctx, null, null, null, null, null, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("SyncBuilderImpl-Background");
      final String path = operationAndData.getData();
      String adjustedPath = client.fixForNamespace(path);
      AsyncCallback.VoidCallback voidCallback = new AsyncCallback.VoidCallback() {
          @Override public void processResult(int rc, String path, Object ctx) {
            trace.commit();
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.SYNC, rc, path, path, ctx, null, null, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      client.getZooKeeper().sync(adjustedPath, voidCallback, backgrounding.getContext());
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> YOURS

