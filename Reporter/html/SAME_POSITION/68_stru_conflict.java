<<<<<<< MINE
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
>>>>>>> YOURS

