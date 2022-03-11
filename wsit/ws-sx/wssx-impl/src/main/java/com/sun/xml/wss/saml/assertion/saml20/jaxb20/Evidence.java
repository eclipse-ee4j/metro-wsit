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
 * $Id: Evidence.java,v 1.2 2010-10-21 15:38:03 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.EvidenceType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import java.util.List;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 * The <code>Evidence</code> element specifies an assertion either by
 * reference or by value. An assertion is specified by reference to the value of
 * the assertion's  <code>AssertionIDReference</code> element.
 * An assertion is specified by value by including the entire
 * <code>Assertion</code> object
 */
public class Evidence extends EvidenceType
    implements com.sun.xml.wss.saml.Evidence {

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
     * Constructs an <code>Evidence</code> object from a block of existing XML
     * that has already been built into a DOM.
     *
     * @param element A <code>org.w3c.dom.Element</code>
     *        representing DOM tree for <code>Evidence</code> object.
     * @exception SAMLException if it could not process the Element properly,
     *            implying that there is an error in the sender or in the
     *            element definition.
     */
    public static EvidenceType fromElement(org.w3c.dom.Element element)
        throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();

            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (EvidenceType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void setAssertionIDReferenceOrAssertion(List evidence) {
        this.assertionIDRefOrAssertionURIRefOrAssertion = evidence;
    }


    /**
     * Constructs an Evidence from a Set of <code>Assertion</code> and
     * <code>AssertionIDReference</code> objects.
     *
     * @param assertionIDRef Set of <code>AssertionIDReference</code> objects.
     * @param assertion Set of <code>Assertion</code> objects.
     */
    public Evidence(List assertionIDRef, List assertion)
        {

        if ( assertionIDRef != null)
            setAssertionIDReferenceOrAssertion(assertionIDRef);
        else if ( assertion != null)
            setAssertionIDReferenceOrAssertion(assertion);
    }

    public Evidence(EvidenceType eveType){
        setAssertionIDReferenceOrAssertion(eveType.getAssertionIDRefOrAssertionURIRefOrAssertion());
    }
}
