<<<<<<< MINE
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
>>>>>>> YOURS

