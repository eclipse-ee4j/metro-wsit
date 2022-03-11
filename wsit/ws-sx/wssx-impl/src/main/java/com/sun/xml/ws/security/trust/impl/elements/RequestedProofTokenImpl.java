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
 * $Id: RequestedProofTokenImpl.java,v 1.2 2010-10-21 15:36:55 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;

import java.net.URI;
import com.sun.xml.ws.security.trust.impl.bindings.ObjectFactory;

import com.sun.xml.ws.security.trust.elements.BinarySecret;
import com.sun.xml.ws.security.trust.elements.RequestedProofToken;
import com.sun.xml.ws.security.trust.impl.bindings.RequestedProofTokenType;
import com.sun.xml.ws.security.trust.impl.bindings.BinarySecretType;

import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.istack.NotNull;
import com.sun.xml.ws.security.trust.WSTrustVersion;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;

/**
 * @author Manveen Kaur
 */
public class RequestedProofTokenImpl extends RequestedProofTokenType implements RequestedProofToken {

    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);

    private String tokenType;
    private URI computedKey;
    private BinarySecret secret;
    private SecurityTokenReference str;

    public RequestedProofTokenImpl() {
        // empty constructor
    }

    public RequestedProofTokenImpl(String proofTokenType) {
        setProofTokenType(proofTokenType);
    }

    public RequestedProofTokenImpl(RequestedProofTokenType rptType){
        final JAXBElement obj = (JAXBElement)rptType.getAny();
        final String local = obj.getName().getLocalPart();
        if (local.equalsIgnoreCase("ComputedKey")) {
            setComputedKey(URI.create((String)obj.getValue()));
        }else if (local.equalsIgnoreCase("BinarySecret")){
            final BinarySecretType bsType = (BinarySecretType)obj.getValue();
            setBinarySecret(new BinarySecretImpl(bsType));
        } else{
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0019_INVALID_PROOF_TOKEN_TYPE(local, null));
            throw new RuntimeException(LogStringsMessages.WST_0019_INVALID_PROOF_TOKEN_TYPE(local, null));
        }
    }

    @Override
    public String getProofTokenType() {
        return tokenType;
    }

    @Override
    public final void setProofTokenType(@NotNull final String proofTokenType) {
        if (! (proofTokenType.equalsIgnoreCase(RequestedProofToken.BINARY_SECRET_TYPE)
        || proofTokenType.equalsIgnoreCase(RequestedProofToken.COMPUTED_KEY_TYPE)
        || proofTokenType.equalsIgnoreCase(RequestedProofToken.ENCRYPTED_KEY_TYPE)
        || proofTokenType.equalsIgnoreCase(RequestedProofToken.CUSTOM_TYPE)
        || proofTokenType.equalsIgnoreCase(RequestedProofToken.TOKEN_REF_TYPE)
        )) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0019_INVALID_PROOF_TOKEN_TYPE(proofTokenType, null));
            throw new RuntimeException(LogStringsMessages.WST_0019_INVALID_PROOF_TOKEN_TYPE(proofTokenType, null));
        }
        tokenType = proofTokenType;
    }

    @Override
    public void setSecurityTokenReference(final SecurityTokenReference reference) {
        if (reference != null) {
            str = reference;
            final JAXBElement<SecurityTokenReferenceType> strElement=
                    (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)reference);
            setAny(strElement);
        }
        setProofTokenType(RequestedProofToken.TOKEN_REF_TYPE);
    }

    @Override
    public SecurityTokenReference getSecurityTokenReference() {
        return str;
    }

    @Override
    public final void setComputedKey(@NotNull final URI computedKey) {

        if (computedKey != null) {
            final String ckString = computedKey.toString();
            if (!(ckString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getCKHASHalgorithmURI()) ||
                    (ckString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getCKPSHA1algorithmURI())))) {
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0028_INVALID_CK(ckString));
                throw new RuntimeException(LogStringsMessages.WST_0028_INVALID_CK(ckString));
            }
            this.computedKey = computedKey;
            final JAXBElement<String> ckElement=
                    (new ObjectFactory()).createComputedKey(computedKey.toString());
            setAny(ckElement);
        }
        setProofTokenType(RequestedProofToken.COMPUTED_KEY_TYPE);
    }

    @Override
    public URI getComputedKey() {
        return computedKey;
    }

    @Override
    public final void setBinarySecret(final BinarySecret secret) {
        if (secret != null) {
            this.secret = secret;
            final JAXBElement<BinarySecretType> bsElement=
                    (new ObjectFactory()).createBinarySecret((BinarySecretType)secret);
            setAny(bsElement);
        }
        setProofTokenType(RequestedProofToken.BINARY_SECRET_TYPE);
    }

    @Override
    public BinarySecret getBinarySecret() {
        return secret;
    }

    public static RequestedProofTokenType fromElement(final org.w3c.dom.Element element)
    throws WSTrustException {
        try {
            final jakarta.xml.bind.Unmarshaller unmarshaller = WSTrustElementFactory.getContext().createUnmarshaller();
            return (RequestedProofTokenType)unmarshaller.unmarshal(element);
        } catch (JAXBException ex) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
            throw new WSTrustException(LogStringsMessages.WST_0021_ERROR_UNMARSHAL_DOM_ELEMENT(), ex);
        }
    }

}
