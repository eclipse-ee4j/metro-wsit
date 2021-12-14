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
 * $Id: EncryptionImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.trust.impl.elements.str.SecurityTokenReferenceImpl;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.elements.Encryption;
import com.sun.xml.ws.security.trust.impl.bindings.EncryptionType;
import jakarta.xml.bind.JAXBElement;

/**
 * @author Manveen Kaur
 */
public class EncryptionImpl extends EncryptionType implements Encryption {
    
    private String targetType = null;
    
    private SecurityTokenReference str = null;
    private Token token = null;
    
    public EncryptionImpl(SecurityTokenReference str) {
        setSecurityTokenReference(str);
        setTargetType(WSTrustConstants.STR_TYPE);
    }
    
    public EncryptionImpl(Token token) {
        setToken(token);
        setTargetType(WSTrustConstants.TOKEN_TYPE);
    }
    
    public EncryptionImpl (EncryptionType encType){
        final JAXBElement obj = (JAXBElement)encType.getAny();
        final String local = obj.getName().getLocalPart();
        if ("SecurityTokenReference".equals(local)) {
            final SecurityTokenReference str = 
                        new SecurityTokenReferenceImpl((SecurityTokenReferenceType)obj.getValue());
            setSecurityTokenReference(str);
            setTargetType(WSTrustConstants.STR_TYPE);
        } else {
            //ToDo
        } 
    }
    
    @Override
    public String getTargetType() {
        return targetType;
    }
    
    public final void setTargetType(final String ttype) {
        targetType = ttype;
    }
    
    @Override
    public final void setSecurityTokenReference(final SecurityTokenReference ref) {
        if (ref != null) {
            str = ref;
            final JAXBElement<SecurityTokenReferenceType> strElement=
                    (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)ref);
            setAny(strElement);
        }
        setTargetType(WSTrustConstants.STR_TYPE);
        token = null;
    }
    
    @Override
    public SecurityTokenReference getSecurityTokenReference() {
        return str;
    }
    
    @Override
    public final void setToken(final Token token) {
        if (token != null) {
            this.token = token;
            setAny(token);
        }
        setTargetType(WSTrustConstants.TOKEN_TYPE);
        str = null;
    }
    
    @Override
    public Token getToken() {
        return token;
    }
    
    
}
