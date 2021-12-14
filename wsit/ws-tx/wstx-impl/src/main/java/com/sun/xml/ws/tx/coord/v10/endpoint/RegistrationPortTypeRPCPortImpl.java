/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.endpoint;

import com.sun.xml.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.ws.tx.coord.v10.types.RegisterResponseType;
import com.sun.xml.ws.tx.coord.v10.types.RegisterType;
import com.sun.xml.ws.tx.coord.v10.types.RegistrationPortTypeRPC;
import com.sun.xml.ws.tx.coord.v10.XmlTypeAdapter;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;

import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.WebServiceContext;

@WebService(portName = "RegistrationPortTypeRPCPort", serviceName = "RegistrationService_V10", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", wsdlLocation = "/wsdls/wsc10/wscoor.wsdl", endpointInterface = "com.sun.xml.ws.tx.coord.v10.types.RegistrationPortTypeRPC")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@MemberSubmissionAddressing
public class RegistrationPortTypeRPCPortImpl
        implements RegistrationPortTypeRPC {

    @jakarta.annotation.Resource
    private WebServiceContext m_context;

    public RegistrationPortTypeRPCPortImpl() {
    }

    /**
     * @return returns com.sun.xml.ws.tx.coord.v10.RegisterResponseType
     */
    @Override
    public RegisterResponseType registerOperation(RegisterType parameters) {
        RegistrationProxyImpl proxy = getProxy();
        BaseRegisterResponseType<MemberSubmissionEndpointReference, RegisterResponseType>
                response = proxy.registerOperation(XmlTypeAdapter.adapt(parameters));
        return response.getDelegate();
    }

    protected RegistrationProxyImpl getProxy() {
        return new RegistrationProxyImpl(m_context);
    }

}
