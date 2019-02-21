<<<<<<< MINE
            collection.insert(doc);
            fail("should throw exception");
||||||| BASE
            collection.insert(new MongoInsert<Document>(doc));
            fail("should throw exception");
=======
            getCollection().insert(new MongoInsert<Document>(doc));
            fail("Should throw MongoDuplicateKeyException");
>>>>>>> YOURS

