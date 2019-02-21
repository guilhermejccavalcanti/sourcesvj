package org.apache.hadoop.hive.ql.stats;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.exec.mr.ExecDriver;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.exec.mr.MapRedTask;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;

public class CounterStatsAggregator implements StatsAggregator {
  private static final Log LOG = LogFactory.getLog(CounterStatsAggregator.class.getName());
  private Counters counters;
  private JobClient jc;
  @Override public boolean connect(Configuration hconf, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_left_2d4709b\ql\src\java\org\apache\hadoop\hive\ql\stats\CounterStatsAggregator.java
Task<?> sourceTask
=======
Task sourceTask
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_right_c164a97\ql\src\java\org\apache\hadoop\hive\ql\stats\CounterStatsAggregator.java
) {
    try {
      jc = new JobClient(toJobConf(hconf));
      RunningJob job = jc.getJob(((MapRedTask)sourceTask).getJobID());
      if (job != null) {
        counters = job.getCounters();
      }
    }
    catch (Exception e) {
      LOG.error("Failed to get Job instance for " + sourceTask.getJobID(), e);
    }
    return counters != null;
  }
  private JobConf toJobConf(Configuration hconf) {
    return hconf instanceof JobConf ? (JobConf)hconf : new JobConf(hconf, ExecDriver.class);
  }
  @Override public String aggregateStats(String counterGrpName, String statType) {
    return String.valueOf(counters.getGroup(counterGrpName).getCounter(statType));
  }
  @Override public boolean closeConnection() {
    try {
      jc.close();
    }
    catch (IOException e) {
      LOG.error("Error closing job client for stats aggregator.", e);
    }
    return true;
  }
  @Override public boolean cleanUp(String keyPrefix) {
    return true;
  }
}

