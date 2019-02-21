<<<<<<< MINE
mapWork = (MapWork)cache.retrieve(key, new Callable<Object>() {
        public Object call() {
          return Utilities.getMapWork(jconf);
        }
    });
=======
((TezContext)MapredContext.get()).setInputs(inputs);
>>>>>>> YOURS

