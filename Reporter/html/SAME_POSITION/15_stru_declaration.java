  @Override public void cleanup() {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\integration\okhttp\src\main\java\com\bumptech\glide\integration\okhttp\OkHttpStreamFetcher.java
if (stream != null) {
      try {
        stream.close();
      }
      catch (IOException e) {
      }
    }
=======
try {
      if (stream != null) {
        stream.close();
      }
    }
    catch (IOException e) {
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\integration\okhttp\src\main\java\com\bumptech\glide\integration\okhttp\OkHttpStreamFetcher.java

    if (responseBody != null) {
      try {
        responseBody.close();
      }
      catch (IOException e) {
      }
    }
  }


