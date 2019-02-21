<<<<<<< MINE
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
>>>>>>> YOURS

