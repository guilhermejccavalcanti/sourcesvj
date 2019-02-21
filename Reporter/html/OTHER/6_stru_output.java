package org.flywaydb.core;
import org.flywaydb.core.api.FlywayCallback;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.command.DbClean;
import org.flywaydb.core.command.DbInit;
import org.flywaydb.core.command.DbMigrate;
import org.flywaydb.core.command.DbSchemas;
import org.flywaydb.core.command.DbRepair;
import org.flywaydb.core.command.DbValidate;
import org.flywaydb.core.command.DbSchemas;
import org.flywaydb.core.dbsupport.DbSupport;
import org.flywaydb.core.command.DbValidate;
import org.flywaydb.core.dbsupport.DbSupportFactory;
import org.flywaydb.core.dbsupport.DbSupport;
import org.flywaydb.core.dbsupport.Schema;
import org.flywaydb.core.dbsupport.DbSupportFactory;
import org.flywaydb.core.info.MigrationInfoServiceImpl;
import org.flywaydb.core.dbsupport.Schema;
import org.flywaydb.core.metadatatable.MetaDataTable;
import org.flywaydb.core.info.MigrationInfoServiceImpl;
import org.flywaydb.core.metadatatable.MetaDataTableImpl;
import org.flywaydb.core.metadatatable.MetaDataTable;
import org.flywaydb.core.resolver.CompositeMigrationResolver;
import org.flywaydb.core.metadatatable.MetaDataTableImpl;
import org.flywaydb.core.resolver.MigrationResolver;
import org.flywaydb.core.resolver.CompositeMigrationResolver;
import org.flywaydb.core.util.Locations;
import org.flywaydb.core.util.StringUtils;
import org.flywaydb.core.util.PlaceholderReplacer;
import org.flywaydb.core.util.jdbc.DriverDataSource;
import org.flywaydb.core.util.StringUtils;
import org.flywaydb.core.util.jdbc.JdbcUtils;
import org.flywaydb.core.util.jdbc.DriverDataSource;
import org.flywaydb.core.util.logging.Log;
import org.flywaydb.core.util.jdbc.JdbcUtils;
import org.flywaydb.core.util.logging.LogFactory;
import org.flywaydb.core.util.logging.Log;
import javax.sql.DataSource;
import org.flywaydb.core.util.logging.LogFactory;
import java.sql.Connection;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.sql.Connection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.Properties;

public class Flyway {
  private static final Log LOG = LogFactory.getLog(Flyway.class);
  private static final String PLACEHOLDERS_PROPERTY_PREFIX = "flyway.placeholders.";
  private Locations locations = new Locations("db/migration");
  private String encoding = "UTF-8";
  private String[] schemaNames = new String[0];
  private String table = "schema_version";
  private MigrationVersion target = MigrationVersion.LATEST;
  private Map<String, String> placeholders = new HashMap<String, String>();
  private String placeholderPrefix = "${";
  private String placeholderSuffix = "}";
  private String sqlMigrationPrefix = "V";
  private String sqlMigrationSuffix = ".sql";
  private boolean ignoreFailedFutureMigration;
  private boolean validateOnMigrate;
  private boolean cleanOnValidationError;
  private MigrationVersion initVersion = MigrationVersion.fromVersion("1");
  private String initDescription = "<< Flyway Init >>";
  private boolean initOnMigrate;
  private boolean outOfOrder;
  private List<FlywayCallback> callbacks = new ArrayList<FlywayCallback>();
  private MigrationResolver[] customMigrationResolvers = new MigrationResolver[0];
  private DataSource dataSource;
  private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  private boolean dbConnectionInfoPrinted;
  public Flyway() {
  }
  public String[] getLocations() {
    String[] result = new String[locations.getLocations().size()];
    for (int i = 0; i < locations.getLocations().size(); i++) {
      result[i] = locations.getLocations().get(i).toString();
    }
    return result;
  }
  public String getEncoding() {
    return encoding;
  }
  public String[] getSchemas() {
    return schemaNames;
  }
  public String getTable() {
    return table;
  }
  public MigrationVersion getTarget() {
    return target;
  }
  public Map<String, String> getPlaceholders() {
    return placeholders;
  }
  public String getPlaceholderPrefix() {
    return placeholderPrefix;
  }
  public String getPlaceholderSuffix() {
    return placeholderSuffix;
  }
  public String getSqlMigrationPrefix() {
    return sqlMigrationPrefix;
  }
  public String getSqlMigrationSuffix() {
    return sqlMigrationSuffix;
  }
  public boolean isIgnoreFailedFutureMigration() {
    return ignoreFailedFutureMigration;
  }
  public boolean isValidateOnMigrate() {
    return validateOnMigrate;
  }
  public boolean isCleanOnValidationError() {
    return cleanOnValidationError;
  }
  public MigrationVersion getInitVersion() {
    return initVersion;
  }
  public String getInitDescription() {
    return initDescription;
  }
  public boolean isInitOnMigrate() {
    return initOnMigrate;
  }
  public boolean isOutOfOrder() {
    return outOfOrder;
  }
  public MigrationResolver[] getCustomMigrationResolvers() {
    return customMigrationResolvers;
  }
  public DataSource getDataSource() {
    return dataSource;
  }
  public ClassLoader getClassLoader() {
    return classLoader;
  }
  public void setIgnoreFailedFutureMigration(boolean ignoreFailedFutureMigration) {
    this.ignoreFailedFutureMigration = ignoreFailedFutureMigration;
  }
  public void setValidateOnMigrate(boolean validateOnMigrate) {
    this.validateOnMigrate = validateOnMigrate;
  }
  public void setCleanOnValidationError(boolean cleanOnValidationError) {
    this.cleanOnValidationError = cleanOnValidationError;
  }
  public void setLocations(String ... locations) {
    this.locations = new Locations(locations);
  }
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }
  public void setSchemas(String ... schemas) {
    this.schemaNames = schemas;
  }
  public void setTable(String table) {
    this.table = table;
  }
  public void setTarget(MigrationVersion target) {
    this.target = target;
  }
  public void setTarget(String target) {
    this.target = MigrationVersion.fromVersion(target);
  }
  public void setPlaceholders(Map<String, String> placeholders) {
    this.placeholders = placeholders;
  }
  public void setPlaceholderPrefix(String placeholderPrefix) {
    this.placeholderPrefix = placeholderPrefix;
  }
  public void setPlaceholderSuffix(String placeholderSuffix) {
    this.placeholderSuffix = placeholderSuffix;
  }
  public void setSqlMigrationPrefix(String sqlMigrationPrefix) {
    this.sqlMigrationPrefix = sqlMigrationPrefix;
  }
  public void setSqlMigrationSuffix(String sqlMigrationSuffix) {
    this.sqlMigrationSuffix = sqlMigrationSuffix;
  }
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  public void setDataSource(String url, String user, String password, String ... initSqls) {
    this.dataSource = new DriverDataSource(classLoader, null, url, user, password, initSqls);
  }
  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }
  public void setInitVersion(MigrationVersion initVersion) {
    this.initVersion = initVersion;
  }
  public void setInitVersion(String initVersion) {
    this.initVersion = MigrationVersion.fromVersion(initVersion);
  }
  public void setInitDescription(String initDescription) {
    this.initDescription = initDescription;
  }
  public void setInitOnMigrate(boolean initOnMigrate) {
    this.initOnMigrate = initOnMigrate;
  }
  public void setOutOfOrder(boolean outOfOrder) {
    this.outOfOrder = outOfOrder;
  }
  public List<FlywayCallback> getCallbacks() {
    return callbacks;
  }
  public void setCustomMigrationResolvers(MigrationResolver ... customMigrationResolvers) {
    this.customMigrationResolvers = customMigrationResolvers;
  }
  public void setCallbacks(List<FlywayCallback> callbacks) {
    this.callbacks = callbacks;
  }
  public int migrate() throws FlywayException {
    return execute(new Command<Integer>() {
        public Integer execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
          for (FlywayCallback callback : getCallbacks()) {
            callback.beforeMigrate(connectionUserObjects);
          }
          MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
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
            }
            else {
              if (nonEmptySchemas.size() == 1) {
                Schema schema = nonEmptySchemas.get(0);
                if (schema.allTables().length != 1 || !schema.getTable(table).exists()) {
                  throw new FlywayException("Found non-empty schema " + schema + " without metadata table! Use init()" + " or set initOnMigrate to true to initialize the metadata table.");
                }
              }
              else {
                throw new FlywayException("Found non-empty schemas " + StringUtils.collectionToCommaDelimitedString(nonEmptySchemas) + " without metadata table! Use init()" + " or set initOnMigrate to true to initialize the metadata table.");
              }
            }
          }
          DbSupport dbSupportUserObjects = DbSupportFactory.createDbSupport(connectionUserObjects, false);
          Schema originalSchemaUserObjects = dbSupportUserObjects.getCurrentSchema();
          boolean schemaChange = !schemas[0].equals(originalSchemaUserObjects);
          if (schemaChange) {
            dbSupportUserObjects.setCurrentSchema(schemas[0]);
          }
          DbMigrate dbMigrator = new DbMigrate(connectionMetaDataTable, connectionUserObjects, dbSupport, metaDataTable, schemas[0], migrationResolver, target, ignoreFailedFutureMigration, outOfOrder);
          dbMigrator.setCallbacks(callbacks);
          try {
            return dbMigrator.migrate();
          }
          finally {
            if (schemaChange) {
              dbSupportUserObjects.setCurrentSchema(originalSchemaUserObjects);
            }
            for (FlywayCallback callback : getCallbacks()) {
              callback.afterMigrate(connectionUserObjects);
            }
          }
        }
    });
  }
  public void validate() throws FlywayException {
    execute(new Command<Void>() {
        public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
          for (FlywayCallback callback : getCallbacks()) {
            callback.beforeValidate(connectionUserObjects);
          }
          MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
          MigrationResolver migrationResolver = createMigrationResolver(dbSupport);
          doValidate(connectionMetaDataTable, migrationResolver, metaDataTable, schemas);
          for (FlywayCallback callback : getCallbacks()) {
            callback.afterValidate(connectionUserObjects);
          }
          return null;
        }
    });
  }
  private void doValidate(Connection connectionMetaDataTable, MigrationResolver migrationResolver, MetaDataTable metaDataTable, Schema[] schemas) {
    String validationError = new DbValidate(connectionMetaDataTable, metaDataTable, migrationResolver, target, outOfOrder).validate();
    if (validationError != null) {
      if (cleanOnValidationError) {
        new DbClean(connectionMetaDataTable, metaDataTable, schemas).clean();
      }
      else {
        throw new FlywayException("Validate failed. Found differences between applied migrations and available migrations: " + validationError);
      }
    }
  }
  public void clean() {
    execute(new Command<Void>() {
        public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
          for (FlywayCallback callback : getCallbacks()) {
            callback.beforeClean(connectionUserObjects);
          }
          MetaDataTableImpl metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
          new DbClean(connectionMetaDataTable, metaDataTable, schemas).clean();
          for (FlywayCallback callback : getCallbacks()) {
            callback.afterClean(connectionUserObjects);
          }
          return null;
        }
    });
  }
  public MigrationInfoService info() {
    return execute(new Command<MigrationInfoService>() {
        public MigrationInfoService execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
          for (FlywayCallback callback : getCallbacks()) {
            callback.beforeInfo(connectionUserObjects);
          }
          MigrationResolver migrationResolver = createMigrationResolver(dbSupport);
          MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
          MigrationInfoServiceImpl migrationInfoService = new MigrationInfoServiceImpl(migrationResolver, metaDataTable, target, outOfOrder);
          migrationInfoService.refresh();
          for (FlywayCallback callback : getCallbacks()) {
            callback.afterInfo(connectionUserObjects);
          }
          return migrationInfoService;
        }
    });
  }
  public void init() throws FlywayException {
    execute(new Command<Void>() {
        public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
          for (FlywayCallback callback : getCallbacks()) {
            callback.beforeInit(connectionUserObjects);
          }
          MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
          new DbSchemas(connectionMetaDataTable, schemas, metaDataTable).create();
          new DbInit(connectionMetaDataTable, metaDataTable, initVersion, initDescription).init();
          for (FlywayCallback callback : getCallbacks()) {
            callback.afterInit(connectionUserObjects);
          }
          return null;
        }
    });
  }
  public void repair() throws FlywayException {
    execute(new Command<Void>() {
        public Void execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\flyway\revisions\rev_58400ef_8c800df\rev_left_58400ef\flyway-core\src\main\java\org\flywaydb\core\Flyway.java
for (FlywayCallback callback : getCallbacks()) {
            callback.beforeRepair(connectionUserObjects);
          }
=======
MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(table), classLoader);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\flyway\revisions\rev_58400ef_8c800df\rev_right_8c800df\flyway-core\src\main\java\org\flywaydb\core\Flyway.java

          new DbRepair(connectionMetaDataTable, metaDataTable).repair();
          for (FlywayCallback callback : getCallbacks()) {
            callback.afterRepair(connectionUserObjects);
          }
          return null;
        }
    });
  }
  private MigrationResolver createMigrationResolver(DbSupport dbSupport) {
    PlaceholderReplacer placeholderReplacer = new PlaceholderReplacer(placeholders, placeholderPrefix, placeholderSuffix);
    return new CompositeMigrationResolver(dbSupport, classLoader, locations, encoding, sqlMigrationPrefix, sqlMigrationSuffix, placeholderReplacer, customMigrationResolvers);
  }
  public void configure(Properties properties) {
    String driverProp = properties.getProperty("flyway.driver");
    String urlProp = properties.getProperty("flyway.url");
    String userProp = properties.getProperty("flyway.user");
    String passwordProp = properties.getProperty("flyway.password");
    if (StringUtils.hasText(urlProp)) {
      setDataSource(new DriverDataSource(classLoader, driverProp, urlProp, userProp, passwordProp));
    }
    else 
      if (!StringUtils.hasText(urlProp) && (StringUtils.hasText(driverProp) || StringUtils.hasText(userProp) || StringUtils.hasText(passwordProp))) {
        LOG.warn("Discarding INCOMPLETE dataSource configuration! flyway.url must be set.");
      }
    String locationsProp = properties.getProperty("flyway.locations");
    if (locationsProp != null) {
      setLocations(StringUtils.tokenizeToStringArray(locationsProp, ","));
    }
    String placeholderPrefixProp = properties.getProperty("flyway.placeholderPrefix");
    if (placeholderPrefixProp != null) {
      setPlaceholderPrefix(placeholderPrefixProp);
    }
    String placeholderSuffixProp = properties.getProperty("flyway.placeholderSuffix");
    if (placeholderSuffixProp != null) {
      setPlaceholderSuffix(placeholderSuffixProp);
    }
    String sqlMigrationPrefixProp = properties.getProperty("flyway.sqlMigrationPrefix");
    if (sqlMigrationPrefixProp != null) {
      setSqlMigrationPrefix(sqlMigrationPrefixProp);
    }
    String sqlMigrationSuffixProp = properties.getProperty("flyway.sqlMigrationSuffix");
    if (sqlMigrationSuffixProp != null) {
      setSqlMigrationSuffix(sqlMigrationSuffixProp);
    }
    String encodingProp = properties.getProperty("flyway.encoding");
    if (encodingProp != null) {
      setEncoding(encodingProp);
    }
    String schemasProp = properties.getProperty("flyway.schemas");
    if (schemasProp != null) {
      setSchemas(StringUtils.tokenizeToStringArray(schemasProp, ","));
    }
    String tableProp = properties.getProperty("flyway.table");
    if (tableProp != null) {
      setTable(tableProp);
    }
    String cleanOnValidationErrorProp = properties.getProperty("flyway.cleanOnValidationError");
    if (cleanOnValidationErrorProp != null) {
      setCleanOnValidationError(Boolean.parseBoolean(cleanOnValidationErrorProp));
    }
    String validateOnMigrateProp = properties.getProperty("flyway.validateOnMigrate");
    if (validateOnMigrateProp != null) {
      setValidateOnMigrate(Boolean.parseBoolean(validateOnMigrateProp));
    }
    String initVersionProp = properties.getProperty("flyway.initVersion");
    if (initVersionProp != null) {
      setInitVersion(MigrationVersion.fromVersion(initVersionProp));
    }
    String initDescriptionProp = properties.getProperty("flyway.initDescription");
    if (initDescriptionProp != null) {
      setInitDescription(initDescriptionProp);
    }
    String initOnMigrateProp = properties.getProperty("flyway.initOnMigrate");
    if (initOnMigrateProp != null) {
      setInitOnMigrate(Boolean.parseBoolean(initOnMigrateProp));
    }
    String ignoreFailedFutureMigrationProp = properties.getProperty("flyway.ignoreFailedFutureMigration");
    if (ignoreFailedFutureMigrationProp != null) {
      setIgnoreFailedFutureMigration(Boolean.parseBoolean(ignoreFailedFutureMigrationProp));
    }
    String targetProp = properties.getProperty("flyway.target");
    if (targetProp != null) {
      setTarget(MigrationVersion.fromVersion(targetProp));
    }
    String outOfOrderProp = properties.getProperty("flyway.outOfOrder");
    if (outOfOrderProp != null) {
      setOutOfOrder(Boolean.parseBoolean(outOfOrderProp));
    }
    String callbacksProp = properties.getProperty("flyway.callbacks");
    if (callbacksProp != null) {
      String[] callbackClasses = callbacksProp.split(",");
      for (String callbackClass : callbackClasses) {
        try {
          Class<?> cbClazz = Class.forName(callbackClass.trim());
          Object obj = cbClazz.newInstance();
          if (!(obj instanceof FlywayCallback)) {
            throw new FlywayException("The property \'flyway.callbacks\' contained a fully qualified classname that does not implement FlywayCallback.  Please check your property classes");
          }
          callbacks.add((FlywayCallback)obj);
        }
        catch (ClassNotFoundException e) {
          throw new FlywayException("The property \'flyway.callbacks\' contain an invalid classname.", e);
        }
        catch (InstantiationException e) {
          throw new FlywayException("Property \'flyway.callbacks\' class instantiation problem.", e);
        }
        catch (IllegalAccessException e) {
          throw new FlywayException("Property \'flyway.callbacks\' illegal access problem.", e);
        }
      }
    }
    Map<String, String> placeholdersFromProps = new HashMap<String, String>();
    for (Object property : properties.keySet()) {
      String propertyName = (String)property;
      if (propertyName.startsWith(PLACEHOLDERS_PROPERTY_PREFIX) && propertyName.length() > PLACEHOLDERS_PROPERTY_PREFIX.length()) {
        String placeholderName = propertyName.substring(PLACEHOLDERS_PROPERTY_PREFIX.length());
        String placeholderValue = properties.getProperty(propertyName);
        placeholdersFromProps.put(placeholderName, placeholderValue);
      }
    }
    setPlaceholders(placeholdersFromProps);
  }
   <T extends java.lang.Object> T execute(Command<T> command) {
    T result;
    Connection connectionMetaDataTable = null;
    Connection connectionUserObjects = null;
    try {
      if (dataSource == null) {
        throw new FlywayException("DataSource not set! Check your configuration!");
      }
      connectionMetaDataTable = JdbcUtils.openConnection(dataSource);
      connectionUserObjects = JdbcUtils.openConnection(dataSource);
      DbSupport dbSupport = DbSupportFactory.createDbSupport(connectionMetaDataTable, !dbConnectionInfoPrinted);
      dbConnectionInfoPrinted = true;
      LOG.debug("DDL Transactions Supported: " + dbSupport.supportsDdlTransactions());
      if (schemaNames.length == 0) {
        Schema currentSchema = dbSupport.getCurrentSchema();
        if (currentSchema == null) {
          throw new FlywayException("Unable to determine schema for the metadata table." + " Set a default schema for the connection or specify one using the schemas property!");
        }
        setSchemas(currentSchema.getName());
      }
      if (schemaNames.length == 1) {
        LOG.debug("Schema: " + schemaNames[0]);
      }
      else {
        LOG.debug("Schemas: " + StringUtils.arrayToCommaDelimitedString(schemaNames));
      }
      Schema[] schemas = new Schema[schemaNames.length];
      for (int i = 0; i < schemaNames.length; i++) {
        schemas[i] = dbSupport.getSchema(schemaNames[i]);
      }
      result = command.execute(connectionMetaDataTable, connectionUserObjects, dbSupport, schemas);
    }
    finally {
      JdbcUtils.closeConnection(connectionUserObjects);
      JdbcUtils.closeConnection(connectionMetaDataTable);
    }
    return result;
  }
  interface Command<T extends java.lang.Object> {
    T execute(Connection connectionMetaDataTable, Connection connectionUserObjects, DbSupport dbSupport, Schema[] schemas);
  }
}

