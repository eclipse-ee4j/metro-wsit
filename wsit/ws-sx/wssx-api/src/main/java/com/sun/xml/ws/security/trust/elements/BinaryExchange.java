/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: BinaryExchange.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import java.util.Map;
import javax.xml.namespace.QName;

/**
 *
 * @author WS-Trust Implementation Team
 */
public interface BinaryExchange {
    /**
     * Gets the value of the encodingType property.
     *
     * @return {@link String}
     *
     */
    String getEncodingType();

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    Map<QName, String> getOtherAttributes();

    /**
     * Gets the decoded value of the text node. This represents the
     *  raw bytes for the Binary Exchange.
     *
     * @return
     *     possible object is
     *     byte[]
     *
     */
    byte[] getRawValue();


    /**
     * Gets the value of the text node. This method will return the
     * encoded value of the binary data exchanged. Encoding is specified
     * with the encodingType attibute.
     *
     * @return {@link String}
     * @see #getRawValue()
     *
     */
    String getTextValue();

    /**
     * Gets the value of the valueType property. ValueType contains the
     * URI that identifies the type of negotiation.
     *
     * @return  {@link String }
     *
     */
    String getValueType();

    /**
     * Sets the value of the encodingType property.
     *
     * @param encodingType {@link String}
     *
     */
    void setEncodingType(String encodingType);

    /**
     * Sets the value of the text node. It is assumed that the
     * proper encoding has already been taken care of to create the
     * text value.
     *
     * @param encodedText {@link String }
     *
     */
    void setTextValue(String encodedText);

    /**
     * Sets the value of the binary exchange as raw bytes.
     * The value that appears in the element will be encoded appropriately.
     *
     * @param rawText
     *     allowed object is
     *     byte[]
     *
     */
    void setRawValue(byte[] rawText);

    /**
     * Sets the value of the valueType property.
     *
     * @param valueType {@link String}
     *
     */
    void setValueType(String valueType);

}
