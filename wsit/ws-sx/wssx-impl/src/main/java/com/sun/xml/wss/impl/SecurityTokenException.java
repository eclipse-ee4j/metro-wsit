/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SecurityTokenException.java,v 1.2 2010-10-21 15:37:15 snajper Exp $
 */

package com.sun.xml.wss.impl;

import com.sun.xml.wss.XWSSecurityException;

/**
  * The root class for Security Token Exceptions.  
  *
  * @author Manveen Kaur
  */
public class SecurityTokenException extends XWSSecurityException {
    
    // ------------ Token related fault code constants -----------
    public static final String INVALID_SECURITY_TOKEN = "Invalid Security Token";
    private static final long serialVersionUID = -3062421461359612078L;

    /*
     * Constructs a XWS Exeception specifying the message string.
     */
    public SecurityTokenException(String message) {
        super(message);
    }

    /*
     * Constructs a message with a nested exception and specifying a message.
     */
    public SecurityTokenException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /*
     * A security exception wrapper around another exception.
     */
    public SecurityTokenException(Throwable cause) {
        super(cause);
    }
}
