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
 * $Id: Authenticator.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import java.util.List;

/**
 * Provides verification (authentication) of a computed hash.
 *
 * @author WS-Trust Implementation Team.
 */

public interface Authenticator {

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    List<Object> getAny();

    /**
     * Gets the raw value of the combinedHash property.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    byte[] getRawCombinedHash();

    /**
     * Sets the value of the combinedHash property.
     *
     * @param rawCombinedHash
     *     allowed object is
     *     byte[]
     */
    void setRawCombinedHash(byte[] rawCombinedHash);

    /**
     * Gets the value of the base64 encoded combinedHash property.
     *
     * @return {@link String}
     */
    String getTextCombinedHash();

    /**
     * Sets the value of the base 64 encoded combinedHash property.
     *
     * @param combinedHash {@link String}
     */
    void setTextCombinedHash(String combinedHash);

}
