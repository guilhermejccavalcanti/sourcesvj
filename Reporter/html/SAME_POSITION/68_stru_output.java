package com.impetus.kundera.persistence;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Map;
import javax.persistence.FetchType;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.PersistenceUtilHelper;
import com.impetus.kundera.client.EnhanceEntity;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.client.EnhanceEntity;
import com.impetus.kundera.metadata.model.Relation;
import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.property.PropertyAccessException;
import com.impetus.kundera.metadata.MetadataUtils;
import com.impetus.kundera.property.PropertyAccessorHelper;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.metadata.model.Relation;
import com.impetus.kundera.metadata.model.Relation.ForeignKey;
import com.impetus.kundera.persistence.context.PersistenceCacheManager;
import com.impetus.kundera.property.PropertyAccessException;
import com.impetus.kundera.property.PropertyAccessorHelper;

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
  public Object handleAssociation(final Object entity, final Map<String, Object> relationsMap, final EntityMetadata m, final PersistenceDelegator pd) {
    for (Relation relation : m.getRelations()) {
      ForeignKey relationType = relation.getType();
      Object relationalObject = PropertyAccessorHelper.getObject(entity, relation.getProperty());
      if (relationalObject == null || PersistenceUtilHelper.instanceOfHibernateProxy(relationalObject) || PersistenceUtilHelper.instanceOfHibernatePersistentSet(relationalObject) || PersistenceUtilHelper.instanceOfHibernatePersistentCollection(relationalObject) || PersistenceUtilHelper.instanceOfHibernatePersistentBag(relationalObject)) {
        onRelation(entity, relationsMap, m, pd, relation, relationType);
      }
    }
    return entity;
  }
  private void onRelation(final Object entity, final Map<String, Object> relationsMap, final EntityMetadata m, final PersistenceDelegator pd, Relation relation, ForeignKey relationType) {
    if (relation.getType().equals(ForeignKey.MANY_TO_MANY)) {
      Field f = relation.getProperty();
      Object object = PropertyAccessorHelper.getObject(entity, f);
      final Object entityId = PropertyAccessorHelper.getId(entity, m);
      PersistenceCacheManager.addEntityToPersistenceCache(entity, pd, entityId);
      associationBuilder.populateRelationForM2M(entity, m, pd, relation, object, relationsMap);
    }
    else {
      onRelation(entity, relationsMap, relation, m, pd);
    }
  }
  private void onRelation(Object entity, Map<String, Object> relationsMap, final Relation relation, final EntityMetadata metadata, final PersistenceDelegator pd) {
    final Object entityId = PropertyAccessorHelper.getId(entity, metadata);
    Object relationValue = relationsMap != null ? relationsMap.get(relation.getJoinColumnName()) : null;
    EntityMetadata targetEntityMetadata = KunderaMetadataManager.getEntityMetadata(relation.getTargetEntity());
    List relationalEntities = fetchRelations(relation, metadata, pd, entityId, relationValue, targetEntityMetadata);
    if (relationalEntities != null) {
      for (Object relationEntity : relationalEntities) {
        onParseRelation(entity, pd, targetEntityMetadata, relationEntity, relation);
      }
    }
  }
  private void onParseRelation(Object entity, final PersistenceDelegator pd, EntityMetadata targetEntityMetadata, Object relationEntity, Relation relation) {
    parseRelations(entity, getEntity(relationEntity), getPersistedRelations(relationEntity), pd, targetEntityMetadata);
    setRelationToEntity(entity, relationEntity, relation);
  }
  private void setRelationToEntity(Object entity, Object relationEntity, Relation relation) {
    if (relation.getTargetEntity().isAssignableFrom(getEntity(relationEntity).getClass())) {
      if (relation.isUnary()) {
        PropertyAccessorHelper.set(entity, relation.getProperty(), getEntity(relationEntity));
      }
      else {
        Object associationObject = PropertyAccessorHelper.getObject(entity, relation.getProperty());
        if (associationObject == null || PersistenceUtilHelper.instanceOfHibernateProxy(associationObject) || PersistenceUtilHelper.instanceOfHibernatePersistentSet(associationObject) || PersistenceUtilHelper.instanceOfHibernatePersistentCollection(associationObject) || PersistenceUtilHelper.instanceOfHibernatePersistentBag(associationObject)) {
          associationObject = PropertyAccessorHelper.getCollectionInstance(relation.getProperty());
          PropertyAccessorHelper.set(entity, relation.getProperty(), associationObject);
        }
        ((Collection)associationObject).add(getEntity(relationEntity));
      }
    }
  }
  private void parseRelations(final Object originalEntity, final Object relationEntity, final Map<String, Object> relationsMap, final PersistenceDelegator pd, final EntityMetadata metadata) {
    for (Relation relation : metadata.getRelations()) {
      if (relation.isUnary() && relation.getTargetEntity().isAssignableFrom(originalEntity.getClass())) {
        Object associationObject = PropertyAccessorHelper.getObject(relationEntity, relation.getProperty());
        if (relation.getType().equals(ForeignKey.ONE_TO_ONE) || ((associationObject == null || PersistenceUtilHelper.instanceOfHibernateProxy(associationObject) || PersistenceUtilHelper.instanceOfHibernatePersistentSet(associationObject) || PersistenceUtilHelper.instanceOfHibernatePersistentCollection(associationObject)))) {
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
              onParseRelation(relationEntity, pd, targetEntityMetadata, immediateRelation, relation);
            }
          }
          setRelationToEntity(relationEntity, originalEntity, relation);
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
  public Object recursivelyFindEntities(Object entity, Map<String, Object> relationsMap, EntityMetadata m, PersistenceDelegator pd) {
    associationBuilder = new AssociationBuilder();
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_644f632_f9821d6\rev_left_644f632\kundera-core\src\main\java\com\impetus\kundera\persistence\AbstractEntityReader.java
return handleAssociation(entity, relationsMap, m, pd);
=======
for (Relation relation : m.getRelations()) {
      FetchType fetchType = relation.getFetchType();
      if (fetchType.equals(FetchType.LAZY)) {
        associationBuilder.setProxyRelationObject(entity, relationsMap, m, pd, entityId, relation);
        continue ;
      }
      associationBuilder.setConcreteRelationObject(entity, relationsMap, m, pd, entityId, relation);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_644f632_f9821d6\rev_right_f9821d6\kundera-core\src\main\java\com\impetus\kundera\persistence\AbstractEntityReader.java

  }
  private Map<String, Object> getPersistedRelations(Object relationEntity) {
    return relationEntity != null && relationEntity.getClass().isAssignableFrom(EnhanceEntity.class) ? ((EnhanceEntity)relationEntity).getRelations() : null;
  }
  private Object getEntity(Object relationEntity) {
    return relationEntity.getClass().isAssignableFrom(EnhanceEntity.class) ? ((EnhanceEntity)relationEntity).getEntity() : relationEntity;
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

