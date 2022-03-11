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
 * $Id: LifetimeImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import jakarta.xml.bind.JAXBContext;

import com.sun.xml.ws.security.wsu10.AttributedDateTime;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.LifetimeType;

import com.sun.xml.ws.security.trust.elements.Lifetime;

/**
 *
 * @author Manveen Kaur
 */
public class LifetimeImpl extends LifetimeType implements Lifetime {

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

    public LifetimeImpl(LifetimeType ltType){
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
    public static LifetimeType fromElement(org.w3c.dom.Element element)
        throws WSTrustException {
        try {
            JAXBContext jc =
                JAXBContext.newInstance("com.sun.xml.ws.security.trust.impl.wssx.elements");
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (LifetimeType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new WSTrustException(ex.getMessage(), ex);
        }
    }

}
