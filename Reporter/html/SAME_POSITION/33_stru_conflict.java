<<<<<<< MINE
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
>>>>>>> YOURS

