@Test
  public void endToEndTestHardDeletes()
      throws Exception {
<<<<<<< MINE
    sslCluster = new MockCluster(notificationSystem, true, "DC1,DC2,DC3", serverSSLProps, true);
    sslCluster.startServers();
    MockClusterMap clusterMap = sslCluster.getClusterMap();
    DataNodeId dataNodeId = clusterMap.getDataNodeIds().get(0);
=======
    MockTime time = new MockTime(SystemTime.getInstance().milliseconds());
    cluster = new MockCluster(notificationSystem, true, "", "", "", time);
    MockClusterMap clusterMap = cluster.getClusterMap();
>>>>>>> YOURS
    ArrayList<byte[]> usermetadata = new ArrayList<byte[]>(9);
    ArrayList<byte[]> data = new ArrayList<byte[]>(9);
    for (int i = 0; i < 9; i++) {
      usermetadata.add(new byte[1000 + i]);
      data.add(new byte[31870 + i]);
      new Random().nextBytes(usermetadata.get(i));
      new Random().nextBytes(data.get(i));
    }

    ArrayList<BlobProperties> properties = new ArrayList<BlobProperties>(9);
    properties.add(new BlobProperties(31870, "serviceid1"));
    properties.add(new BlobProperties(31871, "serviceid1"));
    properties.add(new BlobProperties(31872, "serviceid1"));
    properties.add(new BlobProperties(31873, "serviceid1", "ownerid", "jpeg", false, 0));
    properties.add(new BlobProperties(31874, "serviceid1"));
    properties.add(new BlobProperties(31875, "serviceid1", "ownerid", "jpeg", false, 0));
    properties.add(new BlobProperties(31876, "serviceid1"));
    properties.add(new BlobProperties(31877, "serviceid1"));
    properties.add(new BlobProperties(31878, "serviceid1"));

    List<PartitionId> partitionIds = clusterMap.getWritablePartitionIds();
    ArrayList<BlobId> blobIdList = new ArrayList<BlobId>(9);
    blobIdList.add(new BlobId(partitionIds.get(0)));
    blobIdList.add(new BlobId(partitionIds.get(0)));
    blobIdList.add(new BlobId(partitionIds.get(0)));
    blobIdList.add(new BlobId(partitionIds.get(0)));
    blobIdList.add(new BlobId(partitionIds.get(0)));
    blobIdList.add(new BlobId(partitionIds.get(0)));
    blobIdList.add(new BlobId(partitionIds.get(0)));
    blobIdList.add(new BlobId(partitionIds.get(0)));
    blobIdList.add(new BlobId(partitionIds.get(0)));

    // put blob 0
    PutRequest putRequest0 =
        new PutRequest(1, "client1", blobIdList.get(0), properties.get(0), ByteBuffer.wrap(usermetadata.get(0)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(0))));
    BlockingChannel channel =
        getBlockingChannelBasedOnPortType(new Port(dataNodeId.getPort(), PortType.PLAINTEXT), "localhost",
            clientSSLSocketFactory1, clientSSLConfig1);
    channel.connect();
    channel.send(putRequest0);
    InputStream putResponseStream = channel.receive().getInputStream();
    PutResponse response0 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response0.getError(), ServerErrorCode.No_Error);

    // put blob 1
    PutRequest putRequest1 =
        new PutRequest(1, "client1", blobIdList.get(1), properties.get(1), ByteBuffer.wrap(usermetadata.get(1)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(1))));
    channel.send(putRequest1);
    putResponseStream = channel.receive().getInputStream();
    PutResponse response1 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response1.getError(), ServerErrorCode.No_Error);

    // put blob 2
    PutRequest putRequest2 =
        new PutRequest(1, "client1", blobIdList.get(2), properties.get(2), ByteBuffer.wrap(usermetadata.get(2)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(2))));
    channel.send(putRequest2);
    putResponseStream = channel.receive().getInputStream();
    PutResponse response2 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response2.getError(), ServerErrorCode.No_Error);

    // put blob 3 that is expired
    PutRequest putRequest3 =
        new PutRequest(1, "client1", blobIdList.get(3), properties.get(3), ByteBuffer.wrap(usermetadata.get(3)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(3))));
    channel.send(putRequest3);
    putResponseStream = channel.receive().getInputStream();
    PutResponse response3 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response3.getError(), ServerErrorCode.No_Error);

    // put blob 4
    PutRequest putRequest4 =
        new PutRequest(1, "client1", blobIdList.get(4), properties.get(4), ByteBuffer.wrap(usermetadata.get(4)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(4))));
    channel.send(putRequest4);
    putResponseStream = channel.receive().getInputStream();
    PutResponse response4 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response4.getError(), ServerErrorCode.No_Error);

    // put blob 5 that is expired
    PutRequest putRequest5 =
        new PutRequest(1, "client1", blobIdList.get(5), properties.get(5), ByteBuffer.wrap(usermetadata.get(5)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(5))));
    channel.send(putRequest5);
    putResponseStream = channel.receive().getInputStream();
    PutResponse response5 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response5.getError(), ServerErrorCode.No_Error);

    notificationSystem.awaitBlobCreations(blobIdList.get(0).getID());
    notificationSystem.awaitBlobCreations(blobIdList.get(1).getID());
    notificationSystem.awaitBlobCreations(blobIdList.get(2).getID());
    notificationSystem.awaitBlobCreations(blobIdList.get(4).getID());

    // delete blob 1
    DeleteRequest deleteRequest = new DeleteRequest(1, "client1", blobIdList.get(1));
    channel.send(deleteRequest);
    InputStream deleteResponseStream = channel.receive().getInputStream();
    DeleteResponse deleteResponse = DeleteResponse.readFrom(new DataInputStream(deleteResponseStream));
    Assert.assertEquals(deleteResponse.getError(), ServerErrorCode.No_Error);

    byte[] zeroedMetadata = new byte[usermetadata.get(1).length];
    usermetadata.set(1, zeroedMetadata);
    byte[] zeroedData = new byte[data.get(1).length];
    data.set(1, zeroedData);

    // delete blob 4
    deleteRequest = new DeleteRequest(1, "client1", blobIdList.get(4));
    channel.send(deleteRequest);
    deleteResponseStream = channel.receive().getInputStream();
    deleteResponse = DeleteResponse.readFrom(new DataInputStream(deleteResponseStream));
    Assert.assertEquals(deleteResponse.getError(), ServerErrorCode.No_Error);

    zeroedMetadata = new byte[usermetadata.get(4).length];
    usermetadata.set(4, zeroedMetadata);
    zeroedData = new byte[data.get(4).length];
    data.set(4, zeroedData);

    notificationSystem.awaitBlobDeletions(blobIdList.get(1).getID());
    notificationSystem.awaitBlobDeletions(blobIdList.get(4).getID());

    time.currentMilliseconds = time.currentMilliseconds + Time.SecsPerDay * Time.MsPerSec;
    ensureCleanupTokenCatchesUp(partitionIds.get(0).getReplicaIds().get(0).getReplicaPath(), clusterMap, 198431);
    ensureCleanupTokenCatchesUp(partitionIds.get(0).getReplicaIds().get(1).getReplicaPath(), clusterMap, 132299);
    ensureCleanupTokenCatchesUp(partitionIds.get(0).getReplicaIds().get(2).getReplicaPath(), clusterMap, 132299);

    MockPartitionId partition = (MockPartitionId) clusterMap.getWritablePartitionIds().get(0);

    ArrayList<PartitionRequestInfo> partitionRequestInfoList = new ArrayList<PartitionRequestInfo>();
    ArrayList<BlobId> ids = new ArrayList<BlobId>();
    for (int i = 0; i < 6; i++) {
      ids.add(blobIdList.get(i));
    }

    PartitionRequestInfo partitionRequestInfo = new PartitionRequestInfo(partition, ids);
    partitionRequestInfoList.add(partitionRequestInfo);

    try {
      GetRequest getRequest =
          new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList,
              GetOptions.Include_All);
      channel.send(getRequest);
      InputStream stream = channel.receive().getInputStream();
      GetResponse resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);

      for (int i = 0; i < 6; i++) {
        BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(resp.getInputStream());
        Assert.assertEquals(propertyOutput.getBlobSize(), properties.get(i).getBlobSize());
        Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
      }

      getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.BlobUserMetadata, partitionRequestInfoList,
          GetOptions.Include_All);
      channel.send(getRequest);
      stream = channel.receive().getInputStream();
      resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);

      for (int i = 0; i < 6; i++) {
        ByteBuffer userMetadataOutput = MessageFormatRecord.deserializeUserMetadata(resp.getInputStream());
        Assert.assertArrayEquals(userMetadataOutput.array(), usermetadata.get(i));
      }

      getRequest =
          new GetRequest(1, "clientid2", MessageFormatFlags.Blob, partitionRequestInfoList, GetOptions.Include_All);
      channel.send(getRequest);
      stream = channel.receive().getInputStream();
      resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);

      for (int i = 0; i < 6; i++) {
        BlobOutput blobOutput = MessageFormatRecord.deserializeBlob(resp.getInputStream());
        Assert.assertEquals(blobOutput.getSize(), properties.get(i).getBlobSize());
        byte[] dataOutput = new byte[(int) blobOutput.getSize()];
        blobOutput.getStream().read(dataOutput);
        Assert.assertArrayEquals(dataOutput, data.get(i));
      }
    } catch (Exception e) {
      Assert.assertEquals(false, true);
    }

    // put blob 6
    PutRequest putRequest6 =
        new PutRequest(1, "client1", blobIdList.get(6), properties.get(6), ByteBuffer.wrap(usermetadata.get(6)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(6))));
    channel.send(putRequest6);
    putResponseStream = channel.receive().getInputStream();
    PutResponse response6 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response6.getError(), ServerErrorCode.No_Error);

    // put blob 7
    PutRequest putRequest7 =
        new PutRequest(1, "client1", blobIdList.get(7), properties.get(7), ByteBuffer.wrap(usermetadata.get(7)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(7))));
    channel.send(putRequest7);
    putResponseStream = channel.receive().getInputStream();
    PutResponse response7 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response7.getError(), ServerErrorCode.No_Error);

    // put blob 8
    PutRequest putRequest8 =
        new PutRequest(1, "client1", blobIdList.get(8), properties.get(8), ByteBuffer.wrap(usermetadata.get(8)),
            new ByteBufferInputStream(ByteBuffer.wrap(data.get(8))));
    channel.send(putRequest8);
    putResponseStream = channel.receive().getInputStream();
    PutResponse response9 = PutResponse.readFrom(new DataInputStream(putResponseStream));
    Assert.assertEquals(response9.getError(), ServerErrorCode.No_Error);

    notificationSystem.awaitBlobCreations(blobIdList.get(6).getID());
    notificationSystem.awaitBlobCreations(blobIdList.get(7).getID());
    notificationSystem.awaitBlobCreations(blobIdList.get(8).getID());
    // Do more deletes

    // delete blob 3 that is expired.
    deleteRequest = new DeleteRequest(1, "client1", blobIdList.get(3));
    channel.send(deleteRequest);
    deleteResponseStream = channel.receive().getInputStream();
    deleteResponse = DeleteResponse.readFrom(new DataInputStream(deleteResponseStream));
    Assert.assertEquals(deleteResponse.getError(), ServerErrorCode.No_Error);

    zeroedMetadata = new byte[usermetadata.get(3).length];
    usermetadata.set(3, zeroedMetadata);
    zeroedData = new byte[data.get(3).length];
    data.set(3, zeroedData);

    // delete blob 0
    deleteRequest = new DeleteRequest(1, "client1", blobIdList.get(0));
    channel.send(deleteRequest);
    deleteResponseStream = channel.receive().getInputStream();
    deleteResponse = DeleteResponse.readFrom(new DataInputStream(deleteResponseStream));
    Assert.assertEquals(deleteResponse.getError(), ServerErrorCode.No_Error);

    zeroedMetadata = new byte[usermetadata.get(0).length];
    usermetadata.set(0, zeroedMetadata);
    zeroedData = new byte[data.get(0).length];
    data.set(0, zeroedData);

    // delete blob 6.
    deleteRequest = new DeleteRequest(1, "client1", blobIdList.get(6));
    channel.send(deleteRequest);
    deleteResponseStream = channel.receive().getInputStream();
    deleteResponse = DeleteResponse.readFrom(new DataInputStream(deleteResponseStream));
    Assert.assertEquals(deleteResponse.getError(), ServerErrorCode.No_Error);

    zeroedMetadata = new byte[usermetadata.get(6).length];
    usermetadata.set(6, zeroedMetadata);
    zeroedData = new byte[data.get(6).length];
    data.set(6, zeroedData);

    notificationSystem.awaitBlobDeletions(blobIdList.get(0).getID());
    notificationSystem.awaitBlobDeletions(blobIdList.get(6).getID());

    time.currentMilliseconds = time.currentMilliseconds + Time.SecsPerDay * Time.MsPerSec;
    ensureCleanupTokenCatchesUp(partitionIds.get(0).getReplicaIds().get(0).getReplicaPath(), clusterMap, 297905);
    ensureCleanupTokenCatchesUp(partitionIds.get(0).getReplicaIds().get(1).getReplicaPath(), clusterMap, 231676);
    ensureCleanupTokenCatchesUp(partitionIds.get(0).getReplicaIds().get(2).getReplicaPath(), clusterMap, 231676);

    partitionRequestInfoList = new ArrayList<PartitionRequestInfo>();
    partitionRequestInfo = new PartitionRequestInfo(partition, blobIdList);
    partitionRequestInfoList.add(partitionRequestInfo);

    try {
      GetRequest getRequest =
          new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList,
              GetOptions.Include_All);
      channel.send(getRequest);
      InputStream stream = channel.receive().getInputStream();
      GetResponse resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);

      for (int i = 0; i < 9; i++) {
        BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(resp.getInputStream());
        Assert.assertEquals(propertyOutput.getBlobSize(), properties.get(i).getBlobSize());
        Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
      }

      getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.BlobUserMetadata, partitionRequestInfoList,
          GetOptions.Include_All);
      channel.send(getRequest);
      stream = channel.receive().getInputStream();
      resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);

      for (int i = 0; i < 9; i++) {
        ByteBuffer userMetadataOutput = MessageFormatRecord.deserializeUserMetadata(resp.getInputStream());
        Assert.assertArrayEquals(userMetadataOutput.array(), usermetadata.get(i));
      }

      getRequest =
          new GetRequest(1, "clientid2", MessageFormatFlags.Blob, partitionRequestInfoList, GetOptions.Include_All);
      channel.send(getRequest);
      stream = channel.receive().getInputStream();
      resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);

      for (int i = 0; i < 9; i++) {
        BlobOutput blobOutput = MessageFormatRecord.deserializeBlob(resp.getInputStream());
        Assert.assertEquals(blobOutput.getSize(), properties.get(i).getBlobSize());
        byte[] dataOutput = new byte[(int) blobOutput.getSize()];
        blobOutput.getStream().read(dataOutput);
        Assert.assertArrayEquals(dataOutput, data.get(i));
      }
    } catch (MessageFormatException e) {
      e.printStackTrace();
      Assert.assertEquals(false, true);
    }
  }

