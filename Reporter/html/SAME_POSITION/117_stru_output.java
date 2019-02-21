package org.apache.hadoop.hive.hbase;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.stats.StatsAggregator;

public class HBaseStatsAggregator implements StatsAggregator {
  private HTable htable;
  private final Log LOG = LogFactory.getLog(this.getClass().getName());
  public boolean connect(Configuration hiveconf, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_left_2d4709b\hbase-handler\src\java\org\apache\hadoop\hive\hbase\HBaseStatsAggregator.java
Task<?> sourceTask
=======
Task sourceTask
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_right_c164a97\hbase-handler\src\java\org\apache\hadoop\hive\hbase\HBaseStatsAggregator.java
) {
    try {
      htable = new HTable(HBaseConfiguration.create(hiveconf), HBaseStatsSetupConstants.PART_STAT_TABLE_NAME);
      return true;
    }
    catch (IOException e) {
      LOG.error("Error during HBase connection. ", e);
      return false;
    }
  }
  public String aggregateStats(String rowID, String key) {
    byte[] family, column;
    if (!HBaseStatsUtils.isValidStatistic(key)) {
      LOG.warn("Warning. Invalid statistic: " + key + ", supported stats: " + HBaseStatsUtils.getSupportedStatistics());
      return null;
    }
    family = HBaseStatsUtils.getFamilyName();
    column = HBaseStatsUtils.getColumnName(key);
    try {
      long retValue = 0;
      Scan scan = new Scan();
      scan.addColumn(family, column);
      PrefixFilter filter = new PrefixFilter(Bytes.toBytes(rowID));
      scan.setFilter(filter);
      ResultScanner scanner = htable.getScanner(scan);
      for (Result result : scanner) {
        retValue += Long.parseLong(Bytes.toString(result.getValue(family, column)));
      }
      return Long.toString(retValue);
    }
    catch (IOException e) {
      LOG.error("Error during publishing aggregation. ", e);
      return null;
    }
  }
  public boolean closeConnection() {
    return true;
  }
  public boolean cleanUp(String rowID) {
    try {
      Scan scan = new Scan();
      PrefixFilter filter = new PrefixFilter(Bytes.toBytes(rowID));
      scan.setFilter(filter);
      ResultScanner scanner = htable.getScanner(scan);
      ArrayList<Delete> toDelete = new ArrayList<Delete>();
      for (Result result : scanner) {
        Delete delete = new Delete(result.getRow());
        toDelete.add(delete);
      }
      htable.delete(toDelete);
      return true;
    }
    catch (IOException e) {
      LOG.error("Error during publishing aggregation. ", e);
      return false;
    }
  }
}

