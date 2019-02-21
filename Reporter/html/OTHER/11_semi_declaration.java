private void write(SelectionKey key, Transmissions transmissions)
      throws IOException {
    long startTimeToWriteInMs = time.milliseconds();
    try {
      SocketChannel socketChannel = channel(key);
      NetworkSend networkSend = transmissions.send;
      Send send = networkSend.getPayload();
      if (send == null) {
        throw new IllegalStateException("Registered for write interest but no response attached to key.");
      }
      send.writeTo(socketChannel);
      logger.trace("Bytes written to {} using key {}", socketChannel.socket().getRemoteSocketAddress(),
          transmissions.connectionId);

      if (send.isSendComplete()) {
        logger.trace("Finished writing, registering for read on connection {}",
            socketChannel.socket().getRemoteSocketAddress());
        networkSend.onSendComplete();
        this.completedSends.add(networkSend);
        metrics.sendInFlight.dec();
<<<<<<< MINE
        //metrics.updateNodeSendMetric(transmissions.remoteHostName, transmissions.remotePort, send.sizeInBytes(),
        //    time.milliseconds() - networkSend.getSendStartTimeInMs());
=======
>>>>>>> YOURS
        transmissions.clearSend();
        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE | SelectionKey.OP_READ);
      }
    } finally {
      long writeTime = time.milliseconds() - startTimeToWriteInMs;
      logger.trace("SocketServer time spent on write per key {} = {}", transmissions.connectionId, writeTime);
    }
  }

