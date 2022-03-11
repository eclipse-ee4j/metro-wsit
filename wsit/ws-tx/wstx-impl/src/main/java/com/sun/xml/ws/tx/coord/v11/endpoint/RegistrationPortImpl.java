/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v11.endpoint;

import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;
import com.sun.xml.ws.tx.coord.v11.XmlTypeAdapter;
import com.sun.xml.ws.tx.coord.v11.types.RegisterResponseType;
import com.sun.xml.ws.tx.coord.v11.types.RegisterType;
import com.sun.xml.ws.tx.coord.v11.types.RegistrationPortType;

import jakarta.jws.WebService;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.wsaddressing.W3CEndpointReference;

@WebService(portName = "RegistrationPort", serviceName = "RegistrationService_V11", targetNamespace = "http://docs.oasis-open.org/ws-tx/wscoor/2006/06", wsdlLocation = "/wsdls/wsc11/wstx-wscoor-1.1-wsdl-200702.wsdl", endpointInterface = "com.sun.xml.ws.tx.coord.v11.types.RegistrationPortType")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@Addressing
public class RegistrationPortImpl
    implements RegistrationPortType
{

    @jakarta.annotation.Resource
    private WebServiceContext m_context;

    public RegistrationPortImpl() {
    }

    /**
     *
     * @return
     *     returns com.sun.xml.ws.tx.coord.v11.RegisterResponseType
     */
    @Override
    public RegisterResponseType registerOperation(RegisterType parameters) {
        m_context.getMessageContext().put(BindingProvider.SOAPACTION_USE_PROPERTY,true);
        m_context.getMessageContext().put(BindingProvider.SOAPACTION_URI_PROPERTY,"http://docs.oasis-open.org/ws-tx/wscoor/2006/06/RegisterResponse");
        RegistrationProxyImpl proxy = getProxy();
        BaseRegisterResponseType<W3CEndpointReference, RegisterResponseType>
        response = proxy.registerOperation(XmlTypeAdapter.adapt(parameters));
        return response.getDelegate();
    }

    protected RegistrationProxyImpl getProxy() {
        return new RegistrationProxyImpl(m_context);
    }
}
