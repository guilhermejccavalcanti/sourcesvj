  @Before public void setup() {
    bitmapPool = mock(BitmapPool.class);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_f389e91_431ccaf\rev_left_f389e91\library\src\androidTest\java\com\bumptech\glide\load\resource\bitmap\VideoBitmapDecoderTest.java
resource = mock(ParcelFileDescriptor.class);
=======
decodeFormat = DecodeFormat.PREFER_ARGB_8888;
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_f389e91_431ccaf\rev_right_431ccaf\library\src\androidTest\java\com\bumptech\glide\load\resource\bitmap\VideoBitmapDecoderTest.java

    factory = mock(VideoBitmapDecoder.MediaMetadataRetrieverFactory.class);
    retriever = mock(MediaMetadataRetriever.class);
    when(factory.build()).thenReturn(retriever);
    decoder = new VideoBitmapDecoder(new VideoBitmapDecoder.MediaMetadataRetrieverFactory() {
        @Override public MediaMetadataRetriever build() {
          return factory.build();
        }
    });
    options = new HashMap<>();
  }


