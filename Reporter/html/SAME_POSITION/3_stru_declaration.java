  @Before public void setupCase() throws IllegalStateException, PoolExhaustedException, Exception {
    super.setupClient();
    keyspace = new KeyspaceServiceImpl("Keyspace1", new QuorumAllConsistencyLevelPolicy(), connectionManager, 
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\hector\revisions\rev_f488227_c832174\rev_left_f488227\src\test\java\me\prettyprint\cassandra\service\KeyspaceTest.java
FailoverPolicy.ON_FAIL_TRY_ALL_AVAILABLE
=======
connectionManager
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\hector\revisions\rev_f488227_c832174\rev_right_c832174\src\test\java\me\prettyprint\cassandra\service\KeyspaceTest.java
);
  }


