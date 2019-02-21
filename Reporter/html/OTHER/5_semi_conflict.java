<<<<<<< MINE
        		for (FlywayCallback callback: getCallbacks()) {
        			callback.beforeValidate(connectionUserObjects);
        		}

        		MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table));
||||||| BASE
                MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table));
=======
                MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
>>>>>>> YOURS

