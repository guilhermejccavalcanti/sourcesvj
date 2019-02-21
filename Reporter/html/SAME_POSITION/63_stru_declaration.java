  private void configureMetrics() {
    Preconditions.checkNotNull(configuration);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_b7abc2e_902237a\rev_left_b7abc2e\titan-core\src\main\java\com\thinkaurelius\titan\graphdb\configuration\GraphDatabaseConfiguration.java
metricsPrefix = configuration.get(METRICS_PREFIX);
=======
final boolean enableMetrics = isMetricsEnabledInGraphConfig(configuration);
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_b7abc2e_902237a\rev_right_902237a\titan-core\src\main\java\com\thinkaurelius\titan\graphdb\configuration\GraphDatabaseConfiguration.java

<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_b7abc2e_902237a\rev_left_b7abc2e\titan-core\src\main\java\com\thinkaurelius\titan\graphdb\configuration\GraphDatabaseConfiguration.java
if (!configuration.get(BASIC_METRICS)) {
      metricsPrefix = null;
    }
    else {
      Preconditions.checkNotNull(metricsPrefix);
    }
=======
if (enableMetrics) {
      Configuration metricsConf = configuration.subset(METRICS_NAMESPACE);
      metricsPrefix = metricsConf.getString(METRICS_PREFIX_KEY, METRICS_PREFIX_DEFAULT);
      Preconditions.checkNotNull(metricsPrefix);
      configureMetricsConsoleReporter(metricsConf);
      configureMetricsCsvReporter(metricsConf);
      configureMetricsJmxReporter(metricsConf);
      configureMetricsSlf4jReporter(metricsConf);
      configureMetricsGangliaReporter(metricsConf);
      configureMetricsGraphiteReporter(metricsConf);
    }
    else {
      metricsPrefix = null;
    }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\titan\revisions\rev_b7abc2e_902237a\rev_right_902237a\titan-core\src\main\java\com\thinkaurelius\titan\graphdb\configuration\GraphDatabaseConfiguration.java

    configureMetricsConsoleReporter();
    configureMetricsCsvReporter();
    configureMetricsJmxReporter();
    configureMetricsSlf4jReporter();
    configureMetricsGangliaReporter();
    configureMetricsGraphiteReporter();
  }


