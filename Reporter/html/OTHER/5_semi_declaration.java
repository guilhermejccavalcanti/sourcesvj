public void validate() throws FlywayException {
        execute(new Command<Void>() {
            public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
<<<<<<< MINE
        		for (FlywayCallback callback: getCallbacks()) {
        			callback.beforeValidate(connectionUserObjects);
        		}

        		MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table));
=======
                MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
>>>>>>> YOURS
                MigrationResolver migrationResolver = createMigrationResolver(dbSupport);

                doValidate(connectionMetaDataTable, migrationResolver, metaDataTable, schemas);

        		for (FlywayCallback callback: getCallbacks()) {
        			callback.afterValidate(connectionUserObjects);
        		}
                return null;
            }
        });
    }

