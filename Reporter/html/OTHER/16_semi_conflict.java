<<<<<<< MINE
        collection.save(document);
||||||| BASE
        collection.save(new MongoSave<Document>(document));
=======
        getCollection().save(new MongoSave<Document>(document));
>>>>>>> YOURS

