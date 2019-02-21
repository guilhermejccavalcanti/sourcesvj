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
      GetResponse response = doGetResponse(id, MessageFormatFlags.Blob, channel);
      BlobOutput output = MessageFormatRecord.deserializeBlob(response.getInputStream());
      resource.returnConnection(channel);
      return output;
    }
    catch (Exception e) {
      System.out.println("error " + e);
    }
>>>>>>> YOURS

