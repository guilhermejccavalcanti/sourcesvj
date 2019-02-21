  public Object generateAndSetId(Object e, EntityMetadata m, PersistenceDelegator pd, final KunderaMetadata kunderaMetadata) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_9d00b7f_7202ca5\rev_left_9d00b7f\src\jpa-engine\core\src\main\java\com\impetus\kundera\persistence\IdGenerator.java
Metamodel metamodel = KunderaMetadataManager.getMetamodel(kunderaMetadata, m.getPersistenceUnit());
=======
Client<?> client = pd.getClient(m);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_9d00b7f_7202ca5\rev_right_7202ca5\src\jpa-engine\core\src\main\java\com\impetus\kundera\persistence\IdGenerator.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_9d00b7f_7202ca5\rev_left_9d00b7f\src\jpa-engine\core\src\main\java\com\impetus\kundera\persistence\IdGenerator.java
if (keyValue != null) {
      Client<?> client = pd.getClient(m);
      String clientFactoryName = KunderaMetadataManager.getPersistenceUnitMetadata(kunderaMetadata, m.getPersistenceUnit()).getClient();
      if (clientFactoryName != null && !clientFactoryName.equalsIgnoreCase("com.impetus.client.rdbms.RDBMSClientFactory")) {
        if (client != null) {
          GenerationType type = keyValue.getStrategy();
          switch (type){
            case TABLE:
            return onTableGenerator(m, client, keyValue, e);
            case SEQUENCE:
            return onSequenceGenerator(m, client, keyValue, e);
            case AUTO:
            return onAutoGenerator(m, client, e);
            case IDENTITY:
            throw new UnsupportedOperationException(GenerationType.class.getSimpleName() + "." + type + " Strategy not supported by this client :" + client.getClass().getName());
          }
        }
      }
      else {
        int hashCode = e.hashCode();
        Object generatedId = PropertyAccessorHelper.fromSourceToTargetClass(m.getIdAttribute().getJavaType(), Integer.class, new Integer(hashCode));
        PropertyAccessorHelper.setId(e, m, generatedId);
        return generatedId;
      }
    }
=======
return generateId(e, m, client);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_9d00b7f_7202ca5\rev_right_7202ca5\src\jpa-engine\core\src\main\java\com\impetus\kundera\persistence\IdGenerator.java

    if (keyValue != null) {
      Client<?> client = pd.getClient(m);
      String clientFactoryName = KunderaMetadataManager.getPersistenceUnitMetadata(kunderaMetadata, m.getPersistenceUnit()).getClient();
      if (clientFactoryName != null && !clientFactoryName.equalsIgnoreCase("com.impetus.client.rdbms.RDBMSClientFactory")) {
        if (client != null) {
          GenerationType type = keyValue.getStrategy();
          switch (type){
            case TABLE:
            return onTableGenerator(m, client, keyValue, e);
            case SEQUENCE:
            return onSequenceGenerator(m, client, keyValue, e);
            case AUTO:
            return onAutoGenerator(m, client, e);
            case IDENTITY:
            throw new UnsupportedOperationException(GenerationType.class.getSimpleName() + "." + type + " Strategy not supported by this client :" + client.getClass().getName());
          }
        }
      }
      else {
        int hashCode = e.hashCode();
        Object generatedId = PropertyAccessorHelper.fromSourceToTargetClass(m.getIdAttribute().getJavaType(), Integer.class, new Integer(hashCode));
        PropertyAccessorHelper.setId(e, m, generatedId);
        return generatedId;
      }
    }
    return null;
  }


