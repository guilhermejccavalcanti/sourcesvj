package org.apache.hadoop.hive.ql.stats;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.Task;

public class DummyStatsAggregator implements StatsAggregator {
  String errorMethod = null;
  public boolean connect(Configuration hconf, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_left_2d4709b\itests\util\src\main\java\org\apache\hadoop\hive\ql\stats\DummyStatsAggregator.java
Task<?> sourceTask
=======
Task sourceTask
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_right_c164a97\itests\util\src\main\java\org\apache\hadoop\hive\ql\stats\DummyStatsAggregator.java
) {
    errorMethod = hconf.get("hive.test.dummystats.aggregator", "");
    if (errorMethod.equalsIgnoreCase("connect")) {
      return false;
    }
    return true;
  }
  public String aggregateStats(String keyPrefix, String statType) {
    return null;
  }
  public boolean closeConnection() {
    if (errorMethod.equalsIgnoreCase("closeConnection")) {
      return false;
    }
    return true;
  }
  public boolean cleanUp(String keyPrefix) {
    if (errorMethod.equalsIgnoreCase("cleanUp")) {
      return false;
    }
    return true;
  }
}

