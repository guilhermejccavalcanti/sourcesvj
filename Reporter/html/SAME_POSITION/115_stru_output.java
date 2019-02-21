package org.apache.hadoop.hive.ql.optimizer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.conf.HiveConf.ConfVars;
import org.apache.hadoop.hive.ql.Context;
import org.apache.hadoop.hive.ql.exec.ColumnInfo;
import org.apache.hadoop.hive.ql.exec.ConditionalTask;
import org.apache.hadoop.hive.ql.exec.DemuxOperator;
import org.apache.hadoop.hive.ql.exec.DependencyCollectionTask;
import org.apache.hadoop.hive.ql.exec.FileSinkOperator;
import org.apache.hadoop.hive.ql.exec.JoinOperator;
import org.apache.hadoop.hive.ql.exec.MoveTask;
import org.apache.hadoop.hive.ql.exec.MapJoinOperator;
import org.apache.hadoop.hive.ql.exec.Operator;
import org.apache.hadoop.hive.ql.exec.OperatorFactory;
import org.apache.hadoop.hive.ql.exec.ReduceSinkOperator;
import org.apache.hadoop.hive.ql.exec.RowSchema;
import org.apache.hadoop.hive.ql.exec.SMBMapJoinOperator;
import org.apache.hadoop.hive.ql.exec.TableScanOperator;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.exec.TaskFactory;
import org.apache.hadoop.hive.ql.exec.UnionOperator;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.exec.mr.ExecDriver;
import org.apache.hadoop.hive.ql.exec.mr.MapRedTask;
import org.apache.hadoop.hive.ql.hooks.ReadEntity;
import org.apache.hadoop.hive.ql.io.RCFileInputFormat;
import org.apache.hadoop.hive.ql.io.rcfile.merge.MergeWork;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.metadata.Partition;
import org.apache.hadoop.hive.ql.optimizer.GenMRProcContext.GenMRUnionCtx;
import org.apache.hadoop.hive.ql.optimizer.GenMRProcContext.GenMapRedCtx;
import org.apache.hadoop.hive.ql.optimizer.listbucketingpruner.ListBucketingPruner;
import org.apache.hadoop.hive.ql.optimizer.ppr.PartitionPruner;
import org.apache.hadoop.hive.ql.parse.OpParseContext;
import org.apache.hadoop.hive.ql.parse.ParseContext;
import org.apache.hadoop.hive.ql.parse.PrunedPartitionList;
import org.apache.hadoop.hive.ql.parse.QBJoinTree;
import org.apache.hadoop.hive.ql.parse.RowResolver;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.plan.BaseWork;
import org.apache.hadoop.hive.ql.plan.ConditionalResolverMergeFiles;
import org.apache.hadoop.hive.ql.plan.ConditionalResolverMergeFiles.ConditionalResolverMergeFilesCtx;
import org.apache.hadoop.hive.ql.plan.ConditionalWork;
import org.apache.hadoop.hive.ql.plan.DynamicPartitionCtx;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.FetchWork;
import org.apache.hadoop.hive.ql.plan.FileSinkDesc;
import org.apache.hadoop.hive.ql.plan.FilterDesc.sampleDesc;
import org.apache.hadoop.hive.ql.plan.LoadFileDesc;
import org.apache.hadoop.hive.ql.plan.MapWork;
import org.apache.hadoop.hive.ql.plan.MapredLocalWork;
import org.apache.hadoop.hive.ql.plan.MapredWork;
import org.apache.hadoop.hive.ql.plan.MoveWork;
import org.apache.hadoop.hive.ql.plan.OperatorDesc;
import org.apache.hadoop.hive.ql.plan.PartitionDesc;
import org.apache.hadoop.hive.ql.plan.PlanUtils;
import org.apache.hadoop.hive.ql.plan.ReduceSinkDesc;
import org.apache.hadoop.hive.ql.plan.ReduceWork;
import org.apache.hadoop.hive.ql.plan.StatsWork;
import org.apache.hadoop.hive.ql.plan.TableDesc;
import org.apache.hadoop.hive.ql.plan.TableScanDesc;
import org.apache.hadoop.hive.ql.plan.TezWork;
import org.apache.hadoop.hive.ql.stats.StatsFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.mapred.InputFormat;

public final class GenMapRedUtils {
  private static Log LOG;
  static {
    LOG = LogFactory.getLog("org.apache.hadoop.hive.ql.optimizer.GenMapRedUtils");
  }
  public static boolean needsTagging(ReduceWork rWork) {
    return rWork != null && (rWork.getReducer().getClass() == JoinOperator.class || rWork.getReducer().getClass() == DemuxOperator.class);
  }
  public static void initPlan(ReduceSinkOperator op, GenMRProcContext opProcCtx) throws SemanticException {
    Operator<? extends OperatorDesc> reducer = op.getChildOperators().get(0);
    Map<Operator<? extends OperatorDesc>, GenMapRedCtx> mapCurrCtx = opProcCtx.getMapCurrCtx();
    GenMapRedCtx mapredCtx = mapCurrCtx.get(op.getParentOperators().get(0));
    Task<? extends Serializable> currTask = mapredCtx.getCurrTask();
    MapredWork plan = (MapredWork)currTask.getWork();
    HashMap<Operator<? extends OperatorDesc>, Task<? extends Serializable>> opTaskMap = opProcCtx.getOpTaskMap();
    Operator<? extends OperatorDesc> currTopOp = opProcCtx.getCurrTopOp();
    opTaskMap.put(reducer, currTask);
    plan.setReduceWork(new ReduceWork());
    plan.getReduceWork().setReducer(reducer);
    ReduceSinkDesc desc = op.getConf();
    plan.getReduceWork().setNumReduceTasks(desc.getNumReducers());
    if (needsTagging(plan.getReduceWork())) {
      plan.getReduceWork().setNeedsTagging(true);
    }
    assert currTopOp != null;
    String currAliasId = opProcCtx.getCurrAliasId();
    if (!opProcCtx.isSeenOp(currTask, currTopOp)) {
      setTaskPlan(currAliasId, currTopOp, currTask, false, opProcCtx);
    }
    currTopOp = null;
    currAliasId = null;
    opProcCtx.setCurrTask(currTask);
    opProcCtx.setCurrTopOp(currTopOp);
    opProcCtx.setCurrAliasId(currAliasId);
  }
  public static void initUnionPlan(ReduceSinkOperator op, UnionOperator currUnionOp, GenMRProcContext opProcCtx, Task<? extends Serializable> unionTask) throws SemanticException {
    Operator<? extends OperatorDesc> reducer = op.getChildOperators().get(0);
    MapredWork plan = (MapredWork)unionTask.getWork();
    HashMap<Operator<? extends OperatorDesc>, Task<? extends Serializable>> opTaskMap = opProcCtx.getOpTaskMap();
    opTaskMap.put(reducer, unionTask);
    plan.setReduceWork(new ReduceWork());
    plan.getReduceWork().setReducer(reducer);
    plan.getReduceWork().setReducer(reducer);
    ReduceSinkDesc desc = op.getConf();
    plan.getReduceWork().setNumReduceTasks(desc.getNumReducers());
    if (needsTagging(plan.getReduceWork())) {
      plan.getReduceWork().setNeedsTagging(true);
    }
    initUnionPlan(opProcCtx, currUnionOp, unionTask, false);
  }
  private static void setUnionPlan(GenMRProcContext opProcCtx, boolean local, Task<? extends Serializable> currTask, GenMRUnionCtx uCtx, boolean mergeTask) throws SemanticException {
    Operator<? extends OperatorDesc> currTopOp = opProcCtx.getCurrTopOp();
    if (currTopOp != null) {
      String currAliasId = opProcCtx.getCurrAliasId();
      if (mergeTask || !opProcCtx.isSeenOp(currTask, currTopOp)) {
        setTaskPlan(currAliasId, currTopOp, currTask, local, opProcCtx);
      }
      currTopOp = null;
      opProcCtx.setCurrTopOp(currTopOp);
    }
    else {
      List<String> taskTmpDirLst = uCtx.getTaskTmpDir();
      if ((taskTmpDirLst != null) && !(taskTmpDirLst.isEmpty())) {
        List<TableDesc> tt_descLst = uCtx.getTTDesc();
        assert !taskTmpDirLst.isEmpty() && !tt_descLst.isEmpty();
        assert taskTmpDirLst.size() == tt_descLst.size();
        int size = taskTmpDirLst.size();
        assert local == false;
        List<Operator<? extends OperatorDesc>> topOperators = uCtx.getListTopOperators();
        MapredWork plan = (MapredWork)currTask.getWork();
        for (int pos = 0; pos < size; pos++) {
          String taskTmpDir = taskTmpDirLst.get(pos);
          TableDesc tt_desc = tt_descLst.get(pos);
          MapWork mWork = plan.getMapWork();
          if (mWork.getPathToAliases().get(taskTmpDir) == null) {
            mWork.getPathToAliases().put(taskTmpDir, new ArrayList<String>());
            mWork.getPathToAliases().get(taskTmpDir).add(taskTmpDir);
            mWork.getPathToPartitionInfo().put(taskTmpDir, new PartitionDesc(tt_desc, null));
            mWork.getAliasToWork().put(taskTmpDir, topOperators.get(pos));
          }
        }
      }
    }
  }
  public static void initUnionPlan(GenMRProcContext opProcCtx, UnionOperator currUnionOp, Task<? extends Serializable> currTask, boolean local) throws SemanticException {
    if (currUnionOp != null) {
      GenMRUnionCtx uCtx = opProcCtx.getUnionTask(currUnionOp);
      assert uCtx != null;
      setUnionPlan(opProcCtx, local, currTask, uCtx, false);
    }
  }
  public static void joinUnionPlan(GenMRProcContext opProcCtx, UnionOperator currUnionOp, Task<? extends Serializable> currentUnionTask, Task<? extends Serializable> existingTask, boolean local) throws SemanticException {
    assert currUnionOp != null;
    GenMRUnionCtx uCtx = opProcCtx.getUnionTask(currUnionOp);
    assert uCtx != null;
    setUnionPlan(opProcCtx, local, existingTask, uCtx, true);
    List<Task<? extends Serializable>> parTasks = null;
    if (opProcCtx.getRootTasks().contains(currentUnionTask)) {
      opProcCtx.getRootTasks().remove(currentUnionTask);
      if (!opProcCtx.getRootTasks().contains(existingTask) && (existingTask.getParentTasks() == null || existingTask.getParentTasks().isEmpty())) {
        opProcCtx.getRootTasks().add(existingTask);
      }
    }
    if ((currentUnionTask != null) && (currentUnionTask.getParentTasks() != null) && !currentUnionTask.getParentTasks().isEmpty()) {
      parTasks = new ArrayList<Task<? extends Serializable>>();
      parTasks.addAll(currentUnionTask.getParentTasks());
      Object[] parTaskArr = parTasks.toArray();
      for (Object parTask : parTaskArr) {
        ((Task<? extends Serializable>)parTask).removeDependentTask(currentUnionTask);
      }
    }
    if ((currentUnionTask != null) && (parTasks != null)) {
      for (Task<? extends Serializable> parTask : parTasks) {
        parTask.addDependentTask(existingTask);
        if (opProcCtx.getRootTasks().contains(existingTask)) {
          opProcCtx.getRootTasks().remove(existingTask);
        }
      }
    }
    opProcCtx.setCurrTask(existingTask);
  }
  public static void joinPlan(Task<? extends Serializable> currTask, Task<? extends Serializable> oldTask, GenMRProcContext opProcCtx) throws SemanticException {
    assert currTask != null && oldTask != null;
    Operator<? extends OperatorDesc> currTopOp = opProcCtx.getCurrTopOp();
    List<Task<? extends Serializable>> parTasks = null;
    if (currTask.getParentTasks() != null && !currTask.getParentTasks().isEmpty()) {
      parTasks = new ArrayList<Task<? extends Serializable>>();
      parTasks.addAll(currTask.getParentTasks());
      Object[] parTaskArr = parTasks.toArray();
      for (Object element : parTaskArr) {
        ((Task<? extends Serializable>)element).removeDependentTask(currTask);
      }
    }
    if (currTopOp != null) {
      mergeInput(currTopOp, opProcCtx, oldTask, false);
    }
    if (parTasks != null) {
      for (Task<? extends Serializable> parTask : parTasks) {
        parTask.addDependentTask(oldTask);
      }
    }
    if (oldTask instanceof MapRedTask && currTask instanceof MapRedTask) {
      ((MapRedTask)currTask).getWork().getMapWork().mergingInto(((MapRedTask)oldTask).getWork().getMapWork());
    }
    opProcCtx.setCurrTopOp(null);
    opProcCtx.setCurrTask(oldTask);
  }
  static boolean mergeInput(Operator<? extends OperatorDesc> currTopOp, GenMRProcContext opProcCtx, Task<? extends Serializable> task, boolean local) throws SemanticException {
    if (!opProcCtx.isSeenOp(task, currTopOp)) {
      String currAliasId = opProcCtx.getCurrAliasId();
      setTaskPlan(currAliasId, currTopOp, task, local, opProcCtx);
      return true;
    }
    return false;
  }
  static void splitPlan(ReduceSinkOperator cRS, Task<? extends Serializable> parentTask, Task<? extends Serializable> childTask, GenMRProcContext opProcCtx) throws SemanticException {
    assert parentTask != null && childTask != null;
    splitTasks(cRS, parentTask, childTask, opProcCtx);
  }
  static void splitPlan(ReduceSinkOperator cRS, GenMRProcContext opProcCtx) throws SemanticException {
    ParseContext parseCtx = opProcCtx.getParseCtx();
    Task<? extends Serializable> parentTask = opProcCtx.getCurrTask();
    MapredWork childPlan = getMapRedWork(parseCtx);
    Task<? extends Serializable> childTask = TaskFactory.get(childPlan, parseCtx.getConf());
    Operator<? extends OperatorDesc> reducer = cRS.getChildOperators().get(0);
    ReduceWork rWork = new ReduceWork();
    childPlan.setReduceWork(rWork);
    rWork.setReducer(reducer);
    ReduceSinkDesc desc = cRS.getConf();
    childPlan.getReduceWork().setNumReduceTasks(new Integer(desc.getNumReducers()));
    opProcCtx.getOpTaskMap().put(reducer, childTask);
    splitTasks(cRS, parentTask, childTask, opProcCtx);
  }
  public static void setTaskPlan(String alias_id, Operator<? extends OperatorDesc> topOp, Task<?> task, boolean local, GenMRProcContext opProcCtx) throws SemanticException {
    setTaskPlan(alias_id, topOp, task, local, opProcCtx, null);
  }
  private static ReadEntity getParentViewInfo(String alias_id, Map<String, ReadEntity> viewAliasToInput) {
    String[] aliases = alias_id.split(":");
    String currentAlias = null;
    ReadEntity currentInput = null;
    for (int pos = 0; pos < aliases.length; pos++) {
      currentAlias = currentAlias == null ? aliases[pos] : currentAlias + ":" + aliases[pos];
      ReadEntity input = viewAliasToInput.get(currentAlias);
      if (input == null) {
        return currentInput;
      }
      currentInput = input;
    }
    return currentInput;
  }
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
  public static void setMapWork(MapWork plan, ParseContext parseCtx, Set<ReadEntity> inputs, PrunedPartitionList partsList, Operator<? extends OperatorDesc> topOp, String alias_id, HiveConf conf, boolean local) throws SemanticException {
    ArrayList<Path> partDir = new ArrayList<Path>();
    ArrayList<PartitionDesc> partDesc = new ArrayList<PartitionDesc>();
    Path tblDir = null;
    TableDesc tblDesc = null;
    plan.setNameToSplitSample(parseCtx.getNameToSplitSample());
    if (partsList == null) {
      try {
        TableScanOperator tsOp = (TableScanOperator)topOp;
        partsList = PartitionPruner.prune(tsOp, parseCtx, alias_id);
      }
      catch (SemanticException e) {
        throw e;
      }
      catch (HiveException e) {
        LOG.error(org.apache.hadoop.util.StringUtils.stringifyException(e));
        throw new SemanticException(e.getMessage(), e);
      }
    }
    Set<Partition> parts = partsList.getPartitions();
    PartitionDesc aliasPartnDesc = null;
    try {
      if (!parts.isEmpty()) {
        aliasPartnDesc = Utilities.getPartitionDesc(parts.iterator().next());
      }
    }
    catch (HiveException e) {
      LOG.error(org.apache.hadoop.util.StringUtils.stringifyException(e));
      throw new SemanticException(e.getMessage(), e);
    }
    if (aliasPartnDesc == null) {
      aliasPartnDesc = new PartitionDesc(Utilities.getTableDesc(parseCtx.getTopToTable().get(topOp)), null);
    }
    Map<String, String> props = parseCtx.getTopToProps().get(topOp);
    if (props != null) {
      Properties target = aliasPartnDesc.getProperties();
      if (target == null) {
        aliasPartnDesc.setProperties(target = new Properties());
      }
      target.putAll(props);
    }
    plan.getAliasToPartnInfo().put(alias_id, aliasPartnDesc);
    long sizeNeeded = Integer.MAX_VALUE;
    int fileLimit = -1;
    if (parseCtx.getGlobalLimitCtx().isEnable()) {
      long sizePerRow = HiveConf.getLongVar(parseCtx.getConf(), HiveConf.ConfVars.HIVELIMITMAXROWSIZE);
      sizeNeeded = parseCtx.getGlobalLimitCtx().getGlobalLimit() * sizePerRow;
      fileLimit = HiveConf.getIntVar(parseCtx.getConf(), HiveConf.ConfVars.HIVELIMITOPTLIMITFILE);
      if (sizePerRow <= 0 || fileLimit <= 0) {
        LOG.info("Skip optimization to reduce input size of \'limit\'");
        parseCtx.getGlobalLimitCtx().disableOpt();
      }
      else 
        if (parts.isEmpty()) {
          LOG.info("Empty input: skip limit optimiztion");
        }
        else {
          LOG.info("Try to reduce input size for \'limit\' " + "sizeNeeded: " + sizeNeeded + "  file limit : " + fileLimit);
        }
    }
    boolean isFirstPart = true;
    boolean emptyInput = true;
    boolean singlePartition = (parts.size() == 1);
    ReadEntity parentViewInfo = getParentViewInfo(alias_id, parseCtx.getViewAliasToInput());
    PlanUtils.addInput(inputs, new ReadEntity(parseCtx.getTopToTable().get(topOp), parentViewInfo));
    for (Partition part : parts) {
      if (part.getTable().isPartitioned()) {
        PlanUtils.addInput(inputs, new ReadEntity(part, parentViewInfo));
      }
      else {
        PlanUtils.addInput(inputs, new ReadEntity(part.getTable(), parentViewInfo));
      }
      Path[] paths = null;
      sampleDesc sampleDescr = parseCtx.getOpToSamplePruner().get(topOp);
      Map<String, ExprNodeDesc> partToPruner = parseCtx.getOpToPartToSkewedPruner().get(topOp);
      ExprNodeDesc listBucketingPruner = (partToPruner != null) ? partToPruner.get(part.getName()) : null;
      if (sampleDescr != null) {
        assert (listBucketingPruner == null) : "Sampling and list bucketing can\'t coexit.";
        paths = SamplePruner.prune(part, sampleDescr);
        parseCtx.getGlobalLimitCtx().disableOpt();
      }
      else 
        if (listBucketingPruner != null) {
          assert (sampleDescr == null) : "Sampling and list bucketing can\'t coexist.";
          paths = ListBucketingPruner.prune(parseCtx, part, listBucketingPruner);
        }
        else {
          if (parseCtx.getGlobalLimitCtx().isEnable()) {
            if (isFirstPart) {
              long sizeLeft = sizeNeeded;
              ArrayList<Path> retPathList = new ArrayList<Path>();
              SamplePruner.LimitPruneRetStatus status = SamplePruner.limitPrune(part, sizeLeft, fileLimit, retPathList);
              if (status.equals(SamplePruner.LimitPruneRetStatus.NoFile)) {
                continue ;
              }
              else 
                if (status.equals(SamplePruner.LimitPruneRetStatus.NotQualify)) {
                  LOG.info("Use full input -- first " + fileLimit + " files are more than " + sizeNeeded + " bytes");
                  parseCtx.getGlobalLimitCtx().disableOpt();
                }
                else {
                  emptyInput = false;
                  paths = new Path[retPathList.size()];
                  int index = 0;
                  for (Path path : retPathList) {
                    paths[index++] = path;
                  }
                  if (status.equals(SamplePruner.LimitPruneRetStatus.NeedAllFiles) && singlePartition) {
                    parseCtx.getGlobalLimitCtx().disableOpt();
                  }
                }
              isFirstPart = false;
            }
            else {
              paths = new Path[0];
            }
          }
          if (!parseCtx.getGlobalLimitCtx().isEnable()) {
            paths = part.getPath();
          }
        }
      if (!part.getTable().isPartitioned()) {
        assert ((tblDir == null) && (tblDesc == null));
        tblDir = paths[0];
        tblDesc = Utilities.getTableDesc(part.getTable());
      }
      else 
        if (tblDesc == null) {
          tblDesc = Utilities.getTableDesc(part.getTable());
        }
      if (props != null) {
        Properties target = tblDesc.getProperties();
        if (target == null) {
          tblDesc.setProperties(target = new Properties());
        }
        target.putAll(props);
      }
      for (Path p : paths) {
        if (p == null) {
          continue ;
        }
        String path = p.toString();
        if (LOG.isDebugEnabled()) {
          LOG.debug("Adding " + path + " of table" + alias_id);
        }
        partDir.add(p);
        try {
          if (part.getTable().isPartitioned()) {
            partDesc.add(Utilities.getPartitionDesc(part));
          }
          else {
            partDesc.add(Utilities.getPartitionDescFromTableDesc(tblDesc, part));
          }
        }
        catch (HiveException e) {
          LOG.error(org.apache.hadoop.util.StringUtils.stringifyException(e));
          throw new SemanticException(e.getMessage(), e);
        }
      }
    }
    if (emptyInput) {
      parseCtx.getGlobalLimitCtx().disableOpt();
    }
    Iterator<Path> iterPath = partDir.iterator();
    Iterator<PartitionDesc> iterPartnDesc = partDesc.iterator();
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
        localPlan.getAliasToFetchWork().put(alias_id, new FetchWork(FetchWork.convertPathToStringArray(partDir), partDesc, tblDesc));
      }
      else {
        localPlan.getAliasToFetchWork().put(alias_id, new FetchWork(tblDir.toString(), tblDesc));
      }
      plan.setMapLocalWork(localPlan);
    }
  }
  public static void setTaskPlan(String path, String alias, Operator<? extends OperatorDesc> topOp, MapWork plan, boolean local, TableDesc tt_desc) throws SemanticException {
    if (path == null || alias == null) {
      return ;
    }
    if (!local) {
      if (plan.getPathToAliases().get(path) == null) {
        plan.getPathToAliases().put(path, new ArrayList<String>());
      }
      plan.getPathToAliases().get(path).add(alias);
      plan.getPathToPartitionInfo().put(path, new PartitionDesc(tt_desc, null));
      plan.getAliasToWork().put(alias, topOp);
    }
    else {
      MapredLocalWork localPlan = plan.getMapLocalWork();
      if (localPlan == null) {
        localPlan = new MapredLocalWork(new LinkedHashMap<String, Operator<? extends OperatorDesc>>(), new LinkedHashMap<String, FetchWork>());
      }
      assert localPlan.getAliasToWork().get(alias) == null;
      assert localPlan.getAliasToFetchWork().get(alias) == null;
      localPlan.getAliasToWork().put(alias, topOp);
      localPlan.getAliasToFetchWork().put(alias, new FetchWork(new Path(alias), tt_desc));
      plan.setMapLocalWork(localPlan);
    }
  }
  public static void setKeyAndValueDesc(ReduceWork work, ReduceSinkOperator rs) {
    work.setKeyDesc(rs.getConf().getKeySerializeInfo());
    int tag = Math.max(0, rs.getConf().getTag());
    List<TableDesc> tagToSchema = work.getTagToValueDesc();
    while (tag + 1 > tagToSchema.size()){
      tagToSchema.add(null);
    }
    tagToSchema.set(tag, rs.getConf().getValueSerializeInfo());
  }
  public static void setKeyAndValueDesc(ReduceWork plan, Operator<? extends OperatorDesc> topOp) {
    if (topOp == null) {
      return ;
    }
    if (topOp instanceof ReduceSinkOperator) {
      ReduceSinkOperator rs = (ReduceSinkOperator)topOp;
      setKeyAndValueDesc(plan, rs);
    }
    else {
      List<Operator<? extends OperatorDesc>> children = topOp.getChildOperators();
      if (children != null) {
        for (Operator<? extends OperatorDesc> op : children) {
          setKeyAndValueDesc(plan, op);
        }
      }
    }
  }
  public static void setKeyAndValueDescForTaskTree(Task<? extends Serializable> task) {
    if (task instanceof ConditionalTask) {
      List<Task<? extends Serializable>> listTasks = ((ConditionalTask)task).getListTasks();
      for (Task<? extends Serializable> tsk : listTasks) {
        setKeyAndValueDescForTaskTree(tsk);
      }
    }
    else 
      if (task instanceof ExecDriver) {
        MapredWork work = (MapredWork)task.getWork();
        work.getMapWork().deriveExplainAttributes();
        HashMap<String, Operator<? extends OperatorDesc>> opMap = work.getMapWork().getAliasToWork();
        if (opMap != null && !opMap.isEmpty()) {
          for (Operator<? extends OperatorDesc> op : opMap.values()) {
            setKeyAndValueDesc(work.getReduceWork(), op);
          }
        }
      }
    if (task.getChildTasks() == null) {
      return ;
    }
    for (Task<? extends Serializable> childTask : task.getChildTasks()) {
      setKeyAndValueDescForTaskTree(childTask);
    }
  }
  public static MapredWork getMapRedWork(ParseContext parseCtx) {
    MapredWork work = getMapRedWorkFromConf(parseCtx.getConf());
    work.getMapWork().setNameToSplitSample(parseCtx.getNameToSplitSample());
    return work;
  }
  public static MapredWork getMapRedWorkFromConf(HiveConf conf) {
    MapredWork mrWork = new MapredWork();
    MapWork work = mrWork.getMapWork();
    boolean mapperCannotSpanPartns = conf.getBoolVar(HiveConf.ConfVars.HIVE_MAPPER_CANNOT_SPAN_MULTIPLE_PARTITIONS);
    work.setMapperCannotSpanPartns(mapperCannotSpanPartns);
    work.setPathToAliases(new LinkedHashMap<String, ArrayList<String>>());
    work.setPathToPartitionInfo(new LinkedHashMap<String, PartitionDesc>());
    work.setAliasToWork(new LinkedHashMap<String, Operator<? extends OperatorDesc>>());
    work.setHadoopSupportsSplittable(conf.getBoolVar(HiveConf.ConfVars.HIVE_COMBINE_INPUT_FORMAT_SUPPORTS_SPLITTABLE));
    return mrWork;
  }
  @SuppressWarnings(value = {"nls", }) public static Operator<? extends OperatorDesc> putOpInsertMap(Operator<? extends OperatorDesc> op, RowResolver rr, ParseContext parseCtx) {
    OpParseContext ctx = new OpParseContext(rr);
    parseCtx.getOpParseCtx().put(op, ctx);
    return op;
  }
  public static TableScanOperator createTemporaryTableScanOperator(RowSchema rowSchema) {
    TableScanOperator tableScanOp = (TableScanOperator)OperatorFactory.get(new TableScanDesc(), rowSchema);
    List<Integer> neededColumnIds = new ArrayList<Integer>();
    List<String> neededColumnNames = new ArrayList<String>();
    List<ColumnInfo> parentColumnInfos = rowSchema.getSignature();
    for (int i = 0; i < parentColumnInfos.size(); i++) {
      neededColumnIds.add(i);
      neededColumnNames.add(parentColumnInfos.get(i).getInternalName());
    }
    tableScanOp.setNeededColumnIDs(neededColumnIds);
    tableScanOp.setNeededColumns(neededColumnNames);
    return tableScanOp;
  }
  protected static TableScanOperator createTemporaryFile(Operator<? extends OperatorDesc> parent, Operator<? extends OperatorDesc> child, String taskTmpDir, TableDesc tt_desc, ParseContext parseCtx) {
    boolean compressIntermediate = parseCtx.getConf().getBoolVar(HiveConf.ConfVars.COMPRESSINTERMEDIATE);
    FileSinkDesc desc = new FileSinkDesc(taskTmpDir, tt_desc, compressIntermediate);
    if (compressIntermediate) {
      desc.setCompressCodec(parseCtx.getConf().getVar(HiveConf.ConfVars.COMPRESSINTERMEDIATECODEC));
      desc.setCompressType(parseCtx.getConf().getVar(HiveConf.ConfVars.COMPRESSINTERMEDIATETYPE));
    }
    Operator<? extends OperatorDesc> fileSinkOp = putOpInsertMap(OperatorFactory.get(desc, parent.getSchema()), null, parseCtx);
    parent.replaceChild(child, fileSinkOp);
    fileSinkOp.setParentOperators(Utilities.makeList(parent));
    RowResolver parentRowResolver = parseCtx.getOpParseCtx().get(parent).getRowResolver();
    TableScanOperator tableScanOp = (TableScanOperator)putOpInsertMap(createTemporaryTableScanOperator(parent.getSchema()), parentRowResolver, parseCtx);
    tableScanOp.setChildOperators(Utilities.makeList(child));
    child.replaceParent(parent, tableScanOp);
    return tableScanOp;
  }
  @SuppressWarnings(value = {"nls", }) private static void splitTasks(ReduceSinkOperator op, Task<? extends Serializable> parentTask, Task<? extends Serializable> childTask, GenMRProcContext opProcCtx) throws SemanticException {
    if (op.getNumParent() != 1) {
      throw new IllegalStateException("Expecting operator " + op + " to have one parent. " + "But found multiple parents : " + op.getParentOperators());
    }
    ParseContext parseCtx = opProcCtx.getParseCtx();
    parentTask.addDependentTask(childTask);
    List<Task<? extends Serializable>> rootTasks = opProcCtx.getRootTasks();
    if (rootTasks.contains(childTask)) {
      rootTasks.remove(childTask);
    }
    Context baseCtx = parseCtx.getContext();
    String taskTmpDir = baseCtx.getMRTmpFileURI();
    Operator<? extends OperatorDesc> parent = op.getParentOperators().get(0);
    TableDesc tt_desc = PlanUtils.getIntermediateFileTableDesc(PlanUtils.getFieldSchemasFromRowSchema(parent.getSchema(), "temporarycol"));
    TableScanOperator tableScanOp = createTemporaryFile(parent, op, taskTmpDir, tt_desc, parseCtx);
    Map<Operator<? extends OperatorDesc>, GenMapRedCtx> mapCurrCtx = opProcCtx.getMapCurrCtx();
    mapCurrCtx.put(tableScanOp, new GenMapRedCtx(childTask, null));
    String streamDesc = taskTmpDir;
    MapredWork cplan = (MapredWork)childTask.getWork();
    if (needsTagging(cplan.getReduceWork())) {
      Operator<? extends OperatorDesc> reducerOp = cplan.getReduceWork().getReducer();
      QBJoinTree joinTree = null;
      if (reducerOp instanceof JoinOperator) {
        joinTree = parseCtx.getJoinContext().get(reducerOp);
      }
      else 
        if (reducerOp instanceof MapJoinOperator) {
          joinTree = parseCtx.getMapJoinContext().get(reducerOp);
        }
        else 
          if (reducerOp instanceof SMBMapJoinOperator) {
            joinTree = parseCtx.getSmbMapJoinContext().get(reducerOp);
          }
      if (joinTree != null && joinTree.getId() != null) {
        streamDesc = joinTree.getId() + ":$INTNAME";
      }
      else {
        streamDesc = "$INTNAME";
      }
      String origStreamDesc = streamDesc;
      int pos = 0;
      while (cplan.getMapWork().getAliasToWork().get(streamDesc) != null){
        streamDesc = origStreamDesc.concat(String.valueOf(++pos));
      }
      cplan.getReduceWork().setNeedsTagging(true);
    }
    setTaskPlan(taskTmpDir, streamDesc, tableScanOp, cplan.getMapWork(), false, tt_desc);
    opProcCtx.setCurrTopOp(null);
    opProcCtx.setCurrAliasId(null);
    opProcCtx.setCurrTask(childTask);
    opProcCtx.addRootIfPossible(parentTask);
  }
  static boolean hasBranchFinished(Object ... children) {
    for (Object child : children) {
      if (child == null) {
        return false;
      }
    }
    return true;
  }
  public static void replaceMapWork(String sourceAlias, String targetAlias, MapWork source, MapWork target) {
    Map<String, ArrayList<String>> sourcePathToAliases = source.getPathToAliases();
    Map<String, PartitionDesc> sourcePathToPartitionInfo = source.getPathToPartitionInfo();
    Map<String, Operator<? extends OperatorDesc>> sourceAliasToWork = source.getAliasToWork();
    Map<String, PartitionDesc> sourceAliasToPartnInfo = source.getAliasToPartnInfo();
    Map<String, ArrayList<String>> targetPathToAliases = target.getPathToAliases();
    Map<String, PartitionDesc> targetPathToPartitionInfo = target.getPathToPartitionInfo();
    Map<String, Operator<? extends OperatorDesc>> targetAliasToWork = target.getAliasToWork();
    Map<String, PartitionDesc> targetAliasToPartnInfo = target.getAliasToPartnInfo();
    if (!sourceAliasToWork.containsKey(sourceAlias) || !targetAliasToWork.containsKey(targetAlias)) {
      return ;
    }
    if (sourceAliasToWork.size() > 1) {
      return ;
    }
    targetAliasToWork.remove(targetAlias);
    targetAliasToPartnInfo.remove(targetAlias);
    List<String> pathsToRemove = new ArrayList<String>();
    for (Entry<String, ArrayList<String>> entry : targetPathToAliases.entrySet()) {
      ArrayList<String> aliases = entry.getValue();
      aliases.remove(targetAlias);
      if (aliases.isEmpty()) {
        pathsToRemove.add(entry.getKey());
      }
    }
    for (String pathToRemove : pathsToRemove) {
      targetPathToAliases.remove(pathToRemove);
      targetPathToPartitionInfo.remove(pathToRemove);
    }
    targetAliasToWork.put(sourceAlias, sourceAliasToWork.get(sourceAlias));
    targetAliasToPartnInfo.putAll(sourceAliasToPartnInfo);
    targetPathToPartitionInfo.putAll(sourcePathToPartitionInfo);
    List<String> pathsToAdd = new ArrayList<String>();
    for (Entry<String, ArrayList<String>> entry : sourcePathToAliases.entrySet()) {
      ArrayList<String> aliases = entry.getValue();
      if (aliases.contains(sourceAlias)) {
        pathsToAdd.add(entry.getKey());
      }
    }
    for (String pathToAdd : pathsToAdd) {
      if (!targetPathToAliases.containsKey(pathToAdd)) {
        targetPathToAliases.put(pathToAdd, new ArrayList<String>());
      }
      targetPathToAliases.get(pathToAdd).add(sourceAlias);
    }
  }
  public static void createMRWorkForMergingFiles(FileSinkOperator fsInput, Path finalName, DependencyCollectionTask dependencyTask, List<Task<MoveWork>> mvTasks, HiveConf conf, Task<? extends Serializable> currTask) throws SemanticException {
    FileSinkDesc fsInputDesc = fsInput.getConf();
    RowSchema inputRS = fsInput.getSchema();
    Operator<? extends OperatorDesc> tsMerge = GenMapRedUtils.createTemporaryTableScanOperator(inputRS);
    TableDesc ts = (TableDesc)fsInputDesc.getTableInfo().clone();
    FileSinkDesc fsOutputDesc = new FileSinkDesc(finalName.toUri().toString(), ts, conf.getBoolVar(ConfVars.COMPRESSRESULT));
    FileSinkOperator fsOutput = (FileSinkOperator)OperatorFactory.getAndMakeChild(fsOutputDesc, inputRS, tsMerge);
    DynamicPartitionCtx dpCtx = fsInputDesc.getDynPartCtx();
    if (dpCtx != null && dpCtx.getNumDPCols() > 0) {
      ArrayList<ColumnInfo> signature = inputRS.getSignature();
      String tblAlias = fsInputDesc.getTableInfo().getTableName();
      LinkedHashMap<String, String> colMap = new LinkedHashMap<String, String>();
      StringBuilder partCols = new StringBuilder();
      for (String dpCol : dpCtx.getDPColNames()) {
        ColumnInfo colInfo = new ColumnInfo(dpCol, TypeInfoFactory.stringTypeInfo, tblAlias, true);
        signature.add(colInfo);
        colMap.put(dpCol, dpCol);
        partCols.append(dpCol).append('/');
      }
      partCols.setLength(partCols.length() - 1);
      inputRS.setSignature(signature);
      DynamicPartitionCtx dpCtx2 = new DynamicPartitionCtx(dpCtx);
      dpCtx2.setInputToDPCols(colMap);
      fsOutputDesc.setDynPartCtx(dpCtx2);
      fsInputDesc.getTableInfo().getProperties().setProperty(org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_PARTITION_COLUMNS, partCols.toString());
    }
    else {
      fsInputDesc.getTableInfo().getProperties().remove(org.apache.hadoop.hive.metastore.api.hive_metastoreConstants.META_TABLE_PARTITION_COLUMNS);
    }
    MoveWork dummyMv = new MoveWork(null, null, null, new LoadFileDesc(new Path(fsInputDesc.getFinalDirName()), finalName, true, null, null), false);
    MapWork cplan;
    Serializable work;
    if (conf.getBoolVar(ConfVars.HIVEMERGERCFILEBLOCKLEVEL) && fsInputDesc.getTableInfo().getInputFileFormatClass().equals(RCFileInputFormat.class)) {
      String inputFormatClass = conf.getVar(ConfVars.HIVEMERGEINPUTFORMATBLOCKLEVEL);
      try {
        Class c = (Class<? extends InputFormat>)Class.forName(inputFormatClass);
        LOG.info("RCFile format- Using block level merge");
        cplan = GenMapRedUtils.createRCFileMergeTask(fsInputDesc, finalName, dpCtx != null && dpCtx.getNumDPCols() > 0);
        work = cplan;
      }
      catch (ClassNotFoundException e) {
        String msg = "Illegal input format class: " + inputFormatClass;
        throw new SemanticException(msg);
      }
    }
    else {
      cplan = createMRWorkForMergingFiles(conf, tsMerge, fsInputDesc);
      if (conf.getBoolVar(ConfVars.HIVE_OPTIMIZE_TEZ)) {
        work = new TezWork();
        cplan.setName("Merge");
        ((TezWork)work).add(cplan);
      }
      else {
        work = new MapredWork();
        ((MapredWork)work).setMapWork(cplan);
      }
    }
    cplan.setInputformat("org.apache.hadoop.hive.ql.io.CombineHiveInputFormat");
    ConditionalTask cndTsk = GenMapRedUtils.createCondTask(conf, currTask, dummyMv, work, fsInputDesc.getFinalDirName());
    ConditionalResolverMergeFilesCtx mrCtx = (ConditionalResolverMergeFilesCtx)cndTsk.getResolverCtx();
    mrCtx.setDPCtx(fsInputDesc.getDynPartCtx());
    mrCtx.setLbCtx(fsInputDesc.getLbCtx());
    linkMoveTask(fsOutput, cndTsk, mvTasks, conf, dependencyTask);
  }
  public static void linkMoveTask(FileSinkOperator newOutput, ConditionalTask cndTsk, List<Task<MoveWork>> mvTasks, HiveConf hconf, DependencyCollectionTask dependencyTask) {
    Task<MoveWork> mvTask = GenMapRedUtils.findMoveTask(mvTasks, newOutput);
    for (Task<? extends Serializable> tsk : cndTsk.getListTasks()) {
      linkMoveTask(mvTask, tsk, hconf, dependencyTask);
    }
  }
  public static void linkMoveTask(Task<MoveWork> mvTask, Task<? extends Serializable> task, HiveConf hconf, DependencyCollectionTask dependencyTask) {
    if (task.getDependentTasks() == null || task.getDependentTasks().isEmpty()) {
      addDependentMoveTasks(mvTask, hconf, task, dependencyTask);
    }
    else {
      for (Task<? extends Serializable> childTask : task.getDependentTasks()) {
        linkMoveTask(mvTask, childTask, hconf, dependencyTask);
      }
    }
  }
  public static void addDependentMoveTasks(Task<MoveWork> mvTask, HiveConf hconf, Task<? extends Serializable> parentTask, DependencyCollectionTask dependencyTask) {
    if (mvTask != null) {
      if (dependencyTask != null) {
        parentTask.addDependentTask(dependencyTask);
        if (mvTask.getWork().getLoadTableWork() != null) {
          dependencyTask.addDependentTask(mvTask);
        }
        else {
          parentTask.addDependentTask(mvTask);
        }
      }
      else {
        parentTask.addDependentTask(mvTask);
      }
    }
  }
  public static void addStatsTask(FileSinkOperator nd, MoveTask mvTask, Task<? extends Serializable> currTask, HiveConf hconf) {
    MoveWork mvWork = mvTask.getWork();
    StatsWork statsWork = null;
    if (mvWork.getLoadTableWork() != null) {
      statsWork = new StatsWork(mvWork.getLoadTableWork());
    }
    else 
      if (mvWork.getLoadFileWork() != null) {
        statsWork = new StatsWork(mvWork.getLoadFileWork());
      }
    assert statsWork != null : "Error when genereting StatsTask";
    statsWork.setSourceTask(currTask);
    statsWork.setStatsReliable(hconf.getBoolVar(ConfVars.HIVE_STATS_RELIABLE));
    if (currTask.getWork() instanceof MapredWork) {
      MapredWork mrWork = (MapredWork)currTask.getWork();
      mrWork.getMapWork().setGatheringStats(true);
      if (mrWork.getReduceWork() != null) {
        mrWork.getReduceWork().setGatheringStats(true);
      }
    }
    else {
      TezWork work = (TezWork)currTask.getWork();
      for (BaseWork w : work.getAllWork()) {
        w.setGatheringStats(true);
      }
    }
    statsWork.setAggKey(nd.getConf().getStatsAggPrefix());
    Task<? extends Serializable> statsTask = TaskFactory.get(statsWork, hconf);
    nd.getConf().setGatherStats(true);
    nd.getConf().setStatsReliable(hconf.getBoolVar(ConfVars.HIVE_STATS_RELIABLE));
    nd.getConf().setMaxStatsKeyPrefixLength(StatsFactory.getMaxPrefixLength(hconf));
    mvTask.addDependentTask(statsTask);
    statsTask.subscribeFeed(mvTask);
  }
  public static boolean isInsertInto(ParseContext parseCtx, FileSinkOperator fsOp) {
    return fsOp.getConf().getTableInfo().getTableName() != null && parseCtx.getQB().getParseInfo().isInsertToTable();
  }
  private static MapWork createMRWorkForMergingFiles(HiveConf conf, Operator<? extends OperatorDesc> topOp, FileSinkDesc fsDesc) {
    ArrayList<String> aliases = new ArrayList<String>();
    String inputDir = fsDesc.getFinalDirName();
    TableDesc tblDesc = fsDesc.getTableInfo();
    aliases.add(inputDir);
    MapredWork cMrPlan = GenMapRedUtils.getMapRedWorkFromConf(conf);
    MapWork cplan = cMrPlan.getMapWork();
    cplan.getPathToAliases().put(inputDir, aliases);
    cplan.getPathToPartitionInfo().put(inputDir, new PartitionDesc(tblDesc, null));
    cplan.getAliasToWork().put(inputDir, topOp);
    cplan.setMapperCannotSpanPartns(true);
    return cplan;
  }
  public static MapWork createRCFileMergeTask(FileSinkDesc fsInputDesc, Path finalName, boolean hasDynamicPartitions) throws SemanticException {
    String inputDir = fsInputDesc.getFinalDirName();
    TableDesc tblDesc = fsInputDesc.getTableInfo();
    if (tblDesc.getInputFileFormatClass().equals(RCFileInputFormat.class)) {
      ArrayList<String> inputDirs = new ArrayList<String>();
      if (!hasDynamicPartitions && !GenMapRedUtils.isSkewedStoredAsDirs(fsInputDesc)) {
        inputDirs.add(inputDir);
      }
      MergeWork work = new MergeWork(inputDirs, finalName, hasDynamicPartitions, fsInputDesc.getDynPartCtx());
      LinkedHashMap<String, ArrayList<String>> pathToAliases = new LinkedHashMap<String, ArrayList<String>>();
      pathToAliases.put(inputDir, (ArrayList<String>)inputDirs.clone());
      work.setMapperCannotSpanPartns(true);
      work.setPathToAliases(pathToAliases);
      work.setAliasToWork(new LinkedHashMap<String, Operator<? extends OperatorDesc>>());
      if (hasDynamicPartitions || GenMapRedUtils.isSkewedStoredAsDirs(fsInputDesc)) {
        work.getPathToPartitionInfo().put(inputDir, new PartitionDesc(tblDesc, null));
      }
      work.setListBucketingCtx(fsInputDesc.getLbCtx());
      return work;
    }
    throw new SemanticException("createRCFileMergeTask called on non-RCFile table");
  }
  @SuppressWarnings(value = {"unchecked", }) public static ConditionalTask createCondTask(HiveConf conf, Task<? extends Serializable> currTask, MoveWork mvWork, Serializable mergeWork, String inputPath) {
    Task<? extends Serializable> mergeOnlyMergeTask = TaskFactory.get(mergeWork, conf);
    Task<? extends Serializable> moveOnlyMoveTask = TaskFactory.get(mvWork, conf);
    Task<? extends Serializable> mergeAndMoveMergeTask = TaskFactory.get(mergeWork, conf);
    Task<? extends Serializable> mergeAndMoveMoveTask = TaskFactory.get(mvWork, conf);
    mergeAndMoveMergeTask.addDependentTask(mergeAndMoveMoveTask);
    List<Serializable> listWorks = new ArrayList<Serializable>();
    listWorks.add(mvWork);
    listWorks.add(mergeWork);
    ConditionalWork cndWork = new ConditionalWork(listWorks);
    List<Task<? extends Serializable>> listTasks = new ArrayList<Task<? extends Serializable>>();
    listTasks.add(moveOnlyMoveTask);
    listTasks.add(mergeOnlyMergeTask);
    listTasks.add(mergeAndMoveMergeTask);
    ConditionalTask cndTsk = (ConditionalTask)TaskFactory.get(cndWork, conf);
    cndTsk.setListTasks(listTasks);
    cndTsk.setResolver(new ConditionalResolverMergeFiles());
    ConditionalResolverMergeFilesCtx mrCtx = new ConditionalResolverMergeFilesCtx(listTasks, inputPath);
    cndTsk.setResolverCtx(mrCtx);
    currTask.addDependentTask(cndTsk);
    return cndTsk;
  }
  public static boolean isSkewedStoredAsDirs(FileSinkDesc fsInputDesc) {
    return (fsInputDesc.getLbCtx() == null) ? false : fsInputDesc.getLbCtx().isSkewedStoredAsDir();
  }
  public static Task<MoveWork> findMoveTask(List<Task<MoveWork>> mvTasks, FileSinkOperator fsOp) {
    for (Task<MoveWork> mvTsk : mvTasks) {
      MoveWork mvWork = mvTsk.getWork();
      String srcDir = null;
      if (mvWork.getLoadFileWork() != null) {
        srcDir = mvWork.getLoadFileWork().getSourceDir();
      }
      else 
        if (mvWork.getLoadTableWork() != null) {
          srcDir = mvWork.getLoadTableWork().getSourceDir();
        }
      String fsOpDirName = fsOp.getConf().getFinalDirName();
      if ((srcDir != null) && (srcDir.equalsIgnoreCase(fsOpDirName))) {
        return mvTsk;
      }
    }
    return null;
  }
  public static boolean isMergeRequired(List<Task<MoveWork>> mvTasks, HiveConf hconf, FileSinkOperator fsOp, Task<? extends Serializable> currTask, boolean isInsertTable) {
    if ((mvTasks != null) && (!mvTasks.isEmpty())) {
      MoveTask mvTask = (MoveTask)GenMapRedUtils.findMoveTask(mvTasks, fsOp);
      if (mvTask != null && isInsertTable && hconf.getBoolVar(ConfVars.HIVESTATSAUTOGATHER)) {
        GenMapRedUtils.addStatsTask(fsOp, mvTask, currTask, hconf);
      }
      if ((mvTask != null) && !mvTask.isLocal() && fsOp.getConf().canBeMerged()) {
        if (fsOp.getConf().isLinkedFileSink()) {
          if (hconf.getBoolVar(ConfVars.HIVEMERGEMAPFILES) || hconf.getBoolVar(ConfVars.HIVEMERGEMAPREDFILES)) {
            return true;
          }
        }
        else {
          if (currTask.getWork() instanceof TezWork) {
            return hconf.getBoolVar(ConfVars.HIVEMERGEMAPFILES) || hconf.getBoolVar(ConfVars.HIVEMERGEMAPREDFILES);
          }
          else 
            if (currTask.getWork() instanceof MapredWork) {
              ReduceWork reduceWork = ((MapredWork)currTask.getWork()).getReduceWork();
              boolean mergeMapOnly = hconf.getBoolVar(ConfVars.HIVEMERGEMAPFILES) && reduceWork == null;
              boolean mergeMapRed = hconf.getBoolVar(ConfVars.HIVEMERGEMAPREDFILES) && reduceWork != null;
              if (mergeMapOnly || mergeMapRed) {
                return true;
              }
            }
            else {
              return false;
            }
        }
      }
    }
    return false;
  }
  public static Path createMoveTask(Task<? extends Serializable> currTask, boolean chDir, FileSinkOperator fsOp, ParseContext parseCtx, List<Task<MoveWork>> mvTasks, HiveConf hconf, DependencyCollectionTask dependencyTask) {
    Path dest = null;
    if (chDir) {
      dest = new Path(fsOp.getConf().getFinalDirName());
      Context baseCtx = parseCtx.getContext();
      String tmpDir = baseCtx.getExternalTmpFileURI(dest.toUri());
      FileSinkDesc fileSinkDesc = fsOp.getConf();
      if (fileSinkDesc.isLinkedFileSink()) {
        for (FileSinkDesc fsConf : fileSinkDesc.getLinkedFileSinkDesc()) {
          String fileName = Utilities.getFileNameFromDirName(fsConf.getDirName());
          fsConf.setParentDir(tmpDir);
          fsConf.setDirName(tmpDir + Path.SEPARATOR + fileName);
        }
      }
      else {
        fileSinkDesc.setDirName(tmpDir);
      }
    }
    Task<MoveWork> mvTask = null;
    if (!chDir) {
      mvTask = GenMapRedUtils.findMoveTask(mvTasks, fsOp);
    }
    if (mvTask != null) {
      GenMapRedUtils.addDependentMoveTasks(mvTask, hconf, currTask, dependencyTask);
    }
    return dest;
  }
  private GenMapRedUtils() {
  }
}

