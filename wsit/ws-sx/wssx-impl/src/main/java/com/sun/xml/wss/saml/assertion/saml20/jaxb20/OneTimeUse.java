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
 * $Id: OneTimeUse.java,v 1.2 2010-10-21 15:38:04 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.OneTimeUseType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 *This is an implementation of the abstract <code>Condition</code> class, which
 * specifes that the assertion this <code>OneTimeUse</code> is part of,
 * is the new element in SAML 1.1, that allows an assertion party to express that
 * an assertion should not be cached by the relying party for future use. In another
 * word, such an assertion is meant only for "one-time" use by the relying party.
 */
public class OneTimeUse extends OneTimeUseType
    implements com.sun.xml.wss.saml.OneTimeUse {

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);


    /**
     * Constructs a <code>OneTimeUse</code> element from
     * an existing XML block.
     *
     * @param element A
     *        <code>org.w3c.dom.Element</code> representing DOM tree
     *        for <code>OneTimeUse</code> object.
     * @exception SAMLException if it could not process the
     *            <code>org.w3c.dom.Element</code> properly, implying that
     *            there is an error in the sender or in the element definition.
     */
    public static OneTimeUseType fromElement(org.w3c.dom.Element element)
        throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();

            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (OneTimeUseType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
}
