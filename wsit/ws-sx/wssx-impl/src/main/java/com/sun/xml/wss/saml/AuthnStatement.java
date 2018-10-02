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
 * AuthenticationStatement.java
 *
 * Created on August 18, 2005, 12:30 PM
 *
 */

package com.sun.xml.wss.saml;

import java.util.Date;
import java.util.List;

/**
 *
 * @author abhijit.das@Sun.COM
 */
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
public interface AuthnStatement {            
    
    /**
     * Gets the value of the authnInstant property.
     * 
     * @return object is {@link java.util.Date }
     *     
     */
    public Date getAuthnInstantDate();
    
    /**
     * Gets the value of the sessionIndex property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getSessionIndex();
    
    /**
     * Gets the value of the sessionNotOnOrAfter property.
     * 
     * @return object is {@link java.util.Date }
     *     
     */
    public Date getSessionNotOnOrAfterDate();
       
    /**
     * Gets the value of the SubjectLocality address property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getSubjectLocalityAddress();
    
    /**
     * Gets the value of the SubjectLocality dnsName property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getSubjectLocalityDNSName();
    
    /**
     * Gets the value of the AuthnContext's AuthnContextClassRef property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getAuthnContextClassRef();
    
    /**
     * Gets the value of the AuthnContext's AuthenticatingAuthority property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getAuthenticatingAuthority();
}
