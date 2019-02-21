package ch.qos.logback.core;
import static ch.qos.logback.core.CoreConstants.CONTEXT_NAME_KEY;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.LogbackLock;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.ExecutorServiceUtil;

public class ContextBase implements Context, LifeCycle {
  private long birthTime = System.currentTimeMillis();
  private String name;
  private StatusManager sm = new BasicStatusManager();
  Map<String, String> propertyMap = new HashMap<String, String>();
  Map<String, Object> objectMap = new HashMap<String, Object>();
  LogbackLock configurationLock = new LogbackLock();
  private volatile ExecutorService executorService;
  private LifeCycleManager lifeCycleManager;
  private boolean started;
  public StatusManager getStatusManager() {
    return sm;
  }
  public void setStatusManager(StatusManager statusManager) {
    if (sm == null) {
      throw new IllegalArgumentException("null StatusManager not allowed");
    }
    this.sm = statusManager;
  }
  public Map<String, String> getCopyOfPropertyMap() {
    return new HashMap<String, String>(propertyMap);
  }
  public void putProperty(String key, String val) {
    this.propertyMap.put(key, val);
  }
  public String getProperty(String key) {
    if (CONTEXT_NAME_KEY.equals(key)) 
      return getName();
    return (String)this.propertyMap.get(key);
  }
  public Object getObject(String key) {
    return objectMap.get(key);
  }
  public void putObject(String key, Object value) {
    objectMap.put(key, value);
  }
  public String getName() {
    return name;
  }
  public void start() {
    started = true;
  }
  public void stop() {
    stopExecutorService();
    started = false;
  }
  public boolean isStarted() {
    return started;
  }
  public void reset() {
    getLifeCycleManager().reset();
    propertyMap.clear();
    objectMap.clear();
    resetExecutorService();
  }
  public void setName(String name) throws IllegalStateException {
    if (name != null && name.equals(this.name)) {
      return ;
    }
    if (this.name == null || CoreConstants.DEFAULT_CONTEXT_NAME.equals(this.name)) {
      this.name = name;
    }
    else {
      throw new IllegalStateException("Context has been already given a name");
    }
  }
  public long getBirthTime() {
    return birthTime;
  }
  public Object getConfigurationLock() {
    return configurationLock;
  }
  private synchronized void resetExecutorService() {
    if (executorService != null) {
      executorService.shutdownNow();
      executorService = null;
    }
  }
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
  private synchronized void stopExecutorService() {
    if (executorService != null) {
      ExecutorServiceUtil.shutdown(executorService, getStatusManager());
      executorService = null;
    }
  }
  protected ExecutorService newExecutorService() {
    return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
  }
  public void register(LifeCycle component) {
    getLifeCycleManager().register(component);
  }
  synchronized LifeCycleManager getLifeCycleManager() {
    if (lifeCycleManager == null) {
      lifeCycleManager = new LifeCycleManager();
    }
    return lifeCycleManager;
  }
  @Override public String toString() {
    return name;
  }
}

