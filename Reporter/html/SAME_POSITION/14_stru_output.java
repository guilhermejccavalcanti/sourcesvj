package com.tinkerpop.blueprints.pgm.impls.rexster.util;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.rexster.RexsterGraph;
import com.tinkerpop.blueprints.pgm.impls.rexster.RexsterTokens;
import com.tinkerpop.blueprints.pgm.impls.rexster.RexsterVertex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RexsterVertexSequence extends RexsterElementSequence<Vertex> {
  public RexsterVertexSequence(final String uri, final RexsterGraph graph) {
    super(uri, graph);
  }
  protected void fillBuffer() {
    final int bufferSize = this.graph.getBufferSize();
    final JSONObject object = RestHelper.get(this.uri + this.createSeparator() + RexsterTokens.REXSTER_OFFSET_START + RexsterTokens.EQUALS + this.start + RexsterTokens.AND + RexsterTokens.REXSTER_OFFSET_END + RexsterTokens.EQUALS + this.end);
    for (final Object vertex : (JSONArray)object.get(RexsterTokens.RESULTS)) {
      this.queue.add(new RexsterVertex((JSONObject)vertex, this.graph));
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\blueprints\revisions\rev_9050781_396f9cf\rev_left_9050781\blueprints-rexster-graph\src\main\java\com\tinkerpop\blueprints\pgm\impls\rexster\util\RexsterVertexSequence.java
this.start = this.start + this.graph.getBufferSize();
=======
if (this.queue.size() == bufferSize) {
      this.start = this.start + bufferSize;
      this.end = this.end + bufferSize;
    }
    else {
      this.start = this.end;
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\blueprints\revisions\rev_9050781_396f9cf\rev_right_396f9cf\blueprints-rexster-graph\src\main\java\com\tinkerpop\blueprints\pgm\impls\rexster\util\RexsterVertexSequence.java

  }
}

