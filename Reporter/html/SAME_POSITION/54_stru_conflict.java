<<<<<<< MINE
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
      GetResponse response = doGetResponse(id, MessageFormatFlags.BlobProperties, channel);
      BlobProperties properties = MessageFormatRecord.deserializeBlobProperties(response.getInputStream());
      resource.returnConnection(channel);
      return properties;
    }
    catch (Exception e) {
    }
>>>>>>> YOURS

