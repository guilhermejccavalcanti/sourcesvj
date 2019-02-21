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

        delimiter = DEFAULT_STATEMENT_DELIMITER;
        statementSql = "";
      }
    }
    if (!statementSql.isEmpty()) {
      statements.add(new SqlStatement(statementLineNumber, statementSql));
    }
    return statements;
  }


