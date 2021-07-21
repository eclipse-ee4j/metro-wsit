/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.transaction;

/**
*
*/
public interface TransactionHandler {
    
    /**
     * Begin the transaction
     * @param txTimeout Transaction timeout in seconds
     * @throws TransactionException
     */
    void begin(int txTimeout) throws TransactionException;
    
    /**
     * Commit the transaction
     * @throws TransactionException
     */
    void commit() throws TransactionException;
    
    /**
     * Roll back the transaction
     * @throws TransactionException
     */
    void rollback() throws TransactionException;
    
    /**
     * Mark the transaction as roll back only
     * @throws TransactionException
     */
    void setRollbackOnly() throws TransactionException;
    
    /**
     * Is the UserTransaction available?
     * @return true if UserTransaction can be looked up from JNDI, otherwise false
     * @throws TransactionException
     */
    boolean userTransactionAvailable() throws TransactionException;
    
    /**
     * Is the UserTransaction active on this thread?
     * @return true if UserTransaction status is active
     * @throws TransactionException
     */
    boolean isActive() throws TransactionException;
    
    /**
     * Is the UserTransaction marked for roll back?
     * @return true if UserTransaction is marked for roll back
     * @throws TransactionException
     */
    boolean isMarkedForRollback() throws TransactionException;
    
    /**
     * Can UserTransaction be started? 
     * Can start only if it doesn't already exist.
     * @return true if UserTransaction can be started
     * @throws TransactionException
     */
    boolean canBegin() throws TransactionException;
    
    /**
     * Is transaction associated with the current thread?
     * @return true if transaction is flowing on the current thread
     * @throws TransactionException
     */
    boolean transactionExists() throws TransactionException;
    
    /**
     * javax.transaction.Status of the UserTransaction 
     * @return status integer of the UserTransaction
     * @throws TransactionException
     */
    int getStatus() throws TransactionException;
    
    /**
     * javax.transaction.Status as a String. Useful for logging.
     * @return status as a String
     * @throws TransactionException
     */
    String getStatusAsString() throws TransactionException;
}
