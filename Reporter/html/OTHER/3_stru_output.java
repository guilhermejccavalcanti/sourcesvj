package com.mongodb;
import static com.mongodb.DBObjects.toDBObject;
import static com.mongodb.DBObjects.toDocument;
import static com.mongodb.DBObjects.toFieldSelectorDocument;
import static com.mongodb.DBObjects.toUpdateOperationsDocument;
import com.mongodb.codecs.CollectibleDBObjectCodec;
import com.mongodb.codecs.DBEncoderDecoderCodec;
import org.bson.types.ObjectId;
import org.mongodb.Document;
import org.mongodb.Get;
import org.mongodb.Index;
import org.mongodb.MongoConnector;
import org.mongodb.MongoNamespace;
import org.mongodb.OrderBy;
import org.mongodb.annotations.ThreadSafe;
import org.mongodb.command.CollStats;
import org.mongodb.command.Count;
import org.mongodb.command.CountCommandResult;
import org.mongodb.command.Distinct;
import org.mongodb.command.DistinctCommandResult;
import org.mongodb.command.Drop;
import org.mongodb.command.DropIndex;
import org.mongodb.command.FindAndModifyCommandResult;
import org.mongodb.command.FindAndModifyCommandResultCodec;
import org.mongodb.command.FindAndRemove;
import org.mongodb.command.FindAndReplace;
import org.mongodb.command.FindAndUpdate;
import org.mongodb.command.MongoCommandFailureException;
import org.mongodb.command.MongoDuplicateKeyException;
import org.mongodb.command.RenameCollection;
import org.mongodb.command.RenameCollectionOptions;
import org.mongodb.command.MongoCommand;
import org.mongodb.operation.MongoFind;
import org.mongodb.operation.MongoFindAndRemove;
import org.mongodb.operation.MongoFindAndReplace;
import org.mongodb.operation.MongoFindAndUpdate;
import org.mongodb.operation.MongoInsert;
import org.mongodb.operation.MongoRemove;
import org.mongodb.operation.MongoReplace;
import org.mongodb.operation.MongoUpdate;
import org.mongodb.result.QueryResult;
import org.mongodb.Codec;
import org.mongodb.CollectibleCodec;
import org.mongodb.PrimitiveCodecs;
import org.mongodb.codecs.ObjectIdGenerator;
import org.mongodb.util.FieldHelpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ThreadSafe @SuppressWarnings(value = {"rawtypes", "deprecation", }) public class DBCollection implements IDBCollection {
  private static final String NAMESPACE_KEY_NAME = "ns";
  private final DB database;
  private final String name;
  private final Map<String, Class<? extends DBObject>> pathToClassMap = new HashMap<String, Class<? extends DBObject>>();
  private volatile ReadPreference readPreference;
  private volatile WriteConcern writeConcern;
  private List<DBObject> hintFields;
  private final Bytes.OptionHolder optionHolder;
  private DBEncoderFactory encoderFactory;
  private DBDecoderFactory decoderFactory;
  private final Codec<Document> documentCodec;
  private CollectibleCodec<DBObject> codec;
  DBCollection(final String name, final DB database, final Codec<Document> documentCodec) {
    this.name = name;
    this.database = database;
    this.documentCodec = documentCodec;
    optionHolder = new Bytes.OptionHolder(database.getOptionHolder());
    updateObjectCodec(BasicDBObject.class);
  }
  @Override public WriteResult insert(final DBObject document, final WriteConcern writeConcern) {
    return insert(Arrays.asList(document), writeConcern);
  }
  @Override public WriteResult insert(final DBObject ... documents) {
    return insert(Arrays.asList(documents), getWriteConcern());
  }
  @Override public WriteResult insert(final WriteConcern writeConcern, final DBObject ... documents) {
    return insert(documents, writeConcern);
  }
  @Override public WriteResult insert(final DBObject[] documents, final WriteConcern writeConcern) {
    return insert(Arrays.asList(documents), writeConcern);
  }
  @Override public WriteResult insert(final List<DBObject> documents) {
    return insert(documents, getWriteConcern());
  }
  @Override public WriteResult insert(final List<DBObject> documents, final WriteConcern aWriteConcern) {
    final MongoInsert<DBObject> mongoInsert = new MongoInsert<DBObject>(documents).writeConcern(aWriteConcern.toNew());
    return 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_left_16b59fa\driver-compat\src\main\com\mongodb\DBCollection.java
insert(mongoInsert, codec)
=======
new WriteResult(insertInternal(mongoInsert, serializer), aWriteConcern)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_right_7c9c613\driver-compat\src\main\com\mongodb\DBCollection.java
;
  }
  @Override public WriteResult insert(final DBObject[] documents, final WriteConcern aWriteConcern, final DBEncoder encoder) {
    return insert(Arrays.asList(documents), aWriteConcern, encoder);
  }
  @Override public WriteResult insert(final List<DBObject> documents, final WriteConcern aWriteConcern, final DBEncoder encoder) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_left_16b59fa\driver-compat\src\main\com\mongodb\DBCollection.java
final Codec<DBObject> codec;
=======
final Serializer<DBObject> serializer = toDBObjectSerializer(encoder);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_right_7c9c613\driver-compat\src\main\com\mongodb\DBCollection.java

    final MongoInsert<DBObject> mongoInsert = new MongoInsert<DBObject>(documents).writeConcern(this.writeConcern.toNew());
    return 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_left_16b59fa\driver-compat\src\main\com\mongodb\DBCollection.java
insert(mongoInsert, codec)
=======
new WriteResult(insertInternal(mongoInsert, serializer), aWriteConcern)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_right_7c9c613\driver-compat\src\main\com\mongodb\DBCollection.java
;
    return 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_left_16b59fa\driver-compat\src\main\com\mongodb\DBCollection.java
insert(mongoInsert, codec)
=======
new WriteResult(insertInternal(mongoInsert, serializer), aWriteConcern)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_right_7c9c613\driver-compat\src\main\com\mongodb\DBCollection.java
;
  }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_left_16b59fa\driver-compat\src\main\com\mongodb\DBCollection.java
private WriteResult insert(final MongoInsert<DBObject> mongoInsert, Codec<DBObject> codec) {
    try {
      final org.mongodb.result.WriteResult result = getConnector().insert(getNamespace(), mongoInsert, codec);
      return new WriteResult(result, writeConcern);
    }
    catch (MongoDuplicateKeyException e) {
      throw new MongoException.DuplicateKey(e);
    }
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.

  private Serializer<DBObject> toDBObjectSerializer(DBEncoder encoder) {
    Serializer<DBObject> serializer;
    if (encoder != null) {
      serializer = new DBEncoderDecoderSerializer(encoder, null, null, null);
    }
    else 
      if (encoderFactory != null) {
        serializer = new DBEncoderDecoderSerializer(encoderFactory.create(), null, null, null);
      }
      else {
        serializer = this.serializer;
      }
    return serializer;
  }
  private org.mongodb.result.WriteResult insertInternal(final MongoInsert<DBObject> mongoInsert, Serializer<DBObject> serializer) {
    try {
      return getConnector().insert(getNamespace(), mongoInsert, serializer);
    }
    catch (MongoDuplicateKeyException e) {
      throw new MongoException.DuplicateKey(e);
    }
  }
  @Override public WriteResult save(final DBObject document) {
    return save(document, getWriteConcern());
  }
  @Override public WriteResult save(final DBObject document, final WriteConcern writeConcern) {
    final Object id = getCodec().getId(document);
    if (id == null) {
      return insert(document, writeConcern);
    }
    else {
      return replaceOrInsert(document, writeConcern);
    }
  }
  private WriteResult replaceOrInsert(final DBObject obj, final WriteConcern wc) {
    final Document filter = new Document("_id", getCodec().getId(obj));
    final MongoReplace<DBObject> replace = new MongoReplace<DBObject>(filter, obj).upsert(true).writeConcern(wc.toNew());
    return new WriteResult(getConnector().replace(getNamespace(), replace, getDocumentCodec(), getCodec()), wc);
  }
  @Override public WriteResult update(final DBObject query, final DBObject update, final boolean upsert, final boolean multi, final WriteConcern aWriteConcern) {
    if (update == null) {
      throw new IllegalArgumentException("update can not be null");
    }
    if (query == null) {
      throw new IllegalArgumentException("update query can not be null");
    }
    final MongoUpdate mongoUpdate = new MongoUpdate(toDocument(query), toDocument(update)).upsert(upsert).multi(multi).writeConcern(aWriteConcern.toNew());
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_left_16b59fa\driver-compat\src\main\com\mongodb\DBCollection.java
try {
      final org.mongodb.result.WriteResult result = getConnector().update(getNamespace(), mongoUpdate, documentCodec);
      return new WriteResult(result, writeConcern);
    }
    catch (org.mongodb.MongoException e) {
      throw new MongoException(e);
    }
=======
return new WriteResult(updateInternal(mongoUpdate), aWriteConcern);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_right_7c9c613\driver-compat\src\main\com\mongodb\DBCollection.java

  }
  private org.mongodb.result.WriteResult updateInternal(MongoUpdate mongoUpdate) {
    try {
      return getConnector().update(getNamespace(), mongoUpdate, documentSerializer);
    }
    catch (org.mongodb.MongoException e) {
      throw new MongoException(e);
    }
  }
  @Override public WriteResult update(final DBObject query, final DBObject update, final boolean upsert, final boolean multi, final WriteConcern aWriteConcern, final DBEncoder encoder) {
    if (update == null) {
      throw new IllegalArgumentException("update can not be null");
    }
    if (query == null) {
      throw new IllegalArgumentException("update query can not be null");
    }
    final Document filter = toDocument(query, encoder, getDocumentSerializer());
    final Document updateOperations = toDocument(update, encoder, getDocumentSerializer());
    final MongoUpdate mongoUpdate = new MongoUpdate(filter, updateOperations).upsert(upsert).multi(multi).writeConcern(aWriteConcern.toNew());
    return new WriteResult(updateInternal(mongoUpdate), aWriteConcern);
  }
  @Override public WriteResult update(final DBObject query, final DBObject update, final boolean upsert, final boolean multi) {
    return update(query, update, upsert, multi, getWriteConcern());
  }
  @Override public WriteResult update(final DBObject query, final DBObject update) {
    return update(query, update, false, false);
  }
  @Override public WriteResult updateMulti(final DBObject query, final DBObject update) {
    return update(query, update, false, true);
  }
  @Override public WriteResult remove(final DBObject query) {
    return remove(query, getWriteConcern());
  }
  @Override public WriteResult remove(final DBObject query, final WriteConcern writeConcern) {
    final MongoRemove mongoRemove = new MongoRemove(toDocument(query)).writeConcern(writeConcern.toNew());
    final org.mongodb.result.WriteResult result = getConnector().remove(getNamespace(), mongoRemove, documentCodec);
    return new WriteResult(result, writeConcern);
  }
  @Override public WriteResult remove(final DBObject query, final WriteConcern writeConcern, final DBEncoder encoder) {
    final Document filter = toDocument(query, encoder, getDocumentSerializer());
    final MongoRemove mongoRemove = new MongoRemove(filter).writeConcern(writeConcern.toNew());
    final org.mongodb.result.WriteResult result = getConnector().remove(getNamespace(), mongoRemove, getDocumentSerializer());
    return new WriteResult(result, writeConcern);
  }
  @Override public DBCursor find(final DBObject query, final DBObject projection, final int numToSkip, final int batchSize, final int options) {
    return new DBCursor(this, query, projection, getReadPreference()).batchSize(batchSize).skip(numToSkip).setOptions(options);
  }
  @Override public DBCursor find(final DBObject query, final DBObject projection, final int numToSkip, final int batchSize) {
    return new DBCursor(this, query, projection, getReadPreference()).batchSize(batchSize).skip(numToSkip);
  }
  @Override public DBCursor find(final DBObject query) {
    return new DBCursor(this, query, null, getReadPreference());
  }
  @Override public DBCursor find(final DBObject query, final DBObject projection) {
    return new DBCursor(this, query, projection, getReadPreference());
  }
  @Override public DBCursor find() {
    return find(new BasicDBObject());
  }
  @Override public DBObject findOne() {
    return findOne(new BasicDBObject());
  }
  @Override public DBObject findOne(final DBObject query) {
    return findOne(query, null, null, getReadPreference());
  }
  @Override public DBObject findOne(final DBObject query, final DBObject projection) {
    return findOne(query, projection, null, getReadPreference());
  }
  @Override public DBObject findOne(final DBObject query, final DBObject projection, final DBObject sort) {
    return findOne(query, projection, sort, getReadPreference());
  }
  @Override public DBObject findOne(final DBObject query, final DBObject projection, final ReadPreference readPreference) {
    return findOne(query, projection, null, readPreference);
  }
  @Override public DBObject findOne(final DBObject query, final DBObject projection, final DBObject sort, final ReadPreference readPreference) {
    final MongoFind mongoFind = new MongoFind().select(toFieldSelectorDocument(projection)).where(toDocument(query)).order(toDocument(sort)).readPreference(readPreference.toNew()).batchSize(-1);
    final QueryResult<DBObject> res = getConnector().query(getNamespace(), mongoFind, documentCodec, getCodec());
    if (res.getResults().isEmpty()) {
      return null;
    }
    return res.getResults().get(0);
  }
  @Override public DBObject findOne(final Object id) {
    return findOne(id, null);
  }
  @Override public DBObject findOne(final Object id, final DBObject projection) {
    return findOne(new BasicDBObject("_id", id), projection);
  }
  @Override public Object apply(final DBObject document) {
    return apply(document, true);
  }
  @Override public Object apply(final DBObject document, final boolean ensureId) {
    Object id = document.get("_id");
    if (ensureId && id == null) {
      id = ObjectId.get();
      document.put("_id", id);
    }
    doapply(document);
    return id;
  }
  protected void doapply(final DBObject document) {
  }
  @Override public long count() {
    return getCount(new BasicDBObject(), null);
  }
  @Override public long count(final DBObject query) {
    return getCount(query, null);
  }
  @Override public long count(final DBObject query, final ReadPreference readPreference) {
    return getCount(query, null, readPreference);
  }
  @Override public long getCount() {
    return getCount(new BasicDBObject(), null);
  }
  @Override public long getCount(final ReadPreference readPreference) {
    return getCount(new BasicDBObject(), null, readPreference);
  }
  @Override public long getCount(final DBObject query) {
    return getCount(query, null);
  }
  @Override public long getCount(final DBObject query, final DBObject projection) {
    return getCount(query, projection, 0, 0);
  }
  @Override public long getCount(final DBObject query, final DBObject projection, final ReadPreference readPreference) {
    return getCount(query, projection, 0, 0, readPreference);
  }
  @Override public long getCount(final DBObject query, final DBObject projection, final long limit, final long skip) {
    return getCount(query, projection, limit, skip, getReadPreference());
  }
  @Override public long getCount(final DBObject query, final DBObject projection, final long limit, final long skip, final ReadPreference readPreference) {
    if (limit > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("limit is too large: " + limit);
    }
    if (skip > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("skip is too large: " + skip);
    }
    final Count countCommand = new Count(new MongoFind(toDocument(query)), getName());
    countCommand.limit((int)limit).skip((int)skip).readPreference(readPreference.toNew());
    return new CountCommandResult(getDB().executeCommand(countCommand)).getCount();
  }
  @Override public DBCollection rename(final String newName) {
    return rename(newName, false);
  }
  @Override public DBCollection rename(final String newName, final boolean dropTarget) {
    final RenameCollectionOptions renameCollectionOptions = new RenameCollectionOptions(getName(), newName, dropTarget);
    final RenameCollection renameCommand = new RenameCollection(renameCollectionOptions, getDB().getName());
    try {
      getConnector().command("admin", renameCommand, getDocumentCodec());
      return getDB().getCollection(newName);
    }
    catch (org.mongodb.MongoException e) {
      throw new MongoException(e);
    }
  }
  @Override public DBObject group(final DBObject key, final DBObject cond, final DBObject initial, final String reduce) {
    return group(key, cond, initial, reduce, null);
  }
  @Override public DBObject group(final DBObject key, final DBObject cond, final DBObject initial, final String reduce, final String finalize) {
    return group(key, cond, initial, reduce, finalize, getReadPreference());
  }
  @Override public DBObject group(final DBObject key, final DBObject cond, final DBObject initial, final String reduce, final String finalize, final ReadPreference readPreference) {
    throw new UnsupportedOperationException();
  }
  @Override public DBObject group(final GroupCommand cmd) {
    return group(cmd, getReadPreference());
  }
  @Override public DBObject group(final GroupCommand cmd, final ReadPreference readPreference) {
    throw new UnsupportedOperationException();
  }
  @Override @Deprecated public DBObject group(final DBObject args) {
    throw new UnsupportedOperationException();
  }
  @Override public List distinct(final String fieldName) {
    return distinct(fieldName, getReadPreference());
  }
  @Override public List distinct(final String fieldName, final ReadPreference readPreference) {
    return distinct(fieldName, new BasicDBObject(), readPreference);
  }
  @Override public List distinct(final String fieldName, final DBObject query) {
    return distinct(fieldName, query, getReadPreference());
  }
  @Override public List distinct(final String fieldName, final DBObject query, final ReadPreference readPreference) {
    final MongoFind mongoFind = new MongoFind().filter(toDocument(query)).readPreference(this.readPreference.toNew());
    final Distinct distinctOperation = new Distinct(getName(), fieldName, mongoFind);
    return new DistinctCommandResult(getDB().executeCommand(distinctOperation)).getValue();
  }
  @Override public MapReduceOutput mapReduce(final String map, final String reduce, final String outputTarget, final DBObject query) {
    final MapReduceCommand command = new MapReduceCommand(this, map, reduce, outputTarget, MapReduceCommand.OutputType.REDUCE, query);
    return mapReduce(command);
  }
  @Override public MapReduceOutput mapReduce(final String map, final String reduce, final String outputTarget, final MapReduceCommand.OutputType outputType, final DBObject query) {
    final MapReduceCommand command = new MapReduceCommand(this, map, reduce, outputTarget, outputType, query);
    return mapReduce(command);
  }
  @Override public MapReduceOutput mapReduce(final String map, final String reduce, final String outputTarget, final MapReduceCommand.OutputType outputType, final DBObject query, final ReadPreference readPreference) {
    final MapReduceCommand command = new MapReduceCommand(this, map, reduce, outputTarget, outputType, query);
    command.setReadPreference(readPreference);
    return mapReduce(command);
  }
  @Override public MapReduceOutput mapReduce(final MapReduceCommand command) {
    final DBObject cmd = command.toDBObject();
    final CommandResult res;
    if (command.getOutputType() == MapReduceCommand.OutputType.INLINE) {
      res = database.command(cmd, getOptions(), command.getReadPreference() != null ? command.getReadPreference() : getReadPreference());
    }
    else {
      res = database.command(cmd);
    }
    res.throwOnError();
    return new MapReduceOutput(this, cmd, res);
  }
  @Override public MapReduceOutput mapReduce(final DBObject command) {
    throw new UnsupportedOperationException();
  }
  @Override public AggregationOutput aggregate(final DBObject firstOp, final DBObject ... additionalOps) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }
  @Override public String getName() {
    return name;
  }
  @Override public String getFullName() {
    return getNamespace().getFullName();
  }
  @Override public DBCollection getCollection(final String name) {
    return database.getCollection(getName() + "." + name);
  }
  @Override public void ensureIndex(final DBObject keys) {
    ensureIndex(keys, (DBObject)null);
  }
  @Override public void ensureIndex(final DBObject keys, final String name) {
    final BasicDBObject options = new BasicDBObject("name", name);
    ensureIndex(keys, options);
  }
  @Override public void ensureIndex(final DBObject keys, final String name, final boolean unique) {
    final BasicDBObject options = new BasicDBObject("name", name);
    options.append("unique", unique);
    ensureIndex(keys, options);
  }
  @Override public void ensureIndex(final DBObject keys, final DBObject options) {
    final MongoInsert<Document> insertIndexOperation = new MongoInsert<Document>(toIndexDetailsDocument(keys, options));
    insertIndex(insertIndexOperation, documentCodec);
  }
  @Override public void ensureIndex(final String name) {
    final Index index = getIndexFromName(name);
    final Document indexDetails = index.toDocument();
    indexDetails.append(NAMESPACE_KEY_NAME, getNamespace().getFullName());
    final MongoInsert<Document> insertIndexOperation = new MongoInsert<Document>(indexDetails);
    insertIndex(insertIndexOperation, documentCodec);
  }
  @Override public void createIndex(final DBObject keys) {
    ensureIndex(keys);
  }
  @Override public void createIndex(final DBObject keys, final DBObject options) {
    ensureIndex(keys, options);
  }
  @Override public void createIndex(final DBObject keys, final DBObject options, final DBEncoder encoder) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_left_16b59fa\driver-compat\src\main\com\mongodb\DBCollection.java
final Codec<DBObject> codec;
=======
final Serializer<DBObject> serializer = toDBObjectSerializer(encoder);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_16b59fa_7c9c613\rev_right_7c9c613\driver-compat\src\main\com\mongodb\DBCollection.java

    final Document indexDetails = toIndexDetailsDocument(keys, options);
    final MongoInsert<DBObject> insertIndexOperation = new MongoInsert<DBObject>(toDBObject(indexDetails));
    insertIndex(insertIndexOperation, codec);
    insertIndex(insertIndexOperation, codec);
  }
  private  <T extends java.lang.Object> void insertIndex(final MongoInsert<T> insertIndexOperation, final Codec<T> codec) {
    insertIndexOperation.writeConcern(org.mongodb.WriteConcern.ACKNOWLEDGED);
    try {
      getConnector().insert(new MongoNamespace(getDB().getName(), "system.indexes"), insertIndexOperation, codec);
    }
    catch (MongoDuplicateKeyException exception) {
      throw new MongoException.DuplicateKey(exception);
    }
  }
  @Override public void resetIndexCache() {
  }
  @Override public void setHintFields(final List<DBObject> indexes) {
    hintFields = indexes;
  }
  @Override public DBObject findAndModify(final DBObject query, final DBObject sort, final DBObject update) {
    return findAndModify(query, null, sort, false, update, false, false);
  }
  @Override public DBObject findAndModify(final DBObject query, final DBObject update) {
    return findAndModify(query, null, null, false, update, false, false);
  }
  @Override public DBObject findAndRemove(final DBObject query) {
    return findAndModify(query, null, null, true, null, false, false);
  }
  @Override public DBObject findAndModify(final DBObject query, final DBObject fields, final DBObject sort, final boolean remove, final DBObject update, final boolean returnNew, final boolean upsert) {
    final MongoCommand mongoCommand;
    if (remove) {
      final MongoFindAndRemove<DBObject> mongoFindAndRemove = new MongoFindAndRemove<DBObject>().where(toDocument(query)).sortBy(toDocument(sort)).returnNew(returnNew);
      mongoCommand = new FindAndRemove<DBObject>(mongoFindAndRemove, getName());
    }
    else {
      if (update == null) {
        throw new IllegalArgumentException("Update document can\'t be null");
      }
      if (!update.keySet().isEmpty() && update.keySet().iterator().next().charAt(0) == '$') {
        final MongoFindAndUpdate<DBObject> mongoFindAndUpdate = new MongoFindAndUpdate<DBObject>().where(toDocument(query)).sortBy(toDocument(sort)).returnNew(returnNew).select(toDocument(fields)).updateWith(toUpdateOperationsDocument(update)).upsert(upsert);
        mongoCommand = new FindAndUpdate<DBObject>(mongoFindAndUpdate, getName());
      }
      else {
        final MongoFindAndReplace<DBObject> mongoFindAndReplace = new MongoFindAndReplace<DBObject>(update).where(toDocument(query)).sortBy(toDocument(sort)).select(toDocument(fields)).returnNew(returnNew).upsert(upsert);
        mongoCommand = new FindAndReplace<DBObject>(mongoFindAndReplace, getName());
      }
    }
    final FindAndModifyCommandResultCodec<DBObject> serializer = new FindAndModifyCommandResultCodec<DBObject>(PrimitiveCodecs.createDefault(), getCodec());
    final FindAndModifyCommandResult<DBObject> commandResult = new FindAndModifyCommandResult<DBObject>(getConnector().command(getDB().getName(), mongoCommand, serializer));
    return commandResult.getValue();
  }
  private Get asGetOrder(final boolean returnNew) {
    return returnNew ? Get.BeforeChangeApplied : Get.AfterChangeApplied;
  }
  @Override public DB getDB() {
    return database;
  }
  @Override public Class getObjectClass() {
    return codec.getEncoderClass();
  }
  public synchronized void setObjectClass(final Class<? extends DBObject> objectClass) {
    updateObjectCodec(objectClass);
  }
  @Override public WriteConcern getWriteConcern() {
    if (writeConcern != null) {
      return writeConcern;
    }
    return database.getWriteConcern();
  }
  @Override public void setWriteConcern(final WriteConcern writeConcern) {
    this.writeConcern = writeConcern;
  }
  @Override public ReadPreference getReadPreference() {
    if (readPreference != null) {
      return readPreference;
    }
    return database.getReadPreference();
  }
  @Override public void setReadPreference(final ReadPreference preference) {
    this.readPreference = preference;
  }
  @Override @Deprecated public void slaveOk() {
    addOption(Bytes.QUERYOPTION_SLAVEOK);
  }
  @Override public void addOption(final int option) {
    optionHolder.add(option);
  }
  @Override public void resetOptions() {
    optionHolder.reset();
  }
  @Override public int getOptions() {
    return optionHolder.get();
  }
  @Override public void setOptions(final int options) {
    optionHolder.set(options);
  }
  @Override public void drop() {
    try {
      org.mongodb.result.CommandResult commandResult = getDB().executeCommand(new Drop(getName()));
    }
    catch (MongoCommandFailureException ex) {
      if (!"ns not found".equals(ex.getErrorMessage())) {
        throw new MongoException(ex);
      }
    }
  }
  @Override public DBDecoderFactory getDBDecoderFactory() {
    return decoderFactory;
  }
  @Override public void setDBDecoderFactory(final DBDecoderFactory factory) {
    this.decoderFactory = factory;
  }
  @Override public DBEncoderFactory getDBEncoderFactory() {
    return this.encoderFactory;
  }
  @Override public void setDBEncoderFactory(final DBEncoderFactory factory) {
    this.encoderFactory = factory;
  }
  @Override public List<DBObject> getIndexInfo() {
    final ArrayList<DBObject> res = new ArrayList<DBObject>();
    final MongoFind queryForCollectionNamespace = new MongoFind(new Document(NAMESPACE_KEY_NAME, getNamespace().getFullName())).readPreference(org.mongodb.ReadPreference.primary());
    final QueryResult<Document> systemCollection = getConnector().query(new MongoNamespace(database.getName(), "system.indexes"), queryForCollectionNamespace, documentCodec, documentCodec);
    final List<Document> indexes = systemCollection.getResults();
    for (final Document curIndex : indexes) {
      res.add(DBObjects.toDBObject(curIndex));
    }
    return res;
  }
  @Override public void dropIndex(final DBObject keys) {
    final List<Index.Key> keysFromDBObject = getKeysFromDBObject(keys);
    final Index indexToDrop = new Index(keysFromDBObject.toArray(new Index.Key[keysFromDBObject.size()]));
    final DropIndex dropIndex = new DropIndex(getName(), indexToDrop.getName());
    getDB().executeCommand(dropIndex);
  }
  @Override public void dropIndex(final String name) {
    final DropIndex dropIndex = new DropIndex(getName(), name);
    getDB().executeCommand(dropIndex);
  }
  @Override public void dropIndexes() {
    dropIndexes("*");
  }
  @Override public void dropIndexes(final String name) {
    dropIndex(name);
  }
  @Override public CommandResult getStats() {
    final org.mongodb.result.CommandResult commandResult = getDB().executeCommand(new CollStats(getName()));
    return new CommandResult(commandResult);
  }
  @Override public boolean isCapped() {
    final CommandResult commandResult = getStats();
    final Object cappedField = commandResult.get("capped");
    return cappedField != null && (cappedField.equals(1) || cappedField.equals(true));
  }
  public synchronized void setInternalClass(final String path, final Class<? extends DBObject> clazz) {
    pathToClassMap.put(path, clazz);
  }
  private Index getIndexFromName(final String name) {
    final String[] keysAndTypes = name.split("_");
    final Index.Key[] keys = new Index.Key[keysAndTypes.length / 2];
    for (int i = 0; i < keysAndTypes.length; i = i + 2) {
      final String keyField = keysAndTypes[i];
      final String keyType = keysAndTypes[i + 1];
      final Index.Key key;
      if (keyType.equals("2d")) {
        key = new Index.GeoKey(keyField);
      }
      else {
        key = new Index.OrderedKey(keyField, OrderBy.fromInt(Integer.valueOf(keyType)));
      }
      keys[i / 2] = key;
    }
    return new Index(keys);
  }
  private void updateObjectCodec(final Class<? extends DBObject> objectClass) {
    final HashMap<String, Class<? extends DBObject>> map = new HashMap<String, Class<? extends DBObject>>(pathToClassMap);
    this.codec = new CollectibleDBObjectCodec(database, PrimitiveCodecs.createDefault(), new ObjectIdGenerator(), objectClass, map);
  }
  private Document toIndexDetailsDocument(DBObject keys, DBObject options) {
    String name = null;
    boolean unique = false;
    if (options != null) {
      if (options.get("name") != null) {
        name = (String)options.get("name");
      }
      if (options.get("unique") != null) {
        unique = FieldHelpers.asBoolean(options.get("unique"));
      }
    }
    final List<Index.Key> keyList = getKeysFromDBObject(keys);
    final Index index = new Index(name, unique, keyList.toArray(new Index.Key[keyList.size()]));
    final Document indexDetails = index.toDocument();
    indexDetails.append(NAMESPACE_KEY_NAME, getNamespace().getFullName());
    return indexDetails;
  }
  private List<Index.Key> getKeysFromDBObject(final DBObject fields) {
    final List<Index.Key> keys = new ArrayList<Index.Key>();
    for (final String key : fields.keySet()) {
      final Object keyType = fields.get(key);
      if (keyType instanceof Integer) {
        keys.add(new Index.OrderedKey(key, OrderBy.fromInt((Integer)fields.get(key))));
      }
      else 
        if (keyType.equals("2d")) {
          keys.add(new Index.GeoKey(key));
        }
        else {
          throw new UnsupportedOperationException("Unsupported index type: " + keyType);
        }
    }
    return keys;
  }
  MongoConnector getConnector() {
    return getDB().getConnector();
  }
  CollectibleCodec<DBObject> getCodec() {
    return codec;
  }
  MongoNamespace getNamespace() {
    return new MongoNamespace(getDB().getName(), getName());
  }
  Codec<Document> getDocumentCodec() {
    return documentCodec;
  }
  Bytes.OptionHolder getOptionHolder() {
    return optionHolder;
  }
}

