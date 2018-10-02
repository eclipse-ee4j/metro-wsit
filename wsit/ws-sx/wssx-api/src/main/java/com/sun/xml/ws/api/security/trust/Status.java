/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust;

/**
 * @author Jiandong Guo
 */
public interface Status {
    
    boolean isValid();
    
    /**
     * Gets the value of the code property.
     * 
     * @return {@link String }
     *     
     */
    String getCode();

    /**
     * Gets the value of the reason property.
     * 
     * @return {@link String }
     *     
     */
    String getReason();

    /**
     * Sets the value of the code property.
     * 
     * @param value {@link String }
     *     
     */
    void setCode(String value);

    /**
     * Sets the value of the reason property.
     * 
     * @param value {@link String }
     *     
     */
    void setReason(String value);
}
