  @Override public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\GetACLBuilderImpl.java
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
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\GetACLBuilderImpl.java

    AsyncCallback.ACLCallback callback = new AsyncCallback.ACLCallback() {
        @Override public void processResult(int rc, String path, Object ctx, List<ACL> acl, Stat stat) {
          trace.commit();
          CuratorEventImpl event = new CuratorEventImpl(client, CuratorEventType.GET_ACL, rc, path, null, ctx, stat, null, null, null, acl, null);
          client.processBackgroundOperation(operationAndData, event);
        }
    };
    client.getZooKeeper().getACL(operationAndData.getData(), responseStat, callback, backgrounding.getContext());
  }


