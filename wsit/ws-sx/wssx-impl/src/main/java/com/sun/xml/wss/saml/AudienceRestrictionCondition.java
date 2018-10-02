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
 * AudienceRestrictionCondition.java
 *
 * Created on August 18, 2005, 12:29 PM
 *
 */

package com.sun.xml.wss.saml;

import java.util.List;

/**
 *
 * @author abhijit.das@Sun.COM
 */

/**
 * This is an implementation of the abstract <code>Condition</code> class, which
 * specifes that the assertion this AuthenticationCondition is part of, is
 * addressed to one or more specific audience.
 * 
 * <p>The following schema fragment specifies the expected content contained within SAML
 * AudienceRestrictionCondition element.
 * <pre>
 * &lt;complexType name="AudienceRestrictionConditionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:oasis:names:tc:SAML:1.0:assertion}ConditionAbstractType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}Audience" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public interface AudienceRestrictionCondition {
    
    /**
     * Gets the value of the audience property.    
     *
     * Objects of the following type(s) are in the list {@link java.lang.String }     
     * 
     */
    public List<String> getAudience();
    
}
