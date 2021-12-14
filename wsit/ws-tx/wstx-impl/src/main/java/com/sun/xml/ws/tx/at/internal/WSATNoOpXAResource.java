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

import com.sun.istack.logging.Logger;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.logging.Level;

/**
 * NoOp XAResource implemented in order to insure onePhase optimization is not
 *  used for WS-AT transactions
 * @author paulparkinson
 */
class WSATNoOpXAResource implements XAResource {

    public WSATNoOpXAResource() {
    }

    @Override
    public void commit(Xid xid, boolean bln) {
        debug("commit");
    }

    @Override
    public void end(Xid xid, int i) {
        debug("end");
    }

    @Override
    public void forget(Xid xid) {
    }

    @Override
    public int getTransactionTimeout() {
        return -1; 
    }

    @Override
    public boolean isSameRM(XAResource xar) {
        return false;
    }

    @Override
    public int prepare(Xid xid) {
        debug("prepare");
        return XAResource.XA_OK;
    }

    @Override
    public Xid[] recover(int i) {
        return new Xid[]{};
    }

    @Override
    public void rollback(Xid xid) {
        debug("rollback");
    }

    @Override
    public boolean setTransactionTimeout(int i) {
        return true;
    }

    @Override
    public void start(Xid xid, int i) {
        debug("start");
    }


  private void debug(String msg) {
        Logger.getLogger(WSATNoOpXAResource.class).log(Level.INFO, msg);
    }

}
