package com.impetus.kundera.persistence;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.FetchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.client.EnhanceEntity;
import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.MetadataUtils;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.metadata.model.Relation;
import com.impetus.kundera.metadata.model.Relation.ForeignKey;
import com.impetus.kundera.persistence.context.PersistenceCacheManager;
import com.impetus.kundera.property.PropertyAccessException;
import com.impetus.kundera.property.PropertyAccessorHelper;
import com.impetus.kundera.proxy.ProxyHelper;

public class AbstractEntityReader {
  private static Logger log = LoggerFactory.getLogger(AbstractEntityReader.class);
  protected String luceneQueryFromJPAQuery;
  private AssociationBuilder associationBuilder;
  protected EnhanceEntity findById(Object primaryKey, EntityMetadata m, Client client) {
    try {
      Object o = client.find(m.getEntityClazz(), primaryKey);
      if (o == null) {
        return null;
      }
      else {
        return o instanceof EnhanceEntity ? (EnhanceEntity)o : new EnhanceEntity(o, getId(o, m), null);
      }
    }
    catch (Exception e) {
      throw new EntityReaderException(e);
    }
  }
  public Object handleAssociation(final Object entity, final Map<String, Object> relationsMap, final EntityMetadata m, final PersistenceDelegator pd, boolean lazilyloaded) {
    for (Relation relation : m.getRelations()) {
      ForeignKey relationType = relation.getType();
      Object relationalObject = PropertyAccessorHelper.getObject(entity, relation.getProperty());
      if (relationalObject == null || ProxyHelper.isProxyOrCollection(relationalObject)) {
        onRelation(entity, relationsMap, m, pd, relation, relationType, lazilyloaded);
      }
    }
    return entity;
  }
  private void onRelation(final Object entity, final Map<String, Object> relationsMap, final EntityMetadata m, final PersistenceDelegator pd, Relation relation, ForeignKey relationType, boolean lazilyloaded) {
    FetchType fetchType = relation.getFetchType();
    if (relation.getType().equals(ForeignKey.MANY_TO_MANY)) {
      Field f = relation.getProperty();
      Object object = PropertyAccessorHelper.getObject(entity, f);
      final Object entityId = PropertyAccessorHelper.getId(entity, m);
      PersistenceCacheManager.addEntityToPersistenceCache(entity, pd, entityId);
      associationBuilder.populateRelationForM2M(entity, m, pd, relation, object, relationsMap);
    }
    else {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_b33f815_abf2ae4\rev_left_b33f815\kundera-core\src\main\java\com\impetus\kundera\persistence\AbstractEntityReader.java
onRelation(entity, relationsMap, relation, m, pd, lazilyloaded);
=======
if (relation.getType().equals(ForeignKey.MANY_TO_MANY)) {
        Field f = relation.getProperty();
        Object object = PropertyAccessorHelper.getObject(entity, f);
        final Object entityId = PropertyAccessorHelper.getId(entity, m);
        if (object != null && !ProxyHelper.isProxyCollection(object)) {
          PersistenceCacheManager.addEntityToPersistenceCache(entity, pd, entityId);
        }
        associationBuilder.populateRelationForM2M(entity, m, pd, relation, object, relationsMap);
      }
      else {
        onRelation(entity, relationsMap, relation, m, pd, lazilyloaded);
      }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_b33f815_abf2ae4\rev_right_abf2ae4\kundera-core\src\main\java\com\impetus\kundera\persistence\AbstractEntityReader.java

    }
  }
  private void onRelation(Object entity, Map<String, Object> relationsMap, final Relation relation, final EntityMetadata metadata, final PersistenceDelegator pd, boolean lazilyloaded) {
    final Object entityId = PropertyAccessorHelper.getId(entity, metadata);
    Object relationValue = relationsMap != null ? relationsMap.get(relation.getJoinColumnName()) : null;
    EntityMetadata targetEntityMetadata = KunderaMetadataManager.getEntityMetadata(relation.getTargetEntity());
    List relationalEntities = fetchRelations(relation, metadata, pd, entityId, relationValue, targetEntityMetadata);
    if (relationalEntities != null) {
      for (Object relationEntity : relationalEntities) {
        onParseRelation(entity, pd, targetEntityMetadata, relationEntity, relation, lazilyloaded);
      }
    }
  }
  private void onParseRelation(Object entity, final PersistenceDelegator pd, EntityMetadata targetEntityMetadata, Object relationEntity, Relation relation, boolean lazilyloaded) {
    parseRelations(entity, getEntity(relationEntity), getPersistedRelations(relationEntity), pd, targetEntityMetadata, lazilyloaded);
    setRelationToEntity(entity, relationEntity, relation);
  }
  private void setRelationToEntity(Object entity, Object relationEntity, Relation relation) {
    if (relation.getTargetEntity().isAssignableFrom(getEntity(relationEntity).getClass())) {
      if (relation.isUnary()) {
        PropertyAccessorHelper.set(entity, relation.getProperty(), getEntity(relationEntity));
      }
      else {
        Object associationObject = PropertyAccessorHelper.getObject(entity, relation.getProperty());
        if (associationObject == null || ProxyHelper.isProxyOrCollection(associationObject)) {
          associationObject = PropertyAccessorHelper.getCollectionInstance(relation.getProperty());
          PropertyAccessorHelper.set(entity, relation.getProperty(), associationObject);
        }
        ((Collection)associationObject).add(getEntity(relationEntity));
      }
    }
  }
  private void parseRelations(final Object originalEntity, final Object relationEntity, final Map<String, Object> relationsMap, final PersistenceDelegator pd, final EntityMetadata metadata, boolean lazilyloaded) {
    for (Relation relation : metadata.getRelations()) {
      FetchType fetchType = relation.getFetchType();
      if (relation.isUnary() && relation.getTargetEntity().isAssignableFrom(originalEntity.getClass())) {
        final Object associationObject = PropertyAccessorHelper.getObject(relationEntity, relation.getProperty());
        if (relation.getType().equals(ForeignKey.ONE_TO_ONE) || ((associationObject == null || ProxyHelper.isProxyOrCollection(associationObject)))) {
          PropertyAccessorHelper.set(relationEntity, relation.getProperty(), originalEntity);
        }
        else 
          if (relationsMap != null && relationsMap.containsKey(relation.getJoinColumnName())) {
            PropertyAccessorHelper.set(relationEntity, relation.getProperty(), originalEntity);
          }
      }
      else {
        final Object entityId = PropertyAccessorHelper.getId(relationEntity, metadata);
        Object relationValue = relationsMap != null ? relationsMap.get(relation.getJoinColumnName()) : null;
        final EntityMetadata targetEntityMetadata = KunderaMetadataManager.getEntityMetadata(relation.getTargetEntity());
        List immediateRelations = fetchRelations(relation, metadata, pd, entityId, relationValue, targetEntityMetadata);
        if (immediateRelations != null && !immediateRelations.isEmpty()) {
          for (Object immediateRelation : immediateRelations) {
            if (!compareTo(getEntity(immediateRelation), originalEntity)) {
              onParseRelation(relationEntity, pd, targetEntityMetadata, immediateRelation, relation, lazilyloaded);
            }
          }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_b33f815_abf2ae4\rev_left_b33f815\kundera-core\src\main\java\com\impetus\kundera\persistence\AbstractEntityReader.java
setRelationToEntity(relationEntity, originalEntity, relation);
=======
if (relation.getType().equals(ForeignKey.ONE_TO_ONE)) {
            if ((associationObject == null || ProxyHelper.isProxyOrCollection(associationObject))) {
              PropertyAccessorHelper.set(relationEntity, relation.getProperty(), originalEntity);
            }
          }
          else 
            if (relationsMap != null && relationsMap.containsKey(relation.getJoinColumnName())) {
              PropertyAccessorHelper.set(relationEntity, relation.getProperty(), originalEntity);
            }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_b33f815_abf2ae4\rev_right_abf2ae4\kundera-core\src\main\java\com\impetus\kundera\persistence\AbstractEntityReader.java

        }
        else {
          final Object entityId = PropertyAccessorHelper.getId(relationEntity, metadata);
          Object relationValue = relationsMap != null ? relationsMap.get(relation.getJoinColumnName()) : null;
          final EntityMetadata targetEntityMetadata = KunderaMetadataManager.getEntityMetadata(relation.getTargetEntity());
          List immediateRelations = fetchRelations(relation, metadata, pd, entityId, relationValue, targetEntityMetadata);
          if (immediateRelations != null && !immediateRelations.isEmpty()) {
            for (Object immediateRelation : immediateRelations) {
              if (!compareTo(getEntity(immediateRelation), originalEntity)) {
                onParseRelation(relationEntity, pd, targetEntityMetadata, immediateRelation, relation, lazilyloaded);
              }
            }
            setRelationToEntity(relationEntity, originalEntity, relation);
          }
        }
      }
    }
  }
  private List fetchRelations(final Relation relation, final EntityMetadata metadata, final PersistenceDelegator pd, final Object entityId, Object relationValue, EntityMetadata targetEntityMetadata) {
    List relationalEntities = new ArrayList();
    if ((relationValue != null && relation.isUnary()) || (relation.isJoinedByPrimaryKey())) {
      Object relationEntity = pd.getClient(targetEntityMetadata).find(relation.getTargetEntity(), relationValue != null ? relationValue : entityId);
      if (relationEntity != null) {
        relationalEntities.add(relationEntity);
      }
    }
    else 
      if (!relation.isUnary()) {
        Client associatedClient = pd.getClient(targetEntityMetadata);
        if (!MetadataUtils.useSecondryIndex(targetEntityMetadata.getPersistenceUnit())) {
          relationalEntities = associationBuilder.getAssociatedEntitiesFromIndex(relation.getProperty().getDeclaringClass(), entityId, targetEntityMetadata.getEntityClazz(), associatedClient);
        }
        else {
          relationalEntities = associatedClient.findByRelation(relation.getJoinColumnName(), entityId, relation.getTargetEntity());
        }
      }
    return relationalEntities;
  }
  public Object recursivelyFindEntities(Object entity, Map<String, Object> relationsMap, EntityMetadata m, PersistenceDelegator pd, boolean lazilyLoaded) {
    associationBuilder = new AssociationBuilder();
    return handleAssociation(entity, relationsMap, m, pd, lazilyLoaded);
  }
  private Map<String, Object> getPersistedRelations(Object relationEntity) {
    return relationEntity != null && relationEntity.getClass().isAssignableFrom(EnhanceEntity.class) ? ((EnhanceEntity)relationEntity).getRelations() : null;
  }
  private Object getEntity(Object relationEntity) {
    return relationEntity.getClass().isAssignableFrom(EnhanceEntity.class) ? ((EnhanceEntity)relationEntity).getEntity() : relationEntity;
  }
  private boolean isTraversalRequired(Map<String, Object> relationsMap, ForeignKey type) {
    return !(relationsMap == null && (type.equals(ForeignKey.ONE_TO_ONE) || type.equals(ForeignKey.MANY_TO_ONE)));
  }
  protected List<EnhanceEntity> onAssociationUsingLucene(EntityMetadata m, Client client, List<EnhanceEntity> ls) {
    Set<String> rSet = fetchDataFromLucene(client);
    List resultList = client.findAll(m.getEntityClazz(), null, rSet.toArray(new String[]{  } ));
    return m.getRelationNames() != null && !m.getRelationNames().isEmpty() ? resultList : transform(m, ls, resultList);
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
    Map<String, Object> results = client.getIndexManager().search(luceneQueryFromJPAQuery);
    Set rSet = new HashSet(results.values());
    return rSet;
  }
  protected Object getId(Object entity, EntityMetadata metadata) {
    try {
      return PropertyAccessorHelper.getId(entity, metadata);
    }
    catch (PropertyAccessException e) {
      log.error("Error while Getting ID, Caused by: ", e);
      throw new EntityReaderException("Error while Getting ID for entity " + entity, e);
    }
  }
  private boolean compareTo(Object relationalEntity, Object originalEntity) {
    if (relationalEntity != null && originalEntity != null && relationalEntity.getClass().isAssignableFrom(originalEntity.getClass())) {
      EntityMetadata metadata = KunderaMetadataManager.getEntityMetadata(originalEntity.getClass());
      Object relationalEntityId = PropertyAccessorHelper.getId(relationalEntity, metadata);
      Object originalEntityId = PropertyAccessorHelper.getId(originalEntity, metadata);
      return relationalEntityId.equals(originalEntityId);
    }
    return false;
  }
}

