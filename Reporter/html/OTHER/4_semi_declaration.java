public int migrate() throws FlywayException {
        return execute(new Command<Integer>() {
            public Integer execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
<<<<<<< MINE
        		for (FlywayCallback callback: getCallbacks()) {
        			callback.beforeMigrate(connectionUserObjects);
        		}

        		MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table));
=======
                MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
>>>>>>> YOURS

                MigrationResolver migrationResolver = createMigrationResolver(dbSupport);
                if (validateOnMigrate) {
                    doValidate(connectionMetaDataTable, migrationResolver, metaDataTable, schemas);
                }

                new DbSchemas(connectionMetaDataTable, schemas, metaDataTable).create();

                if (!metaDataTable.hasSchemasMarker() && !metaDataTable.hasInitMarker() && !metaDataTable.hasAppliedMigrations()) {
                    List<Schema> nonEmptySchemas = new ArrayList<Schema>();
                    for (Schema schema : schemas) {
                        if (!schema.empty()) {
                            nonEmptySchemas.add(schema);
                        }
                    }

                    if (initOnMigrate || nonEmptySchemas.isEmpty()) {
                        if (initOnMigrate && !nonEmptySchemas.isEmpty()) {
                            new DbInit(connectionMetaDataTable, metaDataTable, initVersion, initDescription).init();
                        }
                    } else {
                        if (nonEmptySchemas.size() == 1) {
                            Schema schema = nonEmptySchemas.get(0);
                            //Check whether we only have an empty metadata table in an otherwise empty schema
                            if (schema.allTables().length != 1 || !schema.getTable(table).exists()) {
                                throw new FlywayException("Found non-empty schema " + schema
                                        + " without metadata table! Use init()"
                                        + " or set initOnMigrate to true to initialize the metadata table.");
                            }
                        } else {
                            throw new FlywayException("Found non-empty schemas "
                                    + StringUtils.collectionToCommaDelimitedString(nonEmptySchemas)
                                    + " without metadata table! Use init()"
                                    + " or set initOnMigrate to true to initialize the metadata table.");
                        }
                    }
                }

                DbSupport dbSupportUserObjects = DbSupportFactory.createDbSupport(connectionUserObjects, false);
                Schema originalSchemaUserObjects = dbSupportUserObjects.getCurrentSchema();
                boolean schemaChange = !schemas[0].equals(originalSchemaUserObjects);
                if (schemaChange) {
                    dbSupportUserObjects.setCurrentSchema(schemas[0]);
                }

                DbMigrate dbMigrator =
                        new DbMigrate(connectionMetaDataTable, connectionUserObjects, dbSupport, metaDataTable,
                                schemas[0], migrationResolver, target, ignoreFailedFutureMigration, outOfOrder);
                dbMigrator.setCallbacks(callbacks);
                try {
                    return dbMigrator.migrate();
                } finally {
                    if (schemaChange) {
                        dbSupportUserObjects.setCurrentSchema(originalSchemaUserObjects);
                    }

            		for (FlywayCallback callback: getCallbacks()) {
            			callback.afterMigrate(connectionUserObjects);
            		}
                }
            }
        });
    }

