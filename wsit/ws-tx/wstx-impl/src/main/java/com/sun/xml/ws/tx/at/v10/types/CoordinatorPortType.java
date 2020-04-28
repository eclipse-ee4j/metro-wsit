/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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


@WebService(name = "CoordinatorPortType", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CoordinatorPortType {


    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "PreparedOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Prepared")
    @Oneway
    public void preparedOperation(
        @WebParam(name = "Prepared", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
        Notification parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "AbortedOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Aborted")
    @Oneway
    public void abortedOperation(
        @WebParam(name = "Aborted", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
        Notification parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "ReadOnlyOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/ReadOnly")
    @Oneway
    public void readOnlyOperation(
        @WebParam(name = "ReadOnly", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
        Notification parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "CommittedOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Committed")
    @Oneway
    public void committedOperation(
        @WebParam(name = "Committed", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
        Notification parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "ReplayOperation", action = "http://schemas.xmlsoap.org/ws/2004/10/wsat/Replay")
    @Oneway
    public void replayOperation(
        @WebParam(name = "Replay", targetNamespace = "http://schemas.xmlsoap.org/ws/2004/10/wsat", partName = "parameters")
        Notification parameters);

}
