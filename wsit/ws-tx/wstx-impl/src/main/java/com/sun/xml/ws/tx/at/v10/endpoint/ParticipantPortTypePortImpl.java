/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10.endpoint;

import com.sun.xml.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.common.endpoint.Participant;
import com.sun.xml.ws.tx.at.v10.types.Notification;
import com.sun.xml.ws.tx.at.v10.types.ParticipantPortType;

import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.WebServiceContext;


@WebService(portName = "ParticipantPortTypePort", serviceName = "WSAT10Service", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", wsdlLocation = "/wsdls/wsat10/wsat.wsdl", endpointInterface = "com.sun.xml.ws.tx.at.v10.types.ParticipantPortType")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@MemberSubmissionAddressing
public class ParticipantPortTypePortImpl
    implements ParticipantPortType
{

    @jakarta.annotation.Resource
    private WebServiceContext m_context;

    public ParticipantPortTypePortImpl() {
    }

    /**
     *
     */
    @Override
    public void prepare(Notification parameters) {
        Participant<Notification> proxy = getProxy();
        proxy.prepare(parameters);
    }

    /**
     *
     */
    @Override
    public void commit(Notification parameters) {
        Participant<Notification> proxy = getProxy();
        proxy.commit(parameters);
    }

    /**
     *
     */
    @Override
    public void rollback(Notification parameters) {
        Participant<Notification> proxy = getProxy();
        proxy.rollback(parameters);
    }

    protected Participant<Notification> getProxy() {
        return new Participant<>(m_context, WSATVersion.v10);
    }public String toString() {
    return "v10ParticipantPortTypePortImpl hashcode:"+hashCode() + " getProxy():"+getProxy() +
            "m_context:"+m_context + "m_context.getMessageContext:"+m_context.getMessageContext();
}
}
