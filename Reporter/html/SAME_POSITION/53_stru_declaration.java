  @Override public BlobOutput getBlob(String blobIdString) throws CoordinatorException {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_b46877e_b0c21eb\rev_left_b46877e\ambry-coordinator\src\main\java\com.github.ambry.coordinator\AmbryCoordinator.java
BlobId blobId = getBlobIdFromString(blobIdString);
=======
try {
      BlobId id = new BlobId(blobId, map);
      DataNodeId node = id.getPartition().getReplicaIds().get(0).getDataNodeId();
      ConnectionPool resource = pool.get(node.getHostname() + node.getPort());
      if (resource == null) {
        resource = new ConnectionPool(node.getHostname(), node.getPort());
        pool.put(node.getHostname() + node.getPort(), resource);
      }
      BlockingChannel channel = resource.getConnection();
      GetResponse response = doGetResponse(id, MessageFormatFlags.Blob, channel);
      BlobOutput output = MessageFormatRecord.deserializeBlob(response.getInputStream());
      resource.returnConnection(channel);
      return output;
    }
    catch (Exception e) {
      System.out.println("error " + e);
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_b46877e_b0c21eb\rev_right_b0c21eb\ambry-coordinator\src\main\java\com.github.ambry.coordinator\AmbryCoordinator.java

    GetBlobOperation gbdo = new GetBlobOperation(datacenterName, connectionPool, requesterPool, getOperationContext(), blobId, operationTimeoutMs, clusterMap);
    gbdo.execute();
    return gbdo.getBlobOutput();
  }


