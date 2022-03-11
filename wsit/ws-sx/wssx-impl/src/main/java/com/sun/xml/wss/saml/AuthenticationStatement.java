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
 * AuthenticationStatement.java
 *
 * Created on August 18, 2005, 12:30 PM
 *
 */

package com.sun.xml.wss.saml;

import java.util.Date;
import java.util.List;

/**
 * The <code>AuthenticationStatement</code> element supplies a
 * statement by the issuer that its subject was authenticated by a
 * particular means at a particular time. The
 * <code>AuthenticationStatement</code> element is of type
 * <code>AuthenticationStatementType</code>, which extends the
 * <code>SubjectStatementAbstractType</code> with the additional element and
 * attributes.
 *
 * <p>The following schema fragment specifies the expected content contained within SAML
 * AuthenticationStatement element.
 *
 * <pre>
 * &lt;complexType name="AuthenticationStatementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:oasis:names:tc:SAML:1.0:assertion}SubjectStatementAbstractType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}SubjectLocality" minOccurs="0"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}AuthorityBinding" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="AuthenticationInstant" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="AuthenticationMethod" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 */
public interface AuthenticationStatement{

     /**
     * Gets the value of the ipAddress property.
     *
     * @return object is {@link java.lang.String }
     *
     */
     String getSubjectLocalityIPAddress();

    /**
     * Gets the value of the dnsAddress property.
     *
     * @return object is {@link java.lang.String }
     *
     */
    String getSubjectLocalityDNSAddress();

    /**
     * Gets the value of the authorityBinding property.
     *
     * Objects of the following type(s) are in the list {@link AuthorityBinding }
     *
     */
    List<AuthorityBinding> getAuthorityBindingList();

     /**
     * Gets the value of the authenticationInstant property.
     *
     * @return object is {@link java.util.Date }
     *
     */
     Date getAuthenticationInstantDate();

    /**
     * Gets the value of the authenticationMethod property.
     *
     * @return object is {@link java.lang.String }
     *
     */
    String getAuthenticationMethod();

    /**
     * Gets the value of the subject property.
     *
     * @return object is {@link java.lang.String }
     *
     */
    Subject getSubject();

}
