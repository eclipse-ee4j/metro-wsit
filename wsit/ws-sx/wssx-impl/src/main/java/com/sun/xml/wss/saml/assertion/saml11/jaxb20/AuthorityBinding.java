/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: AuthorityBinding.java,v 1.2 2010-10-21 15:38:00 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AuthorityBindingType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;

/**
 * The <code>AuthorityBinding</code> element may be used to indicate
 * to a replying party receiving an <code>AuthenticationStatement</code> that
 * a SAML authority may be available to provide additional information about
 * the subject of the statement. A single SAML authority may advertise its
 * presence over multiple protocol binding, at multiple locations, and as
 * more than one kind of authority by sending multiple elements as needed.
 */
public class AuthorityBinding extends AuthorityBindingType
    implements com.sun.xml.wss.saml.AuthorityBinding {
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

 
    /**
     * Constructs an <code>AuthorityBinding</code> element from an existing XML
     * block.
     *
     * @param element representing a DOM tree element.
     * @exception SAMLException if there is an error in the sender or in the
     *            element definition.
     */
    public static AuthorityBindingType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc = SAMLJAXBUtil.getJAXBContext();
                    
            javax.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AuthorityBindingType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    /**
     *Constructor
     *@param authKind A integer representing the type of SAML protocol queries
     *       to which the authority described by this element will
     *       respond. If you do NO specify this attribute, pass in
     *       value "-1".
     *@param location A URI describing how to locate and communicate with the
     *       authority, the exact syntax of which depends on the
     *       protocol binding in use.
     *@param binding A String representing a URI reference identifying the SAML
     *       protocol binding to use in  communicating with the authority.
     */
    public AuthorityBinding(QName authKind, String location, String binding)
        {
        setAuthorityKind(authKind);
        setLocation(location);
        setBinding(binding);
    }
    
    public AuthorityBinding(AuthorityBindingType authBinType){
        setAuthorityKind(authBinType.getAuthorityKind());
        setLocation(authBinType.getLocation());
        setBinding(authBinType.getBinding());
    }
    
    @Override
    public QName getAuthorityKind(){
        return super.getAuthorityKind();
    }

    @Override
    public String getBinding(){
        return super.getBinding();
    }

    @Override
    public String getLocation(){
        return super.getLocation();
    }
}
