<<<<<<< MINE
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
>>>>>>> YOURS

