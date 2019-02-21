<<<<<<< MINE
AsyncCallback.ACLCallback callback = new AsyncCallback.ACLCallback() {
        @Override public void processResult(int rc, String path, Object ctx, List<ACL> acl, Stat stat) {
          trace.commit();
          CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.GET_ACL, rc, path, null, ctx, stat, null, null, null, acl, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("GetACLBuilderImpl-Background");
      AsyncCallback.ACLCallback callback = new AsyncCallback.ACLCallback() {
          @Override public void processResult(int rc, String path, Object ctx, List<ACL> acl, Stat stat) {
            trace.commit();
            CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.GET_ACL, rc, path, null, ctx, stat, null, null, null, acl);
            client.processBackgroundOperation(operationAndData, event);
          }
      };
      client.getZooKeeper().getACL(operationAndData.getData(), responseStat, callback, backgrounding.getContext());
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> YOURS

