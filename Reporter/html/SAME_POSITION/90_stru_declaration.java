  public ExecutorService getExecutorService() {
    if (executorService == null) {
      synchronized(this) {
        if (executorService == null) {
          
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\logback\revisions\rev_735d127_1911c7f\rev_left_735d127\logback-core\src\main\java\ch\qos\logback\core\ContextBase.java
executorService = ExecutorServiceUtil.newExecutorService()
=======
executorService = newExecutorService()
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\logback\revisions\rev_735d127_1911c7f\rev_right_1911c7f\logback-core\src\main\java\ch\qos\logback\core\ContextBase.java
;
        }
      }
    }
    return executorService;
  }


