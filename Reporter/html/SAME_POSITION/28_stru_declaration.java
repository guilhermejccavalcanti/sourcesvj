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

    _it = _collection.__find(foo, _keysWanted, _skip, _batchSize, _limit, _options, _readPref, getDecoder());
  }


