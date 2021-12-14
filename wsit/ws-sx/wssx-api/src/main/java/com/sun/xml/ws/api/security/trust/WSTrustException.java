/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust;


/**
 * A General WS-Trust Implementation Exception
 */
public class WSTrustException extends Exception {

    private static final long serialVersionUID = -1771080823010581742L;

    public WSTrustException(String msg, Throwable cause) {
        super(msg,cause);
    }
    
     public WSTrustException(String msg) {
        super(msg);
    }
}
