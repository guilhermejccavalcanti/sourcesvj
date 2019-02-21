@Test
    public void testDuplicateKeyException() {
        Document doc = new Document("_id", 1);
<<<<<<< MINE
        collection.insert(doc);
=======
        getCollection().insert(new MongoInsert<Document>(doc));
>>>>>>> YOURS
        try {
<<<<<<< MINE
            collection.insert(doc);
            fail("should throw exception");
=======
            getCollection().insert(new MongoInsert<Document>(doc));
            fail("Should throw MongoDuplicateKeyException");
>>>>>>> YOURS
        } catch (MongoDuplicateKeyException e) {
            assertThat(e.getCommandResult().getErrorCode(), is(11000));
        }
    }

