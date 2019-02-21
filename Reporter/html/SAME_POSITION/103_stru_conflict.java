<<<<<<< MINE
ListenableFuture<Sequence<Row>> future = queryExecutor.submit(new Callable<Sequence<Row>>() {
                      @Override public Sequence<Row> call() throws Exception {
                        return new ExecutorExecutingSequence<Row>(input.run(query, context), queryExecutor);
                      }
                  });
=======
final GroupByQuery queryParam = (GroupByQuery)query;
>>>>>>> YOURS

