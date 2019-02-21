<<<<<<< MINE
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
>>>>>>> YOURS

