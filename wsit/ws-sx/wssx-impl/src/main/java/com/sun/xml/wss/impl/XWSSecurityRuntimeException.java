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
 * $Id: XWSSecurityRuntimeException.java,v 1.2 2010-10-21 15:37:16 snajper Exp $
 */

package com.sun.xml.wss.impl;

/**
 * @author XWS-Security Development Team
 */
public class XWSSecurityRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -4052471715929902049L;

    /*
     * Constructs a XWS Exeception specifying the message string.
     */
    public XWSSecurityRuntimeException(String message) {
        super(message);
    }

    /*
     * Constructs a message with a nested exception and specifying a message.
     */
    public XWSSecurityRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /*
     * A security exception wrapper around another exception.
     */
    public XWSSecurityRuntimeException(Throwable cause) {
        super(cause);
    }
}
