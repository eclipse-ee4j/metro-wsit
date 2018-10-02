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
 * AttributeDesignator.java
 *
 * Created on August 18, 2005, 12:28 PM
 *
 */

package com.sun.xml.wss.saml;

/**
 *
 * @author abhijit.das@Sun.COM
 */

/**
 * The <code>AttributeDesignator</code> element identifies an attribute
 * name within an attribute namespace. The element is used in an attribute query
 * to request that attribute values within a specific namespace be returned.
 *
 * <p>The following schema fragment specifies the expected content contained within SAML 
 * AttributeDesignator element.
 *
 * <pre>
 * &lt;complexType name="AttributeDesignatorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="AttributeName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="AttributeNamespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 */
public interface AttributeDesignator {
    
}
