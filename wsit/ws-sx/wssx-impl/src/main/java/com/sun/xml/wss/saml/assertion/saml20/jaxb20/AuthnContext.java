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
 * $Id: AuthnContext.java,v 1.2 2010-10-21 15:38:03 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.AuthnContextType;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.ObjectFactory;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import org.w3c.dom.Element;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 * The <code>AuthnContext</code> element may be used to indicate
 * to a replying party receiving an <code>AuthenticationStatement</code> that
 * a SAML authority may be available to provide additional information about
 * the subject of the statement. A single SAML authority may advertise its
 * presence over multiple protocol binding, at multiple locations, and as
 * more than one kind of authority by sending multiple elements as needed.
 */
public class AuthnContext extends AuthnContextType
    implements com.sun.xml.wss.saml.AuthnContext {
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

 
    /**
     * Constructs an <code>AuthnContext</code> element from an existing XML
     * block.
     *
     * @param element representing a DOM tree element.
     * @exception SAMLException if there is an error in the sender or in the
     *            element definition.
     */
    public static AuthnContextType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();
                    
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AuthnContextType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    /**
     *Constructor
     */
    public AuthnContext()
        {
//        setAuthorityKind(authKind);
//        setLocation(location);
//        setBinding(binding);
    }
    
    public AuthnContext(String authContextClassref, String authenticatingAuthority){
        ObjectFactory factory = new ObjectFactory();        
        if (authContextClassref != null){
            getContent().add(factory.createAuthnContextClassRef(authContextClassref));
        }
        if(authenticatingAuthority != null){
            getContent().add(factory.createAuthenticatingAuthority(authenticatingAuthority));
        }
    }
}
