package org.apache.hadoop.hive.ql.stats.jdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.exec.mr.MapRedTask;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.stats.StatsAggregator;

public class JDBCStatsAggregator implements StatsAggregator {
  private Connection conn;
  private String connectionString;
  private Configuration hiveconf;
  private Task<?> sourceTask;
  private final Map<String, PreparedStatement> columnMapping;
  private final Log LOG = LogFactory.getLog(this.getClass().getName());
  private int timeout = 30;
  private final String comment = "Hive stats aggregation: " + this.getClass().getName();
  private int maxRetries;
  private int waitWindow;
  private final Random r;
  public JDBCStatsAggregator() {
    columnMapping = new HashMap<String, PreparedStatement>();
    r = new Random();
  }
  @Override public boolean connect(Configuration hiveconf, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_left_2d4709b\ql\src\java\org\apache\hadoop\hive\ql\stats\jdbc\JDBCStatsAggregator.java
Task<?> sourceTask
=======
Task sourceTask
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_right_c164a97\ql\src\java\org\apache\hadoop\hive\ql\stats\jdbc\JDBCStatsAggregator.java
) {
    this.hiveconf = hiveconf;
    timeout = HiveConf.getIntVar(hiveconf, HiveConf.ConfVars.HIVE_STATS_JDBC_TIMEOUT);
    connectionString = HiveConf.getVar(hiveconf, HiveConf.ConfVars.HIVESTATSDBCONNECTIONSTRING);
    String driver = HiveConf.getVar(hiveconf, HiveConf.ConfVars.HIVESTATSJDBCDRIVER);
    maxRetries = HiveConf.getIntVar(hiveconf, HiveConf.ConfVars.HIVE_STATS_RETRIES_MAX);
    waitWindow = HiveConf.getIntVar(hiveconf, HiveConf.ConfVars.HIVE_STATS_RETRIES_WAIT);
    try {
      Class.forName(driver).newInstance();
    }
    catch (Exception e) {
      LOG.error("Error during instantiating JDBC driver " + driver + ". ", e);
      return false;
    }
    DriverManager.setLoginTimeout(timeout);
    Utilities.SQLCommand<Void> setQueryTimeout = new Utilities.SQLCommand<Void>() {
        @Override public Void run(PreparedStatement stmt) throws SQLException {
          stmt.setQueryTimeout(timeout);
          return null;
        }
    };
    for (int failures = 0; true; failures++) {
      try {
        conn = Utilities.connectWithRetry(connectionString, waitWindow, maxRetries);
        for (String statType : JDBCStatsUtils.getSupportedStatistics()) {
          PreparedStatement selStmt = Utilities.prepareWithRetry(conn, JDBCStatsUtils.getSelectAggr(statType, comment), waitWindow, maxRetries);
          columnMapping.put(statType, selStmt);
          Utilities.executeWithRetry(setQueryTimeout, selStmt, waitWindow, failures);
        }
        return true;
      }
      catch (SQLRecoverableException e) {
        if (failures > maxRetries) {
          LOG.error("Error during JDBC connection and preparing statement: " + e);
          return false;
        }
        long waitTime = Utilities.getRandomWaitTime(waitWindow, failures, r);
        try {
          Thread.sleep(waitTime);
        }
        catch (InterruptedException e1) {
        }
      }
      catch (SQLException e) {
        return false;
      }
    }
  }
  @Override public String aggregateStats(String fileID, String statType) {
    if (!JDBCStatsUtils.isValidStatistic(statType)) {
      LOG.warn("Invalid statistic: " + statType + ", supported stats: " + JDBCStatsUtils.getSupportedStatistics());
      return null;
    }
    Utilities.SQLCommand<ResultSet> execQuery = new Utilities.SQLCommand<ResultSet>() {
        @Override public ResultSet run(PreparedStatement stmt) throws SQLException {
          return stmt.executeQuery();
        }
    };
    String keyPrefix = Utilities.escapeSqlLike(fileID) + "%";
    for (int failures = 0; true; failures++) {
      try {
        long retval = 0;
        PreparedStatement selStmt = columnMapping.get(statType);
        selStmt.setString(1, keyPrefix);
        selStmt.setString(2, Character.toString(Utilities.sqlEscapeChar));
        ResultSet result = Utilities.executeWithRetry(execQuery, selStmt, waitWindow, maxRetries);
        if (result.next()) {
          retval = result.getLong(1);
        }
        else {
          LOG.warn("Warning. Nothing published. Nothing aggregated.");
          return null;
        }
        return Long.toString(retval);
      }
      catch (SQLRecoverableException e) {
        if (failures >= maxRetries) {
          return null;
        }
        closeConnection();
        long waitTime = Utilities.getRandomWaitTime(waitWindow, failures, r);
        try {
          Thread.sleep(waitTime);
        }
        catch (InterruptedException iex) {
        }
        if (!connect(hiveconf, sourceTask)) {
          LOG.error("Error during publishing aggregation. " + e);
          return null;
        }
      }
      catch (SQLException e) {
        LOG.error("Error during publishing aggregation. " + e);
        return null;
      }
    }
  }
  @Override public boolean closeConnection() {
    if (conn == null) {
      return true;
    }
    try {
      conn.close();
      if (HiveConf.getVar(hiveconf, HiveConf.ConfVars.HIVESTATSDBCLASS).equalsIgnoreCase("jdbc:derby")) {
        try {
          DriverManager.getConnection(connectionString + ";shutdown=true");
        }
        catch (Exception e) {
        }
      }
      return true;
    }
    catch (SQLException e) {
      LOG.error("Error during JDBC termination. " + e);
      return false;
    }
  }
  @Override public boolean cleanUp(String rowID) {
    Utilities.SQLCommand<Void> execUpdate = new Utilities.SQLCommand<Void>() {
        @Override public Void run(PreparedStatement stmt) throws SQLException {
          stmt.executeUpdate();
          return null;
        }
    };
    try {
      String keyPrefix = Utilities.escapeSqlLike(rowID) + "%";
      PreparedStatement delStmt = Utilities.prepareWithRetry(conn, JDBCStatsUtils.getDeleteAggr(rowID, comment), waitWindow, maxRetries);
      delStmt.setString(1, keyPrefix);
      delStmt.setString(2, Character.toString(Utilities.sqlEscapeChar));
      for (int failures = 0; true; failures++) {
        try {
          Utilities.executeWithRetry(execUpdate, delStmt, waitWindow, maxRetries);
          return true;
        }
        catch (SQLRecoverableException e) {
          if (failures >= maxRetries) {
            LOG.error("Error during clean-up after " + maxRetries + " retries. " + e);
            return false;
          }
          closeConnection();
          long waitTime = Utilities.getRandomWaitTime(waitWindow, failures, r);
          try {
            Thread.sleep(waitTime);
          }
          catch (InterruptedException iex) {
          }
          if (!connect(hiveconf, sourceTask)) {
            LOG.error("Error during clean-up. " + e);
            return false;
          }
        }
        catch (SQLException e) {
          LOG.error("Error during clean-up. " + e);
          return false;
        }
      }
    }
    catch (SQLException e) {
      LOG.error("Error during publishing aggregation. " + e);
      return false;
    }
  }
}

