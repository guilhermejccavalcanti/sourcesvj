<<<<<<< MINE
try {
      MockClusterMap clusterMap = cluster.getClusterMap();
      byte[] usermetadata = new byte[1000];
      byte[] data = new byte[31870];
      BlobProperties properties = new BlobProperties(31870, "serviceid1");
      new Random().nextBytes(usermetadata);
      new Random().nextBytes(data);
      List<PartitionId> partitionIds = clusterMap.getWritablePartitionIds();
      BlobId blobId1 = new BlobId(partitionIds.get(0));
      BlobId blobId2 = new BlobId(partitionIds.get(0));
      BlobId blobId3 = new BlobId(partitionIds.get(0));
      BlobId blobId4 = new BlobId(partitionIds.get(0));
      PutRequest putRequest = new PutRequest(1, "client1", blobId1, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      BlockingChannel channel = getBlockingChannelBasedOnPortType(targetPort, "localhost", clientSSLSocketFactory1, clientSSLConfig1);
      channel.connect();
      channel.send(putRequest);
      InputStream putResponseStream = channel.receive().getInputStream();
      PutResponse response = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response.getError(), ServerErrorCode.No_Error);
      PutRequest putRequest2 = new PutRequest(1, "client1", blobId2, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel.send(putRequest2);
      putResponseStream = channel.receive().getInputStream();
      PutResponse response2 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response2.getError(), ServerErrorCode.No_Error);
      PutRequest putRequest3 = new PutRequest(1, "client1", blobId3, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel.send(putRequest3);
      putResponseStream = channel.receive().getInputStream();
      PutResponse response3 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response3.getError(), ServerErrorCode.No_Error);
      BlobProperties propertiesExpired = new BlobProperties(31870, "serviceid1", "ownerid", "jpeg", false, 0);
      PutRequest putRequest4 = new PutRequest(1, "client1", blobId4, propertiesExpired, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel.send(putRequest4);
      putResponseStream = channel.receive().getInputStream();
      PutResponse response4 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response4.getError(), ServerErrorCode.No_Error);
      ArrayList<BlobId> ids = new ArrayList<BlobId>();
      MockPartitionId partition = (MockPartitionId)clusterMap.getWritablePartitionIds().get(0);
      ids.add(blobId1);
      ArrayList<PartitionRequestInfo> partitionRequestInfoList = new ArrayList<PartitionRequestInfo>();
      PartitionRequestInfo partitionRequestInfo = new PartitionRequestInfo(partition, ids);
      partitionRequestInfoList.add(partitionRequestInfo);
      GetRequest getRequest1 = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList, GetOptions.None);
      channel.send(getRequest1);
      InputStream stream = channel.receive().getInputStream();
      GetResponse resp1 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      try {
        BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(resp1.getInputStream());
        Assert.assertEquals(propertyOutput.getBlobSize(), 31870);
        Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
      }
      catch (MessageFormatException e) {
        Assert.assertEquals(false, true);
      }
      ids = new ArrayList<BlobId>();
      partition = (MockPartitionId)clusterMap.getWritablePartitionIds().get(0);
      ids.add(blobId1);
      partitionRequestInfoList = new ArrayList<PartitionRequestInfo>();
      partitionRequestInfo = new PartitionRequestInfo(partition, ids);
      partitionRequestInfoList.add(partitionRequestInfo);
      getRequest1 = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList, GetOptions.Include_Expired_Blobs);
      channel.send(getRequest1);
      stream = channel.receive().getInputStream();
      resp1 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      try {
        BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(resp1.getInputStream());
        Assert.assertEquals(propertyOutput.getBlobSize(), 31870);
        Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
      }
      catch (MessageFormatException e) {
        Assert.assertEquals(false, true);
      }
      ArrayList<BlobId> idsExpired = new ArrayList<BlobId>();
      MockPartitionId partitionExpired = (MockPartitionId)clusterMap.getWritablePartitionIds().get(0);
      idsExpired.add(blobId4);
      ArrayList<PartitionRequestInfo> partitionRequestInfoListExpired = new ArrayList<PartitionRequestInfo>();
      PartitionRequestInfo partitionRequestInfoExpired = new PartitionRequestInfo(partitionExpired, idsExpired);
      partitionRequestInfoListExpired.add(partitionRequestInfoExpired);
      GetRequest getRequestExpired = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoListExpired, GetOptions.None);
      channel.send(getRequestExpired);
      InputStream streamExpired = channel.receive().getInputStream();
      GetResponse respExpired = GetResponse.readFrom(new DataInputStream(streamExpired), clusterMap);
      Assert.assertEquals(respExpired.getPartitionResponseInfoList().get(0).getErrorCode(), ServerErrorCode.Blob_Expired);
      idsExpired = new ArrayList<BlobId>();
      partitionExpired = (MockPartitionId)clusterMap.getWritablePartitionIds().get(0);
      idsExpired.add(blobId4);
      partitionRequestInfoListExpired = new ArrayList<PartitionRequestInfo>();
      partitionRequestInfoExpired = new PartitionRequestInfo(partitionExpired, idsExpired);
      partitionRequestInfoListExpired.add(partitionRequestInfoExpired);
      getRequestExpired = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoListExpired, GetOptions.Include_Expired_Blobs);
      channel.send(getRequestExpired);
      streamExpired = channel.receive().getInputStream();
      respExpired = GetResponse.readFrom(new DataInputStream(streamExpired), clusterMap);
      try {
        BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(respExpired.getInputStream());
        Assert.assertEquals(propertyOutput.getBlobSize(), 31870);
        Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
        Assert.assertEquals(propertyOutput.getOwnerId(), "ownerid");
      }
      catch (MessageFormatException e) {
        Assert.assertEquals(false, true);
      }
      GetRequest getRequest2 = new GetRequest(1, "clientid2", MessageFormatFlags.BlobUserMetadata, partitionRequestInfoList, GetOptions.None);
      channel.send(getRequest2);
      stream = channel.receive().getInputStream();
      GetResponse resp2 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      try {
        ByteBuffer userMetadataOutput = MessageFormatRecord.deserializeUserMetadata(resp2.getInputStream());
        Assert.assertArrayEquals(userMetadataOutput.array(), usermetadata);
      }
      catch (MessageFormatException e) {
        Assert.assertEquals(false, true);
      }
      try {
        coordinatorProps.setProperty("ssl.enabled.datacenters", sslEnabledDatacenters);
        Properties coordinatorProperties = getCoordinatorProperties(coordinatorDatacenter);
        coordinatorProperties.putAll(coordinatorProps);
        Coordinator coordinator = new AmbryCoordinator(new VerifiableProperties(coordinatorProperties), clusterMap);
        BlobOutput output = coordinator.getBlob(blobId1.getID());
        Assert.assertEquals(output.getSize(), 31870);
        byte[] dataOutputStream = new byte[(int)output.getSize()];
        output.getStream().read(dataOutputStream);
        Assert.assertArrayEquals(dataOutputStream, data);
        coordinator.close();
      }
      catch (CoordinatorException e) {
        e.printStackTrace();
        Assert.assertEquals(false, true);
      }
      ids = new ArrayList<BlobId>();
      partition = (MockPartitionId)clusterMap.getWritablePartitionIds().get(0);
      ids.add(new BlobId(partition));
      partitionRequestInfoList.clear();
      partitionRequestInfo = new PartitionRequestInfo(partition, ids);
      partitionRequestInfoList.add(partitionRequestInfo);
      GetRequest getRequest4 = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList, GetOptions.None);
      channel.send(getRequest4);
      stream = channel.receive().getInputStream();
      GetResponse resp4 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      Assert.assertEquals(resp4.getPartitionResponseInfoList().get(0).getErrorCode(), ServerErrorCode.Blob_Not_Found);
      channel.disconnect();
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.assertEquals(true, false);
    }
=======
cluster = new MockCluster(notificationSystem, enableSSLPorts, sslEnabledDatacentersForDC1, sslEnabledDatacentersForDC2, sslEnabledDatacentersForDC3, SystemTime.getInstance());
>>>>>>> YOURS

