package com.twitter.ambrose.service;
import com.twitter.ambrose.service.impl.SugiyamaLayoutTransformer;
import com.twitter.ambrose.util.JSONUtil;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL) public class DAGNode {
  private String name;
  private String[] aliases;
  private String[] features;
  private String jobId;
  private Collection<DAGNode> successors;
  private Collection<String> successorNames;
  private String runtime;
  private Integer dagLevel;
  private Double x;
  private Double y;
  public DAGNode(String name, String[] aliases, String[] features, String runtime) {
    this.name = name;
    this.aliases = aliases;
    this.features = features;
    this.runtime = runtime;
  }
  @JsonCreator public DAGNode(@JsonProperty(value = "name") String name, @JsonProperty(value = "aliases") String[] aliases, @JsonProperty(value = "features") String[] features, @JsonProperty(value = "jobId") String jobId, @JsonProperty(value = "successorNames") Collection<String> successorNames, @JsonProperty(value = "runtime") String runtime) {
    this.name = name;
    this.aliases = aliases;
    this.features = features;
    this.jobId = jobId;
    this.successorNames = successorNames;
    this.runtime = runtime;
  }
  public String getName() {
    return name;
  }
  public String[] getAliases() {
    return aliases == null ? new String[0] : aliases;
  }
  public String[] getFeatures() {
    return features == null ? new String[0] : features;
  }
  public String getRuntime() {
    return runtime;
  }
  public String getJobId() {
    return jobId;
  }
  public void setJobId(String jobId) {
    this.jobId = jobId;
  }
  public Integer getDagLevel() {
    return dagLevel;
  }
  public void setDagLevel(Integer dagLevel) {
    this.dagLevel = dagLevel;
  }
  public Double getX() {
    return x;
  }
  public void setX(Double x) {
    this.x = x;
  }
  public Double getY() {
    return y;
  }
  public void setY(Double y) {
    this.y = y;
  }
  @JsonIgnore public synchronized Collection<DAGNode> getSuccessors() {
    return successors;
  }
  public synchronized void setSuccessors(Collection<DAGNode> successors) {
    Collection<String> successorNames = new HashSet<String>();
    if (successors != null) {
      for (DAGNode node : successors) {
        successorNames.add(node.getName());
      }
    }
    this.successors = successors;
    this.successorNames = successorNames;
  }
  public synchronized Collection<String> getSuccessorNames() {
    return successorNames;
  }
  @SuppressWarnings(value = {"unchecked", }) public static void main(String[] args) throws IOException {
    String sourceFile = "pig/src/main/resources/web/data/large-dag.json";
    String json = JSONUtil.readFile(sourceFile);
    List<DAGNode> nodes = (List<DAGNode>)JSONUtil.readJson(json, new TypeReference<List<DAGNode>>() {
    });
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambrose\revisions\rev_8000215_8ad1e53\rev_left_8000215\pig\src\main\java\com\twitter\ambrose\service\DAGNode.java
DAGTransformer dagTransformer = new SugiyamaLayoutTransformer(true);
=======
for (DAGNode node : nodes) {
      node.setRuntime("pig");
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambrose\revisions\rev_8000215_8ad1e53\rev_right_8ad1e53\pig\src\main\java\com\twitter\ambrose\service\DAGNode.java

    JSONUtil.writeJson(sourceFile + "2", nodes);
    JSONUtil.writeJson(sourceFile + "2", nodes);
  }
  private void setRuntime(String runtime) {
    this.runtime = runtime;
  }
}

