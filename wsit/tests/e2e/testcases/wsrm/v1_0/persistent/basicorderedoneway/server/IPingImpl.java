/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_0.persistent.basicorderedoneway.server;

import jakarta.annotation.Resource;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.soap.SOAPBinding;

@WebService(endpointInterface = "wsrm.v1_0.persistent.basicorderedoneway.server.IPing")
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class IPingImpl {
    private static final Logger LOGGER = Logger.getLogger(IPingImpl.class.getName());
    private static final AtomicLong CURRENT_PING_VALUE = new AtomicLong(1);
    //
    @Resource WebServiceContext wsContext;

    @WebMethod
    public void ping(String s) {        
        MessageContext msgCtx = wsContext.getMessageContext();
        long msgNumber = (Long) msgCtx.get("com.sun.xml.ws.messagenumber");
        
        LOGGER.log(Level.ALL, String.format("==============  Message [ %d ]: On server side received %s  ===============", msgNumber, s));
        long value = CURRENT_PING_VALUE.getAndIncrement();
        if (msgNumber != value) {
            String errorMessage = String.format("Expected message number: %d, received message number: %d", value, msgNumber);
            LOGGER.severe(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
