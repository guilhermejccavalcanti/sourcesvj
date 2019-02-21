  public void handleGetRequest(Request request) throws IOException, InterruptedException {
    GetRequest getRequest = GetRequest.readFrom(new DataInputStream(request.getInputStream()), clusterMap);
    HistogramMeasurement responseQueueMeasurement = null;
    HistogramMeasurement responseSendMeasurement = null;
    if (getRequest.getMessageFormatFlag() == MessageFormatFlags.Blob) {
      metrics.getBlobRequestQueueTime.update(SystemTime.getInstance().milliseconds() - request.
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_left_2cb49af\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
getStartTimeInMs()
=======
getStartTime()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_right_4a6a7fc\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
);
      metrics.getBlobRequestRate.mark();
      responseQueueMeasurement = new HistogramMeasurement(metrics.getBlobResponseQueueTime);
      responseSendMeasurement = new HistogramMeasurement(metrics.getBlobSendTime);
    }
    else 
      if (getRequest.getMessageFormatFlag() == MessageFormatFlags.BlobProperties) {
        metrics.getBlobPropertiesRequestQueueTime.update(SystemTime.getInstance().milliseconds() - request.
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_left_2cb49af\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
getStartTimeInMs()
=======
getStartTime()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_right_4a6a7fc\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
);
        metrics.getBlobPropertiesRequestRate.mark();
        responseQueueMeasurement = new HistogramMeasurement(metrics.getBlobPropertiesResponseQueueTime);
        responseSendMeasurement = new HistogramMeasurement(metrics.getBlobPropertiesSendTime);
      }
      else 
        if (getRequest.getMessageFormatFlag() == MessageFormatFlags.BlobUserMetadata) {
          metrics.getBlobUserMetadataRequestQueueTime.update(SystemTime.getInstance().milliseconds() - request.
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_left_2cb49af\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
getStartTimeInMs()
=======
getStartTime()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_right_4a6a7fc\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
);
          metrics.getBlobUserMetadataRequestRate.mark();
          responseQueueMeasurement = new HistogramMeasurement(metrics.getBlobUserMetadataResponseQueueTime);
          responseSendMeasurement = new HistogramMeasurement(metrics.getBlobUserMetadataSendTime);
        }
    long startTime = SystemTime.getInstance().milliseconds();
    GetResponse response = null;
    try {
      ServerErrorCode error = validateRequest(getRequest.getPartition(), false);
      if (error != ServerErrorCode.No_Error) {
        logger.error("Validating get request failed with error {}", error);
        response = new GetResponse(getRequest.getCorrelationId(), getRequest.getClientId(), error);
      }
      else {
        Store storeToGet = storeManager.getStore(getRequest.getPartition());
        StoreInfo info = storeToGet.get(getRequest.getBlobIds());
        Send blobsToSend = new MessageFormatSend(info.getMessageReadSet(), getRequest.getMessageFormatFlag(), messageFormatMetrics);
        response = new GetResponse(getRequest.getCorrelationId(), getRequest.getClientId(), info.getMessageReadSetInfo(), blobsToSend, ServerErrorCode.No_Error);
      }
    }
    catch (StoreException e) {
      logger.error("Store exception on a get with error code {} and exception {}", e.getErrorCode(), e);
      if (e.getErrorCode() == StoreErrorCodes.ID_Not_Found) 
        metrics.idNotFoundError.inc();
      else 
        if (e.getErrorCode() == StoreErrorCodes.TTL_Expired) 
          metrics.ttlExpiredError.inc();
        else 
          if (e.getErrorCode() == StoreErrorCodes.ID_Deleted) 
            metrics.idDeletedError.inc();
          else 
            metrics.unExpectedStoreGetError.inc();
      response = new GetResponse(getRequest.getCorrelationId(), getRequest.getClientId(), ErrorMapping.getStoreErrorMapping(e.getErrorCode()));
    }
    catch (MessageFormatException e) {
      logger.error("Message format exception on a get with error code {} and exception {}", e.getErrorCode(), e);
      if (e.getErrorCode() == MessageFormatErrorCodes.Data_Corrupt) 
        metrics.dataCorruptError.inc();
      else 
        if (e.getErrorCode() == MessageFormatErrorCodes.Unknown_Format_Version) 
          metrics.unknownFormatError.inc();
      response = new GetResponse(getRequest.getCorrelationId(), getRequest.getClientId(), ErrorMapping.getMessageFormatErrorMapping(e.getErrorCode()));
    }
    catch (Exception e) {
      logger.error("Unknown exception on a get {}", e);
      response = new GetResponse(getRequest.getCorrelationId(), getRequest.getClientId(), ServerErrorCode.Unknown_Error);
    }
    finally {
      if (getRequest.getMessageFormatFlag() == MessageFormatFlags.Blob) 
        metrics.getBlobProcessingTime.update(SystemTime.getInstance().milliseconds() - startTime);
      else 
        if (getRequest.getMessageFormatFlag() == MessageFormatFlags.BlobProperties) 
          metrics.getBlobPropertiesProcessingTime.update(SystemTime.getInstance().milliseconds() - startTime);
        else 
          if (getRequest.getMessageFormatFlag() == MessageFormatFlags.BlobUserMetadata) 
            metrics.getBlobUserMetadataProcessingTime.update(SystemTime.getInstance().milliseconds() - startTime);
    }
    requestResponseChannel.sendResponse(response, request, responseQueueMeasurement, responseSendMeasurement);
  }


