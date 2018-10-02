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
 * Attribute.java
 *
 * Created on August 18, 2005, 12:28 PM
 */

package com.sun.xml.wss.saml;

import java.util.List;

/**
 *
 * @author abhijit.das@Sun.COM
 */

/**
 * The <code>Attribute</code> element specifies an attribute of the assertion subject.
 * The <code>Attribute</code> element is an extension of the <code>AttributeDesignator</code> element
 * that allows the attribute value to be specified.
 *
 * <p>The following schema fragment specifies the expected content contained within SAML Attribute element.
 *
 * <pre>
 * &lt;complexType name="AttributeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:oasis:names:tc:SAML:1.0:assertion}AttributeDesignatorType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}AttributeValue" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 */
public interface Attribute {
     /**
     * Gets the value of the attributeValue property.
     *           
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *      
     */
    List<Object> getAttributes();
    
    /**
     * Gets the value of the friendlyName property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getFriendlyName();
    
    /**
     * Gets the value of the name property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    String getName();
    
    /**
     * Gets the value of the nameFormat property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getNameFormat();
}
