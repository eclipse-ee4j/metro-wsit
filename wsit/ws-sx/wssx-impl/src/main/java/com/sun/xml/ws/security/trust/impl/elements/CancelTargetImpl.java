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
 * $Id: CancelTargetImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.trust.impl.elements.str.SecurityTokenReferenceImpl;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.elements.CancelTarget;
import com.sun.xml.ws.security.trust.impl.bindings.CancelTargetType;
import jakarta.xml.bind.JAXBElement;

/**
 * Defines Binding for requesting security tokens to be cancelled.
 *
 * @author Manveen Kaur
 */
public class CancelTargetImpl extends CancelTargetType implements CancelTarget {
    
    private String targetType = null;
    
    // either STR will be present or the token will be
    // carried directly. This will typically be a BST.
    private SecurityTokenReference str = null;    
    private Token token = null;
        
    public CancelTargetImpl(SecurityTokenReference str) {
        setSecurityTokenReference(str);
        setTargetType(CancelTarget.STR_TARGET_TYPE);
    }
    
    public CancelTargetImpl(Token token) {
        setToken(token);
        setTargetType(CancelTarget.CUSTOM_TARGET_TYPE);
    }
    
    public CancelTargetImpl (CancelTargetType ctType){
        final JAXBElement obj = (JAXBElement)ctType.getAny();
        final String local = obj.getName().getLocalPart();
        if ("SecurityTokenReference".equals(local)) {
            final SecurityTokenReference str = 
                        new SecurityTokenReferenceImpl((SecurityTokenReferenceType)obj.getValue());
            setSecurityTokenReference(str);
            setTargetType(CancelTarget.STR_TARGET_TYPE);
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
        setTargetType(CancelTarget.STR_TARGET_TYPE);
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
        setTargetType(CancelTarget.CUSTOM_TARGET_TYPE);                
        str = null;
    }
    
    @Override
    public Token getToken() {
        return token;
    }
    
}
