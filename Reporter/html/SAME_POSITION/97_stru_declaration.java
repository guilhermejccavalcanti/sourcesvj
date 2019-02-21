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


