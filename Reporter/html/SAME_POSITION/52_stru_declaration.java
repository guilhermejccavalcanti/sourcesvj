        public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\flyway\revisions\rev_58400ef_8c800df\rev_left_58400ef\flyway-core\src\main\java\org\flywaydb\core\Flyway.java
for (FlywayCallback callback : getCallbacks()) {
            callback.beforeRepair(connectionUserObjects);
          }
=======
MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\flyway\revisions\rev_58400ef_8c800df\rev_right_8c800df\flyway-core\src\main\java\org\flywaydb\core\Flyway.java

          new MetaDataTableImpl(dbSupport, schemas[0].getTable(table)).repair();
          for (FlywayCallback callback : getCallbacks()) {
            callback.afterRepair(connectionUserObjects);
          }
          return null;
        }


