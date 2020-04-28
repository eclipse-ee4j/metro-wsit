/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.AudienceRestrictionType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;

import java.util.List;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 * This is an implementation of the abstract <code>Condition</code> class, which
 * specifes that the assertion this AuthenticationCondition is part of, is
 *addressed to one or more specific audience.
 */
public class AudienceRestriction extends AudienceRestrictionType
    implements com.sun.xml.wss.saml.AudienceRestriction{
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    @SuppressWarnings("unchecked")
    private void setAudience(List audience) {
        this.audience = audience;
    }
    
    /**
    This constructor takes in a <code>List</code> of audience for this
    condition, each of them being a String.
    @param audience A List of audience to be included within this condition
    @exception SAMLException if the <code>List</code> is empty or if there is
    some error in processing the contents of the <code>List</code>
    */
    public AudienceRestriction(List audience) {
        setAudience(audience);
    }

    /**
     * Constructs an <code>AudienceRestriction</code> element from an
     * existing XML block.
     *
     * @param element A
     *        <code>org.w3c.dom.Element</code> representing DOM tree for
     *        <code>AudienceRestriction</code> object.
     * @exception SAMLException if it could not process the
     *            <code>org.w3c.dom.Element</code> properly, implying that there
     *            is an error in the sender or in the element definition.
     */
    public static AudienceRestrictionType fromElement(org.w3c.dom.Element element)
        throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();
                    
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AudienceRestrictionType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
}
