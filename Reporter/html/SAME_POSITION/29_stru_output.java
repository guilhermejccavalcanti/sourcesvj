package com.impetus.client.cassandra.pelops;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.persistence.PersistenceException;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scale7.cassandra.pelops.Bytes;
import org.scale7.cassandra.pelops.Selector;
import com.impetus.kundera.Constants;
import com.impetus.kundera.cache.ElementCollectionCacheManager;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.client.DataHandler;
import com.impetus.kundera.client.EnhanceEntity;
import com.impetus.kundera.db.DataRow;
import com.impetus.kundera.metadata.MetadataUtils;
import com.impetus.kundera.metadata.model.EmbeddedColumn;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.metadata.model.Relation;
import com.impetus.kundera.property.PropertyAccessException;
import com.impetus.kundera.property.PropertyAccessorFactory;
import com.impetus.kundera.property.PropertyAccessorHelper;

public class PelopsDataHandler extends DataHandler {
  private Client client;
  private long timestamp = System.currentTimeMillis();
  public PelopsDataHandler(Client client) {
    super();
    this.client = client;
  }
  private static Log log = LogFactory.getLog(PelopsDataHandler.class);
  public Object fromThriftRow(Selector selector, Class<?> clazz, EntityMetadata m, String rowKey, List<String> relationNames, boolean isWrapReq) throws Exception {
    List<String> superColumnNames = m.getEmbeddedColumnFieldNames();
    Object e = null;
    if (!superColumnNames.isEmpty()) {
      List<SuperColumn> thriftSuperColumns = selector.getSuperColumnsFromRow(m.getTableName(), rowKey, Selector.newColumnsPredicateAll(true, 10000), ConsistencyLevel.ONE);
      e = fromSuperColumnThriftRow(clazz, m, new ThriftRow(rowKey, m.getTableName(), null, thriftSuperColumns));
    }
    else {
      List<ByteBuffer> rowKeys = new ArrayList<ByteBuffer>(1);
      ByteBuffer rKeyAsByte = ByteBufferUtil.bytes(rowKey);
      rowKeys.add(ByteBufferUtil.bytes(rowKey));
      Map<ByteBuffer, List<ColumnOrSuperColumn>> columnOrSuperColumnsFromRow = selector.getColumnOrSuperColumnsFromRows(new ColumnParent(m.getTableName()), rowKeys, Selector.newColumnsPredicateAll(true, 10000), ConsistencyLevel.ONE);
      List<ColumnOrSuperColumn> colList = columnOrSuperColumnsFromRow.get(rKeyAsByte);
      List<Column> thriftColumns = new ArrayList<Column>(colList.size());
      for (ColumnOrSuperColumn col : colList) {
        if (col.super_column == null) {
          thriftColumns.add(col.getColumn());
        }
        else {
          thriftColumns.addAll(col.getSuper_column().getColumns());
        }
      }
      e = fromColumnThriftRow(clazz, m, new ThriftRow(rowKey, m.getTableName(), thriftColumns, null), relationNames, isWrapReq);
    }
    return e;
  }
  public List<Object> fromThriftRow(Selector selector, Class<?> clazz, EntityMetadata m, List<String> relationNames, boolean isWrapReq, String ... rowIds) throws Exception {
    List<Object> entities = new ArrayList<Object>(rowIds.length);
    for (String rowKey : rowIds) {
      Object e = fromThriftRow(selector, clazz, m, rowKey, relationNames, isWrapReq);
      entities.add(e);
    }
    return entities;
  }
  public  <E extends java.lang.Object> E fromThriftRow(Class<E> clazz, EntityMetadata m, DataRow<SuperColumn> tr) throws Exception {
    E e = clazz.newInstance();
    PropertyAccessorHelper.setId(e, m, tr.getId());
    Map<String, Field> columnNameToFieldMap = new HashMap<String, Field>();
    Map<String, Field> superColumnNameToFieldMap = new HashMap<String, Field>();
    MetadataUtils.populateColumnAndSuperColumnMaps(m, columnNameToFieldMap, superColumnNameToFieldMap);
    Collection embeddedCollection = null;
    Field embeddedCollectionField = null;
    for (SuperColumn sc : tr.getColumns()) {
      String scName = PropertyAccessorFactory.STRING.fromBytes(sc.getName());
      String scNamePrefix = null;
      if (scName.indexOf(Constants.EMBEDDED_COLUMN_NAME_DELIMITER) != -1) {
        scNamePrefix = MetadataUtils.getEmbeddedCollectionPrefix(scName);
        embeddedCollectionField = superColumnNameToFieldMap.get(scNamePrefix);
        embeddedCollection = MetadataUtils.getEmbeddedCollectionInstance(embeddedCollectionField);
        Object embeddedObject = populateEmbeddedObject(sc, m);
        embeddedCollection.add(embeddedObject);
        PropertyAccessorHelper.set(e, embeddedCollectionField, embeddedCollection);
      }
      else {
        boolean intoRelations = false;
        if (scName.equals(Constants.FOREIGN_KEY_EMBEDDED_COLUMN_NAME)) {
          intoRelations = true;
        }
        for (Column column : sc.getColumns()) {
          String name = PropertyAccessorFactory.STRING.fromBytes(column.getName());
          byte[] value = column.getValue();
          if (value == null) {
            continue ;
          }
          if (intoRelations) {
            Relation relation = m.getRelation(name);
            String foreignKeys = PropertyAccessorFactory.STRING.fromBytes(value);
            Set<String> keys = MetadataUtils.deserializeKeys(foreignKeys);
          }
          else {
            Field field = columnNameToFieldMap.get(name);
            Object embeddedObject = PropertyAccessorHelper.getObject(e, scName);
            PropertyAccessorHelper.set(embeddedObject, field, value);
          }
        }
      }
    }
    return e;
  }
  public Object fromColumnThriftRow(Class<?> clazz, EntityMetadata m, ThriftRow thriftRow, List<String> relationNames, boolean isWrapperReq) throws Exception {
    Object entity = clazz.newInstance();
    Map<String, Object> relations = new HashMap<String, Object>();
    PropertyAccessorHelper.setId(entity, m, thriftRow.getId());
    for (Column c : thriftRow.getColumns()) {
      String thriftColumnName = PropertyAccessorFactory.STRING.fromBytes(c.getName());
      byte[] thriftColumnValue = c.getValue();
      if (null == thriftColumnValue) {
        continue ;
      }
      com.impetus.kundera.metadata.model.Column column = m.getColumn(thriftColumnName);
      if (column != null) {
        try {
          PropertyAccessorHelper.set(entity, column.getField(), thriftColumnValue);
        }
        catch (PropertyAccessException pae) {
          log.warn(pae.getMessage());
        }
      }
      else {
        if (relationNames != null && !relationNames.isEmpty() && relationNames.contains(thriftColumnName)) {
          String value = PropertyAccessorFactory.STRING.fromBytes(thriftColumnValue);
          relations.put(thriftColumnName, value);
        }
      }
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_5c6ec4a_84557c9\rev_left_5c6ec4a\kundera-cassandra\src\main\java\com\impetus\client\cassandra\pelops\PelopsDataHandler.java
if (columns != null && (columns.size() == 1 ? columns.iterator().next() != null : true)) {
      for (Column c : thriftRow.getColumns()) {
        String thriftColumnName = PropertyAccessorFactory.STRING.fromBytes(c.getName());
        byte[] thriftColumnValue = c.getValue();
        if (null == thriftColumnValue) {
          continue ;
        }
        com.impetus.kundera.metadata.model.Column column = m.getColumn(thriftColumnName);
        if (column != null) {
          try {
            PropertyAccessorHelper.set(entity, column.getField(), thriftColumnValue);
          }
          catch (PropertyAccessException pae) {
            log.warn(pae.getMessage());
          }
        }
        else {
          if (relationNames != null && !relationNames.isEmpty() && relationNames.contains(thriftColumnName)) {
            String value = PropertyAccessorFactory.STRING.fromBytes(thriftColumnValue);
            relations.put(thriftColumnName, value);
          }
        }
      }
    }
    else {
      for (SuperColumn c : thriftRow.getSuperColumns()) {
        for (Column cc : c.getColumns()) {
          String thriftColumnName = PropertyAccessorFactory.STRING.fromBytes(cc.getName());
          byte[] thriftColumnValue = cc.getValue();
          if (null == thriftColumnValue) {
            continue ;
          }
          com.impetus.kundera.metadata.model.Column column = m.getColumn(thriftColumnName);
          if (column != null) {
            try {
              PropertyAccessorHelper.set(entity, column.getField(), thriftColumnValue);
            }
            catch (PropertyAccessException pae) {
              log.warn(pae.getMessage());
            }
          }
          else {
            if (relationNames != null && !relationNames.isEmpty() && relationNames.contains(thriftColumnName)) {
              String value = PropertyAccessorFactory.STRING.fromBytes(thriftColumnValue);
              relations.put(thriftColumnName, value);
            }
          }
        }
      }
    }
=======
return isWrapperReq ? new EnhanceEntity(entity, thriftRow.getId(), relations) : entity;
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\Kundera\revisions\rev_5c6ec4a_84557c9\rev_right_84557c9\kundera-cassandra\src\main\java\com\impetus\client\cassandra\pelops\PelopsDataHandler.java

    return isWrapperReq ? new EnhanceEntity(entity, thriftRow.getId(), relations) : entity;
    return isWrapperReq ? new EnhanceEntity(entity, thriftRow.getId(), relations) : entity;
  }
  public  <E extends java.lang.Object> E fromSuperColumnThriftRow(Class<E> clazz, EntityMetadata m, ThriftRow tr) throws Exception {
    Object entity = clazz.newInstance();
    Map<String, Set<String>> foreignKeysMap = new HashMap<String, Set<String>>();
    PropertyAccessorHelper.setId(entity, m, tr.getId());
    Map<String, Field> columnNameToFieldMap = new HashMap<String, Field>();
    Map<String, Field> superColumnNameToFieldMap = new HashMap<String, Field>();
    MetadataUtils.populateColumnAndSuperColumnMaps(m, columnNameToFieldMap, superColumnNameToFieldMap);
    Collection embeddedCollection = null;
    Field embeddedCollectionField = null;
    for (SuperColumn sc : tr.getSuperColumns()) {
      String scName = PropertyAccessorFactory.STRING.fromBytes(sc.getName());
      String scNamePrefix = null;
      if (scName.indexOf(Constants.EMBEDDED_COLUMN_NAME_DELIMITER) != -1) {
        scNamePrefix = MetadataUtils.getEmbeddedCollectionPrefix(scName);
        embeddedCollectionField = superColumnNameToFieldMap.get(scNamePrefix);
        if (embeddedCollection == null) {
          embeddedCollection = MetadataUtils.getEmbeddedCollectionInstance(embeddedCollectionField);
        }
        Object embeddedObject = MetadataUtils.getEmbeddedGenericObjectInstance(embeddedCollectionField);
        for (Column column : sc.getColumns()) {
          String name = PropertyAccessorFactory.STRING.fromBytes(column.getName());
          byte[] value = column.getValue();
          if (value == null) {
            continue ;
          }
          Field columnField = columnNameToFieldMap.get(name);
          PropertyAccessorHelper.set(embeddedObject, columnField, value);
        }
        embeddedCollection.add(embeddedObject);
        ElementCollectionCacheManager.getInstance().addElementCollectionCacheMapping(tr.getId(), embeddedObject, scName);
      }
      else {
        Field superColumnField = superColumnNameToFieldMap.get(scName);
        if (superColumnField != null) {
          Class superColumnClass = superColumnField.getType();
          Object superColumnObj = superColumnClass.newInstance();
          for (Column column : sc.getColumns()) {
            String name = PropertyAccessorFactory.STRING.fromBytes(column.getName());
            byte[] value = column.getValue();
            Field columnField = columnNameToFieldMap.get(name);
            try {
              PropertyAccessorHelper.set(superColumnObj, columnField, value);
            }
            catch (PropertyAccessException e) {
              log.debug(e.getMessage() + ". Possible case of entity column in a super column family. Will be treated as a super column.");
              superColumnObj = Bytes.toUTF8(value);
            }
          }
          PropertyAccessorHelper.set(entity, superColumnField, superColumnObj);
        }
      }
    }
    if (embeddedCollection != null && !embeddedCollection.isEmpty()) {
      PropertyAccessorHelper.set(entity, embeddedCollectionField, embeddedCollection);
    }
    return (E)entity;
  }
  public Object populateEmbeddedObject(SuperColumn sc, EntityMetadata m) throws Exception {
    Field embeddedCollectionField = null;
    Object embeddedObject = null;
    String scName = PropertyAccessorFactory.STRING.fromBytes(sc.getName());
    String scNamePrefix = null;
    Map<String, Field> columnNameToFieldMap = new HashMap<String, Field>();
    Map<String, Field> superColumnNameToFieldMap = new HashMap<String, Field>();
    MetadataUtils.populateColumnAndSuperColumnMaps(m, columnNameToFieldMap, superColumnNameToFieldMap);
    if (scName.indexOf(Constants.EMBEDDED_COLUMN_NAME_DELIMITER) != -1) {
      StringTokenizer st = new StringTokenizer(scName, Constants.EMBEDDED_COLUMN_NAME_DELIMITER);
      if (st.hasMoreTokens()) {
        scNamePrefix = st.nextToken();
      }
      embeddedCollectionField = superColumnNameToFieldMap.get(scNamePrefix);
      Class<?> embeddedClass = PropertyAccessorHelper.getGenericClass(embeddedCollectionField);
      try {
        embeddedClass.getConstructor();
      }
      catch (NoSuchMethodException nsme) {
        throw new PersistenceException(embeddedClass.getName() + " is @Embeddable and must have a default no-argument constructor.");
      }
      embeddedObject = embeddedClass.newInstance();
      for (Column column : sc.getColumns()) {
        String name = PropertyAccessorFactory.STRING.fromBytes(column.getName());
        byte[] value = column.getValue();
        if (value == null) {
          continue ;
        }
        Field columnField = columnNameToFieldMap.get(name);
        PropertyAccessorHelper.set(embeddedObject, columnField, value);
      }
    }
    else {
      Field superColumnField = superColumnNameToFieldMap.get(scName);
      Class superColumnClass = superColumnField.getType();
      embeddedObject = superColumnClass.newInstance();
      for (Column column : sc.getColumns()) {
        String name = PropertyAccessorFactory.STRING.fromBytes(column.getName());
        byte[] value = column.getValue();
        if (value == null) {
          continue ;
        }
        Field columnField = columnNameToFieldMap.get(name);
        PropertyAccessorHelper.set(embeddedObject, columnField, value);
      }
    }
    return embeddedObject;
  }
  public ThriftRow toThriftRow(PelopsClient client, Object e, String id, EntityMetadata m, String columnFamily) throws Exception {
    ThriftRow tr = new ThriftRow();
    tr.setColumnFamilyName(columnFamily);
    tr.setId(id);
    addSuperColumnsToThriftRow(timestamp, client, tr, m, e, id);
    if (m.getEmbeddedColumnsAsList().isEmpty()) {
      addColumnsToThriftRow(timestamp, tr, m, e);
    }
    return tr;
  }
  private void addColumnsToThriftRow(long timestamp, ThriftRow tr, EntityMetadata m, Object e) throws Exception {
    List<Column> columns = new ArrayList<Column>();
    for (com.impetus.kundera.metadata.model.Column column : m.getColumnsAsList()) {
      Field field = column.getField();
      if (field.getType().isAssignableFrom(Set.class) || field.getType().isAssignableFrom(Collection.class)) {
      }
      else {
        String name = column.getName();
        try {
          byte[] value = PropertyAccessorHelper.get(e, field);
          Column col = new Column();
          col.setName(PropertyAccessorFactory.STRING.toBytes(name));
          col.setValue(value);
          col.setTimestamp(timestamp);
          columns.add(col);
        }
        catch (PropertyAccessException exp) {
          log.warn(exp.getMessage());
        }
      }
    }
    tr.setColumns(columns);
  }
  private void addSuperColumnsToThriftRow(long timestamp, PelopsClient client, ThriftRow tr, EntityMetadata m, Object e, String id) throws Exception {
    for (EmbeddedColumn superColumn : m.getEmbeddedColumnsAsList()) {
      Field superColumnField = superColumn.getField();
      Object superColumnObject = PropertyAccessorHelper.getObject(e, superColumnField);
      String superColumnName = null;
      if (superColumnObject == null) {
        continue ;
      }
      if (superColumnObject instanceof Collection) {
        ElementCollectionCacheManager ecCacheHandler = ElementCollectionCacheManager.getInstance();
        if (ecCacheHandler.isCacheEmpty()) {
          int count = 0;
          for (Object obj : (Collection)superColumnObject) {
            superColumnName = superColumn.getName() + Constants.EMBEDDED_COLUMN_NAME_DELIMITER + count;
            SuperColumn thriftSuperColumn = buildThriftSuperColumn(superColumnName, timestamp, superColumn, obj);
            tr.addSuperColumn(thriftSuperColumn);
            count++;
          }
        }
        else {
          int lastEmbeddedObjectCount = ecCacheHandler.getLastElementCollectionObjectCount(id);
          for (Object obj : (Collection)superColumnObject) {
            superColumnName = ecCacheHandler.getElementCollectionObjectName(id, obj);
            if (superColumnName == null) {
              superColumnName = superColumn.getName() + Constants.EMBEDDED_COLUMN_NAME_DELIMITER + (++lastEmbeddedObjectCount);
            }
            SuperColumn thriftSuperColumn = buildThriftSuperColumn(superColumnName, timestamp, superColumn, obj);
            tr.addSuperColumn(thriftSuperColumn);
          }
        }
      }
      else {
        superColumnName = superColumn.getName();
        SuperColumn thriftSuperColumn = buildThriftSuperColumn(superColumnName, timestamp, superColumn, superColumnObject);
        tr.addSuperColumn(thriftSuperColumn);
      }
    }
  }
  private SuperColumn buildThriftSuperColumn(String superColumnName, long timestamp, EmbeddedColumn superColumn, Object superColumnObject) throws PropertyAccessException {
    List<Column> thriftColumns = new ArrayList<Column>();
    for (com.impetus.kundera.metadata.model.Column column : superColumn.getColumns()) {
      Field field = column.getField();
      String name = column.getName();
      byte[] value = null;
      try {
        value = PropertyAccessorHelper.get(superColumnObject, field);
      }
      catch (PropertyAccessException exp) {
        log.info(exp.getMessage() + ". Possible case of entity column in a super column family. Will be treated as a super column.");
        value = superColumnObject.toString().getBytes();
      }
      if (null != value) {
        Column thriftColumn = new Column();
        thriftColumn.setName(PropertyAccessorFactory.STRING.toBytes(name));
        thriftColumn.setValue(value);
        thriftColumn.setTimestamp(timestamp);
        thriftColumns.add(thriftColumn);
      }
    }
    SuperColumn thriftSuperColumn = new SuperColumn();
    thriftSuperColumn.setName(PropertyAccessorFactory.STRING.toBytes(superColumnName));
    thriftSuperColumn.setColumns(thriftColumns);
    return thriftSuperColumn;
  }
  public  <E extends java.lang.Object> List<E> getForeignKeysFromJoinTable(String inverseJoinColumnName, List<Column> columns) {
    List<E> foreignKeys = new ArrayList<E>();
    if (columns == null || columns.isEmpty()) {
      return foreignKeys;
    }
    for (Column c : columns) {
      try {
        String thriftColumnName = PropertyAccessorFactory.STRING.fromBytes(c.getName());
        byte[] thriftColumnValue = c.getValue();
        if (null == thriftColumnValue) {
          continue ;
        }
        if (thriftColumnName != null && thriftColumnName.startsWith(inverseJoinColumnName)) {
          String val = PropertyAccessorFactory.STRING.fromBytes(thriftColumnValue);
          foreignKeys.add((E)val);
        }
      }
      catch (PropertyAccessException e) {
        continue ;
      }
    }
    return foreignKeys;
  }
  
  public class ThriftRow {
    private String id;
    private String columnFamilyName;
    private List<Column> columns;
    private List<SuperColumn> superColumns;
    public ThriftRow() {
      columns = new ArrayList<Column>();
      superColumns = new ArrayList<SuperColumn>();
    }
    public ThriftRow(String id, String columnFamilyName, List<Column> columns, List<SuperColumn> superColumns) {
      this.id = id;
      this.columnFamilyName = columnFamilyName;
      if (columns != null) {
        this.columns = columns;
      }
      if (superColumns != null) {
        this.superColumns = superColumns;
      }
    }
    public String getId() {
      return id;
    }
    public void setId(String id) {
      this.id = id;
    }
    public String getColumnFamilyName() {
      return columnFamilyName;
    }
    public void setColumnFamilyName(String columnFamilyName) {
      this.columnFamilyName = columnFamilyName;
    }
    public List<Column> getColumns() {
      return columns;
    }
    public void setColumns(List<Column> columns) {
      this.columns = columns;
    }
    public void addColumn(Column column) {
      columns.add(column);
    }
    public List<SuperColumn> getSuperColumns() {
      return superColumns;
    }
    public void setSuperColumns(List<SuperColumn> superColumns) {
      this.superColumns = superColumns;
    }
    public void addSuperColumn(SuperColumn superColumn) {
      this.superColumns.add(superColumn);
    }
  }
  public long getTimestamp() {
    return timestamp;
  }
}

