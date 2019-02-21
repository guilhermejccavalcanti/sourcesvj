private void read(SelectionKey key, Transmissions transmissions)
      throws IOException {
    long startTimeToReadInMs = time.milliseconds();
    try {
      if (!transmissions.hasReceive()) {
        transmissions.receive =
            new NetworkReceive(transmissions.getConnectionId(), new BoundedByteBufferReceive(), time);
      }

      SocketChannel socketChannel = channel(key);
      long bytesRead = transmissions.receive.getReceivedBytes().readFrom(socketChannel);
      if (bytesRead == -1) {
        close(key);
        return;
      }
      metrics.selectorBytesReceived.update(bytesRead);
      metrics.selectorBytesReceivedCount.inc(bytesRead);

      if (transmissions.receive.getReceivedBytes().isReadComplete()) {
        this.completedReceives.add(transmissions.receive);
<<<<<<< MINE
        //metrics.updateNodeReceiveMetric(transmissions.remoteHostName, transmissions.remotePort,
        //    transmissions.receive.getReceivedBytes().getPayload().limit(),
        //    time.milliseconds() - transmissions.receive.getReceiveStartTimeInMs());
=======
>>>>>>> YOURS
        transmissions.clearReceive();
      }
    } finally {
      long readTime = time.milliseconds() - startTimeToReadInMs;
      logger.trace("SocketServer time spent on read per key {} = {}", transmissions.connectionId, readTime);
    }
  }

