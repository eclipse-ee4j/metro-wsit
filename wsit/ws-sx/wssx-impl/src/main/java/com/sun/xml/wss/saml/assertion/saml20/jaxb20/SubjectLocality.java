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
 * $Id: SubjectLocality.java,v 1.2 2010-10-21 15:38:04 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.SubjectLocalityType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
The <code>SubjectLocality</code> element specifies the DNS domain name
and IP address for the system entity that performed the authentication.
It exists as part of <code>AuthenticationStatement</code> element.
*/
public class SubjectLocality extends SubjectLocalityType
    implements com.sun.xml.wss.saml.SubjectLocality {

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
    Constructor
    Constructor taking in nothing (assertion schema 25 allows it )
    */
    public SubjectLocality() {
        super();
    }

    /**
     * Constructs an instance of <code>SubjectLocality</code> from an existing
     * XML block.
     *
     * @param element A <code>org.w3c.dom.Element</code> representing
     *        DOM tree for <code>SubjectLocality</code> object.
     * @exception SAMLException if it could not process the Element properly,
     *            implying that there is an error in the sender or in the
     *            element definition.
     */
    public static SubjectLocalityType fromElement(org.w3c.dom.Element element)
        throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();

            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (SubjectLocalityType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    /**
     * Constructs an instance of <code>SubjectLocality</code>.
     *
     * @param address String representing the IP Address of the entity
     *        that was authenticated.
     * @param dnsName String representing the DNS Address of the entity that
     *        was authenticated. As per SAML specification  they are both
     *        optional, so values can be null.
     */
    public SubjectLocality(String address, String dnsName) {
        setAddress(address);
        setDNSName(dnsName);
    }
}
