package org.elasticsearch.index.query;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import java.io.IOException;
import java.util.Objects;

@Deprecated public class QueryFilterBuilder extends AbstractQueryBuilder<QueryFilterBuilder> {
  public static final String NAME = "query";
  private final QueryBuilder queryBuilder;
  static final QueryFilterBuilder PROTOTYPE = new QueryFilterBuilder(null);
  public QueryFilterBuilder(QueryBuilder queryBuilder) {
    this.queryBuilder = queryBuilder;
  }
  public QueryBuilder innerQuery() {
    return this.queryBuilder;
  }
  @Override protected void doXContent(XContentBuilder builder, Params params) throws IOException {
    builder.field(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\elasticsearch\revisions\rev_8d2a2f8_c10f116\rev_left_8d2a2f8\core\src\main\java\org\elasticsearch\index\query\QueryFilterBuilder.java
NAME
=======
QueryFilterParser.NAME
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\elasticsearch\revisions\rev_8d2a2f8_c10f116\rev_right_c10f116\core\src\main\java\org\elasticsearch\index\query\QueryFilterBuilder.java
);
    queryBuilder.toXContent(builder, params);
  }
  @Override protected Query doToQuery(QueryShardContext context) throws IOException {
    Query innerQuery = this.queryBuilder.toQuery(context);
    if (innerQuery == null) {
      return null;
    }
    return new ConstantScoreQuery(innerQuery);
  }
  @Override protected void setFinalBoost(Query query) {
  }
  @Override public QueryValidationException validate() {
    return validateInnerQuery(queryBuilder, null);
  }
  @Override protected int doHashCode() {
    return Objects.hash(queryBuilder);
  }
  @Override protected boolean doEquals(QueryFilterBuilder other) {
    return Objects.equals(queryBuilder, other.queryBuilder);
  }
  @Override protected QueryFilterBuilder doReadFrom(StreamInput in) throws IOException {
    QueryBuilder innerQueryBuilder = in.readQuery();
    return new QueryFilterBuilder(innerQueryBuilder);
  }
  @Override protected void doWriteTo(StreamOutput out) throws IOException {
    out.writeQuery(queryBuilder);
  }
  @Override public String getWriteableName() {
    return NAME;
  }
}

