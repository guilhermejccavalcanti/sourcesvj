  @Test public void testAlwaysArgb8888() throws FileNotFoundException {
    Bitmap rgb565 = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
    compressBitmap(rgb565, Bitmap.CompressFormat.JPEG);
    Downsampler downsampler = Downsampler.AT_LEAST;
    InputStream is = new FileInputStream(tempFile);
    options.put(Downsampler.KEY_DECODE_FORMAT, DecodeFormat.ALWAYS_ARGB_8888);
    try {
      Bitmap result = downsampler.decode(is, mock(BitmapPool.class), 100, 100, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_f389e91_431ccaf\rev_left_f389e91\library\src\androidTest\java\com\bumptech\glide\load\resource\bitmap\DownsamplerTest.java
options
=======
DecodeFormat.PREFER_ARGB_8888
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_f389e91_431ccaf\rev_right_431ccaf\library\src\androidTest\java\com\bumptech\glide\load\resource\bitmap\DownsamplerTest.java
);
      assertEquals(Bitmap.Config.ARGB_8888, result.getConfig());
    }
  }


