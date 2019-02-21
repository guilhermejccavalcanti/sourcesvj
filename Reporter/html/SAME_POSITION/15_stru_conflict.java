<<<<<<< MINE
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
>>>>>>> YOURS

