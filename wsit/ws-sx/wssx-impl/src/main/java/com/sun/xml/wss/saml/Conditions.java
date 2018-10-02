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
 * Conditions.java
 *
 * Created on August 18, 2005, 12:31 PM
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
 * The validity of an <code>Assertion</code> MAY be subject to a set of
 * <code>Conditions</code>. Each <code>Condition</code> evaluates to a value that
 * is Valid, Invalid or Indeterminate.
 *
 * <p>The following schema fragment specifies the expected content contained within 
 * SAML Conditions element.
 *
 * <pre>
 * &lt;complexType name="ConditionsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}AudienceRestrictionCondition"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}DoNotCacheCondition"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:1.0:assertion}Condition"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="NotBefore" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="NotOnOrAfter" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public interface Conditions {
    
    /**
     * Gets the value of the notBefore property.
     * 
     * @return object is {@link java.util.Date }
     *     
     */
    public Date getNotBeforeDate();
    
    /**
     * Gets the value of the notOnOrAfter property.
     * 
     * @return object is {@link java.util.Date }
     *     
     */
    public Date getNotOnOrAfterDate();
    
     /**
     * Gets the value of the audienceRestrictionConditionOrDoNotCacheConditionOrCondition property.
     *
     * @return Objects of the following type(s) are in the list
     * {@link DoNotCacheCondition }
     * {@link AudienceRestrictionCondition }
     * {@link Condition }
     * 
     * 
     */
    List<Object> getConditions();
    
}
