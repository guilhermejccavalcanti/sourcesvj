  @Override public void performBackgroundOperation(final OperationAndData<PathAndBytes> operationAndData) throws Exception {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_left_8ef32cc\curator-framework\src\main\java\org\apache\curator\framework\imps\CreateBuilderImpl.java
if (storingStat == null) {
      client.getZooKeeper().create(operationAndData.getData().getPath(), operationAndData.getData().getData(), acling.getAclList(operationAndData.getData().getPath()), createMode, new AsyncCallback.StringCallback() {
          @Override public void processResult(int rc, String path, Object ctx, String name) {
            trace.commit();
            if ((rc == KeeperException.Code.NONODE.intValue()) && createParentsIfNeeded) {
              backgroundCreateParentsThenNode(client, operationAndData, operationAndData.getData().getPath(), backgrounding, createParentsAsContainers);
            }
            else 
              if ((rc == KeeperException.Code.NODEEXISTS.intValue()) && setDataIfExists) {
                backgroundSetData(client, operationAndData, operationAndData.getData().getPath(), backgrounding);
              }
              else {
                sendBackgroundResponse(rc, path, ctx, name, null, operationAndData);
              }
          }
      }, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().create(operationAndData.getData().getPath(), operationAndData.getData().getData(), acling.getAclList(operationAndData.getData().getPath()), createMode, new AsyncCallback.Create2Callback() {
          @Override public void processResult(int rc, String path, Object ctx, String name, Stat stat) {
            trace.commit();
            if (stat != null) {
              storingStat.setAversion(stat.getAversion());
              storingStat.setCtime(stat.getCtime());
              storingStat.setCversion(stat.getCversion());
              storingStat.setCzxid(stat.getCzxid());
              storingStat.setDataLength(stat.getDataLength());
              storingStat.setEphemeralOwner(stat.getEphemeralOwner());
              storingStat.setMtime(stat.getMtime());
              storingStat.setMzxid(stat.getMzxid());
              storingStat.setNumChildren(stat.getNumChildren());
              storingStat.setPzxid(stat.getPzxid());
              storingStat.setVersion(stat.getVersion());
            }
            if ((rc == KeeperException.Code.NONODE.intValue()) && createParentsIfNeeded) {
              backgroundCreateParentsThenNode(client, operationAndData, operationAndData.getData().getPath(), backgrounding, createParentsAsContainers);
            }
            else {
              sendBackgroundResponse(rc, path, ctx, name, stat, operationAndData);
            }
          }
      }, backgrounding.getContext());
    }
=======
try {
      final TimeTrace trace = client.getZookeeperClient().startTracer("CreateBuilderImpl-Background");
      client.getZooKeeper().create(operationAndData.getData().getPath(), operationAndData.getData().getData(), acling.getAclList(operationAndData.getData().getPath()), createMode, new AsyncCallback.StringCallback() {
          @Override public void processResult(int rc, String path, Object ctx, String name) {
            trace.commit();
            if ((rc == KeeperException.Code.NONODE.intValue()) && createParentsIfNeeded) {
              backgroundCreateParentsThenNode(client, operationAndData, operationAndData.getData().getPath(), backgrounding, createParentsAsContainers);
            }
            else {
              sendBackgroundResponse(rc, path, ctx, name, operationAndData);
            }
          }
      }, backgrounding.getContext());
    }
    catch (Throwable e) {
      backgrounding.checkError(e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_8ef32cc_8499680\rev_right_8499680\curator-framework\src\main\java\org\apache\curator\framework\imps\CreateBuilderImpl.java

    if (storingStat == null) {
      client.getZooKeeper().create(operationAndData.getData().getPath(), operationAndData.getData().getData(), acling.getAclList(operationAndData.getData().getPath()), createMode, new AsyncCallback.StringCallback() {
          @Override public void processResult(int rc, String path, Object ctx, String name) {
            trace.commit();
            if ((rc == KeeperException.Code.NONODE.intValue()) && createParentsIfNeeded) {
              backgroundCreateParentsThenNode(client, operationAndData, operationAndData.getData().getPath(), backgrounding, createParentsAsContainers);
            }
            else 
              if ((rc == KeeperException.Code.NODEEXISTS.intValue()) && setDataIfExists) {
                backgroundSetData(client, operationAndData, operationAndData.getData().getPath(), backgrounding);
              }
              else {
                sendBackgroundResponse(rc, path, ctx, name, null, operationAndData);
              }
          }
      }, backgrounding.getContext());
    }
    else {
      client.getZooKeeper().create(operationAndData.getData().getPath(), operationAndData.getData().getData(), acling.getAclList(operationAndData.getData().getPath()), createMode, new AsyncCallback.Create2Callback() {
          @Override public void processResult(int rc, String path, Object ctx, String name, Stat stat) {
            trace.commit();
            if (stat != null) {
              storingStat.setAversion(stat.getAversion());
              storingStat.setCtime(stat.getCtime());
              storingStat.setCversion(stat.getCversion());
              storingStat.setCzxid(stat.getCzxid());
              storingStat.setDataLength(stat.getDataLength());
              storingStat.setEphemeralOwner(stat.getEphemeralOwner());
              storingStat.setMtime(stat.getMtime());
              storingStat.setMzxid(stat.getMzxid());
              storingStat.setNumChildren(stat.getNumChildren());
              storingStat.setPzxid(stat.getPzxid());
              storingStat.setVersion(stat.getVersion());
            }
            if ((rc == KeeperException.Code.NONODE.intValue()) && createParentsIfNeeded) {
              backgroundCreateParentsThenNode(client, operationAndData, operationAndData.getData().getPath(), backgrounding, createParentsAsContainers);
            }
            else {
              sendBackgroundResponse(rc, path, ctx, name, stat, operationAndData);
            }
          }
      }, backgrounding.getContext());
    }
  }


