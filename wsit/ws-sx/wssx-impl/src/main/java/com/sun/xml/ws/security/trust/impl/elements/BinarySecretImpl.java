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
 * $Id: BinarySecretImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.WSTrustElementFactory;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;



import com.sun.xml.ws.api.security.trust.WSTrustException;

import com.sun.xml.ws.security.trust.impl.bindings.BinarySecretType;

import com.sun.xml.ws.security.trust.elements.BinarySecret;

import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.exceptions.Base64DecodingException;

import com.sun.istack.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;

/**
 *
 * @author WS-Trust Implementation Team
 */
public class BinarySecretImpl extends BinarySecretType implements BinarySecret {

    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);

    public BinarySecretImpl(@NotNull final byte[] rawValue, String type) {
        setRawValue(rawValue);
        setType(type);

    }

    public BinarySecretImpl(@NotNull final BinarySecretType bsType){
        this(bsType.getValue(), bsType.getType());

    }

    /**
     * Constructs a <code>BinarySecret</code> element from
     * an existing XML block.
     *
     * @param element
     *        <code>org.w3c.dom.Element</code> representing DOM tree
     *        for <code>BinarySecret</code> object.
     * @exception WSTrustException if it could not process the
     *            <code>org.w3c.dom.Element</code> properly, implying that
     *            there is an error in the sender or in the element definition.
     */
    public static BinarySecretType fromElement(@NotNull final org.w3c.dom.Element element)
        throws WSTrustException {
        try {
            final jakarta.xml.bind.Unmarshaller u = WSTrustElementFactory.getContext().createUnmarshaller();
            return (BinarySecretType)((JAXBElement)u.unmarshal(element)).getValue();
        } catch (JAXBException ex) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
            throw new WSTrustException(LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
        }
    }

    @Override
    @NotNull
     public byte[] getRawValue() {
        return super.getValue();
     }

     @Override
     @NotNull
     public String getTextValue() {
        return Base64.encode(getRawValue());
     }

     @Override
     public final void setRawValue(@NotNull final byte[] rawText) {
        setValue(rawText);
     }

     @Override
     public void setTextValue(@NotNull final String encodedText) {
         try {
             setValue(Base64.decode(encodedText));
         } catch (Base64DecodingException de) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0020_ERROR_DECODING(encodedText), de);
             throw new RuntimeException(LogStringsMessages.WST_0020_ERROR_DECODING(encodedText), de);
         }
     }
}
