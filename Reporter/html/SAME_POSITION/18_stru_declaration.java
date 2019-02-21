    @Override public long skip(long byteCount) throws IOException {
      final long result;
      if (returnZeroFlag) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\androidTest\java\com\bumptech\glide\load\resource\bitmap\ImageHeaderParserTest.java
return 0;
=======
result = 0;
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\androidTest\java\com\bumptech\glide\load\resource\bitmap\ImageHeaderParserTest.java

      }
      else {
        result = super.skip(byteCount);
      }
      returnZeroFlag = !returnZeroFlag;
      return 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_left_e161ca9\library\src\androidTest\java\com\bumptech\glide\load\resource\bitmap\ImageHeaderParserTest.java
super.skip(byteCount)
=======
result
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\glide\revisions\rev_e161ca9_7e0f873\rev_right_7e0f873\library\src\androidTest\java\com\bumptech\glide\load\resource\bitmap\ImageHeaderParserTest.java
;
    }


