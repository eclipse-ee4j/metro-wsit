/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: Advice.java,v 1.2 2010-10-21 15:38:00 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AdviceType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;


//import com.sun.xml.bind.util.ListImpl;

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
public class Advice  extends com.sun.xml.wss.saml.internal.saml11.jaxb20.AdviceType implements com.sun.xml.wss.saml.Advice {
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
   
    @SuppressWarnings("unchecked")
    public static AdviceType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc = SAMLJAXBUtil.getJAXBContext();
                    
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AdviceType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    
    private void setAssertionIDReferenceOrAssertionOrAny(
            List<Object> assertionIDReferenceOrAssertionOrAny) {
        this.assertionIDReferenceOrAssertionOrAny = assertionIDReferenceOrAssertionOrAny;
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
            setAssertionIDReferenceOrAssertionOrAny(assertionidreference);
        } else if ( null != assertion) {
            setAssertionIDReferenceOrAssertionOrAny(assertion);
        } else if ( null != otherelement) {
            setAssertionIDReferenceOrAssertionOrAny(otherelement);
        }
    }
    
    public Advice(AdviceType adviceType) {
        if(adviceType != null){
            setAssertionIDReferenceOrAssertionOrAny(adviceType.getAssertionIDReferenceOrAssertionOrAny());
        }
    }
    
    public List<Object> getAdvice(){
        return super.getAssertionIDReferenceOrAssertionOrAny();
    }
}
