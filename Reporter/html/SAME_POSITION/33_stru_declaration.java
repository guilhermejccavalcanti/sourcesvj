  public MockCluster(NotificationSystem notificationSystem, boolean enableSSL, String datacenters, Properties sslProps, boolean enableHardDeletes, Time time) throws IOException, InstantiationException, URISyntaxException, GeneralSecurityException {
    this.notificationSystem = notificationSystem;
    clusterMap = new MockClusterMap(enableSSL);
    serverList = new ArrayList<AmbryServer>();
    ArrayList<String> datacenterList = Utils.splitString(datacenters, ",");
    List<MockDataNodeId> dataNodes = clusterMap.getDataNodes();
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_1f41948_4a4a3e3\rev_left_1f41948\ambry-server\src\test\java\com.github.ambry.server\MockCluster.java
try {
      for (MockDataNodeId dataNodeId : dataNodes) {
        if (enableSSL) {
          String sslEnabledDatacenters = getSSLEnabledDatacenterValue(dataNodeId.getDatacenterName(), datacenterList);
          sslProps.setProperty("ssl.enabled.datacenters", sslEnabledDatacenters);
        }
        initializeServer(dataNodeId, sslProps, enableHardDeletes);
      }
    }
    catch (InstantiationException e) {
      cleanup();
      throw e;
    }
=======
for (MockDataNodeId dataNodeId : dataNodes) {
      if (dataNodeId.getDatacenterName() == "DC1") {
        startServer(dataNodeId, sslEnabledDatacentersForDC1, time);
      }
      else 
        if (dataNodeId.getDatacenterName() == "DC2") {
          startServer(dataNodeId, sslEnabledDatacentersForDC2, time);
        }
        else 
          if (dataNodeId.getDatacenterName() == "DC3") {
            startServer(dataNodeId, sslEnabledDatacentersForDC3, time);
          }
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_1f41948_4a4a3e3\rev_right_4a4a3e3\ambry-server\src\test\java\com.github.ambry.server\MockCluster.java

  }


