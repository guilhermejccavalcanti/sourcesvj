  @Override public TitanVertex getVertex(long vertexid) {
    verifyOpen();
    if (null != config.getGroupName()) {
      MetricManager.INSTANCE.getCounter(config.getGroupName(), "db", "getVertexByID").inc();
    }
    if (!isValidVertexId(vertexid)) 
      return null;
    if (idInspector.isPartitionedVertex(vertexid)) 
      vertexid = idManager.getCanonicalVertexId(vertexid);
    InternalVertex v = null;
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_8889a15_80ac3f3\rev_left_8889a15\titan-core\src\main\java\com\thinkaurelius\titan\graphdb\transaction\StandardTitanTx.java
v = vertexCache.get(vertexid, externalVertexRetriever);
=======
try {
      v = vertexCache.get(vertexid, externalVertexRetriever);
    }
    catch (InvalidIDException e) {
      log.debug("Illegal vertex ID", e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_8889a15_80ac3f3\rev_right_80ac3f3\titan-core\src\main\java\com\thinkaurelius\titan\graphdb\transaction\StandardTitanTx.java

    return (null == v || v.isRemoved()) ? null : v;
  }


