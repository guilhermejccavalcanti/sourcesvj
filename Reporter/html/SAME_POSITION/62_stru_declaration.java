  public Backend(Configuration configuration, Configuration metricsConfig) {
    this.configuration = configuration;
    storeManager = getStorageManager(configuration);
    indexes = getIndexes(configuration);
    storeFeatures = storeManager.getFeatures();
    if (null == metricsConfig) {
      metricsConfig = new BaseConfiguration();
    }
    basicMetrics = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_b7abc2e_902237a\rev_left_b7abc2e\titan-core\src\main\java\com\thinkaurelius\titan\diskstorage\Backend.java
configuration.get(BASIC_METRICS)
=======
GraphDatabaseConfiguration.isMetricsEnabled(storageConfig, metricsConfig)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_b7abc2e_902237a\rev_right_902237a\titan-core\src\main\java\com\thinkaurelius\titan\diskstorage\Backend.java
;
    mergeBasicMetrics = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_b7abc2e_902237a\rev_left_b7abc2e\titan-core\src\main\java\com\thinkaurelius\titan\diskstorage\Backend.java
configuration.get(MERGE_BASIC_METRICS)
=======
GraphDatabaseConfiguration.isMetricsMergingEnabled(storageConfig, metricsConfig)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_b7abc2e_902237a\rev_right_902237a\titan-core\src\main\java\com\thinkaurelius\titan\diskstorage\Backend.java
;
    Preconditions.checkArgument(bufferSizeTmp >= 0, "Buffer size must be non-negative (use 0 to disable)");
    if (!storeFeatures.supportsBatchMutation()) {
      bufferSize = 0;
      log.debug("Buffering disabled because backend does not support batch mutations");
    }
    else 
      bufferSize = bufferSizeTmp;
    writeAttempts = configuration.get(WRITE_ATTEMPTS);
    readAttempts = configuration.get(READ_ATTEMPTS);
    persistAttemptWaittime = configuration.get(STORAGE_ATTEMPT_WAITTIME);
    if (configuration.get(PARALLEL_BACKEND_OPS)) {
      int poolsize = Math.min(1, Runtime.getRuntime().availableProcessors()) * THREAD_POOL_SIZE_SCALE_FACTOR;
      threadPool = Executors.newFixedThreadPool(poolsize);
      log.info("Initiated backend operations thread pool of size {}", poolsize);
    }
    else {
      threadPool = null;
    }
    final String lockBackendName = configuration.get(LOCK_BACKEND);
    if (REGISTERED_LOCKERS.containsKey(lockBackendName)) {
      lockerCreator = REGISTERED_LOCKERS.get(lockBackendName);
    }
    else {
      throw new TitanConfigurationException("Unknown lock backend \"" + lockBackendName + "\".  Known lock backends: " + Joiner.on(", ").join(REGISTERED_LOCKERS.keySet()) + ".");
    }
    Preconditions.checkNotNull(lockerCreator);
    if (storeFeatures.isDistributed() && storeFeatures.isKeyOrdered()) {
      log.debug("Wrapping index store with HashPrefix");
      hashPrefixIndex = true;
    }
    else {
      hashPrefixIndex = false;
    }
  }


