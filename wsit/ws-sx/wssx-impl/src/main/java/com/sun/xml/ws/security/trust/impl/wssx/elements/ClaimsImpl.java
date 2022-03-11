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
* $Id: ClaimsImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import java.util.List;

import com.sun.xml.ws.api.security.trust.WSTrustException;

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ClaimsType;
import java.util.ArrayList;

import jakarta.xml.bind.JAXBElement;
import org.w3c.dom.Element;


/**
 * Implementation class for Claims.
 *
 * @author Manveen Kaur
 */
public class ClaimsImpl extends ClaimsType implements Claims {

    List<Object> supportingInfo = new ArrayList<>();

    public ClaimsImpl() {
        // default constructor
    }

    public ClaimsImpl(String dialect) {
        setDialect(dialect);
    }

    public ClaimsImpl(ClaimsType clType) {
        setDialect(clType.getDialect());
        getAny().addAll(clType.getAny());
        getOtherAttributes().putAll(clType.getOtherAttributes());
    }

    public static ClaimsType fromElement(Element element)
        throws WSTrustException {
        try {
            final jakarta.xml.bind.Unmarshaller unmarshaller = WSTrustElementFactory.getContext(WSTrustVersion.WS_TRUST_13).createUnmarshaller();
            return (ClaimsType)((JAXBElement)unmarshaller.unmarshal(element)).getValue();
        } catch ( Exception ex) {
            throw new WSTrustException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<Object> getSupportingProperties() {
        return supportingInfo;
    }
}
