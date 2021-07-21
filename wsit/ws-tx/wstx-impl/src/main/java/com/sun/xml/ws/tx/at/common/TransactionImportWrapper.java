/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common;

import javax.resource.spi.XATerminator;
import javax.transaction.SystemException;
import javax.transaction.xa.Xid;

/**
 * Duplicates GF Transaction Manager extensions interface {@code TransactionImport}
 * that support transaction inflow w/o resource adapter.
 *
 */
public interface TransactionImportWrapper {

    /**
     * Recreate a transaction based on the Xid. This call causes the calling
     * thread to be associated with the specified transaction.
     *
     * <p>
     * This method imports a transactional context controlled by an external transaction manager.
     *
     * @param xid the Xid object representing a transaction.
     */
    public void recreate(Xid xid, long timeout);

    /**
     * Release a transaction. This call causes the calling thread to be
     * dissociated from the specified transaction.
     *
     * <p>
     * This call releases transactional context imported by recreate method.
     *
     * @param xid the Xid object representing a transaction.
     */
    public void release(Xid xid);

    /**
     * Provides a handle to a <code>XATerminator</code> instance which is used to import
     * an external transaction into Java EE TM.
     *
     * <p> The XATerminator exports 2PC protocol control to an external root transaction coordinator.
     *
     * @return a <code>XATerminator</code> instance.
     */
    public XATerminator getXATerminator();

    /**
     * Return duration before current transaction would timeout.
     *
     * @return Returns the duration in seconds before current transaction would
     *         timeout.
     *         Returns zero if transaction has no timeout set or if any exceptions
     *         occured while looking up remaining transaction timeout.
     *         Returns negative value if transaction already timed out.
     *
     * @exception IllegalStateException Thrown if the current thread is
     *    not associated with a transaction.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     */
    public int getTransactionRemainingTimeout() throws SystemException;
}
