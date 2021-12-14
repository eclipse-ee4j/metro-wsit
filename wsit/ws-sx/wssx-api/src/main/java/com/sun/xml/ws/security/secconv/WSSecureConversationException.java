/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv;

import com.sun.xml.ws.api.security.trust.WSTrustException;

/**
 * A General WS-SecureConversation Implementation Exception
 */
public class WSSecureConversationException extends WSTrustException {

    private static final long serialVersionUID = 145352751337427100L;

    public WSSecureConversationException(String msg, Throwable cause) {
        super(msg,cause);
    }
    
     public WSSecureConversationException(String msg) {
        super(msg);
    }
     
    public WSSecureConversationException(WSTrustException tex){
        super(tex.getMessage(), tex.getCause());
    }
}
