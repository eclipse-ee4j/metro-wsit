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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 */
final class ConnectionManager {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class);

    private final DataSourceProvider dataSourceProvider;

    public static ConnectionManager getInstance(DataSourceProvider dataSourceProvider) {
        return new ConnectionManager(dataSourceProvider);
    }

    private ConnectionManager(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    Connection getConnection() throws PersistenceException {
        try {
            Connection connection = dataSourceProvider.getDataSource().getConnection();
            
            // connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException ex) {
            throw LOGGER.logSevereException(new PersistenceException("Unable to setup required JDBC connection parameters", ex));
        }

    }

    PreparedStatement prepareStatement(Connection sqlConnection, String sqlStatement, boolean isItForExecuteUpdate) throws SQLException {
        LOGGER.finer(String.format("Preparing SQL statement:\n%s", sqlStatement));

        PreparedStatement resultPS;
        if(isItForExecuteUpdate) {
            resultPS = sqlConnection.prepareStatement(sqlStatement);//there is no ResultSet for executeUpdate
        } else {
            resultPS = sqlConnection.prepareStatement(sqlStatement, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        }
        return resultPS;
    }

    void recycle(ResultSet... resources) {
        for (ResultSet resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (SQLException ex) {
                    LOGGER.logException(ex, Level.WARNING);
                }
            }
        }
    }

    void recycle(PreparedStatement... resources) {
        for (PreparedStatement resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (SQLException ex) {
                    LOGGER.logException(ex, Level.WARNING);
                }
            }
        }
    }

    void recycle(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                LOGGER.logException(ex, Level.WARNING);
            }
        }
    }

    void rollback(Connection sqlConnection) {
        rollback(sqlConnection, true);
    }

    void rollback(Connection sqlConnection, boolean markRollbackForXA) {
        if (isDistributedTransactionInUse()) {
            try {
                if (markRollbackForXA) {
                    //Do not roll back ourselves here as we don't own this distributed TX
                    //but mark it so that the only possible outcome of the TX is to 
                    //roll back the TX
                    getUserTransaction().setRollbackOnly();
                }
            } catch (IllegalStateException ise) {
                LOGGER.warning("Was not able to mark distributed transaction for rollback", ise);
            } catch (SystemException se) {
                LOGGER.warning("Was not able to mark distributed transaction for rollback", se);
            }
        } else {
            try {
                sqlConnection.rollback();
            } catch (SQLException ex) {
                LOGGER.warning("Unexpected exception occured while performing transaction rollback", ex);
            }
        }
    }

    void commit(Connection sqlConnection) throws PersistenceException {
        if (isDistributedTransactionInUse()) {
            //Do nothing as the distributed TX will eventually get  
            //committed and this work will be part of that
        } else {
            try {
                sqlConnection.commit();
            } catch (SQLException ex) {
                throw LOGGER.logSevereException(new PersistenceException("Unexpected exception occured while performing transaction commit", ex));
            }
        }
    }

    private boolean isDistributedTransactionInUse() {
        boolean result = false;
        int status = Status.STATUS_NO_TRANSACTION;
        try {
            UserTransaction userTransaction = getUserTransaction();
            if (userTransaction != null) {
                status = userTransaction.getStatus();
            }
        } catch (SystemException se) {
            LOGGER.warning("Not able to determine if distributed transaction is in use", se);
        }

        if (status != Status.STATUS_NO_TRANSACTION) {
            result = true;
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Determined that distributed transaction is in use. Status code: " + status);    
            }
        }
        return result;
    }

    private UserTransaction getUserTransaction() {
        UserTransaction userTransaction = null;
        try {
            Context initialContext = new InitialContext();
            userTransaction = 
                    (UserTransaction)initialContext.lookup("java:comp/UserTransaction");
        } catch (NamingException ne) {
            LOGGER.warning("Not able to lookup UserTransaction from InitialContext", ne);
        }
        return userTransaction;
    }
}
