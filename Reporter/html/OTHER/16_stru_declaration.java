  @Test public void shouldReplaceIfPresent() {
    Document document = new Document();
    getCollection().save(document);
    document.put("x", 1);
    getCollection().save(document);
    assertThat("Did not replace the document", 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_6a995e3_ff5bd7f\rev_left_6a995e3\driver\src\test\org\mongodb\MongoSaveTest.java
collection.one()
=======
getCollection().findOne(new MongoFind())
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_6a995e3_ff5bd7f\rev_right_ff5bd7f\driver\src\test\org\mongodb\MongoSaveTest.java
, is(document));
  }


