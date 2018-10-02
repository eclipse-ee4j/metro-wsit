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
 * AttributeStatement.java
 *
 * Created on August 18, 2005, 12:29 PM
 */

package com.sun.xml.wss.saml;

import java.util.List;

/**
 *
 * @author abhijit.das@Sun.COM
 */

/**
 *The <code>AttributeStatement</code> element supplies a statement by the issuer that the
 *specified subject is associated with the specified attributes.
 *
 * <p>The following schema fragment specifies the expected content contained within SAML 
 * AttributeStatement element.
 *
 * <pre>
 * &lt;complexType name="AttributeStatementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:oasis:names:tc:SAML:1.0:assertion}SubjectStatementAbstractType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}Attribute" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 */
public interface AttributeStatement{        
    
    /**
     * Gets the value of the attribute property.
     *           
     * Objects of the following type(s) are in the list {@link Attribute }
     *      
     */
    List<Attribute> getAttributes();
    
    /**
     * Gets the value of the subject property for SAML1.1 and SAML1.0
     * 
     * @return object is {@link Subject }
     *     
     */
    public Subject getSubject(); 
        
}
