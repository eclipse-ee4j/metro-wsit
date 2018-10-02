/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.internal;

import com.sun.istack.logging.Logger;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.logging.Level;

/**
 *
 * @author paulparkinson
 */
/**
 * NoOp XAResource implemented in order to insure onePhase optimization is not
 *  used for WS-AT transactions
 * @author paulparkinson
 */
class WSATNoOpXAResource implements XAResource {

    public WSATNoOpXAResource() {
    }

    public void commit(Xid xid, boolean bln) throws XAException {
        debug("commit");
    }

    public void end(Xid xid, int i) throws XAException {
        debug("end");
    }

    public void forget(Xid xid) throws XAException {
    }

    public int getTransactionTimeout() throws XAException {
        return -1; 
    }

    public boolean isSameRM(XAResource xar) throws XAException {
        return false;
    }

    public int prepare(Xid xid) throws XAException {
        debug("prepare");
        return XAResource.XA_OK;
    }

    public Xid[] recover(int i) throws XAException {
        return new Xid[]{};
    }

    public void rollback(Xid xid) throws XAException {
        debug("rollback");
    }

    public boolean setTransactionTimeout(int i) throws XAException {
        return true;
    }

    public void start(Xid xid, int i) throws XAException {
        debug("start");
    }


  private void debug(String msg) {
        Logger.getLogger(WSATNoOpXAResource.class).log(Level.INFO, msg);
    }

}
