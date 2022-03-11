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
 * $Id: XWSSecurityException.java,v 1.2 2010-10-21 15:35:42 snajper Exp $
 */

package com.sun.xml.wss;

/**
 * Top level exception used to describe various Errors while processing
 * Secure SOAP messages.
 */
public class XWSSecurityException extends Exception {

    private static final long serialVersionUID = -2786653626203905353L;

    /**
     * Constructor specifying the message string.
     * @param message the exception message string
     */
    public XWSSecurityException(String message) {
        super(message);
    }


    /**
     * Constructor specifying the message string and a  nested exception
     * @param message the exception message string
     * @param cause the nested exception as a Throwable
     */
    public XWSSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor specifying a nested exception
     * @param cause the nested exception as a Throwable
     */
    public XWSSecurityException(Throwable cause) {
        super(cause);
    }
}
