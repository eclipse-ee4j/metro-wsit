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


import com.sun.xml.ws.tx.at.*;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.common.WSATVersionStub;
import com.sun.xml.ws.tx.at.runtime.TransactionServices;
import com.sun.xml.ws.tx.at.internal.XidStub;

import javax.transaction.xa.Xid;
import jakarta.xml.ws.WebServiceContext;

/**
 *
 * @author paulparkinson
 */
public class EmulatedCoordinator extends Coordinator {

    public int preparedOperationCount;
    public int abortedOperationCount;
    public int readOnlyOperationCount;
    public int committedOperationCount;
    public int replayOperationCount;
    private Xid m_xid;
    private WSATXAResourceStub m_wsatXAResourceStub;
    private WSATHelperStub m_wsatHelper;
    private boolean m_isCallSuper;

    public EmulatedCoordinator(
            WebServiceContext m_context, WSATVersion m_version, Xid xid, WSATXAResourceStub wsatXAResourceStub,
            boolean callSuper) {
        super(m_context, m_version);
        m_xid = xid;
        m_wsatXAResourceStub = wsatXAResourceStub;
        m_wsatHelper = new WSATHelperStub(m_wsatXAResourceStub);
        m_isCallSuper = callSuper;
    }

    public static EmulatedCoordinator createDefault() {
        WebServiceContext context = null;
        Xid xid = new XidStub(true);
        WSATXAResourceStub wsatXAResourceStub = WSATXAResourceTest.createStubWSATXAResourceForXid(xid);
        WSATVersion version = new WSATVersionStub();
        return new EmulatedCoordinator(context, version, xid, wsatXAResourceStub, false);
    }

    @Override
    protected TransactionServices getTransactionServices() {
        return new EmulatedTransactionServices(); //this is not actually necessary
    }

    @Override
    WSATXAResource createWSATXAResourceForXidFromReplyTo(Xid xid) {
        return m_wsatXAResourceStub;
    }

    @Override
    Xid getXid() {
        return m_xid;
    }

    @Override
    boolean isDebugEnabled() {
        return false;
    }

    @Override
    protected WSATHelper getWSATHelper() {
        return m_wsatHelper;
    }

    @Override
    public void preparedOperation(Object parameters) {
        if(m_isCallSuper) super.preparedOperation(parameters);
        preparedOperationCount++;
    }

    @Override
    public void abortedOperation(Object parameters) {
        if(m_isCallSuper) super.abortedOperation(parameters);
        abortedOperationCount++;
    }

    @Override
    public void readOnlyOperation(Object parameters) {
        if(m_isCallSuper) super.readOnlyOperation(parameters);
        readOnlyOperationCount++;
    }

    @Override
    public void committedOperation(Object parameters) {
        if(m_isCallSuper) super.committedOperation(parameters);
        committedOperationCount++;
    }

    @Override
    public void replayOperation(Object parameters) {
        if(m_isCallSuper) super.replayOperation(parameters);
        replayOperationCount++;
    }
}
