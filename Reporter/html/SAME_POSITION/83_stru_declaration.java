  @Inject public ZenPingService(Settings settings, Set<ZenPing> zenPings, TransportService transportService, ClusterName clusterName, NetworkService networkService, Version version, ElectMasterService electMasterService, @Nullable Set<UnicastHostsProvider> unicastHostsProviders) {
    super(settings);
    List<ZenPing> zenPingsBuilder = new ArrayList<>();
    this.zenPings = Collections.unmodifiableList(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\elasticsearch\revisions\rev_6520395_119e9ba\rev_left_6520395\core\src\main\java\org\elasticsearch\discovery\zen\ping\ZenPingService.java
zenPingsBuilder
=======
new ArrayList<>(zenPings)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\elasticsearch\revisions\rev_6520395_119e9ba\rev_right_119e9ba\core\src\main\java\org\elasticsearch\discovery\zen\ping\ZenPingService.java
);
    zenPingsBuilder.add(new UnicastZenPing(settings, threadPool, transportService, clusterName, version, electMasterService, unicastHostsProviders));
    this.zenPings = Collections.unmodifiableList(
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\elasticsearch\revisions\rev_6520395_119e9ba\rev_left_6520395\core\src\main\java\org\elasticsearch\discovery\zen\ping\ZenPingService.java
zenPingsBuilder
=======
new ArrayList<>(zenPings)
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\elasticsearch\revisions\rev_6520395_119e9ba\rev_right_119e9ba\core\src\main\java\org\elasticsearch\discovery\zen\ping\ZenPingService.java
);
  }


