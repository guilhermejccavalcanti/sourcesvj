public static DbSupport createDbSupport(Connection connection, boolean printInfo) {
        String databaseProductName = getDatabaseProductName(connection);

        if (printInfo) {
            LOG.info("Database: " + getJdbcUrl(connection) + " (" + databaseProductName + ")");
        }

        if (databaseProductName.startsWith("Apache Derby")) {
            return new DerbyDbSupport(connection);
        }
        if (databaseProductName.startsWith("SQLite")) {
            return new SQLiteDbSupport(connection);
        }
        if (databaseProductName.startsWith("H2")) {
            return new H2DbSupport(connection);
        }
        if (databaseProductName.contains("HSQL Database Engine")) {
            // For regular Hsql and the Google Cloud SQL local default DB.
            return new HsqlDbSupport(connection);
        }
        if (databaseProductName.startsWith("Microsoft SQL Server")) {
            return new SQLServerDbSupport(connection);
        }
        if (databaseProductName.contains("MySQL")) {
            // For regular MySQL, MariaDB and Google Cloud SQL.
            // Google Cloud SQL returns different names depending on the environment and the SDK version.
            //   ex.: Google SQL Service/MySQL
            return new MySQLDbSupport(connection);
        }
        if (databaseProductName.startsWith("Oracle")) {
            return new OracleDbSupport(connection);
        }
        if (databaseProductName.startsWith("PostgreSQL 8")) {
            // Redshift reports a databaseProductName of "PostgreSQL 8.0", and it uses the same JDBC driver,
            // but only supports a subset of features. Therefore, we need to execute a query in order to
            // distinguish it from the real PostgreSQL 8:
            RedshiftDbSupport redshift;
            if ("RedshiftJDBC".equals(getDriverName(connection))) {
                redshift = new RedshfitDbSupportViaRedshiftDriver(connection);
            } else {
                redshift = new RedshfitDbSupportViaPostgreSQLDriver(connection);
            }
            if (redshift.detect()) {
                return redshift;
            }
        }
        if (databaseProductName.startsWith("PostgreSQL")) {
            return new PostgreSQLDbSupport(connection);
        }
        if (databaseProductName.startsWith("DB2")) {
			if (getDatabaseProductVersion(connection).startsWith("DSN")){
				return new DB2zosDbSupport(connection);
			} else {
				return new DB2DbSupport(connection);
			}
        }
        if (databaseProductName.startsWith("Vertica")) {
            return new VerticaDbSupport(connection);
        }
        if (databaseProductName.contains("solidDB")) {
            // SolidDB was originally developed by a company named Solid and was sold afterwards to IBM.
            // In the meanwhile IBM also sold solidDB to Unicom Systems.
            // Therefore no vendor string in search criteria
            return new SolidDbSupport(connection);
        }
<<<<<<< MINE
=======
        if (databaseProductName.startsWith("Phoenix")) {
            return new PhoenixDbSupport(connection);
        }

		//Sybase ASE support
>>>>>>> YOURS
        if (databaseProductName.startsWith("ASE")) {
        	return new SybaseASEDbSupport(connection);
        }
        if (databaseProductName.startsWith("HDB")) {
        	return new SapHanaDbSupport(connection);
        }
        
        throw new FlywayException("Unsupported Database: " + databaseProductName);
    }

