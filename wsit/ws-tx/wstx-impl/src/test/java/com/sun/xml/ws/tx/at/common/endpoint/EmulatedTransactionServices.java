/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common.endpoint;

import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.tx.at.WSATException;
import com.sun.xml.ws.tx.at.internal.XidImpl;
import com.sun.xml.ws.tx.at.runtime.TransactionServices;

import jakarta.transaction.Synchronization;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import jakarta.xml.ws.EndpointReference;

/**
 *
 * @author paulparkinson
 */
public class EmulatedTransactionServices implements TransactionServices {

        public boolean m_isRollbackException = false;
        public boolean m_isPrepareException = false;
        public boolean m_isCommitException = false;
        String m_prepareVote = WSATConstants.PREPARED;

        @Override
        public byte[] getGlobalTransactionId() //the tx id of the tx on this thread
        {
            return new byte[0];
        }

        @Override
        public Xid enlistResource(XAResource resource, Xid xid)  //enlist XAResource (this is essentially the WSAT participant EPR wrapper)
        {
            return new XidImpl(1234, new byte[]{'1','2','3','4','5','6','7','8','9'},
                    new byte[]{'1','2','3','4','5','6','7','8','9'});
        }

        @Override
        public void registerSynchronization(Synchronization synchronization, Xid xid) {

        }

        public int getExpires()//the transaction timeout value
        {
            return 0;
        }

        @Override
        public Xid importTransaction(int timeout, byte[] tId)  //infect thread with tx
        {
            return null;
        }

        @Override
        public String prepare(byte[] tId) throws WSATException//prepare tx/subordinate branch
        {
            if (m_isPrepareException) throw new WSATException("test exception from prepare");
            return m_prepareVote;
        }

        @Override
        public void commit(byte[] tId) throws WSATException//commit tx/subordinate branch
        {
            if (m_isCommitException) throw new WSATException("test exception from commit");
        }

        @Override
        public void rollback(byte[] tId) throws WSATException//rollback tx/subordinate branch
        {
            if (m_isRollbackException) throw new WSATException("test exception from rollback");
        }

        @Override
        public void replayCompletion(String tId, XAResource xaResource) //bottom-up recovery call, as in JTS, a hint to resend
        {

        }

        @Override
        public EndpointReference getParentReference(Xid xid) {
            return null;
        }

        public void setPrepareVoteReturn(String vote) {
            m_prepareVote = vote;
        }
}
