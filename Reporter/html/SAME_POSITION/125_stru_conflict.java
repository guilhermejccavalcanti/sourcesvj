<<<<<<< MINE
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
>>>>>>> YOURS

