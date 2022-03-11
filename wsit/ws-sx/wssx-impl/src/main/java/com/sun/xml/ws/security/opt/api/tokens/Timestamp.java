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
 * Representation of Timestamp SecurityHeaderElement
 * @author Ashutosh.Shahi@sun.com
 */
public interface Timestamp {

    /**
     *
     * @param created set the creation time on timestamp
     */
    void setCreated(final String created);

    /**
     *
     * @param expires set the expiry time on timestamp
     */
    void setExpires(final String expires);

    /**
     *
     * @return the creation time value
     */
    String getCreatedValue();

    /**
     *
     * @return the expiry time value
     */
    String getExpiresValue();
}
