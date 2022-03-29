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
 * $Id: UsernameCallback.java,v 1.2 2010-10-21 15:37:24 snajper Exp $
 */

package com.sun.xml.wss.impl.callback;

import com.sun.xml.wss.impl.MessageConstants;
import javax.security.auth.callback.Callback;

/**
 * This Callback should be handled if the username for the username token
 * needs to be supplied at run-time.
 *
 * @author XWS-Security Team
 */
public class UsernameCallback extends XWSSCallback implements Callback {

    private String username;

    public UsernameCallback() {}

    /**
     * Set the Username.
     *
     * @param username <code>java.lang.String</code> representing the Username.
     */
    public void setUsername(String username) {
        if ( username == null || MessageConstants._EMPTY.equals(username) ) {
            throw new RuntimeException("Username can not be empty or NULL");
        }
        this.username = username;
    }

    /**
     * Get the Username.
     *
     * @return <code>java.lang.String</code> representing the Username.
     */
    public String getUsername() {
        return username;
    }
}
