/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.endpoint;

import com.sun.xml.ws.tx.coord.v10.types.RegistrationCoordinatorPortType;
import com.sun.xml.ws.tx.coord.v10.types.RegisterType;

import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;

import com.sun.xml.ws.developer.MemberSubmissionAddressing;


@WebService(portName = "RegistrationCoordinatorPortTypePort", serviceName = "RegistrationService_V10", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", wsdlLocation = "/wsdls/wsc10/wscoor.wsdl", endpointInterface = "com.sun.xml.ws.tx.coord.v10.types.RegistrationCoordinatorPortType")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@MemberSubmissionAddressing
public class RegistrationCoordinatorPortTypePortImpl
    implements RegistrationCoordinatorPortType
{


    public RegistrationCoordinatorPortTypePortImpl() {
    }

    /**
     * 
     * @param parameters
     */
    public void registerOperation(RegisterType parameters) {
        //replace with your impl here
        return;
    }

}
