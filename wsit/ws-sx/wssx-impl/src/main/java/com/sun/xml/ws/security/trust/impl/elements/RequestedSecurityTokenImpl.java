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
 * $Id: RequestedSecurityTokenImpl.java,v 1.2 2010-10-21 15:36:55 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.impl.bindings.RequestedSecurityTokenType;
import com.sun.xml.ws.security.secconv.WSSCConstants;
import com.sun.xml.ws.security.secconv.impl.elements.SecurityContextTokenImpl;
import com.sun.xml.ws.security.secconv.impl.bindings.SecurityContextTokenType;
import com.sun.xml.ws.security.trust.impl.WSTrustElementFactoryImpl;

import com.sun.istack.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;

/**
 * Implementation for the RequestedSecurityToken.
 *
 * @author Manveen Kaur
 */
public class RequestedSecurityTokenImpl extends RequestedSecurityTokenType implements RequestedSecurityToken {

    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);

    Token containedToken = null;

    private final static QName SCT_QNAME =
            new QName("http://schemas.xmlsoap.org/ws/2005/02/sc", "SecurityContextToken");

    //private final static QName SAML11_Assertion_QNAME =
      //      new QName("urn:oasis:names:tc:SAML:1.0:assertion", "Assertion");

    //private final static QName EncryptedData_QNAME = new QName("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");

    /**
      * Empty default constructor.
      */
    public RequestedSecurityTokenImpl() {
        //Empty default constructor.
    }

    public RequestedSecurityTokenImpl(@NotNull final RequestedSecurityTokenType rdstType){
        final Object rdst = rdstType.getAny();
        if (rdst instanceof JAXBElement){
            final JAXBElement rdstEle = (JAXBElement)rdst;
            final QName name = rdstEle.getName();
            if(SCT_QNAME.equals(name)){
                final SecurityContextTokenType sctType = (SecurityContextTokenType)rdstEle.getValue();
                setToken(new SecurityContextTokenImpl(sctType));
            }/*else if(EncryptedData_QNAME.equals(name)){
               EncryptedDataType edType = (EncryptedDataType)rdstEle.getValue();
               setToken(edType);
            }else if(SAML11_Assertion_QNAME.equals(name)){
                AssertionType assertionType = (AssertionType)rdstEle.getValue();
                setToken(new Assertion(assertionType));
            }*/
            else{
                setAny(rdstEle);
                Element token = (new WSTrustElementFactoryImpl()).toElement(rdstEle);
                containedToken = new GenericToken(token);
            }
        }
        else{
            setToken(new GenericToken((Element)rdst));
        }
    }

    public RequestedSecurityTokenImpl(Token token) {
        setToken(token);
    }

    /**
     * Constructs a <code>RequestedSecurityToken</code> element from
     * an existing XML block.
     *
     * @param element
     *        <code>org.w3c.dom.Element</code> representing DOM tree
     *        for <code>RequestedSecurityToken</code> object.
     * @exception WSTrustException if it could not process the
     *            <code>org.w3c.dom.Element</code> properly, implying that
     *            there is an error in the sender or in the element definition.
     */
    public static RequestedSecurityTokenType fromElement(@NotNull final org.w3c.dom.Element element)
        throws WSTrustException {
        try {
            final JAXBContext context =
                WSTrustElementFactory.getContext();
            final jakarta.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();

            return unmarshaller.unmarshal(element, RequestedSecurityTokenType.class).getValue();
        } catch (JAXBException ex) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
            throw new WSTrustException(LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
        }
    }

    /*
     * Return the security token contained in the RequestedSecurityToken.
     */
    @Override
    public Token getToken() {
        return containedToken;
    }

    @Override
    public final void setToken(final Token token) {
        if (token != null)  {
            final String tokenType = token.getType();
            if (WSSCConstants.SECURITY_CONTEXT_TOKEN.equals(tokenType)){
                final JAXBElement<SecurityContextTokenType> sctElement =
                (new com.sun.xml.ws.security.secconv.impl.bindings.ObjectFactory()).createSecurityContextToken((SecurityContextTokenType)token);
                setAny(sctElement);
            }else {
                final Element element = (Element)token.getTokenValue();
                setAny(element);
            }
        }
        containedToken = token;
    }
}
