/*
 * Copyright (c) 2008 - 2012 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.mongodb; 

import org.bson.types.Document; 
import org.junit.Test; 
 
 

import static org.junit.Assert.assertEquals; 
import static org.junit.Assert.fail; 

public  class  MongoFindTest  extends MongoClientTestBase {
	
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

}

