/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: OnBehalfOf.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import jakarta.xml.ws.EndpointReference;
import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;

/**
 *
 * @author WS-Trust Implementation Team
 */

public interface OnBehalfOf {
    /**
     * Gets the value of the any property.
     * 
     * 
     * @return possible object is
     *     {@link Element }
     *     {@link Object }
     */
    Object getAny();

    /**
     * Sets the value of the any property.
     * 
     * 
     * @param value
     *     allowed object is
     *     {@link Element }
     *     {@link Object }
     */
    void setAny(Object value);

   /**
     * Get the endpoint reference of the issuer, null if none exists.
     */
    EndpointReference getEndpointReference();

   /**
     * Set the endpoint reference of the issuer.
     */
    void setEndpointReference(EndpointReference endpointReference);

   /**
     * Set the STR for OnBehalfOf.
     */
    void setSecurityTokenReference(SecurityTokenReference ref);

    /**
     * Get the STR for OnBehalfOf, null if none exists.
     */
    SecurityTokenReference getSecurityTokenReference();

}
