package com.impetus.kundera.persistence;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.PersistenceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.client.EnhanceEntity;
import com.impetus.kundera.index.DocumentIndexer;
import com.impetus.kundera.metadata.MetadataUtils;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.metadata.model.JoinTableMetadata;
import com.impetus.kundera.metadata.model.Relation;
import com.impetus.kundera.metadata.model.Relation.ForeignKey;
import com.impetus.kundera.persistence.handler.impl.EntitySaveGraph;
import com.impetus.kundera.property.PropertyAccessException;
import com.impetus.kundera.property.PropertyAccessorHelper;
import com.impetus.kundera.query.exception.QueryHandlerException;

public class AbstractEntityReader {
  private static Log log = LogFactory.getLog(AbstractEntityReader.class);
  protected String luceneQueryFromJPAQuery;
  public Object computeGraph(EnhanceEntity e, List<EntitySaveGraph> graphs, Map<Object, Object> collectionHolder, Client client, EntityMetadata m, PersistenceDelegator persistenceDelegeator) throws Exception {
    Client childClient = null;
    Class<?> childClazz = null;
    EntityMetadata childMetadata = null;
    for (EntitySaveGraph g : graphs) {
      Relation relation = m.getRelation(g.getProperty().getName());
      if (relation.isRelatedViaJoinTable()) {
        computeJoinTableRelations(e, client, m, g, persistenceDelegeator, relation);
      }
      else {
        if (e.getEntity().getClass().equals(g.getChildClass())) {
          String relationName = g.getfKeyName();
          Object relationalValue = e.getRelations().get(relationName);
          childClazz = g.getParentClass();
          childMetadata = persistenceDelegeator.getMetadata(childClazz);
          Field f = g.getProperty();
          if (!collectionHolder.containsKey(relationalValue)) {
            childClient = persistenceDelegeator.getClient(childMetadata);
            Object child = null;
            if (childClazz.equals(e.getEntity().getClass())) {
              child = childClient.find(childClazz, childMetadata, relationalValue.toString(), null);
            }
            else {
              child = persistenceDelegeator.find(childClazz, relationalValue.toString(), g);
            }
            collectionHolder.put(relationalValue, child);
          }
          onBiDirection(e, client, g, m, collectionHolder.get(relationalValue), childMetadata, childClient);
          List<Object> collection = new ArrayList<Object>(1);
          collection.add(collectionHolder.get(relationalValue));
          PropertyAccessorHelper.set(e.getEntity(), f, PropertyAccessorHelper.isCollection(f.getType()) ? getFieldInstance(collection, f) : collection.get(0));
        }
        else {
          childClazz = g.getChildClass();
          childMetadata = persistenceDelegeator.getMetadata(childClazz);
          childClient = persistenceDelegeator.getClient(childMetadata);
          String relationName = g.getfKeyName();
          String relationalValue = e.getEntityId();
          Field f = g.getProperty();
          if (!collectionHolder.containsKey(relationalValue)) {
            List<Object> childs = null;
            if (MetadataUtils.useSecondryIndex(childClient.getPersistenceUnit())) {
              childs = childClient.find(relationName, relationalValue, childMetadata);
            }
            else {
              if (g.isSharedPrimaryKey()) {
                childs = new ArrayList();
                childs.add(childClazz.equals(e.getEntity().getClass()) ? childs.add(childClient.find(childClazz, childMetadata, e.getEntityId(), null)) : persistenceDelegeator.find(childClazz, relationalValue.toString()));
              }
              else {
                String query = getQuery(DocumentIndexer.PARENT_ID_CLASS, e.getEntity().getClass().getCanonicalName().toLowerCase(), DocumentIndexer.PARENT_ID_FIELD, e.getEntityId());
                Map<String, String> results = childClient.getIndexManager().search(query);
                Set<String> rsSet = new HashSet<String>(results.values());
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_fa90495_6bd9733\rev_left_fa90495\kundera-core\src\main\java\com\impetus\kundera\persistence\AbstractEntityReader.java
if (childClazz.equals(e.getEntity().getClass())) {
                  childs = (List<Object>)childClient.find(childClazz, rsSet.toArray(new String[]{  } ));
                }
                else {
                  childs = (List<Object>)persistenceDelegeator.find(childClazz, rsSet.toArray(new String[]{  } ));
                }
=======
childs = (List<Object>)(childClazz.equals(e.getEntity().getClass()) ? childClient.findAll(childClazz, rsSet.toArray(new String[]{  } )) : persistenceDelegeator.find(childClazz, rsSet.toArray(new String[]{  } )));
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_fa90495_6bd9733\rev_right_6bd9733\kundera-core\src\main\java\com\impetus\kundera\persistence\AbstractEntityReader.java

              }
            }
            collectionHolder.put(relationalValue, childs);
            if (childs != null) {
              for (Object child : childs) {
                onBiDirection(e, client, g, m, child, childMetadata, childClient);
              }
            }
          }
          onReflect(e.getEntity(), f, (List)collectionHolder.get(relationalValue));
        }
      }
    }
    return e.getEntity();
  }
  private Set<?> onReflect(Object entity, Field f, List<?> childs) throws PropertyAccessException {
    Set chids = new HashSet();
    if (childs != null) {
      chids = new HashSet(childs);
      PropertyAccessorHelper.set(entity, f, PropertyAccessorHelper.isCollection(f.getType()) ? getFieldInstance(childs, f) : childs.get(0));
    }
    return chids;
  }
  private Object getFieldInstance(List chids, Field f) {
    if (Set.class.isAssignableFrom(f.getType())) {
      Set col = new HashSet(chids);
      return col;
    }
    return chids;
  }
  protected static String getQuery(String clazzFieldName, String clazzName, String idFieldName, String idFieldValue) {
    StringBuffer sb = new StringBuffer("+");
    sb.append(clazzFieldName);
    sb.append(":");
    sb.append(clazzName);
    sb.append(" AND ");
    sb.append("+");
    sb.append(idFieldName);
    sb.append(":");
    sb.append(idFieldValue);
    return sb.toString();
  }
  private void onBiDirection(EnhanceEntity e, Client client, EntitySaveGraph objectGraph, EntityMetadata origMetadata, Object child, EntityMetadata childMetadata, Client childClient) throws Exception {
    if (!objectGraph.isUniDirectional()) {
      List obj = new ArrayList();
      Relation relation = childMetadata.getRelation(objectGraph.getBidirectionalProperty().getName());
      if (relation.getType().equals(ForeignKey.ONE_TO_MANY) || relation.getType().equals(ForeignKey.MANY_TO_MANY)) {
        String query = null;
        try {
          String id = PropertyAccessorHelper.getId(child, childMetadata);
          List<Object> results = null;
          if (MetadataUtils.useSecondryIndex(client.getPersistenceUnit())) {
            results = client.find(objectGraph.getfKeyName(), id, origMetadata);
          }
          else {
            Map<String, String> keys = null;
            if (relation.getType().equals(ForeignKey.ONE_TO_MANY)) {
              query = getQuery(DocumentIndexer.PARENT_ID_CLASS, child.getClass().getCanonicalName().toLowerCase(), DocumentIndexer.PARENT_ID_FIELD, id);
              keys = client.getIndexManager().search(query);
            }
            else {
              query = getQuery(DocumentIndexer.ENTITY_CLASS_FIELD, child.getClass().getCanonicalName().toLowerCase(), DocumentIndexer.ENTITY_ID_FIELD, id);
              keys = client.getIndexManager().fetchRelation(query);
            }
            Set<String> uqSet = new HashSet<String>(keys.values());
            results = new ArrayList<Object>();
            for (String rowKey : uqSet) {
              results.add(client.find(e.getEntity().getClass(), origMetadata, rowKey, null));
            }
          }
          if (results != null) {
            obj.addAll(results);
          }
        }
        catch (PropertyAccessException ex) {
          log.error("error on handling bi direction:" + ex.getMessage());
          throw new QueryHandlerException(ex.getMessage());
        }
        for (Object o : obj) {
          Field f = objectGraph.getProperty();
          if (PropertyAccessorHelper.isCollection(f.getType())) {
            List l = new ArrayList();
            l.add(child);
            Object oo = getFieldInstance(l, f);
            PropertyAccessorHelper.set(o, f, oo);
          }
          else {
            PropertyAccessorHelper.set(o, f, child);
          }
        }
      }
      else {
        obj.add(e.getEntity());
      }
      try {
        PropertyAccessorHelper.set(child, objectGraph.getBidirectionalProperty(), PropertyAccessorHelper.isCollection(objectGraph.getBidirectionalProperty().getType()) ? getFieldInstance(obj, objectGraph.getBidirectionalProperty()) : e.getEntity());
      }
      catch (PropertyAccessException ex) {
        log.error("error on handling bi direction:" + ex.getMessage());
        throw new QueryHandlerException(ex.getMessage());
      }
    }
  }
  protected List<EnhanceEntity> onAssociationUsingLucene(EntityMetadata m, Client client, List<EnhanceEntity> ls) {
    Set<String> rSet = fetchDataFromLucene(client);
    try {
      List resultList = client.findAll(m.getEntityClazz(), rSet.toArray(new String[]{  } ));
      return transform(m, ls, resultList);
    }
    catch (Exception e) {
      log.error("Error while executing handleAssociation for cassandra:" + e.getMessage());
      throw new QueryHandlerException(e.getMessage());
    }
  }
  protected List<EnhanceEntity> transform(EntityMetadata m, List<EnhanceEntity> ls, List resultList) {
    if ((ls == null || ls.isEmpty()) && resultList != null && !resultList.isEmpty()) {
      ls = new ArrayList<EnhanceEntity>(resultList.size());
    }
    for (Object r : resultList) {
      EnhanceEntity e = new EnhanceEntity(r, getId(r, m), null);
      ls.add(e);
    }
    return ls;
  }
  protected Set<String> fetchDataFromLucene(Client client) {
    Map<String, String> results = client.getIndexManager().search(luceneQueryFromJPAQuery);
    Set<String> rSet = new HashSet<String>(results.values());
    return rSet;
  }
  protected String getId(Object entity, EntityMetadata metadata) {
    try {
      return PropertyAccessorHelper.getId(entity, metadata);
    }
    catch (PropertyAccessException e) {
      throw new PersistenceException(e.getMessage());
    }
  }
  private void computeJoinTableRelations(EnhanceEntity e, Client client, EntityMetadata entityMetadata, EntitySaveGraph objectGraph, PersistenceDelegator delegator, Relation relation) {
    Object entity = e.getEntity();
    objectGraph.setParentId(getId(entity, entityMetadata));
    JoinTableMetadata jtMetadata = relation.getJoinTableMetadata();
    String joinTableName = jtMetadata.getJoinTableName();
    Set<String> joinColumns = jtMetadata.getJoinColumns();
    Set<String> inverseJoinColumns = jtMetadata.getInverseJoinColumns();
    String joinColumnName = (String)joinColumns.toArray()[0];
    String inverseJoinColumnName = (String)inverseJoinColumns.toArray()[0];
    EntityMetadata relMetadata = delegator.getMetadata(objectGraph.getChildClass());
    Client pClient = delegator.getClient(entityMetadata);
    List<?> foreignKeys = pClient.getForeignKeysFromJoinTable(joinTableName, joinColumnName, inverseJoinColumnName, relMetadata, objectGraph);
    List childrenEntities = new ArrayList();
    for (Object foreignKey : foreignKeys) {
      try {
        EntityMetadata childMetadata = delegator.getMetadata(relation.getTargetEntity());
        Client childClient = delegator.getClient(childMetadata);
        Object child = childClient.find(relation.getTargetEntity(), childMetadata, (String)foreignKey, null);
        onBiDirection(e, client, objectGraph, entityMetadata, child, childMetadata, childClient);
        childrenEntities.add(child);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    Field childField = objectGraph.getProperty();
    try {
      PropertyAccessorHelper.set(entity, childField, PropertyAccessorHelper.isCollection(childField.getType()) ? getFieldInstance(childrenEntities, childField) : childrenEntities.get(0));
    }
    catch (PropertyAccessException ex) {
      ex.printStackTrace();
    }
  }
}

