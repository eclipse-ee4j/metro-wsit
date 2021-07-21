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
 * $Id: BinarySecret.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;
import com.sun.xml.ws.security.trust.WSTrustConstants;

import java.util.Map;
import javax.xml.crypto.XMLStructure;
import javax.xml.namespace.QName;

/**
 * @author WS-Trust Implementation Team
 */
public interface BinarySecret extends XMLStructure {

    /** Predefined constants for the Type of BinarySecret desired in the Security Token
     * Values for the wst:BinarySecret/@Type parameter
     */
    public static final String ASYMMETRIC_KEY_TYPE = WSTrustConstants.WST_NAMESPACE + "/AsymmetricKey";
    public static final String SYMMETRIC_KEY_TYPE = WSTrustConstants.WST_NAMESPACE + "/SymmetricKey";
    public static final String NONCE_KEY_TYPE = WSTrustConstants.WST_NAMESPACE + "/Nonce";

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
     * Gets the value of the type property. This is a URI that indicates the
     * type of secret being encoded.
     *
     * @return {@link String }
     *
     */
    String getType();

    /**
       * Gets the decoded value or the raw bytes of the binary secret.
       *
       * @return
       *     possible object is
       *     byte[]
       *
       */
      byte[] getRawValue();

      /**
       * Gets the encoded value of the binary secret. This represents the
       * base64 encoded BinarySecret.
       *
       * @return {@link String}
       * @see #getRawValue()
       *
       */
      String getTextValue();

    /**
     * Sets the value of the type property indicating the type of
     * secret being encoded.
     *
     * @param type {@link String }
     *
     */
    void setType(String type);

    /**
      * Sets the value of the Binary Secret element.
      * This is the base64 encoded value of the raw BinarySecret.
      *
      * @param encodedText {@link String }
      */
      void setTextValue(String encodedText);

      /**
       * Sets the value of the binary secret as raw bytes.
       * The value that appears in the element will be encoded appropriately.
       *
       * @param rawText
       *     allowed object is
       *     byte[]
       *
       */
      void setRawValue(byte[] rawText);

}
