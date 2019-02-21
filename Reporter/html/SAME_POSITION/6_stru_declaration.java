  private void processNewResponses() throws InterruptedException, IOException {
    SocketServerResponse curr = (SocketServerResponse)channel.receiveResponse(id);
    while (curr != null){
      curr.onDequeueFromResponseQueue();
      curr.
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_left_2cb49af\ambry-network\src\main\java\com.github.ambry.network\SocketServer.java
onSendStart()
=======
setStartSendTime(SystemTime.getInstance().milliseconds())
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\ambry\revisions\rev_2cb49af_4a6a7fc\rev_right_4a6a7fc\ambry-network\src\main\java\com.github.ambry.network\SocketServer.java
;
      SocketServerRequest request = (SocketServerRequest)curr.getRequest();
      SelectionKey key = (SelectionKey)request.getRequestKey();
      try {
        if (curr.getPayload() == null) {
          logger.trace("Socket server received no response and hence closing the connection");
          close(key);
        }
        else {
          logger.trace("Socket server received response to send, registering for write: {}", curr);
          key.interestOps(SelectionKey.OP_WRITE);
          key.attach(curr);
          metrics.sendInFlight.inc();
        }
      }
      catch (CancelledKeyException e) {
        logger.debug("Ignoring response for closed socket.");
        close(key);
      }
      finally {
        curr = (SocketServerResponse)channel.receiveResponse(id);
      }
    }
  }


