<<<<<<< MINE
    sslCluster = new MockCluster(notificationSystem, true, "DC1,DC2,DC3", serverSSLProps, true);
    sslCluster.startServers();
    MockClusterMap clusterMap = sslCluster.getClusterMap();
    DataNodeId dataNodeId = clusterMap.getDataNodeIds().get(0);
||||||| BASE
    cluster = new MockCluster(notificationSystem, true, "", "", "");
    MockClusterMap clusterMap = cluster.getClusterMap();
=======
    MockTime time = new MockTime(SystemTime.getInstance().milliseconds());
    cluster = new MockCluster(notificationSystem, true, "", "", "", time);
    MockClusterMap clusterMap = cluster.getClusterMap();
>>>>>>> YOURS

