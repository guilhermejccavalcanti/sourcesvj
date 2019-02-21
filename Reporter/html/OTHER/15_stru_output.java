package org.mongodb;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.bson.types.Document;
import org.junit.Test;

public class MongoFindTest extends MongoClientTestBase {
  @Test public void shouldThrowQueryFailureException() {
    MongoCollection<Document> collection = getCollection();
    collection.insert(new Document("loc", new double[]{ 0, 0 } ));
    try {
      
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_6a995e3_ff5bd7f\rev_left_6a995e3\driver\src\test\org\mongodb\MongoFindTest.java
collection.filter(new QueryFilterDocument("loc", new Document("$near", new double[]{ 0, 0 } ))).one()
=======
collection.findOne(new MongoFind(new QueryFilterDocument("loc", new Document("$near", new double[]{ 0, 0 } ))))
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\mongo-java-driver\revisions\rev_6a995e3_ff5bd7f\rev_right_ff5bd7f\driver\src\test\org\mongodb\MongoFindTest.java
;
      fail("Should be a query failure since there is no 2d index");
    }
    catch (MongoQueryFailureException e) {
      assertEquals(13038, e.getErrorCode());
    }
  }
}

