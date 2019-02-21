  public void handleDeleteRequest(Request request) throws IOException, InterruptedException {
    DeleteRequest deleteRequest = DeleteRequest.readFrom(new DataInputStream(request.getInputStream()), clusterMap);
    metrics.deleteBlobRequestQueueTime.update(SystemTime.getInstance().milliseconds() - request.
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_left_2cb49af\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
getStartTimeInMs()
=======
getStartTime()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_right_4a6a7fc\ambry-server\src\main\java\com.github.ambry.server\AmbryRequests.java
);
    metrics.deleteBlobRequestRate.mark();
    long startTime = SystemTime.getInstance().milliseconds();
    DeleteResponse response = null;
    try {
      ServerErrorCode error = validateRequest(deleteRequest.getBlobId().getPartition(), false);
      if (error != ServerErrorCode.No_Error) {
        logger.error("Validating delete request failed with error {}", error);
        response = new DeleteResponse(deleteRequest.getCorrelationId(), deleteRequest.getClientId(), error);
      }
      else {
        MessageFormatInputStream stream = new DeleteMessageFormatInputStream(deleteRequest.getBlobId());
        MessageInfo info = new MessageInfo(deleteRequest.getBlobId(), stream.getSize());
        ArrayList<MessageInfo> infoList = new ArrayList<MessageInfo>();
        infoList.add(info);
        MessageFormatWriteSet writeset = new MessageFormatWriteSet(stream, infoList);
        Store storeToDelete = storeManager.getStore(deleteRequest.getBlobId().getPartition());
        storeToDelete.delete(writeset);
        response = new DeleteResponse(deleteRequest.getCorrelationId(), deleteRequest.getClientId(), ServerErrorCode.No_Error);
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
            metrics.unExpectedStoreDeleteError.inc();
      response = new DeleteResponse(deleteRequest.getCorrelationId(), deleteRequest.getClientId(), ErrorMapping.getStoreErrorMapping(e.getErrorCode()));
    }
    catch (Exception e) {
      logger.error("Unknown exception on delete {}", e);
      response = new DeleteResponse(deleteRequest.getCorrelationId(), deleteRequest.getClientId(), ServerErrorCode.Unknown_Error);
    }
    finally {
      metrics.deleteBlobProcessingTime.update(SystemTime.getInstance().milliseconds() - startTime);
    }
    requestResponseChannel.sendResponse(response, request, new HistogramMeasurement(metrics.deleteBlobResponseQueueTime), new HistogramMeasurement(metrics.deleteBlobSendTime));
  }


