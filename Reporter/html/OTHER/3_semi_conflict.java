<<<<<<< MINE
        return insert(mongoInsert, codec);
||||||| BASE
        return insert(mongoInsert, serializer);
=======
        return new WriteResult(insertInternal(mongoInsert, serializer), aWriteConcern);
>>>>>>> YOURS

