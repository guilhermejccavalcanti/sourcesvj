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

    dagTransformer.transform(nodes);
    JSONUtil.writeJson(sourceFile + "2", nodes);
  }


