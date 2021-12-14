/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.v10.types;

import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;


@WebService(name = "RegistrationRequesterPortType", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface RegistrationRequesterPortType {


    /**
     *
     */
    @WebMethod(operationName = "RegisterResponseOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/RegisterResponse")
    @Oneway
    void registerResponse(
            @WebParam(name = "RegisterResponseOperation", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", partName = "parameters")
                    RegisterResponseType parameters);

}
