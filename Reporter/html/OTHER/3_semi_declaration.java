@Override
<<<<<<< MINE
    public WriteResult insert(final List<DBObject> documents, final WriteConcern writeConcern, final DBEncoder encoder) {
        final Codec<DBObject> codec;
        //TODO: Is this really how it should work?
        if (encoder != null) {
            codec = new DBEncoderDecoderCodec(encoder, null, null, null);
        } else if (encoderFactory != null) {
            codec = new DBEncoderDecoderCodec(encoderFactory.create(), null, null, null);
        } else {
            codec = this.codec;
        }
=======
    public WriteResult insert(final List<DBObject> documents, final WriteConcern aWriteConcern, final DBEncoder encoder) {
        final Serializer<DBObject> serializer = toDBObjectSerializer(encoder);
>>>>>>> YOURS

        final MongoInsert<DBObject> mongoInsert = new MongoInsert<DBObject>(documents)
                .writeConcern(this.writeConcern.toNew());
<<<<<<< MINE
        return insert(mongoInsert, codec);
=======
        return new WriteResult(insertInternal(mongoInsert, serializer), aWriteConcern);
>>>>>>> YOURS
    }

