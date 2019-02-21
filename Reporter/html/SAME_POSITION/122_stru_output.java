package org.apache.hadoop.hive.ql.stats;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.exec.mr.MapRedTask;

public interface StatsAggregator {
  public boolean connect(Configuration hconf, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_left_2d4709b\ql\src\java\org\apache\hadoop\hive\ql\stats\StatsAggregator.java
Task<?> sourceTask
=======
Task sourceTask
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_right_c164a97\ql\src\java\org\apache\hadoop\hive\ql\stats\StatsAggregator.java
);
  public String aggregateStats(String keyPrefix, String statType);
  public boolean closeConnection();
  public boolean cleanUp(String keyPrefix);
}

