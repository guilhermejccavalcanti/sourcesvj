  public static void setTaskPlan(String alias_id, Operator<? extends OperatorDesc> topOp, Task<?> task, boolean local, GenMRProcContext opProcCtx, PrunedPartitionList pList) throws SemanticException {
    setMapWork(((MapredWork)task.getWork()).getMapWork(), opProcCtx.getParseCtx(), opProcCtx.getInputs(), pList, topOp, alias_id, opProcCtx.getConf(), local);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_35c0a67_9461ead\rev_left_35c0a67\ql\src\java\org\apache\hadoop\hive\ql\optimizer\GenMapRedUtils.java
opProcCtx.addSeenOp(task, topOp);
=======
if (!local) {
      while (iterPath.hasNext()){
        assert iterPartnDesc.hasNext();
        String path = iterPath.next().toString();
        PartitionDesc prtDesc = iterPartnDesc.next();
        if (plan.getPathToAliases().get(path) == null) {
          plan.getPathToAliases().put(path, new ArrayList<String>());
        }
        plan.getPathToAliases().get(path).add(alias_id);
        plan.getPathToPartitionInfo().put(path, prtDesc);
        if (LOG.isDebugEnabled()) {
          LOG.debug("Information added for path " + path);
        }
      }
      assert plan.getAliasToWork().get(alias_id) == null;
      plan.getAliasToWork().put(alias_id, topOp);
    }
    else {
      MapredLocalWork localPlan = plan.getMapLocalWork();
      if (localPlan == null) {
        localPlan = new MapredLocalWork(new LinkedHashMap<String, Operator<? extends OperatorDesc>>(), new LinkedHashMap<String, FetchWork>());
      }
      assert localPlan.getAliasToWork().get(alias_id) == null;
      assert localPlan.getAliasToFetchWork().get(alias_id) == null;
      localPlan.getAliasToWork().put(alias_id, topOp);
      if (tblDir == null) {
        tblDesc = Utilities.getTableDesc(partsList.getSourceTable());
        localPlan.getAliasToFetchWork().put(alias_id, new FetchWork(partDir, partDesc, tblDesc));
      }
      else {
        localPlan.getAliasToFetchWork().put(alias_id, new FetchWork(tblDir, tblDesc));
      }
      plan.setMapLocalWork(localPlan);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_35c0a67_9461ead\rev_right_9461ead\ql\src\java\org\apache\hadoop\hive\ql\optimizer\GenMapRedUtils.java

  }


