  @Override public Object next(Object previous) throws IOException {
    try {
      final Object result = reader.next(previous);
      rowInStripe += 1;
      advanceToNextRow(rowInStripe + rowBaseInStripe);
      if (isLogTraceEnabled) {
        LOG.trace("row from " + reader.path);
        LOG.trace("orc row = " + result);
      }
      return result;
    }
    catch (IOException e) {
      throw new IOException("Error reading file: " + path, e);
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_36aa3e9_35a7a81\rev_left_36aa3e9\ql\src\java\org\apache\hadoop\hive\ql\io\orc\RecordReaderImpl.java
advanceToNextRow(reader, rowInStripe + rowBaseInStripe, true);
=======
try {
      final Object result = reader.next(previous);
      rowInStripe += 1;
      advanceToNextRow(rowInStripe + rowBaseInStripe);
      if (isLogTraceEnabled) {
        LOG.trace("row from " + reader.path);
        LOG.trace("orc row = " + result);
      }
      return result;
    }
    catch (IOException e) {
      throw new IOException("Error reading file: " + path, e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_36aa3e9_35a7a81\rev_right_35a7a81\ql\src\java\org\apache\hadoop\hive\ql\io\orc\RecordReaderImpl.java

    advanceToNextRow(reader, rowInStripe + rowBaseInStripe, true);
    return result;
  }


