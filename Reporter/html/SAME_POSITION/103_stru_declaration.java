                @Override public Sequence<Row> run(final Query<Row> query, final Map<String, Object> context) {
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\druid\revisions\rev_c4397bd_e4a3108\rev_left_c4397bd\processing\src\main\java\io\druid\query\groupby\GroupByQueryRunnerFactory.java
ListenableFuture<Sequence<Row>> future = queryExecutor.submit(new Callable<Sequence<Row>>() {
                      @Override public Sequence<Row> call() throws Exception {
                        return new ExecutorExecutingSequence<Row>(input.run(query, context), queryExecutor);
                      }
                  });
=======
final GroupByQuery queryParam = (GroupByQuery)query;
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\druid\revisions\rev_c4397bd_e4a3108\rev_right_e4a3108\processing\src\main\java\io\druid\query\groupby\GroupByQueryRunnerFactory.java

                  try {
                    queryWatcher.registerQuery(query, future);
                    final Number timeout = query.getContextValue("timeout", (Number)null);
                    return timeout == null ? future.get() : future.get(timeout.longValue(), TimeUnit.MILLISECONDS);
                  }
                  catch (InterruptedException e) {
                    log.warn(e, "Query interrupted, cancelling pending results, query id [%s]", query.getId());
                    future.cancel(true);
                    throw new QueryInterruptedException("Query interrupted");
                  }
                  catch (CancellationException e) {
                    throw new QueryInterruptedException("Query cancelled");
                  }
                  catch (TimeoutException e) {
                    log.info("Query timeout, cancelling pending results for query id [%s]", query.getId());
                    future.cancel(true);
                    throw new QueryInterruptedException("Query timeout");
                  }
                  catch (ExecutionException e) {
                    throw Throwables.propagate(e.getCause());
                  }
                }


