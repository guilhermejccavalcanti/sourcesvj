package org.apache.hadoop.hive.ql.stats;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.session.SessionState;

public class KeyVerifyingStatsAggregator implements StatsAggregator {
  public boolean connect(Configuration hconf, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_left_2d4709b\itests\util\src\main\java\org\apache\hadoop\hive\ql\stats\KeyVerifyingStatsAggregator.java
Task<?> sourceTask
=======
Task sourceTask
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_right_c164a97\itests\util\src\main\java\org\apache\hadoop\hive\ql\stats\KeyVerifyingStatsAggregator.java
) {
    return true;
  }
  public String aggregateStats(String keyPrefix, String statType) {
    SessionState ss = SessionState.get();
    ss.out.println("Stats prefix is hashed: " + new Boolean(keyPrefix.length() <= 17));
    return null;
  }
  public boolean closeConnection() {
    return true;
  }
  public boolean cleanUp(String keyPrefix) {
    return true;
  }
}

