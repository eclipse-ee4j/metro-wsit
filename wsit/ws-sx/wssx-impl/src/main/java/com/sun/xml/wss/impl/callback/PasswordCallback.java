/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: PasswordCallback.java,v 1.2 2010-10-21 15:37:24 snajper Exp $
 */

package com.sun.xml.wss.impl.callback;

import javax.security.auth.callback.Callback;


/**
 * This Callback should be handled if the password for the username token
 * needs to be supplied at run-time.
 *
 * @author XWS-Security Team
 */
public class PasswordCallback extends XWSSCallback implements Callback {

    private String password;

    /**
     * Set the Password.
     *
     * @param password <code>java.lang.String</code> representing the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the password stored in this request.
     *
     * @return <code>java.lang.String</code> representing the password.
     */
    public String getPassword() {
        return password;
    }
}
