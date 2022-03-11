/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api.tokens;

/**
 * Representation of UsernameToken SecurityHeaderElement
 * @author Ashutosh.Shahi@sun.com
 */
public interface UsernameToken {

    /**
     *
     * @return the username value
     */
    String getUsernameValue();

    /**
     * sets the username value for this token
     * @param username username value
     */
    void setUsernameValue(final String username);

    /**
     *
     * @return the password for this token
     */
    String getPasswordValue();

    /**
     * sets the password value for this token
     * @param passwd the password value
     */
    void setPasswordValue(final String passwd);

}
