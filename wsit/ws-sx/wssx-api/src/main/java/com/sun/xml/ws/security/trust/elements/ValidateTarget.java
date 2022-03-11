/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;

/**
 *
 * @author Jiandong Guo
 */

public interface ValidateTarget {
    /**
     * Gets the value of the any property.
     *
     *
     * @return possible object is
     *     {@link org.w3c.dom.Element }
     *     {@link Object }
     */
    Object getAny();

    /**
     * Sets the value of the any property.
     *
     *
     * @param value
     *     allowed object is
     *     {@link org.w3c.dom.Element }
     *     {@link Object }
     */
    void setAny(Object value);

   /**
     * Set the STR for OnBehalfOf.
     */
    void setSecurityTokenReference(SecurityTokenReference ref);

    /**
     * Get the STR for OnBehalfOf, null if none exists.
     */
    SecurityTokenReference getSecurityTokenReference();

}

