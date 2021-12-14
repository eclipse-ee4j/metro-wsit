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
 * AuthorityBinding.java
 *
 * Created on August 18, 2005, 12:30 PM
 *
 */

package com.sun.xml.wss.saml;

import javax.xml.namespace.QName;

/**
 * The <code>AuthorityBinding</code> element may be used to indicate
 * to a replying party receiving an <code>AuthenticationStatement</code> that
 * a SAML authority may be available to provide additional information about
 * the subject of the statement. A single SAML authority may advertise its
 * presence over multiple protocol binding, at multiple locations, and as
 * more than one kind of authority by sending multiple elements as needed.
 *
 * <p>The following schema fragment specifies the expected content contained within 
 * SAML AuthorityBinding element.
 *
 * <pre>
 * &lt;complexType name="AuthorityBindingType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="AuthorityKind" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" /&gt;
 *       &lt;attribute name="Binding" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *       &lt;attribute name="Location" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 */
public interface AuthorityBinding {

    /**
     * Gets the value of the authorityKind property.
     * 
     * @return object is {@link QName }
     *     
     */
    QName getAuthorityKind();

    /**
     * Gets the value of the binding property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    String getBinding();

    /**
     * Gets the value of the location property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    String getLocation();
}
