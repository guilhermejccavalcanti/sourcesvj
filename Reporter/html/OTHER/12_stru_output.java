package com.yahoo.omid.examples.notifications;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import com.yahoo.omid.client.TransactionManager;
import com.yahoo.omid.client.TransactionState;
import com.yahoo.omid.client.TransactionalTable;
import com.yahoo.omid.examples.Constants;
import com.yahoo.omid.notifications.Interest;
import com.yahoo.omid.notifications.client.OmidDelta;
import com.yahoo.omid.notifications.client.IncrementalApplication;
import com.yahoo.omid.notifications.client.Observer;
import com.yahoo.omid.notifications.client.DeltaOmid;

public class ClientNotificationAppExample {
  private static final Logger logger = Logger.getLogger(ClientNotificationAppExample.class);
  private static CountDownLatch cdl;
  @SuppressWarnings(value = {"static-access", }) public static void main(String[] args) throws Exception {
    final OmidDelta registrationService = new OmidDelta("ExampleApp");
    CommandLineParser cmdLineParser = new ExtendedPosixParser(true);
    Options options = new Options();
    options.addOption(OptionBuilder.withLongOpt("txs").withDescription("Number of transactions to execute").withType(Number.class).hasArg().withArgName("argname").create());
    options.addOption(OptionBuilder.withLongOpt("rows-per-tx").withDescription("Number of rows that each transaction inserts").withType(Number.class).hasArg().withArgName("argname").create());
    int txsToExecute = 1;
    int rowsPerTx = 1;
    try {
      CommandLine cmdLine = cmdLineParser.parse(options, args);
      if (cmdLine.hasOption("txs")) {
        txsToExecute = ((Number)cmdLine.getParsedOptionValue("txs")).intValue();
      }
      if (cmdLine.hasOption("rows-per-tx")) {
        rowsPerTx = ((Number)cmdLine.getParsedOptionValue("rows-per-tx")).intValue();
      }
      cdl = new CountDownLatch(txsToExecute * rowsPerTx * 2);
    }
    catch (ParseException e) {
      e.printStackTrace();
      System.exit(1);
    }
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\omid\revisions\rev_d4aa650_fe8d343\rev_left_d4aa650\src\main\java\com\yahoo\omid\examples\notifications\ClientNotificationAppExample.java
Configuration tsoClientHbaseConf = HBaseConfiguration.create();
=======
Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          try {
            registrationService.close();
            logger.info("ooo Omid ooo - Omid\'s Notification Example App Stopped (CTRL+C) - ooo Omid ooo");
          }
          catch (IOException e) {
          }
        }
    });
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\omid\revisions\rev_d4aa650_fe8d343\rev_right_fe8d343\src\main\java\com\yahoo\omid\examples\notifications\ClientNotificationAppExample.java

    tsoClientHbaseConf.set("tso.host", "localhost");
    tsoClientHbaseConf.setInt("tso.port", 1234);
    logger.info("ooo Omid ooo - STARTING OMID\'S EXAMPLE NOTIFICATION APP. - ooo Omid ooo");
    logger.info("ooo Omid ooo -" + " A table called " + Constants.TABLE_1 + " with a column Family " + Constants.COLUMN_FAMILY_1 + " has been already created by the Omid Infrastructure " + "- ooo Omid ooo");
    Observer obs1 = new Observer() {
        Interest interestObs1 = new Interest(Constants.TABLE_1, Constants.COLUMN_FAMILY_1, Constants.COLUMN_1);
        public void onColumnChanged(byte[] column, byte[] columnFamily, byte[] table, byte[] rowKey, TransactionState tx) {
          logger.info("ooo Omid ooo -" + "I\'M OBSERVER o1." + " An update has occurred on Table: " + Bytes.toString(table) + " RowKey: " + Bytes.toString(rowKey) + " ColumnFamily: " + Bytes.toString(columnFamily) + " Column: " + Bytes.toString(column) + " !!! - ooo Omid ooo");
          logger.info("ooo Omid ooo - OBSERVER o1 INSERTING A NEW ROW ON COLUMN " + Constants.COLUMN_2 + " UNDER TRANSACTIONAL CONTEXT " + tx + " - ooo Omid ooo");
          Configuration tsoClientConf = HBaseConfiguration.create();
          tsoClientConf.set("tso.host", "localhost");
          tsoClientConf.setInt("tso.port", 1234);
          try {
            TransactionalTable tt = new TransactionalTable(tsoClientConf, Constants.TABLE_1);
            doTransactionalPut(tx, tt, rowKey, Bytes.toBytes(Constants.COLUMN_FAMILY_1), Bytes.toBytes(Constants.COLUMN_2), Bytes.toBytes("Data written by OBSERVER o1"));
          }
          catch (IOException e) {
            e.printStackTrace();
          }
          cdl.countDown();
        }
        @Override public String getName() {
          return "o1";
        }
        @Override public List<Interest> getInterests() {
          return Collections.singletonList(interestObs1);
        }
    };
    Observer obs2 = new Observer() {
        Interest interestObs2 = new Interest(Constants.TABLE_1, Constants.COLUMN_FAMILY_1, Constants.COLUMN_2);
        public void onColumnChanged(byte[] column, byte[] columnFamily, byte[] table, byte[] rowKey, TransactionState tx) {
          logger.info("ooo Omid ooo - " + "I\'M OBSERVER o2." + " An update has occurred on Table: " + Bytes.toString(table) + " RowKey: " + Bytes.toString(rowKey) + " ColumnFamily: " + Bytes.toString(columnFamily) + " Column: " + Bytes.toString(column) + " !!! I\'M NOT GONNA DO ANYTHING ELSE - ooo Omid ooo");
          cdl.countDown();
        }
        @Override public String getName() {
          return "o2";
        }
        @Override public List<Interest> getInterests() {
          return Collections.singletonList(interestObs2);
        }
    };
    final IncrementalApplication app = new DeltaOmid.AppBuilder("ExampleApp").addObserver(obs1).addObserver(obs2).build();
    Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          try {
            app.close();
            logger.info("ooo Omid ooo - Omid\'s Notification Example App Stopped (CTRL+C) - ooo Omid ooo");
          }
          catch (IOException e) {
          }
        }
    });
    logger.info("ooo Omid ooo - WAITING 5 SECONDS TO ALLOW OBSERVER REGISTRATION - ooo Omid ooo");
    Thread.currentThread().sleep(5000);
    TransactionManager tm = new TransactionManager(tsoClientHbaseConf);
    TransactionalTable tt = new TransactionalTable(tsoClientHbaseConf, Constants.TABLE_1);
    logger.info("ooo Omid ooo - STARTING " + txsToExecute + " TRIGGER TXS INSERTING " + rowsPerTx + " ROWS EACH IN COLUMN " + Constants.COLUMN_1 + " - ooo Omid ooo");
    for (int i = 0; i < txsToExecute; i++) {
      TransactionState tx = tm.beginTransaction();
      for (int j = 0; j < rowsPerTx; j++) {
        Put row = new Put(Bytes.toBytes("row-" + Integer.toString(i + (j * 10000))));
        row.add(Bytes.toBytes(Constants.COLUMN_FAMILY_1), Bytes.toBytes(Constants.COLUMN_1), Bytes.toBytes("testWrite-" + Integer.toString(i + (j * 10000))));
        tt.put(tx, row);
      }
      tm.tryCommit(tx);
    }
    logger.info("ooo Omid ooo - TRIGGER TXS COMMITTED - ooo Omid ooo");
    tt.close();
    logger.info("ooo Omid ooo - WAITING TO ALLOW THE 2 OBSERVERS RECEIVING ALL THE NOTIFICATIONS - ooo Omid ooo");
    cdl.await();
    logger.info("ooo Omid ooo - OBSERVERS HAVE RECEIVED ALL THE NOTIFICATIONS WAITING 30 SECONDS TO ALLOW FINISHING CLEARING STUFF - ooo Omid ooo");
    Thread.currentThread().sleep(30000);
    app.close();
    Thread.currentThread().sleep(10000);
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\omid\revisions\rev_d4aa650_fe8d343\rev_left_d4aa650\src\main\java\com\yahoo\omid\examples\notifications\ClientNotificationAppExample.java
logger.info("ooo Omid ooo - OMID\'S NOTIFICATION APP FINISHED - ooo Omid ooo");
=======
registrationService.close();
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\omid\revisions\rev_d4aa650_fe8d343\rev_right_fe8d343\src\main\java\com\yahoo\omid\examples\notifications\ClientNotificationAppExample.java

  }
  
  private static class ExtendedPosixParser extends PosixParser {
    private boolean ignoreUnrecognizedOption;
    public ExtendedPosixParser(final boolean ignoreUnrecognizedOption) {
      this.ignoreUnrecognizedOption = ignoreUnrecognizedOption;
    }
    @Override protected void processOption(final String arg, final ListIterator iter) throws ParseException {
      boolean hasOption = getOptions().hasOption(arg);
      if (hasOption || !ignoreUnrecognizedOption) {
        super.processOption(arg, iter);
      }
    }
  }
  private static void doTransactionalPut(TransactionState tx, TransactionalTable tt, byte[] rowName, byte[] colFamName, byte[] colName, byte[] dataValue) throws IOException {
    Put row = new Put(rowName);
    row.add(colFamName, colName, dataValue);
    tt.put(tx, row);
  }
}

