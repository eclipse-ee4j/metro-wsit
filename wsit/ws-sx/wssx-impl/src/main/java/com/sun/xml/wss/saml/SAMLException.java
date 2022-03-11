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
 * $Id: SAMLException.java,v 1.2 2010-10-21 15:37:55 snajper Exp $
 */

package com.sun.xml.wss.saml;


/**
 * This class is an extension point for all SAML related exceptions.
 */
public class SAMLException extends Exception {
    private static final long serialVersionUID = 818447395740605907L;

    /**
     * Create an <code>SAMLException</code> with no message.
     */
    public SAMLException() {
        super();
    }

    /**
     * Create an <code>SAMLException</code> with a message.
     *
     * @param s exception message.
     */
    public SAMLException(String s) {
        super(s);
    }

    /**
     * Create an <code>SAMLException</code>
     *
     * @param e the cause
     */
    public SAMLException (Throwable e) {
        super(e);
    }

}
