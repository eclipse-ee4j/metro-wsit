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
 * $Id: PolicyViolationException.java,v 1.2 2010-10-21 15:37:15 snajper Exp $
 */

package com.sun.xml.wss.impl;

import com.sun.xml.wss.XWSSecurityException;

/**
 *Exception indicating a Policy Violation typically encountered when processing
 * an Inbound Message.
 */
public class PolicyViolationException extends XWSSecurityException {
    private static final long serialVersionUID = 1594899126413362743L;

    /**
     * Constructor specifying the message string.
     * @param message the exception message string
     */
    public PolicyViolationException(String message) {
        super(message);
    }

    /**
     * Constructor specifying the message string and a  nested exception
     * @param message the exception message string
     * @param cause the nested exception as a Throwable
     */
    public PolicyViolationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor specifying a nested exception
     * @param cause the nested exception as a Throwable
     */
    public PolicyViolationException(Throwable cause) {
        super(cause);
    }
}
