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
 * $Id: SecurityContextTokenImpl.java,v 1.2 2010-10-21 15:36:41 snajper Exp $
 */

package com.sun.xml.ws.security.secconv.impl.elements;

import com.sun.xml.ws.security.SecurityContextToken;
import com.sun.xml.ws.security.secconv.WSSCConstants;
import com.sun.xml.ws.security.secconv.impl.bindings.ObjectFactory;
import com.sun.xml.ws.security.secconv.impl.bindings.SecurityContextTokenType;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.secconv.logging.LogDomainConstants;
import com.sun.xml.ws.security.secconv.logging.LogStringsMessages;
import com.sun.xml.wss.WSITXMLFactory;

/**
 * SecurityContextToken Implementation
 *
 * @author Manveen Kaur manveen.kaur@sun.com
 */
public class SecurityContextTokenImpl extends SecurityContextTokenType implements SecurityContextToken {
    
    private String instance = null;
    private URI identifier = null;
    private List<Object> extElements = null;
    
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSSC_IMPL_DOMAIN,
            LogDomainConstants.WSSC_IMPL_DOMAIN_BUNDLE);
    
    public SecurityContextTokenImpl() {
        // empty c'tor
    }
    
    public SecurityContextTokenImpl(URI identifier, String instance, String wsuId) {
        if (identifier != null) {
            setIdentifier(identifier);
        }
        if (instance != null) {
            setInstance(instance);
        }
        
        if (wsuId != null){
            setWsuId(wsuId);
        }
    }
    
    // useful for converting from JAXB to our owm impl class
    public SecurityContextTokenImpl(SecurityContextTokenType sTokenType){
        final List<Object> list = sTokenType.getAny();
        for (int i = 0; i < list.size(); i++) {
            final Object object = list.get(i);
            if(object instanceof JAXBElement){
                final JAXBElement obj = (JAXBElement)object;
                
                final String local = obj.getName().getLocalPart();
                if (local.equalsIgnoreCase("Instance")) {
                    setInstance((String)obj.getValue());
                } else if (local.equalsIgnoreCase("Identifier")){
                    setIdentifier(URI.create((String)obj.getValue()));
                }
            }else{
                getAny().add(object);
                if(extElements == null){
                    extElements = new ArrayList<>();
                    extElements.add(object);
                }
            }
        }
        
        setWsuId(sTokenType.getId());
    }
    
    @Override
    public URI getIdentifier() {
        return identifier;
    }
    
    public final void setIdentifier(final URI identifier) {
        this.identifier = identifier;
        final JAXBElement<String> iElement =
                (new ObjectFactory()).createIdentifier(identifier.toString());
        getAny().add(iElement);
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE,
                    LogStringsMessages.WSSC_1004_SECCTX_TOKEN_ID_VALUE(identifier.toString()));
        }
    }
    
    @Override
    public String getInstance() {
        return instance;
    }
    
    public final void setInstance(final String instance) {
        this.instance = instance;
        final JAXBElement<String> iElement =
                (new ObjectFactory()).createInstance(instance);
        getAny().add(iElement);
    }
    
    public final void setWsuId(final String wsuId){
        setId(wsuId);
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE,
                    LogStringsMessages.WSSC_1005_SECCTX_TOKEN_WSUID_VALUE(wsuId));
        }
    }
    
    @Override
    public String getWsuId(){
        return getId();
    }
    
    @Override
    public String getType() {
        return WSSCConstants.SECURITY_CONTEXT_TOKEN;
    }
    
    @Override
    public Object getTokenValue() {
        try {
            final DocumentBuilderFactory dbf = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
            dbf.setNamespaceAware(true);
            final DocumentBuilder builder = dbf.newDocumentBuilder();
            final Document doc = builder.newDocument();
            
            final jakarta.xml.bind.Marshaller marshaller = WSTrustElementFactory.getContext().createMarshaller();
            final JAXBElement<SecurityContextTokenType> tElement =  (new ObjectFactory()).createSecurityContextToken(this);
            marshaller.marshal(tElement, doc);
            return doc.getDocumentElement();
            
        } catch (Exception ex) {
            log.log(Level.SEVERE, 
                    LogStringsMessages.WSSC_0019_ERR_TOKEN_VALUE(), ex);
            throw new RuntimeException(LogStringsMessages.WSSC_0019_ERR_TOKEN_VALUE(), ex);
        }
    }
    
    @Override
    public List getExtElements() {
        return extElements;
    }
}
