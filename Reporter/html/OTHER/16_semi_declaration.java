@Test
    public void shouldReplaceIfPresent() {
        Document document = new Document();
<<<<<<< MINE
        collection.save(document);
=======
        getCollection().save(new MongoSave<Document>(document));
>>>>>>> YOURS

        document.put("x", 1);
<<<<<<< MINE
        collection.save(document);
        assertThat("Did not replace the document", collection.one(), is(document));
=======
        getCollection().save(new MongoSave<Document>(document));
        assertThat("Did not replace the document", getCollection().findOne(new MongoFind()), is(document));
>>>>>>> YOURS
    }

