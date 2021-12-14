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
 * $Id: EntropyImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;


import jakarta.xml.bind.JAXBElement;

import javax.xml.namespace.QName;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.elements.BinarySecret;
import com.sun.xml.ws.security.EncryptedKey;

import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.impl.bindings.EntropyType;
import com.sun.xml.ws.security.trust.impl.bindings.BinarySecretType;
import com.sun.xml.ws.security.trust.impl.bindings.ObjectFactory;
import java.util.List;

import com.sun.istack.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import jakarta.xml.bind.JAXBException;
/**
 * Implementation of Entropy Interface.
 *
 * @author Manveen Kaur
 */
public class EntropyImpl extends EntropyType implements Entropy {
    
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);
        
    private String entropyType;
    private final static QName _EntropyType_QNAME = new QName("http://schemas.xmlsoap.org/ws/2005/02/trust", "Type");
    
    private BinarySecret binarySecret = null;
    
    private EncryptedKey encryptedKey = null;
    
    public EntropyImpl() {
        //default constructor
    }
    
    public EntropyImpl(BinarySecret binarySecret) {
        setEntropyType(Entropy.BINARY_SECRET_TYPE);
        setBinarySecret(binarySecret);
    }
    
    public EntropyImpl(EncryptedKey encryptedKey) {
        setEntropyType(Entropy.ENCRYPTED_KEY_TYPE);
        setEncryptedKey(encryptedKey);
    }
    
    public EntropyImpl(@NotNull final EntropyType etype) {
        entropyType = etype.getOtherAttributes().get(_EntropyType_QNAME);
        final List list = etype.getAny();
        for (int i = 0; i < list.size(); i++) {
            final JAXBElement obj = (JAXBElement)list.get(i);
            final String local = obj.getName().getLocalPart();
            if (local.equalsIgnoreCase("BinarySecret")) {
                final BinarySecretType bst = (BinarySecretType) obj.getValue();
                setBinarySecret(new BinarySecretImpl(bst));
            }
        }
    }
    
    /**
     * Constructs a <code>Entropy</code> element from
     * an existing XML block.
     *
     * @param element A
     *        <code>org.w3c.dom.Element</code> representing DOM tree
     *        for <code>Entropy</code> object.
     * @exception WSTrustException if it could not process the
     *            <code>org.w3c.dom.Element</code> properly, implying that
     *            there is an error in the sender or in the element definition.
     */
    public static EntropyType fromElement(final org.w3c.dom.Element element)
    throws WSTrustException {
        try {
            final jakarta.xml.bind.Unmarshaller unmarshaller = WSTrustElementFactory.getContext().createUnmarshaller();
            return (EntropyType)unmarshaller.unmarshal(element);
        } catch (JAXBException ex) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
            throw new WSTrustException(LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
        }
    }
    
    /**
     *Gets the type of the Entropy contents
     */
    @Override
    public String getEntropyType() {
        return entropyType;
    }
    
    /**
     *Sets the type of the Entropy contents
     */
    @Override
    public final void setEntropyType(@NotNull final String type)  {
        if (!(type.equalsIgnoreCase(this.BINARY_SECRET_TYPE)  ||
                type.equalsIgnoreCase(this.CUSTOM_TYPE)
                || type.equalsIgnoreCase(this.ENCRYPTED_KEY_TYPE))) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0022_INVALID_ENTROPY(type));
            throw new RuntimeException(LogStringsMessages.WST_0022_INVALID_ENTROPY(type));
        }
        entropyType = type;
        getOtherAttributes().put(_EntropyType_QNAME,type);
    }
    
    
    /** Gets the BinarySecret (if any) inside this Entropy
     * @return BinarySecret if set, null otherwise
     */
    @Override
    public BinarySecret getBinarySecret() {
        return binarySecret;
    }
    
    /**
     * Sets the BinarySecret (if any) inside this Entropy
     */
    @Override
    public final void setBinarySecret(final BinarySecret binarySecret) {
        if (binarySecret != null) {
            this.binarySecret = binarySecret;
            final JAXBElement<BinarySecretType> bsElement =
                    (new ObjectFactory()).createBinarySecret((BinarySecretType)binarySecret);
            getAny().add(bsElement);
        }
    }
    
    /**
     * Gets the xenc:EncryptedKey set inside this Entropy instance
     * @return xenc:EncryptedKey if set, null otherwise
     */
    @Override
    public EncryptedKey getEncryptedKey() {
        return encryptedKey;
    }
    
    /**
     * Sets the xenc:EncryptedKey set inside this Entropy instance
     */
    @Override
    public final void setEncryptedKey(final EncryptedKey encryptedKey) {
        if (encryptedKey != null) {
            this.encryptedKey = encryptedKey;
            getAny().add(encryptedKey);
        }
    }
}
