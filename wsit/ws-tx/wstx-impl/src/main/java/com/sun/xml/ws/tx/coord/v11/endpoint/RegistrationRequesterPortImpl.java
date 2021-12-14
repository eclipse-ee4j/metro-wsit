/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11.endpoint;

import com.sun.xml.ws.tx.coord.common.endpoint.RegistrationRequester;
import com.sun.xml.ws.tx.coord.v11.XmlTypeAdapter;
import com.sun.xml.ws.tx.coord.v11.types.RegisterResponseType;
import com.sun.xml.ws.tx.coord.v11.types.RegistrationRequesterPortType;

import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;

@WebService(portName = "RegistrationRequesterPort", serviceName = "RegistrationService_V11", targetNamespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06", wsdlLocation = "/wsdls/wsc11/wstx-wscoor-1.1-wsdl-200702.wsdl", endpointInterface = "com.sun.xml.ws.tx.coord.v11.types.RegistrationRequesterPortType")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@Addressing
public class RegistrationRequesterPortImpl
    implements RegistrationRequesterPortType
{

    @jakarta.annotation.Resource
    private WebServiceContext m_context;

    public RegistrationRequesterPortImpl() {
    }

    /**
     *
     */
    @Override
    public void registerResponse(RegisterResponseType parameters) {
        RegistrationRequester proxy = new RegistrationRequesterImpl(m_context);
        proxy.registerResponse(XmlTypeAdapter.adapt(parameters));
    }

}
