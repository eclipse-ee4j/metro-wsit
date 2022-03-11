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
 * $Id: ClaimsImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.security.trust.impl.bindings.ClaimsType;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.ws.security.trust.logging.LogDomainConstants;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;

import java.util.ArrayList;


/**
 * Implementation class for Claims.
 *
 * @author Manveen Kaur
 */
public class ClaimsImpl extends ClaimsType implements Claims {

    List<Object> supportingInfo = new ArrayList<>();
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);


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

    public static ClaimsType fromElement(final org.w3c.dom.Element element)
    throws WSTrustException {
        try {
            final jakarta.xml.bind.Unmarshaller unmarshaller = WSTrustElementFactory.getContext().createUnmarshaller();
            return (ClaimsType)((JAXBElement)unmarshaller.unmarshal(element)).getValue();
        } catch (JAXBException ex) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
            throw new WSTrustException(LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
        }
    }

    @Override
    public List<Object> getSupportingProperties() {
        return supportingInfo;
    }

}
