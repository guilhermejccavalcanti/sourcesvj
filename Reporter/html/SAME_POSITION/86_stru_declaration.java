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


