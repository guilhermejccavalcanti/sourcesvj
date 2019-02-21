<<<<<<< MINE
try {
      MockClusterMap clusterMap = cluster.getClusterMap();
      List<AmbryServer> serverList = cluster.getServers();
      byte[] usermetadata = new byte[100];
      byte[] data = new byte[100];
      BlobProperties properties = new BlobProperties(100, "serviceid1");
      new Random().nextBytes(usermetadata);
      new Random().nextBytes(data);
      BlockingChannel channel1 = getBlockingChannelBasedOnPortType(dataNode1Port, "localhost", clientSSLSocketFactory1, clientSSLConfig1);
      BlockingChannel channel2 = getBlockingChannelBasedOnPortType(dataNode2Port, "localhost", clientSSLSocketFactory2, clientSSLConfig2);
      BlockingChannel channel3 = getBlockingChannelBasedOnPortType(dataNode3Port, "localhost", clientSSLSocketFactory3, clientSSLConfig3);
      channel1.connect();
      channel2.connect();
      channel3.connect();
      int noOfParallelThreads = 3;
      CountDownLatch latch = new CountDownLatch(noOfParallelThreads);
      List<PutRequestRunnable> runnables = new ArrayList<PutRequestRunnable>(noOfParallelThreads);
      BlockingChannel channel = null;
      for (int i = 0; i < noOfParallelThreads; i++) {
        if (i % noOfParallelThreads == 0) {
          channel = channel1;
        }
        else 
          if (i % noOfParallelThreads == 1) {
            channel = channel2;
          }
          else 
            if (i % noOfParallelThreads == 2) {
              channel = channel3;
            }
        PutRequestRunnable runnable = new PutRequestRunnable(cluster, channel, 50, data, usermetadata, properties, latch);
        runnables.add(runnable);
        Thread threadToRun = new Thread(runnable);
        threadToRun.start();
      }
      latch.await();
      List<BlobId> blobIds = new ArrayList<BlobId>();
      for (int i = 0; i < runnables.size(); i++) {
        blobIds.addAll(runnables.get(i).getBlobIds());
      }
      for (BlobId blobId : blobIds) {
        notificationSystem.awaitBlobCreations(blobId.getID());
      }
      for (int i = 0; i < 3; i++) {
        channel = null;
        if (i == 0) {
          channel = channel1;
        }
        else 
          if (i == 1) {
            channel = channel2;
          }
          else 
            if (i == 2) {
              channel = channel3;
            }
        ArrayList<PartitionRequestInfo> partitionRequestInfoList = new ArrayList<PartitionRequestInfo>();
        for (int j = 0; j < blobIds.size(); j++) {
          ArrayList<BlobId> ids = new ArrayList<BlobId>();
          ids.add(blobIds.get(j));
          partitionRequestInfoList.clear();
          PartitionRequestInfo partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
          partitionRequestInfoList.add(partitionRequestInfo);
          GetRequest getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList, GetOptions.None);
          channel.send(getRequest);
          InputStream stream = channel.receive().getInputStream();
          GetResponse resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
          try {
            BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(resp.getInputStream());
            Assert.assertEquals(propertyOutput.getBlobSize(), 100);
            Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
          }
          catch (MessageFormatException e) {
            Assert.assertEquals(false, true);
          }
          ids.clear();
          ids.add(blobIds.get(j));
          partitionRequestInfoList.clear();
          partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
          partitionRequestInfoList.add(partitionRequestInfo);
          getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.BlobUserMetadata, partitionRequestInfoList, GetOptions.None);
          channel.send(getRequest);
          stream = channel.receive().getInputStream();
          resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
          try {
            ByteBuffer userMetadataOutput = MessageFormatRecord.deserializeUserMetadata(resp.getInputStream());
            Assert.assertArrayEquals(userMetadataOutput.array(), usermetadata);
          }
          catch (MessageFormatException e) {
            e.printStackTrace();
            Assert.assertEquals(false, true);
          }
          ids.clear();
          ids.add(blobIds.get(j));
          partitionRequestInfoList.clear();
          partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
          partitionRequestInfoList.add(partitionRequestInfo);
          getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.Blob, partitionRequestInfoList, GetOptions.None);
          channel.send(getRequest);
          stream = channel.receive().getInputStream();
          resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
          try {
            BlobOutput blobOutput = MessageFormatRecord.deserializeBlob(resp.getInputStream());
            byte[] blobout = new byte[(int)blobOutput.getSize()];
            int readsize = 0;
            while (readsize < blobOutput.getSize()){
              readsize += blobOutput.getStream().read(blobout, readsize, (int)blobOutput.getSize() - readsize);
            }
            Assert.assertArrayEquals(blobout, data);
          }
          catch (MessageFormatException e) {
            e.printStackTrace();
            Assert.assertEquals(false, true);
          }
        }
      }
      Set<BlobId> blobsDeleted = new HashSet<BlobId>();
      Set<BlobId> blobsChecked = new HashSet<BlobId>();
      for (int i = 0; i < blobIds.size(); i++) {
        int j = new Random().nextInt(3);
        if (j == 0) {
          j = new Random().nextInt(3);
          if (j == 0) {
            channel = channel1;
          }
          else 
            if (j == 1) {
              channel = channel2;
            }
            else 
              if (j == 2) {
                channel = channel3;
              }
          DeleteRequest deleteRequest = new DeleteRequest(1, "reptest", blobIds.get(i));
          channel.send(deleteRequest);
          InputStream deleteResponseStream = channel.receive().getInputStream();
          DeleteResponse deleteResponse = DeleteResponse.readFrom(new DataInputStream(deleteResponseStream));
          Assert.assertEquals(deleteResponse.getError(), ServerErrorCode.No_Error);
          blobsDeleted.add(blobIds.get(i));
        }
      }
      Iterator<BlobId> iterator = blobsDeleted.iterator();
      ArrayList<PartitionRequestInfo> partitionRequestInfoList = new ArrayList<PartitionRequestInfo>();
      while (iterator.hasNext()){
        BlobId deletedId = iterator.next();
        notificationSystem.awaitBlobDeletions(deletedId.getID());
        for (int j = 0; j < 3; j++) {
          if (j == 0) {
            channel = channel1;
          }
          else 
            if (j == 1) {
              channel = channel2;
            }
            else 
              if (j == 2) {
                channel = channel3;
              }
          ArrayList<BlobId> ids = new ArrayList<BlobId>();
          ids.add(deletedId);
          partitionRequestInfoList.clear();
          PartitionRequestInfo partitionRequestInfo = new PartitionRequestInfo(deletedId.getPartition(), ids);
          partitionRequestInfoList.add(partitionRequestInfo);
          GetRequest getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.Blob, partitionRequestInfoList, GetOptions.None);
          channel.send(getRequest);
          InputStream stream = channel.receive().getInputStream();
          GetResponse resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
          Assert.assertEquals(resp.getPartitionResponseInfoList().get(0).getErrorCode(), ServerErrorCode.Blob_Deleted);
        }
      }
      serverList.get(0).shutdown();
      serverList.get(0).awaitShutdown();
      MockDataNodeId dataNode = (MockDataNodeId)clusterMap.getDataNodeId("localhost", interestedDataNodePortNumber);
      System.out.println("Cleaning mount path " + dataNode.getMountPaths().get(0));
      for (ReplicaId replicaId : clusterMap.getReplicaIds(dataNode)) {
        if (replicaId.getMountPath().compareToIgnoreCase(dataNode.getMountPaths().get(0)) == 0) {
          System.out.println("Cleaning partition " + replicaId.getPartitionId());
        }
      }
      deleteFolderContent(new File(dataNode.getMountPaths().get(0)), false);
      int totalblobs = 0;
      for (int i = 0; i < blobIds.size(); i++) {
        for (ReplicaId replicaId : blobIds.get(i).getPartition().getReplicaIds()) {
          if (replicaId.getMountPath().compareToIgnoreCase(dataNode.getMountPaths().get(0)) == 0) {
            if (blobsDeleted.contains(blobIds.get(i))) {
              notificationSystem.decrementDeletedReplica(blobIds.get(i).getID());
            }
            else {
              totalblobs++;
              notificationSystem.decrementCreatedReplica(blobIds.get(i).getID());
            }
          }
        }
      }
      serverList.get(0).startup();
      channel1.disconnect();
      channel1.connect();
      for (int j = 0; j < blobIds.size(); j++) {
        if (blobsDeleted.contains(blobIds.get(j))) {
          notificationSystem.awaitBlobDeletions(blobIds.get(j).getID());
        }
        else {
          notificationSystem.awaitBlobCreations(blobIds.get(j).getID());
        }
        ArrayList<BlobId> ids = new ArrayList<BlobId>();
        ids.add(blobIds.get(j));
        partitionRequestInfoList.clear();
        PartitionRequestInfo partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
        partitionRequestInfoList.add(partitionRequestInfo);
        GetRequest getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList, GetOptions.None);
        channel1.send(getRequest);
        InputStream stream = channel1.receive().getInputStream();
        GetResponse resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
        if (resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Deleted || resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Not_Found) {
          Assert.assertTrue(blobsDeleted.contains(blobIds.get(j)));
        }
        else {
          try {
            BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(resp.getInputStream());
            Assert.assertEquals(propertyOutput.getBlobSize(), 100);
            Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
          }
          catch (MessageFormatException e) {
            Assert.assertEquals(false, true);
          }
        }
        ids.clear();
        ids.add(blobIds.get(j));
        partitionRequestInfoList.clear();
        partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
        partitionRequestInfoList.add(partitionRequestInfo);
        getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.BlobUserMetadata, partitionRequestInfoList, GetOptions.None);
        channel1.send(getRequest);
        stream = channel1.receive().getInputStream();
        resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
        if (resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Deleted || resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Not_Found) {
          Assert.assertTrue(blobsDeleted.contains(blobIds.get(j)));
        }
        else {
          try {
            ByteBuffer userMetadataOutput = MessageFormatRecord.deserializeUserMetadata(resp.getInputStream());
            Assert.assertArrayEquals(userMetadataOutput.array(), usermetadata);
          }
          catch (MessageFormatException e) {
            Assert.assertEquals(false, true);
          }
        }
        ids.clear();
        ids.add(blobIds.get(j));
        partitionRequestInfoList.clear();
        partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
        partitionRequestInfoList.add(partitionRequestInfo);
        getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.Blob, partitionRequestInfoList, GetOptions.None);
        channel1.send(getRequest);
        stream = channel1.receive().getInputStream();
        resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
        if (resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Deleted || resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Not_Found) {
          Assert.assertTrue(blobsDeleted.contains(blobIds.get(j)));
          blobsDeleted.remove(blobIds.get(j));
          blobsChecked.add(blobIds.get(j));
        }
        else {
          try {
            BlobOutput blobOutput = MessageFormatRecord.deserializeBlob(resp.getInputStream());
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
        }
      }
      Assert.assertEquals(blobsDeleted.size(), 0);
      serverList.get(0).shutdown();
      serverList.get(0).awaitShutdown();
      dataNode = (MockDataNodeId)clusterMap.getDataNodeId("localhost", interestedDataNodePortNumber);
      for (int i = 0; i < dataNode.getMountPaths().size(); i++) {
        System.out.println("Cleaning mount path " + dataNode.getMountPaths().get(i));
        for (ReplicaId replicaId : clusterMap.getReplicaIds(dataNode)) {
          if (replicaId.getMountPath().compareToIgnoreCase(dataNode.getMountPaths().get(i)) == 0) {
            System.out.println("Cleaning partition " + replicaId.getPartitionId());
          }
        }
        deleteFolderContent(new File(dataNode.getMountPaths().get(i)), false);
      }
      for (int i = 0; i < blobIds.size(); i++) {
        if (blobsChecked.contains(blobIds.get(i))) {
          notificationSystem.decrementDeletedReplica(blobIds.get(i).getID());
        }
        else {
          notificationSystem.decrementCreatedReplica(blobIds.get(i).getID());
        }
      }
      serverList.get(0).startup();
      channel1.disconnect();
      channel1.connect();
      for (int j = 0; j < blobIds.size(); j++) {
        if (blobsChecked.contains(blobIds.get(j))) {
          notificationSystem.awaitBlobDeletions(blobIds.get(j).getID());
        }
        else {
          notificationSystem.awaitBlobCreations(blobIds.get(j).getID());
        }
        ArrayList<BlobId> ids = new ArrayList<BlobId>();
        ids.add(blobIds.get(j));
        partitionRequestInfoList.clear();
        PartitionRequestInfo partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
        partitionRequestInfoList.add(partitionRequestInfo);
        GetRequest getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.BlobProperties, partitionRequestInfoList, GetOptions.None);
        channel1.send(getRequest);
        InputStream stream = channel1.receive().getInputStream();
        GetResponse resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
        if (resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Deleted || resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Not_Found) {
          Assert.assertTrue(blobsChecked.contains(blobIds.get(j)));
        }
        else {
          try {
            BlobProperties propertyOutput = MessageFormatRecord.deserializeBlobProperties(resp.getInputStream());
            Assert.assertEquals(propertyOutput.getBlobSize(), 100);
            Assert.assertEquals(propertyOutput.getServiceId(), "serviceid1");
          }
          catch (MessageFormatException e) {
            Assert.assertEquals(false, true);
          }
        }
        ids.clear();
        ids.add(blobIds.get(j));
        partitionRequestInfoList.clear();
        partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
        partitionRequestInfoList.add(partitionRequestInfo);
        getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.BlobUserMetadata, partitionRequestInfoList, GetOptions.None);
        channel1.send(getRequest);
        stream = channel1.receive().getInputStream();
        resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
        if (resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Deleted || resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Not_Found) {
          Assert.assertTrue(blobsChecked.contains(blobIds.get(j)));
        }
        else {
          try {
            ByteBuffer userMetadataOutput = MessageFormatRecord.deserializeUserMetadata(resp.getInputStream());
            Assert.assertArrayEquals(userMetadataOutput.array(), usermetadata);
          }
          catch (MessageFormatException e) {
            Assert.assertEquals(false, true);
          }
        }
        ids.clear();
        ids.add(blobIds.get(j));
        partitionRequestInfoList.clear();
        partitionRequestInfo = new PartitionRequestInfo(blobIds.get(j).getPartition(), ids);
        partitionRequestInfoList.add(partitionRequestInfo);
        getRequest = new GetRequest(1, "clientid2", MessageFormatFlags.Blob, partitionRequestInfoList, GetOptions.None);
        channel1.send(getRequest);
        stream = channel1.receive().getInputStream();
        resp = GetResponse.readFrom(new DataInputStream(stream), clusterMap);
        if (resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Deleted || resp.getPartitionResponseInfoList().get(0).getErrorCode() == ServerErrorCode.Blob_Not_Found) {
          Assert.assertTrue(blobsChecked.contains(blobIds.get(j)));
          blobsChecked.remove(blobIds.get(j));
        }
        else {
          try {
            BlobOutput blobOutput = MessageFormatRecord.deserializeBlob(resp.getInputStream());
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
        }
      }
      Assert.assertEquals(blobsChecked.size(), 0);
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
>>>>>>> YOURS

