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
 * $Id: Entropy.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import com.sun.xml.ws.security.EncryptedKey;

/**
 *
 * @author WS-Trust Implementation Team
 */
public interface Entropy {

    /**
     * Constants defining the Type of Entropy
     */
    String BINARY_SECRET_TYPE="BinarySecret";
     String ENCRYPTED_KEY_TYPE="EncryptedKey";
     String CUSTOM_TYPE="Custom";

     /**
      *Gets the type of the Entropy contents
      */
      String getEntropyType();

    /**
      *Sets the type of the Entropy contents
      */
      void setEntropyType(String entropyType);

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
     * {@link org.w3c.dom.Element }
     * {@link Object }
     *
     *
     */
    List<Object> getAny();

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


    /** Gets the BinarySecret (if any) inside this Entropy
     * @return BinarySecret if set, null otherwise
     */
    BinarySecret getBinarySecret();

  /**
   * Sets the BinarySecret (if any) inside this Entropy
   */
    void setBinarySecret(BinarySecret binarySecret);

    /**
     * Gets the xenc:EncryptedKey set inside this Entropy instance
     * @return xenc:EncryptedKey if set, null otherwise
     */
    EncryptedKey getEncryptedKey();

    /**
     * Sets the xenc:EncryptedKey set inside this Entropy instance
     */
    void setEncryptedKey(EncryptedKey encryptedKey);

}
