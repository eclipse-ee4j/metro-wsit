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
 * $Id: Renewing.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

/**
 * Used to specify renew semantics for types that support this operation.
 *
 * @author WS-Trust Implementation Team
 */
public interface Renewing {

    /**
     * Gets the value of the allow property.
     *
     * @return {@link Boolean }
     *     
     */
    Boolean isAllow();

    /**
     * Gets the value of the ok property.
     * 
     * @return {@link Boolean }
     *     
     */
    Boolean isOK();

    /**
     * Sets the value of the allow property.
     * 
     * @param value {@link Boolean }
     *     
     */
    void setAllow(Boolean value);

    /**
     * Sets the value of the ok property.
     * 
     * @param value {@link Boolean }
     *     
     */
    void setOK(Boolean value);
    
}
