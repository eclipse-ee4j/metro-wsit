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

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;


@WebService(name = "RegistrationPortTypeRPC", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface RegistrationPortTypeRPC {


    /**
     * 
     * @return
     *     returns com.sun.xml.ws.tx.coord.v10.types.RegisterResponseType
     */
    @WebMethod(operationName = "RegisterOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wscoor/Register")
    @WebResult(name = "RegisterResponse", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", partName = "parameters")
    RegisterResponseType registerOperation(
            @WebParam(name = "Register", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wscoor", partName = "parameters")
                    RegisterType parameters);

}
