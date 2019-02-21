package com.impetus.kundera.persistence;
import javax.persistence.GenerationType;
import javax.persistence.metamodel.Metamodel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.impetus.kundera.KunderaException;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.generator.AutoGenerator;
import com.impetus.kundera.generator.SequenceGenerator;
import com.impetus.kundera.generator.TableGenerator;
import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.metadata.model.IdDiscriptor;
import com.impetus.kundera.metadata.model.MetamodelImpl;
import com.impetus.kundera.persistence.EntityManagerFactoryImpl.KunderaMetadata;
import com.impetus.kundera.property.PropertyAccessorHelper;

public class IdGenerator {
  private static final Logger log = LoggerFactory.getLogger(IdGenerator.class);
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

    return generateId(e, m, client);
    return generateId(e, m, client);
  }
  public Object generateAndSetId(Object e, EntityMetadata m, Client client) {
    return generateId(e, m, client);
  }
  private Object generateId(Object e, EntityMetadata m, Client<?> client) {
    Metamodel metamodel = KunderaMetadataManager.getMetamodel(m.getPersistenceUnit());
    IdDiscriptor keyValue = ((MetamodelImpl)metamodel).getKeyValue(e.getClass().getName());
    if (keyValue != null) {
      String clientFactoryName = KunderaMetadataManager.getPersistenceUnitMetadata(m.getPersistenceUnit()).getClient();
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
  private Object onAutoGenerator(EntityMetadata m, Client<?> client, Object e) {
    if (client instanceof AutoGenerator) {
      Object generatedId = ((AutoGenerator)client).generate();
      try {
        generatedId = PropertyAccessorHelper.fromSourceToTargetClass(m.getIdAttribute().getJavaType(), generatedId.getClass(), generatedId);
        PropertyAccessorHelper.setId(e, m, generatedId);
        return generatedId;
      }
      catch (IllegalArgumentException iae) {
        log.error("Unknown data type for ids : " + m.getIdAttribute().getJavaType());
        throw new KunderaException("Unknown data type for ids : " + m.getIdAttribute().getJavaType(), iae);
      }
    }
    throw new IllegalArgumentException(GenerationType.class.getSimpleName() + "." + GenerationType.AUTO + " Strategy not supported by this client :" + client.getClass().getName());
  }
  private Object onSequenceGenerator(EntityMetadata m, Client<?> client, IdDiscriptor keyValue, Object e) {
    if (client instanceof SequenceGenerator) {
      Object generatedId = ((SequenceGenerator)client).generate(keyValue.getSequenceDiscriptor());
      try {
        generatedId = PropertyAccessorHelper.fromSourceToTargetClass(m.getIdAttribute().getJavaType(), generatedId.getClass(), generatedId);
        PropertyAccessorHelper.setId(e, m, generatedId);
        return generatedId;
      }
      catch (IllegalArgumentException iae) {
        log.error("Unknown integral data type for ids : " + m.getIdAttribute().getJavaType());
        throw new KunderaException("Unknown integral data type for ids : " + m.getIdAttribute().getJavaType(), iae);
      }
    }
    throw new IllegalArgumentException(GenerationType.class.getSimpleName() + "." + GenerationType.SEQUENCE + " Strategy not supported by this client :" + client.getClass().getName());
  }
  private Object onTableGenerator(EntityMetadata m, Client<?> client, IdDiscriptor keyValue, Object e) {
    if (client instanceof TableGenerator) {
      Object generatedId = ((TableGenerator)client).generate(keyValue.getTableDiscriptor());
      try {
        generatedId = PropertyAccessorHelper.fromSourceToTargetClass(m.getIdAttribute().getJavaType(), generatedId.getClass(), generatedId);
        PropertyAccessorHelper.setId(e, m, generatedId);
        return generatedId;
      }
      catch (IllegalArgumentException iae) {
        log.error("Unknown integral data type for ids : " + m.getIdAttribute().getJavaType());
        throw new KunderaException("Unknown integral data type for ids : " + m.getIdAttribute().getJavaType(), iae);
      }
    }
    throw new IllegalArgumentException(GenerationType.class.getSimpleName() + "." + GenerationType.TABLE + " Strategy not supported by this client :" + client.getClass().getName());
  }
}

