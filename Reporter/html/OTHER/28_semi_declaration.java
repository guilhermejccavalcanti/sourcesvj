private void analyzeAlterTablePartMergeFiles(ASTNode ast,
      String tableName, HashMap<String, String> partSpec)
      throws SemanticException {
    AlterTablePartMergeFilesDesc mergeDesc = new AlterTablePartMergeFilesDesc(
        tableName, partSpec);

    List<Path> inputDir = new ArrayList<Path>();
    Path oldTblPartLoc = null;
    Path newTblPartLoc = null;
    Table tblObj = null;
    ListBucketingCtx lbCtx = null;

    try {
      tblObj = getTable(tableName);
      // TODO: we should probably block all ACID tables here.
      if (MetaStoreUtils.isInsertOnlyTable(tblObj.getParameters())) {
        throw new SemanticException("Merge is not supported for MM tables");
      }
      mergeDesc.setTableDesc(Utilities.getTableDesc(tblObj));

      List<String> bucketCols = null;
      Class<? extends InputFormat> inputFormatClass = null;
      boolean isArchived = false;
      boolean checkIndex = HiveConf.getBoolVar(conf,
          HiveConf.ConfVars.HIVE_CONCATENATE_CHECK_INDEX);
      if (checkIndex) {
        List<Index> indexes = db.getIndexes(tblObj.getDbName(), tblObj.getTableName(),
            Short.MAX_VALUE);
        if (indexes != null && indexes.size() > 0) {
          throw new SemanticException("can not do merge because source table "
              + tableName + " is indexed.");
        }
      }

      if (tblObj.isPartitioned()) {
        if (partSpec == null) {
          throw new SemanticException("source table " + tableName
              + " is partitioned but no partition desc found.");
        } else {
          Partition part = getPartition(tblObj, partSpec, false);
          if (part == null) {
            throw new SemanticException("source table " + tableName
                + " is partitioned but partition not found.");
          }
          bucketCols = part.getBucketCols();
          inputFormatClass = part.getInputFormatClass();
          isArchived = ArchiveUtils.isArchived(part);

          Path tabPath = tblObj.getPath();
          Path partPath = part.getDataLocation();

          // if the table is in a different dfs than the partition,
          // replace the partition's dfs with the table's dfs.
          newTblPartLoc = new Path(tabPath.toUri().getScheme(), tabPath.toUri()
              .getAuthority(), partPath.toUri().getPath());

          oldTblPartLoc = partPath;

          lbCtx = constructListBucketingCtx(part.getSkewedColNames(), part.getSkewedColValues(),
              part.getSkewedColValueLocationMaps(), part.isStoredAsSubDirectories(), conf);
        }
      } else {
        inputFormatClass = tblObj.getInputFormatClass();
        bucketCols = tblObj.getBucketCols();

        // input and output are the same
        oldTblPartLoc = tblObj.getPath();
        newTblPartLoc = tblObj.getPath();

        lbCtx = constructListBucketingCtx(tblObj.getSkewedColNames(), tblObj.getSkewedColValues(),
            tblObj.getSkewedColValueLocationMaps(), tblObj.isStoredAsSubDirectories(), conf);
      }

      // throw a HiveException for other than rcfile and orcfile.
      if (!((inputFormatClass.equals(RCFileInputFormat.class) ||
          (inputFormatClass.equals(OrcInputFormat.class))))) {
        throw new SemanticException(
            "Only RCFile and ORCFile Formats are supported right now.");
      }
      mergeDesc.setInputFormatClass(inputFormatClass);

      // throw a HiveException if the table/partition is bucketized
      if (bucketCols != null && bucketCols.size() > 0) {
        throw new SemanticException(
            "Merge can not perform on bucketized partition/table.");
      }

      // throw a HiveException if the table/partition is archived
      if (isArchived) {
        throw new SemanticException(
            "Merge can not perform on archived partitions.");
      }

      inputDir.add(oldTblPartLoc);

      mergeDesc.setInputDir(inputDir);

      mergeDesc.setLbCtx(lbCtx);

      addInputsOutputsAlterTable(tableName, partSpec, AlterTableTypes.MERGEFILES);
      DDLWork ddlWork = new DDLWork(getInputs(), getOutputs(), mergeDesc);
      ddlWork.setNeedLock(true);
      Task<? extends Serializable> mergeTask = TaskFactory.get(ddlWork, conf);
      TableDesc tblDesc = Utilities.getTableDesc(tblObj);
      Path queryTmpdir = ctx.getExternalTmpPath(newTblPartLoc);
      mergeDesc.setOutputDir(queryTmpdir);
      // No need to handle MM tables - unsupported path.
      LoadTableDesc ltd = new LoadTableDesc(queryTmpdir, tblDesc,
<<<<<<< MINE
          partSpec == null ? new HashMap<String, String>() : partSpec, null);
=======
          partSpec == null ? new HashMap<>() : partSpec);
>>>>>>> YOURS
      ltd.setLbCtx(lbCtx);
      Task<MoveWork> moveTsk = TaskFactory.get(new MoveWork(null, null, ltd, null, false), conf);
      mergeTask.addDependentTask(moveTsk);

      if (conf.getBoolVar(HiveConf.ConfVars.HIVESTATSAUTOGATHER)) {
        StatsWork statDesc;
        if (oldTblPartLoc.equals(newTblPartLoc)) {
          // If we're merging to the same location, we can avoid some metastore calls
          TableSpec tableSpec = new TableSpec(db, tableName, partSpec);
          statDesc = new StatsWork(tableSpec);
        } else {
          statDesc = new StatsWork(ltd);
        }
        statDesc.setNoStatsAggregator(true);
        statDesc.setClearAggregatorStats(true);
        statDesc.setStatsReliable(conf.getBoolVar(HiveConf.ConfVars.HIVE_STATS_RELIABLE));
        Task<? extends Serializable> statTask = TaskFactory.get(statDesc, conf);
        moveTsk.addDependentTask(statTask);
      }

      rootTasks.add(mergeTask);
    } catch (Exception e) {
      throw new SemanticException(e);
    }
  }

