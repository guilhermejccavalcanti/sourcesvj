<<<<<<< MINE
client.getZooKeeper().delete(operationAndData.getData(), version, new AsyncCallback.VoidCallback() {
        @Override public void processResult(int rc, String path, Object ctx) {
          trace.commit();
          if ((rc == KeeperException.Code.NOTEMPTY.intValue()) && deletingChildrenIfNeeded) {
            backgroundDeleteChildrenThenNode(operationAndData);
          }
          else {
            if ((rc == KeeperException.Code.NONODE.intValue()) && quietly) {
              rc = KeeperException.Code.OK.intValue();
            }
            CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.DELETE, rc, path, null, ctx, null, null, null, null, null, null);
            client.processBackgroundOperation(operationAndData, event);
          }
        }
    }, backgrounding.getContext());
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("DeleteBuilderImpl-Background");
      client.getZooKeeper().delete(operationAndData.getData(), version, new AsyncCallback.VoidCallback() {
          @Override public void processResult(int rc, String path, Object ctx) {
            trace.commit();
            if ((rc == KeeperException.Code.NOTEMPTY.intValue()) && deletingChildrenIfNeeded) {
              backgroundDeleteChildrenThenNode(operationAndData);
            }
            else {
              CuratorEvent event = new CuratorEventImpl(client, CuratorEventType.DELETE, rc, path, null, ctx, null, null, null, null, null);
              client.processBackgroundOperation(operationAndData, event);
            }
          }
      }, backgrounding.getContext());
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> YOURS

