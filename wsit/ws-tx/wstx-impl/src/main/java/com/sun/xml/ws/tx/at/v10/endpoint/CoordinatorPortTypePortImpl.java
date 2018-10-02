/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.tx.at.common.endpoint.Coordinator;
import com.sun.xml.ws.tx.at.v10.types.CoordinatorPortType;
import com.sun.xml.ws.tx.at.v10.types.Notification;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;


@WebService(portName = "CoordinatorPortTypePort", serviceName = "WSAT10Service", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", wsdlLocation = "/wsdls/wsat10/wsat.wsdl", endpointInterface = "com.sun.xml.ws.tx.at.v10.types.CoordinatorPortType")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@MemberSubmissionAddressing
public class CoordinatorPortTypePortImpl
    implements CoordinatorPortType
{

    @javax.annotation.Resource
    private WebServiceContext m_context;

    public CoordinatorPortTypePortImpl() {
    }

    /**
     * 
     * @param parameters
     */
    public void preparedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.preparedOperation(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void abortedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.abortedOperation(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void readOnlyOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.readOnlyOperation(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void committedOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.committedOperation(parameters);
    }

    /**
     * 
     * @param parameters
     */
    public void replayOperation(Notification parameters) {
        Coordinator<Notification> proxy = getProxy();
        proxy.replayOperation(parameters);
    }

    protected Coordinator<Notification> getProxy() {
        Coordinator<Notification> proxy = new Coordinator<Notification>(m_context, WSATVersion.v10);
        return proxy;
    }


}
