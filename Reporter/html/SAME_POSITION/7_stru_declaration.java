  public void handlePutRequest(Request request) throws IOException, InterruptedException {
    PutRequest putRequest = PutRequest.readFrom(new DataInputStream(request.getInputStream()), clusterMap);
    metrics.putBlobRequestQueueTime.update(SystemTime.getInstance().milliseconds() - request.
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_left_2cb49af\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
getStartTimeInMs()
=======
getStartTime()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_right_4a6a7fc\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
);
    metrics.putBlobRequestRate.mark();
    long startTime = SystemTime.getInstance().milliseconds();
    PutResponse response = null;
    try {
      ServerErrorCode error = validateRequest(putRequest.getBlobId().getPartition(), true);
      if (error != ServerErrorCode.No_Error) {
        logger.error("Validating put request failed with error {}", error);
        response = new PutResponse(putRequest.getCorrelationId(), putRequest.getClientId(), error);
      }
      else {
        MessageFormatInputStream stream = new PutMessageFormatInputStream(putRequest.getBlobId(), putRequest.getBlobProperties(), putRequest.getUsermetadata(), putRequest.getData(), putRequest.getBlobProperties().getBlobSize());
        MessageInfo info = new MessageInfo(putRequest.getBlobId(), stream.getSize(), putRequest.getBlobProperties().getTimeToLiveInMs());
        ArrayList<MessageInfo> infoList = new ArrayList<MessageInfo>();
        infoList.add(info);
        MessageFormatWriteSet writeset = new MessageFormatWriteSet(stream, infoList);
        Store storeToPut = storeManager.getStore(putRequest.getBlobId().getPartition());
        storeToPut.put(writeset);
        response = new PutResponse(putRequest.getCorrelationId(), putRequest.getClientId(), ServerErrorCode.No_Error);
      }
    }
    catch (StoreException e) {
      logger.error("Store exception on a put with error code {} and exception {}", e.getErrorCode(), e);
      if (e.getErrorCode() == StoreErrorCodes.Already_Exist) 
        metrics.idAlreadyExistError.inc();
      else 
        if (e.getErrorCode() == StoreErrorCodes.IOError) 
          metrics.storeIOError.inc();
        else 
          metrics.unExpectedStorePutError.inc();
      response = new PutResponse(putRequest.getCorrelationId(), putRequest.getClientId(), ErrorMapping.getStoreErrorMapping(e.getErrorCode()));
    }
    catch (Exception e) {
      logger.error("Unknown exception on a put {} ", e);
      response = new PutResponse(putRequest.getCorrelationId(), putRequest.getClientId(), ServerErrorCode.Unknown_Error);
    }
    finally {
      metrics.putBlobProcessingTime.update(SystemTime.getInstance().milliseconds() - startTime);
    }
    requestResponseChannel.sendResponse(response, request, new HistogramMeasurement(metrics.putBlobResponseQueueTime), new HistogramMeasurement(metrics.putBlobSendTime));
  }


