package org.apache.curator.ensemble.fixed;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.zookeeper.ZooKeeper;
import org.apache.curator.ensemble.EnsembleProvider;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class FixedEnsembleProvider implements EnsembleProvider {
  private final AtomicReference<String> connectionString = new AtomicReference<>();
  private final boolean updateServerListEnabled;
  public FixedEnsembleProvider(String connectionString) {
    this(connectionString, true);
  }
  public FixedEnsembleProvider(String connectionString, boolean updateServerListEnabled) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(connectionString), "connectionString cannot be null or empty");
    this.updateServerListEnabled = 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_7d44bae_337b9d9\rev_left_7d44bae\curator-client\src\main\java\org\apache\curator\ensemble\fixed\FixedEnsembleProvider.java
updateServerListEnabled
=======
connectionString
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\curator\revisions\rev_7d44bae_337b9d9\rev_right_337b9d9\curator-client\src\main\java\org\apache\curator\ensemble\fixed\FixedEnsembleProvider.java
;
    this.connectionString.set(Preconditions.checkNotNull(connectionString, "connectionString cannot be null"));
  }
  @Override public void start() throws Exception {
  }
  @Override public void close() throws IOException {
  }
  @Override public void setConnectionString(String connectionString) {
    this.connectionString.set(connectionString);
  }
  @Override public String getConnectionString() {
    return connectionString.get();
  }
  @Override public boolean updateServerListEnabled() {
    return updateServerListEnabled;
  }
}

