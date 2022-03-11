/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security;

import java.util.List;

/**
 * TODO: refine/define the methods in this interface
 * @author root
 */
public interface SecurityTokenReference extends Token {
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
    List getAny();

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getId();

    /*
      Gets a map that contains attributes that aren't bound to any typed property on this class.

      <p>
      the map is keyed by the name of the attribute and
      the value is the string value of the attribute.

      the map returned by this method is live, and you can add new attribute
      by updating the map directly. Because of this design, there's no setter.


      @return
     *     always non-null
     */
    //Map<QName, String> getOtherAttributes();

    /*
      Gets the value of the usage property.

      <p>
      This accessor method returns a reference to the live list,
      not a snapshot. Therefore any modification you make to the
      returned list will be present inside the JAXB object.
      This is why there is not a <CODE>set</CODE> method for the usage property.

      <p>
      For example, to add a new item, do as follows:
      <pre>
         getUsage().add(newItem);
      </pre>


      <p>
      Objects of the following type(s) are allowed in the list
      {@link String }


     */
    //List<String> getUsage();

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    void setId(String value);

}
