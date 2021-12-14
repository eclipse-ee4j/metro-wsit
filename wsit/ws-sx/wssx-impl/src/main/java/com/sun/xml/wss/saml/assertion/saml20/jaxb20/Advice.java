/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.wss.saml.internal.saml20.jaxb20.AdviceType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import jakarta.xml.bind.JAXBContext;
import org.w3c.dom.Element;

import java.util.List;
import java.util.logging.Logger;


/**
 *The <code>Advice</code> element contains additional information that the issuer wishes to
 *provide. This information MAY be ignored by applications without affecting
 *either the semantics or validity. Advice elements MAY be specified in
 *an extension schema.
 */
public class Advice  extends AdviceType implements com.sun.xml.wss.saml.Advice {
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    public static AdviceType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AdviceType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    private void setAssertionIDRefOrAssertionURIRefOrAssertion(
            List assertionIDRefOrAssertionURIRefOrAssertion) {
        this.assertionIDRefOrAssertionURIRefOrAssertion = assertionIDRefOrAssertionURIRefOrAssertion;
    }
    
    /**
     * Constructor
     *
     * @param assertionidreference A List of <code>AssertionIDReference</code>.
     * @param assertion A List of Assertion
     * @param otherelement A List of any element defined as
     *        <code>&lt;any namespace="##other" processContents="lax"&gt;</code>;
     */
    @SuppressWarnings("unchecked")
    public Advice(List assertionidreference, List assertion, List otherelement) {
        if ( null != assertionidreference ) {
            setAssertionIDRefOrAssertionURIRefOrAssertion(assertionidreference);
        } else if ( null != assertion) {
            setAssertionIDRefOrAssertionURIRefOrAssertion(assertion);
        } else if ( null != otherelement) {
            setAssertionIDRefOrAssertionURIRefOrAssertion(otherelement);
        }
    }
    
    public Advice(AdviceType adviceType) {
        if(adviceType != null){
            setAssertionIDRefOrAssertionURIRefOrAssertion(adviceType.getAssertionIDRefOrAssertionURIRefOrAssertion());
        }
    }

    @Override
    public List<Object> getAdvice() {
        return super.getAssertionIDRefOrAssertionURIRefOrAssertion();
    }
}
