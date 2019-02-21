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


