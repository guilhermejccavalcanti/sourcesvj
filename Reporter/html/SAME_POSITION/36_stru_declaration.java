  private void endToEndReplicationWithMultiNodeSinglePartitionTest(String coordinatorDatacenter, String sslEnabledDatacenters, int interestedDataNodePortNumber, Port dataNode1Port, Port dataNode2Port, Port dataNode3Port, MockCluster cluster) throws InterruptedException, IOException, InstantiationException {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_1f41948_4a4a3e3\rev_left_1f41948\ambry-server\src\test\java\com.github.ambry.server\ServerTest.java
try {
      MockClusterMap clusterMap = cluster.getClusterMap();
      byte[] usermetadata = new byte[1000];
      byte[] data = new byte[1000];
      BlobProperties properties = new BlobProperties(1000, "serviceid1");
      new Random().nextBytes(usermetadata);
      new Random().nextBytes(data);
      PartitionId partition = clusterMap.getWritablePartitionIds().get(0);
      BlobId blobId1 = new BlobId(partition);
      BlobId blobId2 = new BlobId(partition);
      BlobId blobId3 = new BlobId(partition);
      BlobId blobId4 = new BlobId(partition);
      BlobId blobId5 = new BlobId(partition);
      BlobId blobId6 = new BlobId(partition);
      BlobId blobId7 = new BlobId(partition);
      BlobId blobId8 = new BlobId(partition);
      BlobId blobId9 = new BlobId(partition);
      BlobId blobId10 = new BlobId(partition);
      BlobId blobId11 = new BlobId(partition);
      PutRequest putRequest = new PutRequest(1, "client1", blobId1, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      BlockingChannel channel1 = getBlockingChannelBasedOnPortType(dataNode1Port, "localhost", clientSSLSocketFactory1, clientSSLConfig1);
      BlockingChannel channel2 = getBlockingChannelBasedOnPortType(dataNode2Port, "localhost", clientSSLSocketFactory1, clientSSLConfig1);
      BlockingChannel channel3 = getBlockingChannelBasedOnPortType(dataNode3Port, "localhost", clientSSLSocketFactory1, clientSSLConfig1);
      channel1.connect();
      channel2.connect();
      channel3.connect();
      channel1.send(putRequest);
      InputStream putResponseStream = channel1.receive().getInputStream();
      PutResponse response = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response.getError(), ServerErrorCode.No_Error);
      PutRequest putRequest2 = new PutRequest(1, "client1", blobId2, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel2.send(putRequest2);
      putResponseStream = channel2.receive().getInputStream();
      PutResponse response2 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response2.getError(), ServerErrorCode.No_Error);
      PutRequest putRequest3 = new PutRequest(1, "client1", blobId3, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel3.send(putRequest3);
      putResponseStream = channel3.receive().getInputStream();
      PutResponse response3 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response3.getError(), ServerErrorCode.No_Error);
      putRequest = new PutRequest(1, "client1", blobId4, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel1.send(putRequest);
      putResponseStream = channel1.receive().getInputStream();
      response = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response.getError(), ServerErrorCode.No_Error);
      putRequest2 = new PutRequest(1, "client1", blobId5, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel2.send(putRequest2);
      putResponseStream = channel2.receive().getInputStream();
      response2 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response2.getError(), ServerErrorCode.No_Error);
      putRequest3 = new PutRequest(1, "client1", blobId6, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel3.send(putRequest3);
      putResponseStream = channel3.receive().getInputStream();
      response3 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response3.getError(), ServerErrorCode.No_Error);
      notificationSystem.awaitBlobCreations(blobId1.getID());
      notificationSystem.awaitBlobCreations(blobId2.getID());
      notificationSystem.awaitBlobCreations(blobId3.getID());
      notificationSystem.awaitBlobCreations(blobId4.getID());
      notificationSystem.awaitBlobCreations(blobId5.getID());
      notificationSystem.awaitBlobCreations(blobId6.getID());
      ArrayList<BlobId> ids = new ArrayList<BlobId>();
      MockPartitionId mockPartitionId = (MockPartitionId)clusterMap.getWritablePartitionIds().get(0);
      ids.add(blobId3);
      ArrayList<PartitionRequestInfo> partitionRequestInfoList = new ArrayList<PartitionRequestInfo>();
      PartitionRequestInfo partitionRequestInfo = new PartitionRequestInfo(mockPartitionId, ids);
      partitionRequestInfoList.add(partitionRequestInfo);
      GetRequest getRequest1 = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList, GetOptions.None);
      channel2.send(getRequest1);
      InputStream stream = channel2.receive().getInputStream();
      GetResponse resp1 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      Assert.assertEquals(resp1.getError(), ServerErrorCode.No_Error);
      Assert.assertEquals(resp1.getPartitionResponseInfoList().get(0).getErrorCode(), ServerErrorCode.No_Error);
      try {
        BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(resp1.getInputStream());
        Assert.assertEquals(propertyOutput.getBlobSize(), 1000);
        Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
      }
      catch (MessageFormatException e) {
        Assert.assertEquals(false, true);
      }
      ids.clear();
      ids.add(blobId2);
      GetRequest getRequest2 = new GetRequest(1, "clientid2", MessageFormatFlags.BlobUserMetadata, partitionRequestInfoList, GetOptions.None);
      channel1.send(getRequest2);
      stream = channel1.receive().getInputStream();
      GetResponse resp2 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      Assert.assertEquals(resp2.getError(), ServerErrorCode.No_Error);
      Assert.assertEquals(resp2.getPartitionResponseInfoList().get(0).getErrorCode(), ServerErrorCode.No_Error);
      try {
        ByteBuffer userMetadataOutput = MessageFormatRecord.deserializeUserMetadata(resp2.getInputStream());
        Assert.assertArrayEquals(userMetadataOutput.array(), usermetadata);
      }
      catch (MessageFormatException e) {
        Assert.assertEquals(false, true);
      }
      ids.clear();
      ids.add(blobId1);
      GetRequest getRequest3 = new GetRequest(1, "clientid2", MessageFormatFlags.Blob, partitionRequestInfoList, GetOptions.None);
      channel3.send(getRequest3);
      stream = channel3.receive().getInputStream();
      GetResponse resp3 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      try {
        BlobOutput blobOutput = MessageFormatRecord.deserializeBlob(resp3.getInputStream());
        byte[] blobout = new byte[(int)blobOutput.getSize()];
        int readsize = 0;
        while (readsize < blobOutput.getSize()){
          readsize += blobOutput.getStream().read(blobout, readsize, (int)blobOutput.getSize() - readsize);
        }
        Assert.assertArrayEquals(blobout, data);
      }
      catch (MessageFormatException e) {
        Assert.assertEquals(false, true);
      }
      try {
        coordinatorProps.setProperty("ssl.enabled.datacenters", sslEnabledDatacenters);
        Properties coordinatorProperties = getCoordinatorProperties(coordinatorDatacenter);
        coordinatorProperties.putAll(coordinatorProps);
        Coordinator coordinator = new AmbryCoordinator(new VerifiableProperties(coordinatorProperties), clusterMap);
        checkBlobId(coordinator, blobId1, data);
        checkBlobId(coordinator, blobId2, data);
        checkBlobId(coordinator, blobId3, data);
        checkBlobId(coordinator, blobId4, data);
        checkBlobId(coordinator, blobId5, data);
        checkBlobId(coordinator, blobId6, data);
        coordinator.close();
      }
      catch (CoordinatorException e) {
        e.printStackTrace();
        Assert.assertEquals(false, true);
      }
      ids = new ArrayList<BlobId>();
      mockPartitionId = (MockPartitionId)clusterMap.getWritablePartitionIds().get(0);
      ids.add(new BlobId(mockPartitionId));
      partitionRequestInfoList.clear();
      partitionRequestInfo = new PartitionRequestInfo(mockPartitionId, ids);
      partitionRequestInfoList.add(partitionRequestInfo);
      GetRequest getRequest4 = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList, GetOptions.None);
      channel3.send(getRequest4);
      stream = channel3.receive().getInputStream();
      GetResponse resp4 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      Assert.assertEquals(resp4.getError(), ServerErrorCode.No_Error);
      Assert.assertEquals(resp4.getPartitionResponseInfoList().get(0).getErrorCode(), ServerErrorCode.Blob_Not_Found);
      DeleteRequest deleteRequest = new DeleteRequest(1, "reptest", blobId1);
      channel1.send(deleteRequest);
      InputStream deleteResponseStream = channel1.receive().getInputStream();
      DeleteResponse deleteResponse = DeleteResponse.readFrom(new DataInputStream(deleteResponseStream));
      Assert.assertEquals(deleteResponse.getError(), ServerErrorCode.No_Error);
      notificationSystem.awaitBlobDeletions(blobId1.getID());
      ids = new ArrayList<BlobId>();
      ids.add(blobId1);
      partitionRequestInfoList.clear();
      partitionRequestInfo = new PartitionRequestInfo(partition, ids);
      partitionRequestInfoList.add(partitionRequestInfo);
      GetRequest getRequest5 = new GetRequest(1, "clientid2", MessageFormatFlags.Blob, partitionRequestInfoList, GetOptions.None);
      channel3.send(getRequest5);
      stream = channel3.receive().getInputStream();
      GetResponse resp5 = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
      Assert.assertEquals(resp5.getError(), ServerErrorCode.No_Error);
      Assert.assertEquals(resp5.getPartitionResponseInfoList().get(0).getErrorCode(), ServerErrorCode.Blob_Deleted);
      cluster.getServers().get(0).shutdown();
      cluster.getServers().get(0).awaitShutdown();
      DataNodeId dataNodeId = clusterMap.getDataNodeId("localhost", interestedDataNodePortNumber);
      List<String> mountPaths = ((MockDataNodeId)dataNodeId).getMountPaths();
      Set<String> setToCheck = new HashSet<String>();
      List<ReplicaId> replicaIds = clusterMap.getReplicaIds(dataNodeId);
      for (ReplicaId replicaId : replicaIds) {
        List<ReplicaId> peerReplicas = replicaId.getPeerReplicaIds();
        for (ReplicaId peerReplica : peerReplicas) {
          setToCheck.add(replicaId.getPartitionId().toString() + peerReplica.getDataNodeId().getHostname() + peerReplica.getDataNodeId().getPort());
        }
      }
      for (String mountPath : mountPaths) {
        File replicaTokenFile = new File(mountPath, "replicaTokens");
        if (replicaTokenFile.exists()) {
          CrcInputStream crcStream = new CrcInputStream(new FileInputStream(replicaTokenFile));
          DataInputStream dataInputStream = new DataInputStream(crcStream);
          try {
            short version = dataInputStream.readShort();
            Assert.assertEquals(version, 0);
            StoreKeyFactory storeKeyFactory = Utils.getObj("com.github.ambry.commons.BlobIdFactory", clusterMap);
            FindTokenFactory factory = Utils.getObj("com.github.ambry.store.StoreFindTokenFactory", storeKeyFactory);
            System.out.println("setToCheck" + setToCheck.size());
            while (dataInputStream.available() > 8){
              PartitionId partitionId = clusterMap.getPartitionIdFromStream(dataInputStream);
              String hostname = Utils.readIntString(dataInputStream);
              Utils.readIntString(dataInputStream);
              int port = dataInputStream.readInt();
              Assert.assertTrue(setToCheck.contains(partitionId.toString() + hostname + port));
              setToCheck.remove(partitionId.toString() + hostname + port);
              dataInputStream.readLong();
              FindToken token = factory.getFindToken(dataInputStream);
              System.out.println("partitionId " + partitionId + " hostname " + hostname + " port " + port + " token " + token);
              ByteBuffer bytebufferToken = ByteBuffer.wrap(token.toBytes());
              Assert.assertEquals(bytebufferToken.getShort(), 0);
              int size = bytebufferToken.getInt();
              bytebufferToken.position(bytebufferToken.position() + size);
              long parsedToken = bytebufferToken.getLong();
              System.out.println("The parsed token is " + parsedToken);
              Assert.assertTrue(parsedToken == -1 || parsedToken == 13062);
            }
            long crc = crcStream.getValue();
            Assert.assertEquals(crc, dataInputStream.readLong());
          }
          catch (IOException e) {
            Assert.assertTrue(false);
          }
          finally {
            dataInputStream.close();
          }
        }
        else {
          Assert.assertTrue(false);
        }
      }
      putRequest2 = new PutRequest(1, "client1", blobId7, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel2.send(putRequest2);
      putResponseStream = channel2.receive().getInputStream();
      response2 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response2.getError(), ServerErrorCode.No_Error);
      putRequest3 = new PutRequest(1, "client1", blobId8, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel3.send(putRequest3);
      putResponseStream = channel3.receive().getInputStream();
      response3 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response3.getError(), ServerErrorCode.No_Error);
      putRequest2 = new PutRequest(1, "client1", blobId9, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel2.send(putRequest2);
      putResponseStream = channel2.receive().getInputStream();
      response2 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response2.getError(), ServerErrorCode.No_Error);
      putRequest3 = new PutRequest(1, "client1", blobId10, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel3.send(putRequest3);
      putResponseStream = channel3.receive().getInputStream();
      response3 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response3.getError(), ServerErrorCode.No_Error);
      putRequest2 = new PutRequest(1, "client1", blobId11, properties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(data)));
      channel2.send(putRequest2);
      putResponseStream = channel2.receive().getInputStream();
      response2 = PutResponse.readFrom(new DataInputStream(putResponseStream));
      Assert.assertEquals(response2.getError(), ServerErrorCode.No_Error);
      cluster.getServers().get(0).startup();
      notificationSystem.awaitBlobCreations(blobId7.getID());
      notificationSystem.awaitBlobCreations(blobId8.getID());
      notificationSystem.awaitBlobCreations(blobId9.getID());
      notificationSystem.awaitBlobCreations(blobId10.getID());
      notificationSystem.awaitBlobCreations(blobId11.getID());
      channel1.disconnect();
      channel1.connect();
      try {
        checkBlobContent(clusterMap, blobId2, channel1, data);
        checkBlobContent(clusterMap, blobId3, channel1, data);
        checkBlobContent(clusterMap, blobId4, channel1, data);
        checkBlobContent(clusterMap, blobId5, channel1, data);
        checkBlobContent(clusterMap, blobId6, channel1, data);
        checkBlobContent(clusterMap, blobId7, channel1, data);
        checkBlobContent(clusterMap, blobId8, channel1, data);
        checkBlobContent(clusterMap, blobId9, channel1, data);
        checkBlobContent(clusterMap, blobId10, channel1, data);
        checkBlobContent(clusterMap, blobId11, channel1, data);
      }
      catch (MessageFormatException e) {
        Assert.assertFalse(true);
      }
      cluster.getServers().get(0).shutdown();
      cluster.getServers().get(0).awaitShutdown();
      File mountFile = new File(clusterMap.getReplicaIds(dataNodeId).get(0).getMountPath());
      for (File toDelete : mountFile.listFiles()) {
        deleteFolderContent(toDelete, true);
      }
      notificationSystem.decrementCreatedReplica(blobId2.getID());
      notificationSystem.decrementCreatedReplica(blobId3.getID());
      notificationSystem.decrementCreatedReplica(blobId4.getID());
      notificationSystem.decrementCreatedReplica(blobId5.getID());
      notificationSystem.decrementCreatedReplica(blobId6.getID());
      notificationSystem.decrementCreatedReplica(blobId7.getID());
      notificationSystem.decrementCreatedReplica(blobId8.getID());
      notificationSystem.decrementCreatedReplica(blobId9.getID());
      notificationSystem.decrementCreatedReplica(blobId10.getID());
      notificationSystem.decrementCreatedReplica(blobId11.getID());
      cluster.getServers().get(0).startup();
      notificationSystem.awaitBlobCreations(blobId2.getID());
      notificationSystem.awaitBlobCreations(blobId3.getID());
      notificationSystem.awaitBlobCreations(blobId4.getID());
      notificationSystem.awaitBlobCreations(blobId5.getID());
      notificationSystem.awaitBlobCreations(blobId6.getID());
      notificationSystem.awaitBlobCreations(blobId7.getID());
      notificationSystem.awaitBlobCreations(blobId8.getID());
      notificationSystem.awaitBlobCreations(blobId9.getID());
      notificationSystem.awaitBlobCreations(blobId10.getID());
      notificationSystem.awaitBlobCreations(blobId11.getID());
      channel1.disconnect();
      channel1.connect();
      try {
        checkBlobContent(clusterMap, blobId2, channel1, data);
        checkBlobContent(clusterMap, blobId3, channel1, data);
        checkBlobContent(clusterMap, blobId4, channel1, data);
        checkBlobContent(clusterMap, blobId5, channel1, data);
        checkBlobContent(clusterMap, blobId6, channel1, data);
        checkBlobContent(clusterMap, blobId7, channel1, data);
        checkBlobContent(clusterMap, blobId8, channel1, data);
        checkBlobContent(clusterMap, blobId9, channel1, data);
        checkBlobContent(clusterMap, blobId10, channel1, data);
        checkBlobContent(clusterMap, blobId11, channel1, data);
      }
      catch (MessageFormatException e) {
        Assert.assertFalse(true);
      }
      channel1.disconnect();
      channel2.disconnect();
      channel3.disconnect();
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
=======
cluster = new MockCluster(notificationSystem, enableSSLPorts, sslEnabledDatacentersForDC1, sslEnabledDatacentersForDC2, sslEnabledDatacentersForDC3, SystemTime.getInstance());
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_1f41948_4a4a3e3\rev_right_4a4a3e3\ambry-server\src\test\java\com.github.ambry.server\ServerTest.java

  }


