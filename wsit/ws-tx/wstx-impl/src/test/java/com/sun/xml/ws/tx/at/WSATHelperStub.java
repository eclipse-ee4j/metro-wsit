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

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceContext;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author paulparkinson
 */
public class WSATHelperStub extends WSATHelper {

    public Map m_durableParticipantXAResourceMap = new HashMap<Xid, WSATXAResource>();
    private WSATXAResource m_wsatXAResource;

    @Override
    public String getWSATTidFromWebServiceContextHeaderList(WebServiceContext context) {
        return "testtid";
    }

    public WSATHelperStub() {

    }

    public WSATHelperStub(WSATXAResource wsatxaResource) {
        m_wsatXAResource = wsatxaResource;
    }

    @Override
    public boolean setDurableParticipantStatus(Xid xid, String status) {
        if(m_wsatXAResource==null) return false;
        m_wsatXAResource.setStatus(status);
        return true;
    }

    @Override
    Map<Xid, WSATXAResource> getDurableParticipantXAResourceMap() {
        return m_durableParticipantXAResourceMap;
    }

    @Override
    public void prepare(EndpointReference epr, Xid xid, WSATXAResource wsatXAResource) {
        ;
    }

    @Override
    public void commit(EndpointReference epr, Xid xid, WSATXAResource wsatXAResource) {
        ;
    }

    @Override
    public void rollback(EndpointReference epr, Xid xid, WSATXAResource wsatXAResource) {
        ;
    }

    @Override
    public void beforeCompletion(EndpointReference address, Xid xid, WSATSynchronization wsatSynchronization) throws SOAPException {
        ;
    }
}
