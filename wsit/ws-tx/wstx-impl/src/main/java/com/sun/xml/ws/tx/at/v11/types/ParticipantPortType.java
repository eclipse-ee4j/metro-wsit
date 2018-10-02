/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.v11.types;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

@WebService(name = "ParticipantPortType", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ParticipantPortType {


    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "PrepareOperation", action = "http://docs.oasis-open.org/ws-tx/wsat/2006/06/Prepare")
    @Oneway
    public void prepareOperation(
        @WebParam(name = "Prepare", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", partName = "parameters")
        Notification parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "CommitOperation", action = "http://docs.oasis-open.org/ws-tx/wsat/2006/06/Commit")
    @Oneway
    public void commitOperation(
        @WebParam(name = "Commit", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", partName = "parameters")
        Notification parameters);

    /**
     * 
     * @param parameters
     */
    @WebMethod(operationName = "RollbackOperation", action = "http://docs.oasis-open.org/ws-tx/wsat/2006/06/Rollback")
    @Oneway
    public void rollbackOperation(
        @WebParam(name = "Rollback", targetNamespace = "http://docs.oasis-open.org/ws-tx/wsat/2006/06", partName = "parameters")
        Notification parameters);

}
