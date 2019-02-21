public void init() throws FlywayException {
        execute(new Command<Void>() {
            public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
<<<<<<< MINE
        		for (FlywayCallback callback: getCallbacks()) {
        			callback.beforeInit(connectionUserObjects);
        		}

        		MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table));
=======
                MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
>>>>>>> YOURS
                new DbSchemas(connectionMetaDataTable, schemas, metaDataTable).create();
                new DbInit(connectionMetaDataTable, metaDataTable, initVersion, initDescription).init();

        		for (FlywayCallback callback: getCallbacks()) {
        			callback.afterInit(connectionUserObjects);
        		}

                return null;
            }
        });
    }

