  @Override protected void optimizeTaskPlan(List<Task<? extends Serializable>> rootTasks, ParseContext pCtx, Context ctx) throws SemanticException {
    PhysicalContext physicalCtx = new PhysicalContext(conf, pCtx, pCtx.getContext(), rootTasks, pCtx.getFetchTask());
    if (conf.getBoolVar(HiveConf.ConfVars.HIVENULLSCANOPTIMIZE)) {
      physicalCtx = new NullScanOptimizer().resolve(physicalCtx);
    }
    else {
      LOG.debug("Skipping null scan query optimization");
    }
    if (conf.getBoolVar(HiveConf.ConfVars.HIVEMETADATAONLYQUERIES)) {
      physicalCtx = new MetadataOnlyOptimizer().resolve(physicalCtx);
    }
    else {
      LOG.debug("Skipping metadata only query optimization");
    }
    if (conf.getBoolVar(HiveConf.ConfVars.HIVE_CHECK_CROSS_PRODUCT)) {
      physicalCtx = new CrossProductCheck().resolve(physicalCtx);
    }
    else {
      LOG.debug("Skipping cross product analysis");
    }
    if (conf.getBoolVar(HiveConf.ConfVars.HIVE_VECTORIZATION_ENABLED)) {
      physicalCtx = new Vectorizer().resolve(physicalCtx);
    }
    else {
      LOG.debug("Skipping vectorization");
    }
    if (!"none".equalsIgnoreCase(conf.getVar(HiveConf.ConfVars.HIVESTAGEIDREARRANGE))) {
      physicalCtx = new StageIDsRearranger().resolve(physicalCtx);
    }
    else {
      LOG.debug("Skipping stage id rearranger");
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_4f097fd_88482c3\rev_left_4f097fd\ql\src\java\org\apache\hadoop\hive\ql\parse\TezCompiler.java
if ("llap".equalsIgnoreCase(conf.getVar(HiveConf.ConfVars.HIVE_EXECUTION_MODE))) {
      physicalCtx = new LlapDecider().resolve(physicalCtx);
    }
    else {
      LOG.debug("Skipping llap decider");
    }
=======
if ((conf.getBoolVar(HiveConf.ConfVars.HIVE_TEZ_ENABLE_MEMORY_MANAGER)) && (conf.getBoolVar(HiveConf.ConfVars.HIVEUSEHYBRIDGRACEHASHJOIN))) {
      physicalCtx = new MemoryDecider().resolve(physicalCtx);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_4f097fd_88482c3\rev_right_88482c3\ql\src\java\org\apache\hadoop\hive\ql\parse\TezCompiler.java

    return ;
  }


