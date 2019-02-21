package com.mongodb;
import java.io.Closeable;
import com.mongodb.DBApiLayer.Result;
import com.mongodb.QueryOpBuilder;
import java.util.*;

public class DBCursor implements Iterator<DBObject>, Iterable<DBObject>, Closeable {
  public DBCursor(DBCollection collection, DBObject q, DBObject k, ReadPreference preference) {
    if (collection == null) {
      throw new IllegalArgumentException("collection is null");
    }
    _collection = collection;
    _query = q == null ? new BasicDBObject() : q;
    _keysWanted = k;
    _options = _collection.getOptions();
    _readPref = preference;
    _decoderFact = collection.getDBDecoderFactory();
  }
  static enum CursorType {
    ITERATOR(),

    ARRAY(),

  ;
  }
  public DBCursor copy() {
    DBCursor c = new DBCursor(_collection, _query, _keysWanted, _readPref);
    c._orderBy = _orderBy;
    c._hint = _hint;
    c._hintDBObj = _hintDBObj;
    c._limit = _limit;
    c._skip = _skip;
    c._options = _options;
    c._batchSize = _batchSize;
    c._snapshot = _snapshot;
    c._explain = _explain;
    if (_specialFields != null) 
      c._specialFields = new BasicDBObject(_specialFields.toMap());
    return c;
  }
  public Iterator<DBObject> iterator() {
    return this.copy();
  }
  public DBCursor sort(DBObject orderBy) {
    if (_it != null) 
      throw new IllegalStateException("can\'t sort after executing query");
    _orderBy = orderBy;
    return this;
  }
  public DBCursor addSpecial(String name, Object o) {
    if (_specialFields == null) 
      _specialFields = new BasicDBObject();
    _specialFields.put(name, o);
    return this;
  }
  public DBCursor hint(String indexName) {
    if (_it != null) 
      throw new IllegalStateException("can\'t hint after executing query");
    _hint = indexName;
    return this;
  }
  public DBCursor hint(DBObject indexKeys) {
    if (_it != null) 
      throw new IllegalStateException("can\'t hint after executing query");
    _hintDBObj = indexKeys;
    return this;
  }
  public DBCursor snapshot() {
    if (_it != null) 
      throw new IllegalStateException("can\'t snapshot after executing the query");
    _snapshot = true;
    return this;
  }
  public DBObject explain() {
    DBCursor c = copy();
    c._explain = true;
    if (c._limit > 0) {
      c._batchSize = c._limit * -1;
      c._limit = 0;
    }
    return c.next();
  }
  public DBCursor limit(int n) {
    if (_it != null) 
      throw new IllegalStateException("can\'t set limit after executing query");
    if (n > 0) 
      _limit = n;
    else 
      if (n < 0) 
        batchSize(n);
    return this;
  }
  public DBCursor batchSize(int n) {
    if (n == 1) 
      n = 2;
    if (_it != null) {
      if (_it instanceof DBApiLayer.Result) 
        ((DBApiLayer.Result)_it).setBatchSize(n);
    }
    _batchSize = n;
    return this;
  }
  public DBCursor skip(int n) {
    if (_it != null) 
      throw new IllegalStateException("can\'t set skip after executing query");
    _skip = n;
    return this;
  }
  public long getCursorId() {
    if (_it instanceof Result) 
      return ((Result)_it).getCursorId();
    return 0;
  }
  public void close() {
    if (_it instanceof Result) 
      ((Result)_it).close();
  }
  @Deprecated public DBCursor slaveOk() {
    return addOption(Bytes.QUERYOPTION_SLAVEOK);
  }
  public DBCursor addOption(int option) {
    if (option == Bytes.QUERYOPTION_EXHAUST) 
      throw new IllegalArgumentException("The exhaust option is not user settable.");
    _options |= option;
    return this;
  }
  public DBCursor setOptions(int options) {
    _options = options;
    return this;
  }
  public DBCursor resetOptions() {
    _options = 0;
    return this;
  }
  public int getOptions() {
    return _options;
  }
  private void _check() throws MongoException {
    if (_it != null) 
      return ;
    _lookForHints();
    DBObject queryOp = new QueryOpBuilder().addQuery(_query).addOrderBy(_orderBy).addHint(_hintDBObj).addHint(_hint).addExplain(_explain).addSnapshot(_snapshot).addSpecialFields(_specialFields).get();
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_e499124_f45f8f4\rev_left_e499124\src\main\com\mongodb\DBCursor.java
if (hasSpecialQueryFields()) {
      foo = _specialFields == null ? new BasicDBObject() : _specialFields;
      _addToQueryObject(foo, "query", _query, true);
      _addToQueryObject(foo, "orderby", _orderBy, false);
      if (_hint != null) 
        _addToQueryObject(foo, "$hint", _hint);
      if (_hintDBObj != null) 
        _addToQueryObject(foo, "$hint", _hintDBObj);
      if (_explain) 
        foo.put("$explain", true);
      if (_snapshot) 
        foo.put("$snapshot", true);
      if (_readPref != null) 
        foo.put("$readPreference", _readPref.toDBObject());
    }
=======
_it = _collection.__find(queryOp, _keysWanted, _skip, _batchSize, _limit, _options, _readPref, getDecoder());
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_e499124_f45f8f4\rev_right_f45f8f4\src\main\com\mongodb\DBCursor.java

    _it = _collection.__find(queryOp, _keysWanted, _skip, _batchSize, _limit, _options, _readPref, getDecoder());
  }
  private DBDecoder getDecoder() {
    return _decoderFact != null ? _decoderFact.create() : null;
  }
  private void _lookForHints() {
    if (_hint != null) 
      return ;
    if (_collection._hintFields == null) 
      return ;
    Set<String> mykeys = _query.keySet();
    for (DBObject o : _collection._hintFields) {
      Set<String> hintKeys = o.keySet();
      if (!mykeys.containsAll(hintKeys)) 
        continue ;
      hint(o);
      return ;
    }
  }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_e499124_f45f8f4\rev_left_e499124\src\main\com\mongodb\DBCursor.java
boolean hasSpecialQueryFields() {
    if (_specialFields != null) 
      return true;
    if (_orderBy != null && _orderBy.keySet().size() > 0) 
      return true;
    if (_hint != null || _hintDBObj != null || _snapshot || _readPref != null) 
      return true;
    return _explain;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.

  void _checkType(CursorType type) {
    if (_cursorType == null) {
      _cursorType = type;
      return ;
    }
    if (type == _cursorType) 
      return ;
    throw new IllegalArgumentException("can\'t switch cursor access methods");
  }
  private DBObject _next() throws MongoException {
    if (_cursorType == null) 
      _checkType(CursorType.ITERATOR);
    _check();
    _cur = _it.next();
    _num++;
    if (_keysWanted != null && _keysWanted.keySet().size() > 0) {
      _cur.markAsPartialObject();
    }
    if (_cursorType == CursorType.ARRAY) {
      _all.add(_cur);
    }
    return _cur;
  }
  public int numGetMores() {
    if (_it instanceof DBApiLayer.Result) 
      return ((DBApiLayer.Result)_it).numGetMores();
    throw new IllegalArgumentException("_it not a real result");
  }
  public List<Integer> getSizes() {
    if (_it instanceof DBApiLayer.Result) 
      return ((DBApiLayer.Result)_it).getSizes();
    throw new IllegalArgumentException("_it not a real result");
  }
  private boolean _hasNext() throws MongoException {
    _check();
    if (_limit > 0 && _num >= _limit) 
      return false;
    return _it.hasNext();
  }
  public int numSeen() {
    return _num;
  }
  public boolean hasNext() throws MongoException {
    _checkType(CursorType.ITERATOR);
    return _hasNext();
  }
  public DBObject next() throws MongoException {
    _checkType(CursorType.ITERATOR);
    return _next();
  }
  public DBObject curr() {
    _checkType(CursorType.ITERATOR);
    return _cur;
  }
  public void remove() {
    throw new UnsupportedOperationException("can\'t remove from a cursor");
  }
  void _fill(int n) throws MongoException {
    _checkType(CursorType.ARRAY);
    while (n >= _all.size() && _hasNext())
      _next();
  }
  public int length() throws MongoException {
    _checkType(CursorType.ARRAY);
    _fill(Integer.MAX_VALUE);
    return _all.size();
  }
  public List<DBObject> toArray() throws MongoException {
    return toArray(Integer.MAX_VALUE);
  }
  public List<DBObject> toArray(int max) throws MongoException {
    _checkType(CursorType.ARRAY);
    _fill(max - 1);
    return _all;
  }
  public int itcount() {
    int n = 0;
    while (this.hasNext()){
      this.next();
      n++;
    }
    return n;
  }
  public int count() throws MongoException {
    if (_collection == null) 
      throw new IllegalArgumentException("why is _collection null");
    if (_collection._db == null) 
      throw new IllegalArgumentException("why is _collection._db null");
    return (int)_collection.getCount(this._query, this._keysWanted, getReadPreference());
  }
  public int size() throws MongoException {
    if (_collection == null) 
      throw new IllegalArgumentException("why is _collection null");
    if (_collection._db == null) 
      throw new IllegalArgumentException("why is _collection._db null");
    return (int)_collection.getCount(this._query, this._keysWanted, this._limit, this._skip, getReadPreference());
  }
  public DBObject getKeysWanted() {
    return _keysWanted;
  }
  public DBObject getQuery() {
    return _query;
  }
  public DBCollection getCollection() {
    return _collection;
  }
  public ServerAddress getServerAddress() {
    if (_it != null && _it instanceof DBApiLayer.Result) 
      return ((DBApiLayer.Result)_it).getServerAddress();
    return null;
  }
  public DBCursor setReadPreference(ReadPreference preference) {
    _readPref = preference;
    return this;
  }
  public ReadPreference getReadPreference() {
    return _readPref;
  }
  public DBCursor setDecoderFactory(DBDecoderFactory fact) {
    _decoderFact = fact;
    return this;
  }
  public DBDecoderFactory getDecoderFactory() {
    return _decoderFact;
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Cursor id=").append(getCursorId());
    sb.append(", ns=").append(getCollection().getFullName());
    sb.append(", query=").append(getQuery());
    if (getKeysWanted() != null) 
      sb.append(", fields=").append(getKeysWanted());
    sb.append(", numIterated=").append(_num);
    if (_skip != 0) 
      sb.append(", skip=").append(_skip);
    if (_limit != 0) 
      sb.append(", limit=").append(_limit);
    if (_batchSize != 0) 
      sb.append(", batchSize=").append(_batchSize);
    ServerAddress addr = getServerAddress();
    if (addr != null) 
      sb.append(", addr=").append(addr);
    if (_readPref != null) 
      sb.append(", readPreference=").append(_readPref.toString());
    return sb.toString();
  }
  private final DBCollection _collection;
  private final DBObject _query;
  private final DBObject _keysWanted;
  private DBObject _orderBy = null;
  private String _hint = null;
  private DBObject _hintDBObj = null;
  private boolean _explain = false;
  private int _limit = 0;
  private int _batchSize = 0;
  private int _skip = 0;
  private boolean _snapshot = false;
  private int _options = 0;
  private ReadPreference _readPref;
  private DBDecoderFactory _decoderFact;
  private DBObject _specialFields;
  private Iterator<DBObject> _it = null;
  private CursorType _cursorType = null;
  private DBObject _cur = null;
  private int _num = 0;
  private final ArrayList<DBObject> _all = new ArrayList<DBObject>();
  private int _num = 0;
  private final ArrayList<DBObject> _all = new ArrayList<DBObject>();
}

