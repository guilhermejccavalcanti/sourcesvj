package com.impetus.client.cassandra.query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.PersistenceException;
import org.apache.cassandra.thrift.IndexClause;
import org.apache.cassandra.thrift.IndexExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.impetus.client.cassandra.pelops.PelopsClient;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.client.EnhanceEntity;
import com.impetus.kundera.metadata.MetadataUtils;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.persistence.AbstractEntityReader;
import com.impetus.kundera.persistence.EntityReader;
import com.impetus.kundera.query.exception.QueryHandlerException;

public class CassandraEntityReader extends AbstractEntityReader implements EntityReader {
  Map<Boolean, List<IndexClause>> conditions = new HashMap<Boolean, List<IndexClause>>();
  private static Log log = LogFactory.getLog(CassandraEntityReader.class);
  public CassandraEntityReader(String luceneQuery) {
    this.luceneQueryFromJPAQuery = luceneQuery;
  }
  public CassandraEntityReader() {
  }
  @Override public EnhanceEntity findById(String primaryKey, EntityMetadata m, List<String> relationNames, Client client) {
    try {
      return (EnhanceEntity)client.find(m.getEntityClazz(), m, primaryKey, relationNames);
    }
    catch (Exception e) {
      log.error("Error while handling find by id");
      throw new PersistenceException(e.getMessage());
    }
  }
  @Override public List<EnhanceEntity> populateRelation(EntityMetadata m, List<String> relationNames, boolean isParent, Client client) {
    List<EnhanceEntity> ls = null;
    boolean isRowKeyQuery = conditions.keySet().iterator().next();
    if (!isRowKeyQuery) {
      if (!isParent) {
        if (MetadataUtils.useSecondryIndex(m.getPersistenceUnit())) {
          ls = ((PelopsClient)client).find(m, relationNames, this.conditions.get(isRowKeyQuery));
        }
        else {
          Set<String> rSet = fetchDataFromLucene(client);
          try {
            ls = (List<EnhanceEntity>)((PelopsClient)client).find(m.getEntityClazz(), relationNames, true, m, rSet.toArray(new String[]{  } ));
          }
          catch (Exception e) {
            log.error("Error while executing handleAssociation for cassandra:" + e.getMessage());
            throw new QueryHandlerException(e.getMessage());
          }
        }
      }
      else {
        if (MetadataUtils.useSecondryIndex(m.getPersistenceUnit())) {
          ls = ((PelopsClient)client).find(this.conditions.get(isRowKeyQuery), m, true, null);
        }
        else {
          onAssociationUsingLucene(m, client, ls);
        }
      }
    }
    else {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_91ed291_ca19f8f\rev_left_91ed291\kundera-cassandra\src\main\java\com\impetus\client\cassandra\query\CassandraEntityReader.java
ls = handleFindByRange(m, client, ls, conditions, isRowKeyQuery);
=======
if (MetadataUtils.useSecondryIndex(m.getPersistenceUnit())) {
        ls = ((PelopsClient)client).find(this.conditions, m, true, null);
      }
      else {
        ls = onAssociationUsingLucene(m, client, ls);
      }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_91ed291_ca19f8f\rev_right_ca19f8f\kundera-cassandra\src\main\java\com\impetus\client\cassandra\query\CassandraEntityReader.java

    }
    return ls;
  }
  public List handleFindByRange(EntityMetadata m, Client client, List result, Map<Boolean, List<IndexClause>> ixClause, boolean isRowKeyQuery) {
    List<IndexExpression> expressions = ixClause.get(isRowKeyQuery).get(0).getExpressions();
    byte[] minValue = expressions.get(0).getValue();
    byte[] maxVal = expressions.get(1).getValue();
    try {
      result = ((PelopsClient)client).findByRange(minValue, maxVal, m, false, null);
    }
    catch (Exception e) {
      log.error("Error while executing find by range");
      throw new QueryHandlerException(e.getMessage());
    }
    return result;
  }
  public void setConditions(Map<Boolean, List<IndexClause>> conditions) {
    this.conditions = conditions;
  }
}

