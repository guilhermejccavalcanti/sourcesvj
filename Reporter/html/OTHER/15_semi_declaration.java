@Test
    public void shouldThrowQueryFailureException() {
<<<<<<< MINE
        collection.insert(new Document("loc", new double[] {0, 0}));
=======
        MongoCollection<Document> collection = getCollection();
        collection.insert(new MongoInsert<Document>(new Document("loc", new double[]{0, 0})));
>>>>>>> YOURS
        try {
<<<<<<< MINE
            collection.filter(new QueryFilterDocument("loc", new Document("$near", new double[] {0, 0}))).one();
=======
            collection.findOne(new MongoFind(new QueryFilterDocument("loc", new Document("$near", new double[]{0,
                    0}))));
>>>>>>> YOURS
            fail("Should be a query failure since there is no 2d index");
        } catch (MongoQueryFailureException e) {
            assertEquals(13038, e.getErrorCode());
        }
    }

