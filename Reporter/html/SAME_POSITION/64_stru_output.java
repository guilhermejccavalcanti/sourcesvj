package com.googlecode.flyway.core.migration.sql;
import com.googlecode.flyway.core.util.ResourceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SqlScript {
  private static final Log LOG = LogFactory.getLog(SqlScript.class);
  private static final String DEFAULT_STATEMENT_DELIMITER = ";";
  private final List<SqlStatement> sqlStatements;
  private final Long checksum;
  public SqlScript(String sqlScriptSource, PlaceholderReplacer placeholderReplacer) {
    this.sqlStatements = parse(sqlScriptSource, placeholderReplacer);
    this.checksum = ResourceUtils.calculateChecksum(sqlScriptSource);
  }
  public SqlScript(List<SqlStatement> sqlStatements) {
    this.sqlStatements = sqlStatements;
    this.checksum = null;
  }
  protected SqlScript() {
    sqlStatements = null;
    checksum = null;
  }
  public List<SqlStatement> getSqlStatements() {
    return sqlStatements;
  }
  public Long getChecksum() {
    return checksum;
  }
  public void execute(TransactionTemplate transactionTemplate, final JdbcTemplate jdbcTemplate) {
    transactionTemplate.execute(new TransactionCallback() {
        @Override public Void doInTransaction(TransactionStatus status) {
          for (SqlStatement sqlStatement : sqlStatements) {
            sqlStatement.execute(jdbcTemplate);
          }
          return null;
        }
    });
  }
  private List<SqlStatement> parse(String sqlScriptSource, PlaceholderReplacer placeholderReplacer) {
    Reader reader = new StringReader(sqlScriptSource);
    try {
      List<String> rawLines = readLines(reader);
      List<String> trimmedLines = trimLines(rawLines);
      List<String> noCommentLines = stripSqlComments(trimmedLines);
      List<String> noPlaceholderLines = replacePlaceholders(noCommentLines, placeholderReplacer);
      return linesToStatements(noPlaceholderLines);
    }
    finally {
      ResourceUtils.closeQuietly(reader);
    }
  }
  List<SqlStatement> linesToStatements(List<String> lines) {
    List<SqlStatement> statements = new ArrayList<SqlStatement>();
    int statementLineNumber = 0;
    String statementSql = "";
    String delimiter = DEFAULT_STATEMENT_DELIMITER;
    for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
      String line = lines.get(lineNumber - 1);
      if (line.isEmpty()) {
        continue ;
      }
      if (statementSql.isEmpty()) {
        statementLineNumber = lineNumber;
      }
      else {
        statementSql += " ";
      }
      statementSql += line;
      String newDelimiter = checkForNewDelimiter(line);
      if (newDelimiter != null) {
        delimiter = newDelimiter;
        if (isDelimiterChangeExplicit()) {
          statementSql = "";
          continue ;
        }
      }
      if (line.endsWith(delimiter)) {
        String noDelimiterStatementSql = stripDelimiter(statementSql, delimiter);
        statements.add(new SqlStatement(statementLineNumber, noDelimiterStatementSql));
<<<<<<< C:\Users\user\Desktop\gjcc\amostra\projects\flyway\revisions\rev_4c0b2d3_0559a45\rev_left_4c0b2d3\flyway-core\src\main\java\com\googlecode\flyway\core\migration\sql\SqlScript.java
if (LOG.isDebugEnabled()) {
          LOG.debug("Found statement at line " + statementLineNumber + ": " + statementSql);
        }
=======
if (!isDelimiterChangeExplicit()) {
          delimiter = DEFAULT_STATEMENT_DELIMITER;
        }
>>>>>>> C:\Users\user\Desktop\gjcc\amostra\projects\flyway\revisions\rev_4c0b2d3_0559a45\rev_right_0559a45\flyway-core\src\main\java\com\googlecode\flyway\core\migration\sql\SqlScript.java

        statementSql = "";
        statementSql = "";
      }
    }
    if (!statementSql.isEmpty()) {
      statements.add(new SqlStatement(statementLineNumber, statementSql));
    }
    return statements;
  }
  @SuppressWarnings(value = {"UnusedDeclaration", }) protected String checkForNewDelimiter(String line) {
    return null;
  }
  protected boolean isDelimiterChangeExplicit() {
    return false;
  }
  private static String stripDelimiter(String sql, String delimiter) {
    return sql.substring(0, sql.length() - delimiter.length());
  }
  List<String> stripSqlComments(List<String> lines) {
    List<String> noCommentLines = new ArrayList<String>(lines.size());
    boolean inMultilineComment = false;
    for (String line : lines) {
      String trimmedLine = line.trim();
      if (trimmedLine.startsWith("--")) {
        noCommentLines.add("");
        continue ;
      }
      if (trimmedLine.startsWith("/*")) {
        inMultilineComment = true;
      }
      if (inMultilineComment) {
        if (trimmedLine.endsWith("*/")) {
          inMultilineComment = false;
        }
        noCommentLines.add("");
        continue ;
      }
      noCommentLines.add(trimmedLine);
    }
    return noCommentLines;
  }
  private List<String> trimLines(List<String> lines) {
    List<String> trimmedLines = new ArrayList<String>(lines.size());
    for (String line : lines) {
      String trimmedLine = line.trim();
      trimmedLines.add(trimmedLine);
    }
    return trimmedLines;
  }
  private List<String> readLines(Reader reader) {
    List<String> lines = new ArrayList<String>();
    BufferedReader bufferedReader = new BufferedReader(reader);
    String line;
    try {
      while ((line = bufferedReader.readLine()) != null){
        lines.add(line);
      }
    }
    catch (IOException e) {
      throw new IllegalStateException("Cannot parse lines", e);
    }
    return lines;
  }
  private List<String> replacePlaceholders(List<String> lines, PlaceholderReplacer placeholderReplacer) {
    List<String> noPlaceholderLines = new ArrayList<String>(lines.size());
    for (String line : lines) {
      noPlaceholderLines.add(placeholderReplacer.replacePlaceholders(line));
    }
    return noPlaceholderLines;
  }
}

