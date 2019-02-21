<<<<<<< MINE
        collection.insert(new Document("loc", new double[] {0, 0}));
||||||| BASE
        collection.insert(new MongoInsert<Document>(new Document("loc", new double[] {0, 0})));
=======
        MongoCollection<Document> collection = getCollection();
        collection.insert(new MongoInsert<Document>(new Document("loc", new double[]{0, 0})));
>>>>>>> YOURS

