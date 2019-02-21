<<<<<<< MINE
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
>>>>>>> YOURS

