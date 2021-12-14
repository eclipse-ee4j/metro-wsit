/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10.types;

import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;


@WebService(name = "ParticipantPortType", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ParticipantPortType {


    /**
     *
     */
    @WebMethod(operationName = "PrepareOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Prepare")
    @Oneway
    void prepare(
            @WebParam(name = "Prepare", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
                    Notification parameters);

    /**
     *
     */
    @WebMethod(operationName = "CommitOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Commit")
    @Oneway
    void commit(
            @WebParam(name = "Commit", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
                    Notification parameters);

    /**
     *
     */
    @WebMethod(operationName = "RollbackOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Rollback")
    @Oneway
    void rollback(
            @WebParam(name = "Rollback", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
                    Notification parameters);

}
