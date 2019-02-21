package org.apache.hadoop.hive.ql.plan;
import java.io.Serializable;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.parse.BaseSemanticAnalyzer.tableSpec;

@Explain(displayName = "Stats-Aggr Operator") public class StatsWork implements Serializable {
  private static final long serialVersionUID = 1L;
  private tableSpec tableSpecs;
  private LoadTableDesc loadTableDesc;
  private LoadFileDesc loadFileDesc;
  private String aggKey;
  private boolean statsReliable;
  private boolean clearAggregatorStats = false;
  private boolean noStatsAggregator = false;
  private boolean isNoScanAnalyzeCommand = false;
  private boolean isPartialScanAnalyzeCommand = false;
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_left_2d4709b\ql\src\java\org\apache\hadoop\hive\ql\plan\StatsWork.java
private transient Task<?> sourceTask;
=======
private transient Task sourceTask;
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_right_c164a97\ql\src\java\org\apache\hadoop\hive\ql\plan\StatsWork.java

  public StatsWork() {
  }
  public StatsWork(tableSpec tableSpecs) {
    this.tableSpecs = tableSpecs;
  }
  public StatsWork(LoadTableDesc loadTableDesc) {
    this.loadTableDesc = loadTableDesc;
  }
  public StatsWork(LoadFileDesc loadFileDesc) {
    this.loadFileDesc = loadFileDesc;
  }
  public StatsWork(boolean statsReliable) {
    this.statsReliable = statsReliable;
  }
  public tableSpec getTableSpecs() {
    return tableSpecs;
  }
  public LoadTableDesc getLoadTableDesc() {
    return loadTableDesc;
  }
  public LoadFileDesc getLoadFileDesc() {
    return loadFileDesc;
  }
  public void setAggKey(String aggK) {
    aggKey = aggK;
  }
  @Explain(displayName = "Stats Aggregation Key Prefix", normalExplain = false) public String getAggKey() {
    return aggKey;
  }
  public boolean getNoStatsAggregator() {
    return noStatsAggregator;
  }
  public void setNoStatsAggregator(boolean noStatsAggregator) {
    this.noStatsAggregator = noStatsAggregator;
  }
  public boolean isStatsReliable() {
    return statsReliable;
  }
  public void setStatsReliable(boolean statsReliable) {
    this.statsReliable = statsReliable;
  }
  public boolean isClearAggregatorStats() {
    return clearAggregatorStats;
  }
  public void setClearAggregatorStats(boolean clearAggregatorStats) {
    this.clearAggregatorStats = clearAggregatorStats;
  }
  public boolean isNoScanAnalyzeCommand() {
    return isNoScanAnalyzeCommand;
  }
  public void setNoScanAnalyzeCommand(boolean isNoScanAnalyzeCommand) {
    this.isNoScanAnalyzeCommand = isNoScanAnalyzeCommand;
  }
  public boolean isPartialScanAnalyzeCommand() {
    return isPartialScanAnalyzeCommand;
  }
  public void setPartialScanAnalyzeCommand(boolean isPartialScanAnalyzeCommand) {
    this.isPartialScanAnalyzeCommand = isPartialScanAnalyzeCommand;
  }
  public Task<?> getSourceTask() {
    return sourceTask;
  }
  public Task getSourceTask() {
    return sourceTask;
  }
  public void setSourceTask(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_left_2d4709b\ql\src\java\org\apache\hadoop\hive\ql\plan\StatsWork.java
Task<?> sourceTask
=======
Task sourceTask
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_2d4709b_c164a97\rev_right_c164a97\ql\src\java\org\apache\hadoop\hive\ql\plan\StatsWork.java
) {
    this.sourceTask = sourceTask;
  }
}

