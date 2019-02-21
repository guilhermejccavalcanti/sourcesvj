  public void handleTTLRequest(Request request) throws IOException, InterruptedException {
    TTLRequest ttlRequest = TTLRequest.readFrom(new DataInputStream(request.getInputStream()), clusterMap);
    metrics.ttlBlobRequestQueueTime.update(SystemTime.getInstance().milliseconds() - request.
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_left_2cb49af\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
getStartTimeInMs()
=======
getStartTime()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_right_4a6a7fc\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
);
    metrics.ttlBlobRequestRate.mark();
    long startTime = SystemTime.getInstance().milliseconds();
    TTLResponse response = null;
    try {
      ServerErrorCode error = validateRequest(ttlRequest.getBlobId().getPartition(), false);
      if (error != ServerErrorCode.No_Error) {
        logger.error("Validating ttl request failed with error {}", error);
        response = new TTLResponse(ttlRequest.getCorrelationId(), ttlRequest.getClientId(), error);
      }
      else {
        MessageFormatInputStream stream = new TTLMessageFormatInputStream(ttlRequest.getBlobId(), ttlRequest.getNewTTL());
        MessageInfo info = new MessageInfo(ttlRequest.getBlobId(), stream.getSize(), ttlRequest.getNewTTL());
        ArrayList<MessageInfo> infoList = new ArrayList<MessageInfo>();
        infoList.add(info);
        MessageFormatWriteSet writeset = new MessageFormatWriteSet(stream, infoList);
        Store storeToUpdateTTL = storeManager.getStore(ttlRequest.getBlobId().getPartition());
        storeToUpdateTTL.updateTTL(writeset);
        response = new TTLResponse(ttlRequest.getCorrelationId(), ttlRequest.getClientId(), ServerErrorCode.No_Error);
      }
    }
    catch (StoreException e) {
      logger.error("Store exception on a put with error code {} and exception {}", e.getErrorCode(), e);
      if (e.getErrorCode() == StoreErrorCodes.ID_Not_Found) 
        metrics.idNotFoundError.inc();
      else 
        if (e.getErrorCode() == StoreErrorCodes.TTL_Expired) 
          metrics.ttlExpiredError.inc();
        else 
          if (e.getErrorCode() == StoreErrorCodes.ID_Deleted) 
            metrics.idDeletedError.inc();
          else 
            metrics.unExpectedStoreTTLError.inc();
      response = new TTLResponse(ttlRequest.getCorrelationId(), ttlRequest.getClientId(), ErrorMapping.getStoreErrorMapping(e.getErrorCode()));
    }
    catch (Exception e) {
      logger.error("Unknown exception on ttl {}", e);
      response = new TTLResponse(ttlRequest.getCorrelationId(), ttlRequest.getClientId(), ServerErrorCode.Unknown_Error);
    }
    finally {
      metrics.ttlBlobProcessingTime.update(SystemTime.getInstance().milliseconds() - startTime);
    }
    requestResponseChannel.sendResponse(response, request, new HistogramMeasurement(metrics.ttlBlobResponseQueueTime), new HistogramMeasurement(metrics.ttlBlobSendTime));
  }


