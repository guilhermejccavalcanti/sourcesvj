  public void start() throws IOException, InterruptedException {
    logger.info("Starting {} processor threads", numProcessorThreads);
    for (int i = 0; i < numProcessorThreads; i++) {
      processors.add(i, new Processor(i, maxRequestSize, requestResponseChannel, metrics));
      Utils.newThread("ambry-processor-" + port + " " + i, processors.get(i), false).start();
    }
    requestResponseChannel.addResponseListener(new ResponseListener() {
        @Override public void onResponse(int processorId) {
          processors.get(processorId).wakeup();
        }
    });
    logger.info("Starting acceptor threads");
    Acceptor plainTextAcceptor = new Acceptor(host, port, processors, sendBufferSize, recvBufferSize);
    
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_3ee53a1_d5fbd56\rev_left_3ee53a1\ambry-network\src\main\java\com.github.ambry.network\SocketServer.java
this.acceptors.add(plainTextAcceptor)
=======
this.acceptor = new Acceptor(host, port, processors, sendBufferSize, recvBufferSize, metrics)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_3ee53a1_d5fbd56\rev_right_d5fbd56\ambry-network\src\main\java\com.github.ambry.network\SocketServer.java
;
    Utils.newThread("ambry-acceptor", plainTextAcceptor, false).start();
    Port sslPort = ports.get(PortType.SSL);
    if (sslPort != null) {
      SSLAcceptor sslAcceptor = new SSLAcceptor(host, sslPort.getPort(), processors, sendBufferSize, recvBufferSize);
      acceptors.add(sslAcceptor);
      Utils.newThread("ambry-sslacceptor", sslAcceptor, false).start();
    }
    for (Acceptor acceptor : acceptors) {
      acceptor.awaitStartup();
    }
    logger.info("Started server");
  }


