<<<<<<< MINE
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
>>>>>>> YOURS

