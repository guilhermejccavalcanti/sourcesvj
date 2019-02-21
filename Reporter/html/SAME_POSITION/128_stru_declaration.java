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
      }
    }
  }


