/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: PolicyGenerationException.java,v 1.2 2010-10-21 15:37:33 snajper Exp $
 */

package com.sun.xml.wss.impl.policy;

/**
 * Thrown by the classes implementing the Policy framework
 */
public class PolicyGenerationException extends com.sun.xml.wss.XWSSecurityException {

    private static final long serialVersionUID = -8769174380621481812L;

    /**
     * Constructs an Exception specifying a message
     * @param message  the exception string
     */
    public PolicyGenerationException (String message) {
        super (message);
    }
    
    /**
     * Constructs an Exception with a nested exception and specifying a message
     * @param message the exception string
     * @param cause the original cause
     */
    public PolicyGenerationException (String message, Throwable cause) {
        super (message, cause);
    }
    
    /**
     * An Exception wrapper around another exception
     * @param cause the original cause
     */
    public PolicyGenerationException (Throwable cause) {
        super (cause);
    }
}

