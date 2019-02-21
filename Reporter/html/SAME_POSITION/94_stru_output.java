package org.apache.hadoop.hive.ql.exec.tez;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import java.util.Set;
import org.apache.commons.logging.LogFactory;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.Callable;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.llap.io.api.LlapIoProxy;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.exec.DummyStoreOperator;
import org.apache.hadoop.hive.ql.exec.HashTableDummyOperator;
import org.apache.hadoop.hive.ql.exec.MapOperator;
import org.apache.hadoop.hive.ql.exec.MapredContext;
import org.apache.hadoop.hive.ql.exec.ObjectCache;
import org.apache.hadoop.hive.ql.exec.ObjectCacheFactory;
import org.apache.hadoop.hive.ql.exec.Operator;
import org.apache.hadoop.hive.ql.exec.OperatorUtils;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.exec.mr.ExecMapper.ReportStats;
import org.apache.hadoop.hive.ql.exec.mr.ExecMapperContext;
import org.apache.hadoop.hive.ql.exec.tez.TezProcessor.TezKVOutputCollector;
import org.apache.hadoop.hive.ql.exec.tez.tools.KeyValueInputMerger;
import org.apache.hadoop.hive.ql.exec.vector.VectorMapOperator;
import org.apache.hadoop.hive.ql.log.PerfLogger;
import org.apache.hadoop.hive.ql.plan.MapWork;
import org.apache.hadoop.hive.ql.plan.OperatorDesc;
import org.apache.hadoop.hive.serde2.Deserializer;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.mapred.JobConf;
import org.apache.tez.mapreduce.input.MRInputLegacy;
import org.apache.tez.mapreduce.input.MultiMRInput;
import org.apache.tez.mapreduce.processor.MRTaskReporter;
import org.apache.tez.runtime.api.Input;
import org.apache.tez.runtime.api.LogicalInput;
import org.apache.tez.runtime.api.LogicalOutput;
import org.apache.tez.runtime.api.ProcessorContext;
import org.apache.tez.runtime.library.api.KeyValueReader;

public class MapRecordProcessor extends RecordProcessor {
  private MapOperator mapOp;
  private final List<MapOperator> mergeMapOpList = new ArrayList<MapOperator>();
  public static final Log l4j = LogFactory.getLog(MapRecordProcessor.class);
  private MapRecordSource[] sources;
  private final Map<String, MultiMRInput> multiMRInputMap = new HashMap<String, MultiMRInput>();
  private int position = 0;
  MRInputLegacy legacyMRInput = null;
  MultiMRInput mainWorkMultiMRInput = null;
  private ExecMapperContext execContext = null;
  private boolean abort = false;
  protected static final String MAP_PLAN_KEY = "__MAP_PLAN__";
  private MapWork mapWork;
  List<MapWork> mergeWorkList = null;
  List<String> cacheKeys;
  ObjectCache cache;
  public MapRecordProcessor(final JobConf jconf) throws Exception {
    if (LlapIoProxy.isDaemon()) {
      cache = new org.apache.hadoop.hive.ql.exec.mr.ObjectCache();
    }
    else {
      cache = ObjectCacheFactory.getCache(jconf);
    }
    execContext = new ExecMapperContext(jconf);
    execContext.setJc(jconf);
    cacheKeys = new ArrayList<String>();
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_left_07253e8\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
cacheKeys = new ArrayList<String>();
=======
mapWork = (MapWork)cache.retrieve(key, new Callable<Object>() {
        @Override public Object call() {
          return Utilities.getMapWork(jconf);
        }
    });
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_right_5f7ea30\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_left_07253e8\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java
cacheKeys = new ArrayList<String>();
=======
if (prefixes != null) {
      mergeWorkList = new ArrayList<MapWork>();
      for (final String prefix : prefixes.split(",")) {
        if (prefix == null || prefix.isEmpty()) {
          continue ;
        }
        key = queryId + prefix;
        cacheKeys.add(key);
        mergeWorkList.add((MapWork)cache.retrieve(key, new Callable<Object>() {
            @Override public Object call() {
              return Utilities.getMergeWork(jconf, prefix);
            }
        }));
      }
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_07253e8_5f7ea30\rev_right_5f7ea30\ql\src\java\org\apache\hadoop\hive\ql\exec\tez\MapRecordProcessor.java

  }
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
            mergeMapOp.initialize(jconf, null);
            mergeMapOp.setChildren(jconf);
            DummyStoreOperator dummyOp = getJoinParentOp(mergeMapOp);
            mapOp.setConnectedOperators(mergeMapWork.getTag(), dummyOp);
            mergeMapOp.passExecContext(new ExecMapperContext(jconf));
            mergeMapOp.initializeLocalWork(jconf);
          }
        }
      }
      mapOp.setConf(mapWork);
      l4j.info("Main input name is " + mapWork.getName());
      jconf.set(Utilities.INPUT_NAME, mapWork.getName());
      mapOp.initialize(jconf, null);
      mapOp.setChildren(jconf);
      mapOp.passExecContext(execContext);
      l4j.info(mapOp.dump(0));
      mapOp.initializeLocalWork(jconf);
      initializeMapRecordSources();
      mapOp.initializeMapOperator(jconf);
      if ((mergeMapOpList != null) && mergeMapOpList.isEmpty() == false) {
        for (MapOperator mergeMapOp : mergeMapOpList) {
          jconf.set(Utilities.INPUT_NAME, mergeMapOp.getConf().getName());
          mergeMapOp.initializeMapOperator(jconf);
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
      mapOp.setReporter(reporter);
      MapredContext.get().setReporter(reporter);
    }
    catch (Throwable e) {
      abort = true;
      if (e instanceof OutOfMemoryError) {
        throw (OutOfMemoryError)e;
      }
      else {
        throw new RuntimeException("Map operator initialization failed", e);
      }
    }
    perfLogger.PerfLogEnd(CLASS_NAME, PerfLogger.TEZ_INIT_OPERATORS);
  }
  private void initializeMapRecordSources() throws Exception {
    int size = mergeMapOpList.size() + 1;
    sources = new MapRecordSource[size];
    position = mapOp.getConf().getTag();
    sources[position] = new MapRecordSource();
    KeyValueReader reader = null;
    if (mainWorkMultiMRInput != null) {
      reader = getKeyValueReader(mainWorkMultiMRInput.getKeyValueReaders(), mapOp);
    }
    else {
      reader = legacyMRInput.getReader();
    }
    sources[position].init(jconf, mapOp, reader);
    for (MapOperator mapOp : mergeMapOpList) {
      int tag = mapOp.getConf().getTag();
      sources[tag] = new MapRecordSource();
      String inputName = mapOp.getConf().getName();
      MultiMRInput multiMRInput = multiMRInputMap.get(inputName);
      Collection<KeyValueReader> kvReaders = multiMRInput.getKeyValueReaders();
      l4j.debug("There are " + kvReaders.size() + " key-value readers for input " + inputName);
      reader = getKeyValueReader(kvReaders, mapOp);
      sources[tag].init(jconf, mapOp, reader);
    }
    ((TezContext)MapredContext.get()).setRecordSources(sources);
  }
  @SuppressWarnings(value = {"deprecation", }) private KeyValueReader getKeyValueReader(Collection<KeyValueReader> keyValueReaders, MapOperator mapOp) throws Exception {
    List<KeyValueReader> kvReaderList = new ArrayList<KeyValueReader>(keyValueReaders);
    mapOp.initializeContexts();
    Deserializer deserializer = mapOp.getCurrentDeserializer();
    KeyValueReader reader = new KeyValueInputMerger(kvReaderList, deserializer, new ObjectInspector[]{ deserializer.getObjectInspector() } , mapOp.getConf().getSortCols());
    return reader;
  }
  private DummyStoreOperator getJoinParentOp(Operator<? extends OperatorDesc> mergeMapOp) {
    for (Operator<? extends OperatorDesc> childOp : mergeMapOp.getChildOperators()) {
      if ((childOp.getChildOperators() == null) || (childOp.getChildOperators().isEmpty())) {
        return (DummyStoreOperator)childOp;
      }
      else {
        return getJoinParentOp(childOp);
      }
    }
    return null;
  }
  @Override void run() throws Exception {
    while (sources[position].pushRecord()){
    }
  }
  @Override void close() {
    if (!abort) {
      abort = execContext.getIoCxt().getIOExceptions();
    }
    if (cache != null && cacheKeys != null) {
      for (String k : cacheKeys) {
        cache.release(k);
      }
    }
    try {
      if (mapOp == null || mapWork == null) {
        return ;
      }
      mapOp.close(abort);
      if (mergeMapOpList.isEmpty() == false) {
        for (MapOperator mergeMapOp : mergeMapOpList) {
          mergeMapOp.close(abort);
        }
      }
      List<HashTableDummyOperator> dummyOps = mapWork.getDummyOps();
      if (dummyOps != null) {
        for (Operator<? extends OperatorDesc> dummyOp : dummyOps) {
          dummyOp.close(abort);
        }
      }
      ReportStats rps = new ReportStats(reporter, jconf);
      mapOp.preorderMap(rps);
      return ;
    }
    catch (Exception e) {
      if (!abort) {
        l4j.error("Hit error while closing operators - failing tree");
        throw new RuntimeException("Hive Runtime Error while closing operators", e);
      }
    }
    finally {
      Utilities.clearWorkMap();
      MapredContext.close();
    }
  }
  private MRInputLegacy getMRInput(Map<String, LogicalInput> inputs) throws Exception {
    MRInputLegacy theMRInput = null;
    Set<Input> li = new HashSet<Input>();
    for (LogicalInput inp : inputs.values()) {
      if (inp instanceof MRInputLegacy || inp instanceof MultiMRInput) {
        inp.start();
        li.add(inp);
      }
    }
    processorContext.waitForAllInputsReady(li);
    l4j.info("The input names are: " + Arrays.toString(inputs.keySet().toArray()));
    for (Entry<String, LogicalInput> inp : inputs.entrySet()) {
      if (inp.getValue() instanceof MRInputLegacy) {
        if (theMRInput != null) {
          throw new IllegalArgumentException("Only one MRInput is expected");
        }
        theMRInput = (MRInputLegacy)inp.getValue();
      }
      else 
        if (inp.getValue() instanceof MultiMRInput) {
          multiMRInputMap.put(inp.getKey(), (MultiMRInput)inp.getValue());
        }
    }
    if (theMRInput != null) {
      theMRInput.init();
    }
    else {
      String alias = mapWork.getAliasToWork().keySet().iterator().next();
      if (inputs.get(alias) instanceof MultiMRInput) {
        mainWorkMultiMRInput = (MultiMRInput)inputs.get(alias);
      }
      else {
        throw new IOException("Unexpected input type found: " + inputs.get(alias).getClass().getCanonicalName());
      }
    }
    return theMRInput;
  }
}

