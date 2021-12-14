/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v11.endpoint;

import com.sun.xml.ws.tx.at.common.endpoint.Coordinator;
import com.sun.xml.ws.tx.at.common.WSATVersion;
import com.sun.xml.ws.tx.at.v11.types.CoordinatorPortType;
import com.sun.xml.ws.tx.at.v11.types.Notification;

import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;


@WebService(portName = "CoordinatorPort", serviceName = "WSAT11Service", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", wsdlLocation = "/wsdls/wsat11/wstx-wsat-1.1-wsdl-200702.wsdl", endpointInterface = "com.sun.xml.ws.tx.at.v11.types.CoordinatorPortType")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@Addressing
public class CoordinatorPortImpl
    implements CoordinatorPortType
{

    @jakarta.annotation.Resource
    private WebServiceContext m_context;

    public CoordinatorPortImpl() {
    }

    /**
     *
     */
    @Override
    public void preparedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.preparedOperation(parameters);
    }

    /**
     *
     */
    @Override
    public void abortedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.abortedOperation(parameters);
    }

    /**
     *
     */
    @Override
    public void readOnlyOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.readOnlyOperation(parameters);
    }

    /**
     *
     */
    @Override
    public void committedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.committedOperation(parameters);
    }

    protected Coordinator<Notification> getProxy() {
        return new Coordinator<>(m_context, WSATVersion.v11);
    }

}
