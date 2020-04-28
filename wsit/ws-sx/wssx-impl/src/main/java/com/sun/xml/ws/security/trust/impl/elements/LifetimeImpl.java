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
 * $Id: LifetimeImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import jakarta.xml.bind.JAXBException;

import com.sun.xml.ws.security.wsu10.AttributedDateTime;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.impl.bindings.LifetimeType;

import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.istack.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;


/**
 *
 * @author Manveen Kaur
 */
public class LifetimeImpl extends LifetimeType implements Lifetime {
    
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);
    
    public LifetimeImpl() {
        // default empty constructor
    }
    
    public LifetimeImpl(AttributedDateTime created,  AttributedDateTime expires) {
        if (created != null) {
            setCreated(created);
        }
        if (expires !=null) {
            setExpires(expires);
        }
    }
    
    public LifetimeImpl(@NotNull final LifetimeType ltType){
        this(ltType.getCreated(), ltType.getExpires());
    }
    
    /**
     * Constructs a <code>Lifetime</code> element from
     * an existing XML block.
     *
     * @param element
     *        <code>org.w3c.dom.Element</code> representing DOM tree
     *        for <code>Lifetime</code> object.
     * @exception WSTrustException if it could not process the
     *            <code>org.w3c.dom.Element</code> properly, implying that
     *            there is an error in the sender or in the element definition.
     */
    public static LifetimeType fromElement(@NotNull final org.w3c.dom.Element element)
    throws WSTrustException {
        try {
            final jakarta.xml.bind.Unmarshaller unmarshaller = WSTrustElementFactory.getContext().createUnmarshaller();
            return (LifetimeType)unmarshaller.unmarshal(element);
        } catch ( JAXBException ex) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT());
            throw new WSTrustException(LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
        }
    }
    
}
