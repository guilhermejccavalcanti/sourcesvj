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


