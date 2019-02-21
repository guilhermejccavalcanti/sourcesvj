<<<<<<< MINE
Configuration tsoClientHbaseConf = HBaseConfiguration.create();
=======
Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          try {
            registrationService.close();
            logger.info("ooo Omid ooo - Omid\'s Notification Example App Stopped (CTRL+C) - ooo Omid ooo");
          }
          catch (IOException e) {
          }
        }
    });
>>>>>>> YOURS

