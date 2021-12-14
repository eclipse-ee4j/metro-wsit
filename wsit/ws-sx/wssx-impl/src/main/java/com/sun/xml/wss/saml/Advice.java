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
 * Advice.java
 *
 * Created on August 18, 2005, 12:00 PM
 *
 */

package com.sun.xml.wss.saml;

import java.util.List;

/**
 *The <code>Advice</code> element contains additional information that the issuer wishes to
 *provide. This information MAY be ignored by applications without affecting
 *either the semantics or validity. Advice elements MAY be specified in
 *an extension schema.
 *
 * <p>The following schema fragment specifies the expected content contained within SAML Advice element.
 *
 * <pre>
 * &lt;complexType name="AdviceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}AssertionIDReference"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}Assertion"/&gt;
 *         &lt;any/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 */
public interface Advice {
    /**
     * Gets the value of the assertionIDReferenceOrAssertionOrAny property.         
     * 
     * <p>
     * Objects of the following type(s) are in the list
     * {@link Assertion }
     * {@link java.lang.String }
     * {@link org.w3c.dom.Element }
     * {@link Object }
     *      
     */
    
    List<Object> getAdvice();
    
}
