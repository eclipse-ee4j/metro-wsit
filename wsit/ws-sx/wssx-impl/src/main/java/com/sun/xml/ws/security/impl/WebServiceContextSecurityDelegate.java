/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */


package com.sun.xml.ws.security.impl;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.wss.impl.MessageConstants;
import java.security.Principal;
import java.util.Set;
import javax.security.auth.Subject;


public class WebServiceContextSecurityDelegate implements WebServiceContextDelegate {

    private WebServiceContextDelegate delegate = null;

    public WebServiceContextSecurityDelegate(WebServiceContextDelegate delegate) {
        this.delegate = delegate;
    }
    @Override
    public Principal getUserPrincipal(Packet packet) {
       Subject subject =  (Subject)packet.invocationProperties.get(MessageConstants.AUTH_SUBJECT);
       if (subject == null) {
           //log a warning ?
           return null;
       }
       Set<Principal> set = subject.getPrincipals(Principal.class);
       if (set.isEmpty()) {
           return null;
       }

       return set.iterator().next();
    }

    @Override
    public boolean isUserInRole(Packet arg0, String role) {
        //we have to invoke some glassfish methods.
        return false;
    }

    @Override
    public String getEPRAddress(Packet arg0, WSEndpoint arg1) {
        return delegate.getEPRAddress(arg0, arg1);
    }

    @Override
    public String getWSDLAddress(Packet arg0, WSEndpoint arg1) {
        return delegate.getWSDLAddress(arg0, arg1);
    }



}
