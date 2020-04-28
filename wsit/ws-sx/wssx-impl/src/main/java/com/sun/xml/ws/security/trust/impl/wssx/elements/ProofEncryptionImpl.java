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
 * $Id: ProofEncryptionImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.trust.impl.elements.str.SecurityTokenReferenceImpl;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.elements.ProofEncryption;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ProofEncryptionType;
import jakarta.xml.bind.JAXBElement;

/**
 * @author WS-Trust Implementation Team.
 */
public class ProofEncryptionImpl extends ProofEncryptionType implements ProofEncryption {
    
    private String targetType = null;
    
    private SecurityTokenReference str = null;
    private Token token = null;
    
    public ProofEncryptionImpl(SecurityTokenReference str) {
        setSecurityTokenReference(str);
        setTargetType(WSTrustConstants.STR_TYPE);
    }
    
    public ProofEncryptionImpl(Token token) {
        setToken(token);
        setTargetType(WSTrustConstants.TOKEN_TYPE);
    }
    
     public ProofEncryptionImpl (ProofEncryptionType peType)throws Exception{
        JAXBElement obj = (JAXBElement)peType.getAny();
        String local = obj.getName().getLocalPart();
        if ("SecurityTokenReference".equals(local)) {
            SecurityTokenReference str = 
                        new SecurityTokenReferenceImpl((SecurityTokenReferenceType)obj.getValue());
            setSecurityTokenReference(str);
            setTargetType(WSTrustConstants.STR_TYPE);
        } else {
            //ToDo
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
            setAny(token);
        }
        setTargetType(WSTrustConstants.TOKEN_TYPE);
        str = null;
    }
    
    public Token getToken() {
        return token;
    }
    
}
