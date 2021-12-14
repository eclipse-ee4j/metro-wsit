/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at;

import com.sun.xml.ws.api.tx.at.Transactional;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import jakarta.xml.ws.EndpointReference;

/**
 *
 * @author paulparkinson
 */
public class WSATXAResourceStub extends WSATXAResource {
    public int m_prepareReturn = XAResource.XA_OK;
    public String m_status;

    public WSATXAResourceStub(EndpointReference epr, Xid xid) {
        super(epr, xid);
    }

    public WSATXAResourceStub(Transactional.Version version, EndpointReference epr, Xid xid) {
        super(version, epr, xid);
    }

    public WSATXAResourceStub(Transactional.Version version, EndpointReference epr, Xid xid, boolean isRecovery) {
        super(version, epr, xid, isRecovery);
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
        m_status = status;
    }

    @Override
    public int prepare(Xid xid) {
        return m_prepareReturn;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) {

    }

    @Override
    public void rollback(Xid xid) {

    }
}

