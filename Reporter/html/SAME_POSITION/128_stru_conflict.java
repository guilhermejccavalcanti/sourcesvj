<<<<<<< MINE
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
>>>>>>> YOURS

