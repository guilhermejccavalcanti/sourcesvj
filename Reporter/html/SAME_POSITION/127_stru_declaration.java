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


