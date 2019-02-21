private void handleConnect(SelectionKey key, Transmissions transmissions)
      throws IOException {
    SocketChannel socketChannel = channel(key);
    socketChannel.finishConnect();
    key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
    this.connected.add(transmissions.getConnectionId());
    this.metrics.selectorConnectionCreated.inc();
<<<<<<< MINE
    //this.metrics.initializeSelectorNodeMetricIfRequired(transmissions.remoteHostName, transmissions.remotePort);
=======
>>>>>>> YOURS
  }

