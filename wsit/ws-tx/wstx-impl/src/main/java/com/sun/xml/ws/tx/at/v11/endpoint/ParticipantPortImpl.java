/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v11.endpoint;

import com.sun.xml.ws.tx.at.common.endpoint.Participant;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.v11.types.Notification;
import com.sun.xml.ws.tx.at.v11.types.ParticipantPortType;

import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;

@WebService(portName = "ParticipantPort", serviceName = "WSAT11Service", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", wsdlLocation = "/wsdls/wsat11/wstx-wsat-1.1-wsdl-200702.wsdl", endpointInterface = "com.sun.xml.ws.tx.at.v11.types.ParticipantPortType")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@Addressing
public class ParticipantPortImpl
    implements ParticipantPortType
{

    @jakarta.annotation.Resource
    private WebServiceContext m_context;

    public ParticipantPortImpl() {
    }

    /**
     * 
     * @param parameters
     */
    public void prepareOperation(Notification parameters) {
        Participant<Notification> proxy = getPoxy();
        proxy.prepare(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void commitOperation(Notification parameters) {
        Participant<Notification> proxy = getPoxy();
        proxy.commit(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void rollbackOperation(Notification parameters) {
        Participant<Notification> proxy = getPoxy();
        proxy.rollback(parameters);
    }

    protected Participant<Notification> getPoxy() {
        return new Participant<Notification>(m_context, WSATVersion.v11);
    }

}
