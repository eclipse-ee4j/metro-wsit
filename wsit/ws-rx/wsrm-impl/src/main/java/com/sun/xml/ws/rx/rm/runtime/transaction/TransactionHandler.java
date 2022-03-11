/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
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
     */
    void begin(int txTimeout) throws TransactionException;

    /**
     * Commit the transaction
     */
    void commit() throws TransactionException;

    /**
     * Roll back the transaction
     */
    void rollback() throws TransactionException;

    /**
     * Mark the transaction as roll back only
     */
    void setRollbackOnly() throws TransactionException;

    /**
     * Is the UserTransaction available?
     * @return true if UserTransaction can be looked up from JNDI, otherwise false
     */
    boolean userTransactionAvailable() throws TransactionException;

    /**
     * Is the UserTransaction active on this thread?
     * @return true if UserTransaction status is active
     */
    boolean isActive() throws TransactionException;

    /**
     * Is the UserTransaction marked for roll back?
     * @return true if UserTransaction is marked for roll back
     */
    boolean isMarkedForRollback() throws TransactionException;

    /**
     * Can UserTransaction be started?
     * Can start only if it doesn't already exist.
     * @return true if UserTransaction can be started
     */
    boolean canBegin() throws TransactionException;

    /**
     * Is transaction associated with the current thread?
     * @return true if transaction is flowing on the current thread
     */
    boolean transactionExists() throws TransactionException;

    /**
     * jakarta.transaction.Status of the UserTransaction
     * @return status integer of the UserTransaction
     */
    int getStatus() throws TransactionException;

    /**
     * jakarta.transaction.Status as a String. Useful for logging.
     * @return status as a String
     */
    String getStatusAsString() throws TransactionException;
}
