/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * GenericToken.java
 *
 * Created on February 15, 2006, 2:06 PM
 */

package com.sun.xml.ws.security.trust;

import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import java.util.UUID;

import com.sun.xml.ws.security.Token;

import org.w3c.dom.Element;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author Jiandong Guo
 */
public class GenericToken implements Token{
    
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);
    
    private Object token;
    
    //private JAXBElement tokenEle;
    
    private String tokenType;
    private SecurityHeaderElement she = null;
    private String id;
    
    /** Creates a new instance of GenericToken */
    public GenericToken(Element token) {
        this.token = token;
        id = token.getAttributeNS(null,"AssertionID");
        if(id == null || id.length() ==0){
            id = token.getAttributeNS(null,"ID");
        }
        if(id == null || id.length() ==0){
            id = token.getAttributeNS(null,"Id");
        }
        if(id == null || id.length() == 0){
            id = UUID.randomUUID().toString();
        }
    }

    public GenericToken(JAXBElement token){
        this.token = token;
    }
    
    public GenericToken(Element token, String tokenType){
        this(token);
        
        this.tokenType = tokenType;
    }
    
    public GenericToken(SecurityHeaderElement headerElement){
        this.she = headerElement;
    }


    
    public String getType(){
        if (tokenType != null) {
            if(log.isLoggable(Level.FINE)) {
                log.log(Level.FINE,
                       LogStringsMessages.WST_1001_TOKEN_TYPE(tokenType)); 
            }
            return tokenType;
        }
        return WSTrustConstants.OPAQUE_TYPE;
    }
    
    public Object getTokenValue(){
        return this.token;
    }
    
    public SecurityHeaderElement getElement(){
        return this.she;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
