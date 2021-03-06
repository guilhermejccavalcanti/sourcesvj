/**
 * Copyright (C) 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * Sql script containing a series of statements terminated by semi-columns (;).
 * Single-line (--) and multi-line (/* * /) comments are stripped and ignored.
 */
public class SqlScript {
    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(SqlScript.class);

    /**
     * The default Statement delimiter.
     */
    private static final String DEFAULT_STATEMENT_DELIMITER = ";";

    /**
     * The sql statements contained in this script.
     */
    private final List<SqlStatement> sqlStatements;

    /**
     * The crc32 checksum of the sql script.
     */
    private final Long checksum;

    /**
     * Creates a new sql script from this source with these placeholders to replace.
     *
     * @param sqlScriptSource     The sql script as a text block with all placeholders still present.
     * @param placeholderReplacer The placeholder replacer to apply to sql migration scripts.
     * @throws IllegalStateException Thrown when the script could not be read from this resource.
     */
    public SqlScript(String sqlScriptSource, PlaceholderReplacer placeholderReplacer) {
        this.sqlStatements = parse(sqlScriptSource, placeholderReplacer);
        this.checksum = ResourceUtils.calculateChecksum(sqlScriptSource);
    }

    /**
     * Creates a new SqlScript with these statements and this name.
     *
     * @param sqlStatements The statements of the script.
     */
    public SqlScript(List<SqlStatement> sqlStatements) {
        this.sqlStatements = sqlStatements;
        this.checksum = null;
    }

    /**
     * Dummy constructor to increase testability.
     */
    protected SqlScript() {
        sqlStatements = null;
        checksum = null;
    }

    /**
     * @return The sql statements contained in this script.
     */
    public List<SqlStatement> getSqlStatements() {
        return sqlStatements;
    }

    public Long getChecksum() {
        return checksum;
    }

    /**
     * Executes this script against the database.
     *
     * @param transactionTemplate The transaction template to use.
     * @param jdbcTemplate        The jdbc template to use to execute this script.
     */
    public void execute(TransactionTemplate transactionTemplate, final JdbcTemplate jdbcTemplate) {
        transactionTemplate.execute(new TransactionCallback() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
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
        } finally {
            ResourceUtils.closeQuietly(reader);
        }
    }


    /**
     * Turns these lines in a series of statements.
     *
     * @param lines The lines to analyse.
     * @return The statements contained in these lines (in order).
     */
    /* private -> for testing */
    List<SqlStatement> linesToStatements(List<String> lines) {
        List<SqlStatement> statements = new ArrayList<SqlStatement>();

        int statementLineNumber = 0;
        String statementSql = "";

        String delimiter = DEFAULT_STATEMENT_DELIMITER;

        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);

            if (line.isEmpty()) {
                continue;
            }

            if (statementSql.isEmpty()) {
                statementLineNumber = lineNumber;
            } else {
                statementSql += " ";
            }
            statementSql += line;

            String newDelimiter = checkForNewDelimiter(line);
            if (newDelimiter != null) {
                delimiter = newDelimiter;
            }

            if (line.endsWith(delimiter)) {
                String noDelimiterStatementSql = stripDelimiter(statementSql, delimiter);
                statements.add(new SqlStatement(statementLineNumber, noDelimiterStatementSql));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Found statement at line " + statementLineNumber + ": " + statementSql);
                }

                delimiter = DEFAULT_STATEMENT_DELIMITER;
                statementSql = "";
            }
        }

        // Catch any statements not followed by delimiter.
        if (!statementSql.isEmpty()) {
            statements.add(new SqlStatement(statementLineNumber, statementSql));
        }

        return statements;
    }

    /**
     * Checks whether this line in the sql script indicates that the statement delimiter will be different from the
     * default one. Useful for database-specific stored procedures and block constructs.
     *
     * @param line The line to analyse.
     * @return The new delimiter to use or {@code null} if no change in delimiter is required.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected String checkForNewDelimiter(String line) {
        return null;
    }

    /**
     * Strips this delimiter from this sql statement.
     *
     * @param sql       The statement to parse.
     * @param delimiter The delimiter to strip.
     * @return The sql statement without delimiter.
     */
    private static String stripDelimiter(String sql, String delimiter) {
        return sql.substring(0, sql.length() - delimiter.length());
    }

    /**
     * Strip single line (--) and multi-line (/* * /) comments from these lines.
     *
     * @param lines The input lines.
     * @return The input lines, trimmed of leading and trailing whitespace, with the comments lines left blank.
     */
    /* private -> for testing */
    List<String> stripSqlComments(List<String> lines) {
        List<String> noCommentLines = new ArrayList<String>(lines.size());

        boolean inMultilineComment = false;
        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.startsWith("--")) {
                noCommentLines.add("");
                continue;
            }

            if (trimmedLine.startsWith("/*")) {
                inMultilineComment = true;
            }

            if (inMultilineComment) {
                if (trimmedLine.endsWith("*/")) {
                    inMultilineComment = false;
                }
                noCommentLines.add("");
                continue;
            }

            noCommentLines.add(trimmedLine);
        }

        return noCommentLines;
    }

    /**
     * Trims these lines of leading and trailing whitespace.
     *
     * @param lines The input lines.
     * @return The input lines, trimmed of leading and trailing whitespace.
     */
    private List<String> trimLines(List<String> lines) {
        List<String> trimmedLines = new ArrayList<String>(lines.size());

        for (String line : lines) {
            String trimmedLine = line.trim();
            trimmedLines.add(trimmedLine);
        }

        return trimmedLines;
    }

    /**
     * Parses the textual data provided by this reader into a list of lines.
     *
     * @param reader The reader for the textual data.
     * @return The list of lines (in order).
     * @throws IllegalStateException Thrown when the textual data parsing failed.
     */
    private List<String> readLines(Reader reader) {
        List<String> lines = new ArrayList<String>();

        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot parse lines", e);
        }

        return lines;
    }

    /**
     * Replaces the placeholders in these lines with their values.
     *
     * @param lines               The input lines.
     * @param placeholderReplacer The placeholder replacer to apply to sql migration scripts.
     * @return The lines with placeholders replaced.
     */
    private List<String> replacePlaceholders(List<String> lines, PlaceholderReplacer placeholderReplacer) {
        List<String> noPlaceholderLines = new ArrayList<String>(lines.size());

        for (String line : lines) {
            noPlaceholderLines.add(placeholderReplacer.replacePlaceholders(line));
        }

        return noPlaceholderLines;
    }
}
