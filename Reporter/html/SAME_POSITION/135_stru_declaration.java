  @Override public VectorizedRowBatch nextBatch(VectorizedRowBatch previous) throws IOException {
    try {
      final VectorizedRowBatch result;
      if (rowInStripe >= rowCountInStripe) {
        currentStripe += 1;
        readStripe();
      }
      long batchSize = 0;
      if (rowIndexStride != 0 && includedRowGroups != null && rowInStripe < rowCountInStripe) {
        int startRowGroup = (int)(rowInStripe / rowIndexStride);
        if (!includedRowGroups[startRowGroup]) {
          while (startRowGroup < includedRowGroups.length && !includedRowGroups[startRowGroup]){
            startRowGroup += 1;
          }
        }
        int endRowGroup = startRowGroup;
        while (endRowGroup < includedRowGroups.length && includedRowGroups[endRowGroup]){
          endRowGroup += 1;
        }
        final long markerPosition = (endRowGroup * rowIndexStride) < rowCountInStripe ? (endRowGroup * rowIndexStride) : rowCountInStripe;
        batchSize = Math.min(VectorizedRowBatch.DEFAULT_SIZE, (markerPosition - rowInStripe));
        if (isLogDebugEnabled && batchSize < VectorizedRowBatch.DEFAULT_SIZE) {
          LOG.debug("markerPosition: " + markerPosition + " batchSize: " + batchSize);
        }
      }
      else {
        batchSize = Math.min(VectorizedRowBatch.DEFAULT_SIZE, (rowCountInStripe - rowInStripe));
      }
      rowInStripe += batchSize;
      if (previous == null) {
        ColumnVector[] cols = (ColumnVector[])reader.nextVector(null, (int)batchSize);
        result = new VectorizedRowBatch(cols.length);
        result.cols = cols;
      }
      else {
        result = previous;
        result.selectedInUse = false;
        reader.nextVector(result.cols, (int)batchSize);
      }
      result.size = (int)batchSize;
      advanceToNextRow(rowInStripe + rowBaseInStripe);
      return result;
    }
    catch (IOException e) {
      throw new IOException("Error reading file: " + path, e);
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_36aa3e9_35a7a81\rev_left_36aa3e9\ql\src\java\org\apache\hadoop\hive\ql\io\orc\RecordReaderImpl.java
long batchSize = computeBatchSize(VectorizedRowBatch.DEFAULT_SIZE);
=======
try {
      final VectorizedRowBatch result;
      if (rowInStripe >= rowCountInStripe) {
        currentStripe += 1;
        readStripe();
      }
      long batchSize = 0;
      if (rowIndexStride != 0 && includedRowGroups != null && rowInStripe < rowCountInStripe) {
        int startRowGroup = (int)(rowInStripe / rowIndexStride);
        if (!includedRowGroups[startRowGroup]) {
          while (startRowGroup < includedRowGroups.length && !includedRowGroups[startRowGroup]){
            startRowGroup += 1;
          }
        }
        int endRowGroup = startRowGroup;
        while (endRowGroup < includedRowGroups.length && includedRowGroups[endRowGroup]){
          endRowGroup += 1;
        }
        final long markerPosition = (endRowGroup * rowIndexStride) < rowCountInStripe ? (endRowGroup * rowIndexStride) : rowCountInStripe;
        batchSize = Math.min(VectorizedRowBatch.DEFAULT_SIZE, (markerPosition - rowInStripe));
        if (isLogDebugEnabled && batchSize < VectorizedRowBatch.DEFAULT_SIZE) {
          LOG.debug("markerPosition: " + markerPosition + " batchSize: " + batchSize);
        }
      }
      else {
        batchSize = Math.min(VectorizedRowBatch.DEFAULT_SIZE, (rowCountInStripe - rowInStripe));
      }
      rowInStripe += batchSize;
      if (previous == null) {
        ColumnVector[] cols = (ColumnVector[])reader.nextVector(null, (int)batchSize);
        result = new VectorizedRowBatch(cols.length);
        result.cols = cols;
      }
      else {
        result = previous;
        result.selectedInUse = false;
        reader.nextVector(result.cols, (int)batchSize);
      }
      result.size = (int)batchSize;
      advanceToNextRow(rowInStripe + rowBaseInStripe);
      return result;
    }
    catch (IOException e) {
      throw new IOException("Error reading file: " + path, e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_36aa3e9_35a7a81\rev_right_35a7a81\ql\src\java\org\apache\hadoop\hive\ql\io\orc\RecordReaderImpl.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_36aa3e9_35a7a81\rev_left_36aa3e9\ql\src\java\org\apache\hadoop\hive\ql\io\orc\RecordReaderImpl.java
advanceToNextRow(reader, rowInStripe + rowBaseInStripe, true);
=======
try {
      final VectorizedRowBatch result;
      if (rowInStripe >= rowCountInStripe) {
        currentStripe += 1;
        readStripe();
      }
      long batchSize = 0;
      if (rowIndexStride != 0 && includedRowGroups != null && rowInStripe < rowCountInStripe) {
        int startRowGroup = (int)(rowInStripe / rowIndexStride);
        if (!includedRowGroups[startRowGroup]) {
          while (startRowGroup < includedRowGroups.length && !includedRowGroups[startRowGroup]){
            startRowGroup += 1;
          }
        }
        int endRowGroup = startRowGroup;
        while (endRowGroup < includedRowGroups.length && includedRowGroups[endRowGroup]){
          endRowGroup += 1;
        }
        final long markerPosition = (endRowGroup * rowIndexStride) < rowCountInStripe ? (endRowGroup * rowIndexStride) : rowCountInStripe;
        batchSize = Math.min(VectorizedRowBatch.DEFAULT_SIZE, (markerPosition - rowInStripe));
        if (isLogDebugEnabled && batchSize < VectorizedRowBatch.DEFAULT_SIZE) {
          LOG.debug("markerPosition: " + markerPosition + " batchSize: " + batchSize);
        }
      }
      else {
        batchSize = Math.min(VectorizedRowBatch.DEFAULT_SIZE, (rowCountInStripe - rowInStripe));
      }
      rowInStripe += batchSize;
      if (previous == null) {
        ColumnVector[] cols = (ColumnVector[])reader.nextVector(null, (int)batchSize);
        result = new VectorizedRowBatch(cols.length);
        result.cols = cols;
      }
      else {
        result = previous;
        result.selectedInUse = false;
        reader.nextVector(result.cols, (int)batchSize);
      }
      result.size = (int)batchSize;
      advanceToNextRow(rowInStripe + rowBaseInStripe);
      return result;
    }
    catch (IOException e) {
      throw new IOException("Error reading file: " + path, e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hive\revisions\rev_36aa3e9_35a7a81\rev_right_35a7a81\ql\src\java\org\apache\hadoop\hive\ql\io\orc\RecordReaderImpl.java

    rowInStripe += batchSize;
    if (previous == null) {
      ColumnVector[] cols = (ColumnVector[])reader.nextVector(null, (int)batchSize);
      result = new VectorizedRowBatch(cols.length);
      result.cols = cols;
    }
    else {
      result = (VectorizedRowBatch)previous;
      result.selectedInUse = false;
      reader.nextVector(result.cols, (int)batchSize);
    }
    result.size = (int)batchSize;
    advanceToNextRow(reader, rowInStripe + rowBaseInStripe, true);
    return result;
  }


