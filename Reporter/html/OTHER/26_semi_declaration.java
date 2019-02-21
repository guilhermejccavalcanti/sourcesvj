@SuppressWarnings("rawtypes")
	public static void localListen(	int port,
									Map<String, SocketAction> actions,
									ExecutorService service,
									Class<? extends SocketAtom> klass) {
		try {
			// ???????
			SocketActionTable saTable = new SocketActionTable(actions);

			// ??? socket ??
			final ServerSocket server;
			try {
				server = new ServerSocket(port);
			}
			catch (IOException e1) {
				throw Lang.wrapThrow(e1);
			}

			if (log.isInfoEnabled())
				log.infof("Local socket is up at :%d with %d action ready", port, actions.size());

			final Context context = Lang.context();
			context.set("stop", false);
			/*
			 * ???????????????? socket ??
			 */
			(new Thread() {
				@Override
				public void run() {
					setName("Nutz.Sockets monitor thread");
					while (true) {
						try {
							Thread.sleep(1000);
<<<<<<< MINE
							//if(log.isDebugEnabled())
							//	log.debug(" %% check ... " + context.getBoolean("stop"));
=======
>>>>>>> YOURS
							if (context.getBoolean("stop")) {
								try {
									server.close();
								}
								catch (Throwable e) {}
								return;
							}
						}
						catch (Throwable e) {}
					}
				}
			}).start();
			/*
			 * ?? SocketAtom ????
			 */
			Borning borning = Mirror.me(klass).getBorningByArgTypes(Context.class,
																	Socket.class,
																	SocketActionTable.class);
			if (borning == null) {
				log.error("boring == null !!!!");
				return;
			}
			/*
			 * ??????
			 */
			while (!context.getBoolean("stop")) {
				try {
					if (log.isDebugEnabled())
						log.debug("Waiting for new socket");
					Socket socket = server.accept();
					if (context.getBoolean("stop")) {
						Sockets.safeClose(socket);
						break;//??????????,????????,????????
					}
					if (log.isDebugEnabled())
<<<<<<< MINE
						log.debug("accept a new socket, create new SocketAtom to handle it ...");
					Runnable runnable = (Runnable) borning.born(new Object[]{context,socket, saTable});
=======
						log.debug("Appact a new socket, create new SocketAtom to handle it ...");
					Runnable runnable = (Runnable) borning.born(new Object[]{	context,
																				socket,
																				saTable});
>>>>>>> YOURS
					service.execute(runnable);
				}
				catch (Throwable e) {
					log.info("Throwable catched!! maybe ask to exit", e);
				}
<<<<<<< MINE
=======
				if (log.isDebugEnabled())
					log.debugf("next loop '%s'", context.getBoolean("stop"));
				if (context.getBoolean("stop"))
					break;
>>>>>>> YOURS
			}
			
			if (!server.isClosed()) {
				try {
					server.close();
				}
				catch (Throwable e) {}
			}
			
			log.info("Seem stop signal was got, all running thread to exit in 60s");
			
			try {
				service.shutdown();
				service.awaitTermination(15, TimeUnit.SECONDS);
			}
			catch (InterruptedException e) {}
			try {
				service.shutdownNow();
			}
			catch (Throwable e2) {}
		}
		catch (RuntimeException e) {
			throw e;
		}
		finally {
			if (log.isInfoEnabled())
				log.info("Stop services ...");
			service.shutdown();
		}

		if (log.isInfoEnabled())
			log.infof("Local socket is down for :%d", port);

	}

