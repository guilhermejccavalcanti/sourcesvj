@Override
  public void poll(long timeoutMs, List<NetworkSend> sends)
      throws IOException {
    clear();

    // register for write interest on any new sends
    if (sends != null) {
      for (NetworkSend networkSend : sends) {
        send(networkSend);
      }
    }

    // check ready keys
    long startSelect = time.milliseconds();
    int readyKeys = select(timeoutMs);
    long endSelect = time.milliseconds();
    this.metrics.selectorSelectTime.update(endSelect - startSelect);
    this.metrics.selectorSelectRate.inc();

    if (readyKeys > 0) {
      Set<SelectionKey> keys = nioSelector.selectedKeys();
      Iterator<SelectionKey> iter = keys.iterator();
      while (iter.hasNext()) {
        SelectionKey key = iter.next();
        iter.remove();

        Transmissions transmissions = transmissions(key);
<<<<<<< MINE
        // register all per-node metrics at once
        //metrics.initializeSelectorNodeMetricIfRequired(transmissions.remoteHostName, transmissions.remotePort);
=======
>>>>>>> YOURS
        try {
          if (key.isConnectable()) {
            handleConnect(key, transmissions);
          } else if (key.isReadable()) {
            read(key, transmissions);
          } else if (key.isWritable()) {
            write(key, transmissions);
          } else if (!key.isValid()) {
            close(key);
          } else {
            throw new IllegalStateException("Unrecognized key state for processor thread.");
          }
        } catch (IOException e) {
          String socketDescription = socketDescription(channel(key));
          if (e instanceof EOFException || e instanceof ConnectException) {
            metrics.selectorDisconnectedErrorCount.inc();
            logger.error("Connection {} disconnected", socketDescription, e);
          } else {
            metrics.selectorIOErrorCount.inc();
            logger.warn("Error in I/O with connection to {}", socketDescription, e);
          }
          close(key);
        } catch (Exception e) {
          metrics.selectorKeyOperationErrorCount.inc();
          logger.error("closing key on exception remote host {}", channel(key).socket().getRemoteSocketAddress(), e);
          close(key);
        }
      }
      this.metrics.selectorIORate.inc();
    }
    long endIo = time.milliseconds();
    this.metrics.selectorIOTime.update(endIo - endSelect);
  }

