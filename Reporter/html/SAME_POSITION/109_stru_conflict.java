<<<<<<< MINE
if (state.compareAndSet(State.STARTED, State.CLOSED)) {
      client.getConnectionStateListenable().removeListener(connectionStateListener);
      executorService.shutdownNow();
    }
=======
executorService.close();
>>>>>>> YOURS

