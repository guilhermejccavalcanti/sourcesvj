  @Override public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\DeleteBuilderImpl.java
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
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\DeleteBuilderImpl.java

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
  }


