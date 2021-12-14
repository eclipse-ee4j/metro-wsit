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
 * $Id: UseKeyImpl.java,v 1.2 2010-10-21 15:36:55 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.GenericToken;
import java.net.URI;

import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.elements.UseKey;
import com.sun.xml.ws.security.trust.impl.bindings.UseKeyType;

import com.sun.istack.NotNull;

import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import org.w3c.dom.Element;
import jakarta.xml.bind.JAXBElement;


/**
 * @author Manveen Kaur
 */
public class UseKeyImpl extends UseKeyType implements UseKey {
    
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);
    
    public UseKeyImpl(Token token) {
        setToken(token);
    }
    
    public UseKeyImpl (@NotNull final UseKeyType ukType){
        setAny(ukType.getAny());
        setSig(ukType.getSig());
    }
    
    @Override
    public void setToken(@NotNull final Token token) {
        setAny(token.getTokenValue());
    }
    
    @Override
    public Token getToken() {
        Object value = getAny();
        if (value instanceof Element){
            return new GenericToken((Element)value);
        } else if (value instanceof JAXBElement){
            return new GenericToken((JAXBElement)value);
        }
        
        //ToDo
        return null;
    }
    
    @Override
    public void setSignatureID(@NotNull final URI sigID) {
        setSig(sigID.toString());
    }
    
    @Override
    public URI getSignatureID() {
        return URI.create(getSig());
    }
    
}
