<<<<<<< MINE
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
>>>>>>> YOURS
