/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.persistent;

import com.sun.istack.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 */
public class EmbeddedDerbyDbInstance {

    private static final Logger LOGGER = Logger.getLogger(EmbeddedDerbyDbInstance.class);
    private static final String EMBEDDED_DERBY_DRIVER_CLASS_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
    /**
     * Derby connection URL
     */
    private final String connectionUrl;
    private final String databaseName;
    private Connection connection;

    private EmbeddedDerbyDbInstance(String databaseName) throws PersistenceException {
        this.databaseName = databaseName;
        this.connectionUrl = String.format("jdbc:derby:%s;create=true", databaseName);
        try {
            // Loading the Derby JDBC driver. When the embedded Driver is used, this action also start the Derby engine.
            Class.forName(EMBEDDED_DERBY_DRIVER_CLASS_NAME).newInstance();
            LOGGER.config(EMBEDDED_DERBY_DRIVER_CLASS_NAME + " loaded.");
        } catch (java.lang.ClassNotFoundException ex) {
            LOGGER.severe(String.format("Unable to load JDBC driver class '%s'. Please, check your classpath", EMBEDDED_DERBY_DRIVER_CLASS_NAME), ex);
        } catch (InstantiationException ex) {
            LOGGER.severe(String.format("Unable to instantiate the JDBC driver class '%s'. Please, check your classpath", EMBEDDED_DERBY_DRIVER_CLASS_NAME), ex);
        } catch (IllegalAccessException ex) {
            LOGGER.severe(String.format("Unable to access the JDBC driver class '%s'. Please, check your security policy", EMBEDDED_DERBY_DRIVER_CLASS_NAME), ex);
        }

        this.connection = createConnection(databaseName, connectionUrl);
    }

    private static Connection createConnection(String databaseName, String connectionUrl) {
        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            LOGGER.config(String.format("Connection to database [ %s ] established succesfully", databaseName));
            return connection;
        } catch (SQLException ex) {
            throw LOGGER.logSevereException(new PersistenceException(String.format("Connection to database could not be [ %s ] established", databaseName), ex));
        }

    }

    public static EmbeddedDerbyDbInstance start(String databaseName) throws PersistenceException {
        return new EmbeddedDerbyDbInstance(databaseName);
    }

    public void stop() {
        try {
            connection.close();
        } catch (SQLException ex) {
            LOGGER.warning("Error closing connection", ex);
        }

        // shutting down ddatabase
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            if (ex.getSQLState().equals("XJ015")) {
                // Shutdown throws the XJ015 exception to confirm success.
                LOGGER.config("Database was shut down.");
            } else {
                LOGGER.warning("An unexpected error occured while shutting down the database.", ex);
            }
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = createConnection(databaseName, connectionUrl);
            }
        } catch (SQLException ex) {
            throw LOGGER.logSevereException(new PersistenceException("Connection.isClosed() invocation failed", ex));
        }

        return connection;
    }

    public void releaseConnection(final Connection connection) {
        // do nothing
    }

    public void releaseStatement(final Statement statement) {
        try {
            statement.close();
        } catch (SQLException ex) {
            LOGGER.warning("Closing an SQL statement threw an exception", ex);
        }
    }

    public boolean tableExists(String tableName) throws PersistenceException {
        tableName = tableName.replaceAll("\\s", ""); // protection against malicious code execution attack :)

        final Connection con = getConnection();
        Statement s = null;
        try {
            s = con.createStatement();
            s.execute(String.format("SELECT COUNT(*) FROM %s", tableName));

            return true;
        } catch (SQLException ex) {
            String theError = ex.getSQLState();
            if (theError.equals("42X05")) {
                // Table does not exist
                return false;
            } else if (theError.equals("42X14") || theError.equals("42821")) {
                throw LOGGER.logSevereException(new PersistenceException(String.format("Incorrect table definition. Drop and recreate table %s", tableName), ex));
            } else {
                throw LOGGER.logSevereException(new PersistenceException("Unexpected exception", ex));
            }
        } finally {
            releaseStatement(s);
            releaseConnection(con);
        }
    }

    public void createTable(String tableName, String createTableStatement, boolean dropIfExists) {
        tableName = tableName.replaceAll("\\s", "");

        final Connection con = getConnection();
        try {
            if (tableExists(tableName)) {
                if (dropIfExists) { // drop table
                    Statement s = null;
                    try {
                        LOGGER.config(String.format("Dropping table %s", tableName));
                        s = con.createStatement();
                        s.execute(String.format("DROP TABLE %s", tableName));
                    } catch (SQLException ex) {
                        throw LOGGER.logSevereException(new PersistenceException(String.format("An unexpected exception occured while dropping table [ %s ]", tableName), ex));
                    } finally {
                        releaseStatement(s);
                    }
                } else { // just delete all data
                    Statement s = null;
                    try {
                        LOGGER.config(String.format("Deleting all records from table %s", tableName));
                        s = con.createStatement();
                        s.execute(String.format("DELETE FROM %s", tableName));
                    } catch (SQLException ex) {
                        throw LOGGER.logSevereException(new PersistenceException(String.format("An unexpected exception occured while deleting all records from table [ %s ]", tableName), ex));
                    } finally {
                        releaseStatement(s);
                    }
                }
            }
            
            if (!tableExists(tableName)) { // table was not dropped or didn't exist
                Statement s = null;
                try {
                    LOGGER.config(String.format("Creating table %s", tableName));
                    s = con.createStatement();
                    LOGGER.info(String.format("Executing SQL statement to create [ %s ] table:\n%S", tableName, createTableStatement));
                    s.execute(String.format(createTableStatement));
                } catch (SQLException ex) {
                    throw LOGGER.logSevereException(new PersistenceException(String.format("An unexpected exception occured while creating table [ %s ]", tableName), ex));
                } finally {
                    releaseStatement(s);
                }
            }
        } finally {
            releaseConnection(con);
        }
    }

    public void execute(String sqlCommand) {
        Connection con = getConnection();
        Statement s = null;
        try {
            LOGGER.config(String.format("Executing SQL statement:\n%s", sqlCommand));
            s = con.createStatement();
            s.execute(sqlCommand);
        } catch (SQLException ex) {
            throw LOGGER.logSevereException(new PersistenceException(String.format("An unexpected exception occured while executing statement:\n%s", sqlCommand), ex));
        } finally {
            releaseStatement(s);
            releaseConnection(con);
        }
    }
}
