/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api.keyinfo;

import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import java.math.BigInteger;

/**
 *
 * @author K.Venugopal@sun.com
 */
public interface DerivedKeyToken {
    /**
     * Gets the value of the algorithm property.
     *
     *
     * @return possible object is
     *     {@link String }
     */
    String getAlgorithm();

    /**
     * Gets the value of the generation property.
     *
     *
     * @return possible object is
     *     {@link BigInteger }
     */
    BigInteger getGeneration();

    /**
     * Gets the value of the id property.
     *
     *
     * @return possible object is
     *     {@link String }
     */
    String getId();

    /**
     * Gets the value of the label property.
     *
     *
     * @return possible object is
     *     {@link String }
     */
    String getLabel();

    /**
     * Gets the value of the length property.
     *
     *
     * @return possible object is
     *     {@link BigInteger }
     */
    BigInteger getLength();

    /**
     * Gets the value of the nonce property.
     *
     *
     * @return possible object is
     *     byte[]
     */
    byte[] getNonce();

    /**
     * Gets the value of the offset property.
     *
     *
     * @return possible object is
     *     {@link BigInteger }
     */
    BigInteger getOffset();

    /**
     * Gets the value of the securityTokenReference property.
     *
     *
     * @return possible object is
     *     {@link SecurityTokenReferenceType }
     */
    SecurityTokenReferenceType getSecurityTokenReference();

    /**
     * Sets the value of the algorithm property.
     *
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    void setAlgorithm(String value);

    /**
     * Sets the value of the generation property.
     *
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     */
    void setGeneration(BigInteger value);

    /**
     * Sets the value of the id property.
     *
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    void setId(String value);

    /**
     * Sets the value of the label property.
     *
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    void setLabel(String value);

    /**
     * Sets the value of the length property.
     *
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     */
    void setLength(BigInteger value);

    /**
     * Sets the value of the nonce property.
     *
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    void setNonce(byte[] value);

    /**
     * Sets the value of the offset property.
     *
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     */
    void setOffset(BigInteger value);

    /**
     * Sets the value of the securityTokenReference property.
     *
     *
     * @param value
     *     allowed object is
     *     {@link SecurityTokenReferenceType }
     */
    void setSecurityTokenReference(SecurityTokenReferenceType value);

}
