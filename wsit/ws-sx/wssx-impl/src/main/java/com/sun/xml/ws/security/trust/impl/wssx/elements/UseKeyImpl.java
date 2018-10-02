/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: UseKeyImpl.java,v 1.2 2010-10-21 15:37:05 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import java.net.URI;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.trust.impl.elements.str.SecurityTokenReferenceImpl;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.elements.UseKey;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.UseKeyType;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Element;

/**
 * @author Manveen Kaur
 */
public class UseKeyImpl extends UseKeyType implements UseKey {
    
    private String targetType = null;
    
    private SecurityTokenReference str = null;
    private Token token = null;
    
    public UseKeyImpl(Token token) {
        setToken(token);
    }
    
    public UseKeyImpl(SecurityTokenReference str) {
        setSecurityTokenReference(str);
        setTargetType(WSTrustConstants.STR_TYPE);
    }
    
    public UseKeyImpl (UseKeyType ukType)throws Exception{
        Object obj = ukType.getAny();
        if (obj instanceof JAXBElement){
           token = new GenericToken((JAXBElement)obj);
        }else{
            token = new GenericToken((Element)obj);
        }
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String ttype) {
        targetType = ttype;
    }
    
    public void setSecurityTokenReference(SecurityTokenReference ref) {
        if (ref != null) {
            str = ref;
            JAXBElement<SecurityTokenReferenceType> strElement=
                    (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)ref);
            setAny(strElement);
        }
        setTargetType(WSTrustConstants.STR_TYPE);
        token = null;
    }
    
    public SecurityTokenReference getSecurityTokenReference() {
        return str;
    }
    
    public void setToken(Token token) {
        if (token != null) {
            this.token = token;
            setAny(token.getTokenValue());
        }
        setTargetType(WSTrustConstants.TOKEN_TYPE);
        str = null;
    }
    
    public Token getToken() {
        return token;
    }
    
    public void setSignatureID(URI sigID) {
        setSig(sigID.toString());
    }
    
    public URI getSignatureID() {
        try {
            return new URI(getSig());
        } catch (URISyntaxException ue) {
            throw new RuntimeException("URI syntax invalid ", ue);
        }
    }
    
}
