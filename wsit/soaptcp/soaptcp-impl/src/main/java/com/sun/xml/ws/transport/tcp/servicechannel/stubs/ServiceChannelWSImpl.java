/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.servicechannel.stubs;

import com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelException;
import java.util.List;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.1.1-hudson-2014-Nightly
 * Generated source version: 2.1
 * 
 */
@WebService(name = "ServiceChannelWSImpl", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ServiceChannelWSImpl {


    /**
     *
     */
    @WebMethod
    @RequestWrapper(localName = "initiateSession", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.InitiateSession")
    @ResponseWrapper(localName = "initiateSessionResponse", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.InitiateSessionResponse")
    void initiateSession()
    ;

    /**
     * 
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(name = "channelId")
    @RequestWrapper(localName = "openChannel", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.OpenChannel")
    @ResponseWrapper(localName = "openChannelResponse", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.OpenChannelResponse")
    int openChannel(
            @WebParam(name = "targetWSURI")
                    String targetWSURI,
            @WebParam(name = "negotiatedMimeTypes", mode = WebParam.Mode.INOUT)
                    Holder<List<String>> negotiatedMimeTypes,
            @WebParam(name = "negotiatedParams", mode = WebParam.Mode.INOUT)
                    Holder<List<String>> negotiatedParams)
    ;

    /**
     *
     */
    @WebMethod
    @RequestWrapper(localName = "closeChannel", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.CloseChannel")
    @ResponseWrapper(localName = "closeChannelResponse", targetNamespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", className = "com.sun.xml.ws.transport.tcp.servicechannel.stubs.CloseChannelResponse")
    void closeChannel(
            @WebParam(name = "channelId")
                    int channelId)
    ;

}
