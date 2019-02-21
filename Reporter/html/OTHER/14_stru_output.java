package org.mongodb;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.bson.types.Document;
import org.junit.Test;
import org.mongodb.command.MongoDuplicateKeyException;

public class GetLastErrorTest extends MongoClientTestBase {
  @Test public void testDuplicateKeyException() {
    Document doc = new Document("_id", 1);
    getCollection().insert(doc);
    try {
      getCollection().insert(doc);
      fail("Should throw MongoDuplicateKeyException");
    }
    catch (MongoDuplicateKeyException e) {
      assertThat(e.getCommandResult().getErrorCode(), is(11000));
    }
  }
}

