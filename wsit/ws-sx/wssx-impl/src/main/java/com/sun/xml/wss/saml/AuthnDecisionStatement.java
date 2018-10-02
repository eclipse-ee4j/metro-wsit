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
 * AuthorizationDecisionStatement.java
 *
 * Created on August 18, 2005, 12:31 PM
 *
 */

package com.sun.xml.wss.saml;

import java.util.List;

/**
 *
 * @author abhijit.das@Sun.COM
 */

/**
 * The <code>AuthorizationDecisionStatement</code> element supplies a statement
 * by the issuer that the request for access by the specified subject to the
 * specified resource has resulted in the specified decision on the basis of
 * some optionally specified evidence.
 *
 * <p>The following schema fragment specifies the expected content contained within 
 * SAML AuthorizationDecisionStatement element.
 * <pre>
 * &lt;complexType name="AuthorizationDecisionStatementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:oasis:names:tc:SAML:1.0:assertion}SubjectStatementAbstractType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}Action" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}Evidence" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Decision" use="required" type="{urn:oasis:names:tc:SAML:1.0:assertion}DecisionType" /&gt;
 *       &lt;attribute name="Resource" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 *
 */
public interface AuthnDecisionStatement {
    
    /**
     * Gets the value of the action property.   
     *
     * @return Objects of the following type(s) are in the list {@link Action }
     * 
     * 
     */
    public List<Action> getActionList();
    
    /**
     * Gets the value of the evidence property.
     * 
     * @return object is {@link Evidence }
     *     
     */
    public Evidence getEvidence();
    
    /**
     * Gets the value of the decision property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getDecisionValue();
    
    /**
     * Gets the value of the resource property.
     * 
     * @return object is {@link java.lang.String }
     *     
     */
    public String getResource();
}
