/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.internal;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;


/**
 * Delegates to WSATGatewayRM for peer/delegate recovery.
 * The main/only purpose of this class is the special recover call made on WSATGatewayRM to identify the instance
 *  and/for log location
*
* @author paulparkinson
*/
public class WSATGatewayRMPeerRecoveryDelegate implements XAResource {
    String peerLogLocation;


    public WSATGatewayRMPeerRecoveryDelegate() {
    }

    public WSATGatewayRMPeerRecoveryDelegate(String peerLogLocation) {
        this.peerLogLocation = peerLogLocation;
    }

    @Override
    public void commit(Xid xid, boolean b) throws XAException {
        WSATGatewayRM.getInstance().commit(xid, b);
    }

    @Override
    public void end(Xid xid, int i) throws XAException {
        WSATGatewayRM.getInstance().end(xid, i);
    }

    @Override
    public void forget(Xid xid) throws XAException {
        WSATGatewayRM.getInstance().forget(xid);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return WSATGatewayRM.getInstance().getTransactionTimeout();
    }

    @Override
    public boolean isSameRM(XAResource xaResource) throws XAException {
        return WSATGatewayRM.getInstance().isSameRM(xaResource);
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        return WSATGatewayRM.getInstance().prepare(xid);
    }
    
    @Override
    public Xid[] recover(int i) throws XAException {
            return peerLogLocation==null?
                    WSATGatewayRM.getInstance().recover(i):
                    WSATGatewayRM.getInstance().recover(i, peerLogLocation);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        WSATGatewayRM.getInstance().rollback(xid);
    }

    @Override
    public boolean setTransactionTimeout(int i) throws XAException {
        return WSATGatewayRM.getInstance().setTransactionTimeout(i);
    }

    @Override
    public void start(Xid xid, int i) throws XAException {
        WSATGatewayRM.getInstance().start(xid, i);
    }
}
