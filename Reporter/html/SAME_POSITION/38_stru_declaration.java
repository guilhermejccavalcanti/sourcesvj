  private void endToEndReplicationWithMultiNodeMultiPartitionMultiDCTest(String sourceDatacenter, PortType portType, MockCluster cluster) throws Exception {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_1f41948_4a4a3e3\rev_left_1f41948\ambry-server\src\test\java\com.github.ambry.server\ServerTest.java
Properties props = new Properties();
=======
cluster = new MockCluster(notificationSystem, enableSSLPorts, sslEnabledDatacenter1, sslEnabledDatacenter2, sslEnabledDatacenter3, SystemTime.getInstance());
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_1f41948_4a4a3e3\rev_right_4a4a3e3\ambry-server\src\test\java\com.github.ambry.server\ServerTest.java

    props.setProperty("coordinator.hostname", "localhost");
    props.setProperty("coordinator.datacenter.name", sourceDatacenter);
    props.putAll(coordinatorProps);
    VerifiableProperties verifiableProperties = new VerifiableProperties(props);
    Coordinator coordinator = new AmbryCoordinator(verifiableProperties, cluster.getClusterMap());
    Thread[] senderThreads = new Thread[3];
    LinkedBlockingQueue<Payload> blockingQueue = new LinkedBlockingQueue<Payload>();
    int numberOfSenderThreads = 3;
    int numberOfVerifierThreads = 3;
    CountDownLatch senderLatch = new CountDownLatch(numberOfSenderThreads);
    int numberOfRequestsToSendPerThread = 5;
    for (int i = 0; i < numberOfSenderThreads; i++) {
      senderThreads[i] = new Thread(new Sender(blockingQueue, senderLatch, numberOfRequestsToSendPerThread, coordinator));
      senderThreads[i].start();
    }
    senderLatch.await();
    if (blockingQueue.size() != numberOfRequestsToSendPerThread * numberOfSenderThreads) {
      throw new IllegalStateException();
    }
    Properties sslProps = new Properties();
    sslProps.putAll(coordinatorProps);
    sslProps.setProperty("ssl.enabled.datacenters", "DC1,DC2,DC3");
    ConnectionPool connectionPool = new BlockingChannelConnectionPool(new ConnectionPoolConfig(new VerifiableProperties(new Properties())), new SSLConfig(new VerifiableProperties(sslProps)), new MetricRegistry());
    CountDownLatch verifierLatch = new CountDownLatch(numberOfVerifierThreads);
    AtomicInteger totalRequests = new AtomicInteger(numberOfRequestsToSendPerThread * numberOfSenderThreads);
    AtomicInteger verifiedRequests = new AtomicInteger(0);
    AtomicBoolean cancelTest = new AtomicBoolean(false);
    for (int i = 0; i < numberOfVerifierThreads; i++) {
      Thread thread = new Thread(new Verifier(blockingQueue, verifierLatch, totalRequests, verifiedRequests, cluster.getClusterMap(), cancelTest, portType, connectionPool));
      thread.start();
    }
    verifierLatch.await();
    Assert.assertEquals(totalRequests.get(), verifiedRequests.get());
    coordinator.close();
    connectionPool.shutdown();
  }


