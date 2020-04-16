/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package wsrm.v1_0.invm.jcapsnack.server;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

@WebService(endpointInterface = "wsrm.v1_0.invm.jcapsnack.server.IPing")
@BindingType(jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class IPingImpl {
    private static final Logger LOGGER = Logger.getLogger(IPingImpl.class.getName());
    private static final AtomicBoolean FIRST_MESSAGE_ALREADY_REJECTED = new AtomicBoolean(false);
    private static final AtomicBoolean FIRST_MESSAGE_RESEND_DETECTED = new AtomicBoolean(false);
    //
    @Resource WebServiceContext wsContext;
    
    @WebMethod
    public void ping(String message) {
        MessageContext msgCtx = wsContext.getMessageContext();        
        long msgNumber = (Long) msgCtx.get("com.sun.xml.ws.messagenumber");

        if (msgNumber == 1) {
            if (FIRST_MESSAGE_ALREADY_REJECTED.compareAndSet(false, true)) {
                msgCtx.put("RM_ACK", "false");                
                LOGGER.log(Level.ALL, String.format("Rejecting message '%s' with message number %d", message, msgNumber));
            } else {
                LOGGER.log(Level.ALL, String.format("Detected resent message '%s' with message number %d", message, msgNumber));
                FIRST_MESSAGE_RESEND_DETECTED.set(true);
            }
        } else if (!FIRST_MESSAGE_RESEND_DETECTED.get()) {
            String errorMessage = String.format("Received message '%s' with message number %d without detecting a resend of rejected message.", message, msgNumber);
            LOGGER.log(Level.ALL, errorMessage);
            throw new RuntimeException(errorMessage);
        } else {
            LOGGER.log(Level.ALL, String.format("Received expected message '%s' with message number %d", message, msgNumber));            
        }             
    }
}
