  @Override void init(final JobConf jconf, ProcessorContext processorContext, MRTaskReporter mrReporter, Map<String, LogicalInput> inputs, Map<String, LogicalOutput> outputs) throws Exception {
    perfLogger.PerfLogBegin(CLASS_NAME, PerfLogger.TEZ_INIT_OPERATORS);
    super.init(jconf, processorContext, mrReporter, inputs, outputs);
    String queryId = HiveConf.getVar(jconf, HiveConf.ConfVars.HIVEQUERYID);
    String key = queryId + processorContext.getTaskVertexName() + MAP_PLAN_KEY;
    
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_left_07253e8\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
cacheKeys.add(key)
=======
MapredContext.init(true, new JobConf(jconf))
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_right_5f7ea30\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
;
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_left_07253e8\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
mapWork = (MapWork)cache.retrieve(key, new Callable<Object>() {
        public Object call() {
          return Utilities.getMapWork(jconf);
        }
    });
=======
((TezContext)MapredContext.get()).setInputs(inputs);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_right_5f7ea30\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java

    
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_left_07253e8\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
Utilities.setMapWork(jconf, mapWork)
=======
((TezContext)MapredContext.get()).setTezProcessorContext(processorContext)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_right_5f7ea30\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
;
    String prefixes = jconf.get(DagUtils.TEZ_MERGE_WORK_FILE_PREFIXES);
    if (prefixes != null) {
      mergeWorkList = new ArrayList<MapWork>();
      for (final String prefix : prefixes.split(",")) {
        if (prefix == null || prefix.isEmpty()) {
          continue ;
        }
        key = queryId + processorContext.getTaskVertexName() + prefix;
        cacheKeys.add(key);
        mergeWorkList.add((MapWork)cache.retrieve(key, new Callable<Object>() {
            public Object call() {
              return Utilities.getMergeWork(jconf, prefix);
            }
        }));
      }
    }
    legacyMRInput = getMRInput(inputs);
    if (legacyMRInput != null) {
      Configuration updatedConf = legacyMRInput.getConfigUpdates();
      if (updatedConf != null) {
        for (Entry<String, String> entry : updatedConf) {
          jconf.set(entry.getKey(), entry.getValue());
        }
      }
    }
    createOutputMap();
    for (Entry<String, LogicalOutput> outputEntry : outputs.entrySet()) {
      l4j.debug("Starting Output: " + outputEntry.getKey());
      outputEntry.getValue().start();
      ((TezKVOutputCollector)outMap.get(outputEntry.getKey())).initialize();
    }
    try {
      if (mapWork.getVectorMode()) {
        mapOp = new VectorMapOperator();
      }
      else {
        mapOp = new MapOperator();
      }
      mapOp.
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_left_07253e8\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
clearConnectedOperators()
=======
setExecContext(execContext)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_right_5f7ea30\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
;
      if (mergeWorkList != null) {
        MapOperator mergeMapOp = null;
        for (MapWork mergeMapWork : mergeWorkList) {
          if (mergeMapWork.getVectorMode()) {
            mergeMapOp = new VectorMapOperator();
          }
          else {
            mergeMapOp = new MapOperator();
          }
          mergeMapOpList.add(mergeMapOp);
          if (mergeMapOp != null) {
            mergeMapOp.setConf(mergeMapWork);
            l4j.info("Input name is " + mergeMapWork.getName());
            jconf.set(Utilities.INPUT_NAME, mergeMapWork.getName());
            mergeMapOp.setChildren(jconf);
            DummyStoreOperator dummyOp = getJoinParentOp(mergeMapOp);
            mapOp.setConnectedOperators(mergeMapWork.getTag(), dummyOp);
            mergeMapOp.setExecContext(new ExecMapperContext(jconf));
            mergeMapOp.initializeLocalWork(jconf);
          }
        }
      }
      mapOp.setConf(mapWork);
      l4j.info("Main input name is " + mapWork.getName());
      jconf.set(Utilities.INPUT_NAME, mapWork.getName());
      mapOp.setChildren(jconf);
      l4j.info(mapOp.dump(0));
      MapredContext.init(true, new JobConf(jconf));
      ((TezContext)MapredContext.get()).setInputs(inputs);
      ((TezContext)MapredContext.get()).setTezProcessorContext(processorContext);
      mapOp.setExecContext(execContext);
      mapOp.initializeLocalWork(jconf);
      initializeMapRecordSources();
      mapOp.initialize(jconf, null);
      if ((mergeMapOpList != null) && mergeMapOpList.isEmpty() == false) {
        for (MapOperator mergeMapOp : mergeMapOpList) {
          jconf.set(Utilities.INPUT_NAME, mergeMapOp.getConf().getName());
          mergeMapOp.initialize(jconf, null);
        }
      }
      List<HashTableDummyOperator> dummyOps = mapWork.getDummyOps();
      jconf.set(Utilities.INPUT_NAME, mapWork.getName());
      if (dummyOps != null) {
        for (Operator<? extends OperatorDesc> dummyOp : dummyOps) {
          dummyOp.setExecContext(execContext);
          dummyOp.initialize(jconf, null);
        }
      }
      OperatorUtils.setChildrenCollector(mapOp.getChildOperators(), outMap);
      mapOp.setReporter(reporter);
      MapredContext.get().setReporter(reporter);
    }
    perfLogger.PerfLogEnd(CLASS_NAME, PerfLogger.TEZ_INIT_OPERATORS);
  }


