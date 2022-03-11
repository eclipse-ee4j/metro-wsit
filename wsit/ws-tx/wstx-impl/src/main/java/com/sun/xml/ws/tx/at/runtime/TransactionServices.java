/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.runtime;

import com.sun.xml.ws.tx.at.WSATException;

import jakarta.transaction.Synchronization;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import jakarta.xml.ws.EndpointReference;

/**
 * Defines the interface between WS-AT and underlying transaction processing system
 */
public interface TransactionServices {
    /**
     * The tx id of the tx on this thread
     * @return byte[] tid
     */
    byte[] getGlobalTransactionId();

    /**
     * Called by Registraion Service during register call in order to enlist WSAT XAResource
     *  (this is essentially the WSAT participant EPR wrapper that is serialized for recovery)
     * and return branchqual in order to create RegisterResponseType
     *
     * @param resource (WSAT)XAResource
     * @param xid Xid
     * @return byte[] branchqual to use for
     * @throws WSATException any error during enlist as WSAT GatewayRM
     */
   Xid enlistResource(XAResource resource, Xid xid) throws WSATException;

    /**
     * Called by Registration service to register a volatile participant
     * @param synchronization jakarta.transaction.Synchronization
     * @param xid Xid
     */
    void registerSynchronization(Synchronization synchronization, Xid xid);

    /**
     * Called by server tube (WSATServerHelper) to infect thread with tx
     * @param timeout timeout/ttl
     * @param tId byte[]
     */
  Xid importTransaction(int timeout, byte[] tId);

    /**
     * Called by Participant endpoint to prepare tx/subordinate branch
     * @param tId byte[]
     * @return String vote, see WSATConstants
     * @throws WSATException wsatXAResource
     */
    String prepare(byte[] tId) throws WSATException;

    /**
     * Called by Participant endpoint to commit tx/subordinate branch
     * @param tId byte[]
     * @throws WSATException wsatXAResource
     */
    void commit(byte[] tId) throws WSATException;//commit tx/subordinate branch

    /**
     * Called by Participant endpoint to prepare tx/subordinate branch
     * @param tId byte[]
     * @throws WSATException wsatXAResource
     */
    void rollback(byte[] tId) throws WSATException;

    /**
     * Called by Coordinator replay operation
     * Bottom-up recovery call, as in JTS, a hint to resend
     * @param tId byte[]
     * @param xaResource (WSAT)XAResource
     * @throws WSATException wsatXAResource
     */
    void replayCompletion(String tId, XAResource xaResource) throws WSATException;

    /**
     * Called from Participant service to get the Coordinator(PortType) for this Xid
     * @param xid Xid
     * @return EndpointReference of Coordinator (as obtained from earlier RegisterResponse)
     */
    EndpointReference getParentReference(Xid xid);
}
