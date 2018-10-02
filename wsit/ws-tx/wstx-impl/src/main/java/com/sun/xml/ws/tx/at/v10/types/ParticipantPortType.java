/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v10.types;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


@WebService(name = "ParticipantPortType", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ParticipantPortType {


    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "PrepareOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Prepare")
    @Oneway
    public void prepare(
        @WebParam(name = "Prepare", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
        Notification parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "CommitOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Commit")
    @Oneway
    public void commit(
        @WebParam(name = "Commit", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
        Notification parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "RollbackOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Rollback")
    @Oneway
    public void rollback(
        @WebParam(name = "Rollback", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
        Notification parameters);

}
