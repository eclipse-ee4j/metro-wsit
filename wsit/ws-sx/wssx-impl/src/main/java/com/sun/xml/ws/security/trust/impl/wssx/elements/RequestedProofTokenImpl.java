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
 * $Id: RequestedProofTokenImpl.java,v 1.2 2010-10-21 15:37:05 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;

import com.sun.xml.ws.api.security.trust.WSTrustException;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;

import java.net.URI;
import java.net.URISyntaxException;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ObjectFactory;

import com.sun.xml.ws.security.trust.elements.BinarySecret;
import com.sun.xml.ws.security.trust.elements.RequestedProofToken;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestedProofTokenType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.BinarySecretType;

import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.trust.WSTrustVersion;

/**
 * @author Manveen Kaur
 */
public class RequestedProofTokenImpl extends RequestedProofTokenType implements RequestedProofToken {

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

    public RequestedProofTokenImpl (RequestedProofTokenType rptType){
        JAXBElement obj = (JAXBElement)rptType.getAny();
        String local = obj.getName().getLocalPart();
        if (local.equalsIgnoreCase("ComputedKey")) {
            try {
                setComputedKey(new URI((String)obj.getValue()));
            } catch (URISyntaxException ex){
                throw new RuntimeException(ex);
            }
        }else if (local.equalsIgnoreCase("BinarySecret")){
            BinarySecretType bsType = (BinarySecretType)obj.getValue();
            setBinarySecret(new BinarySecretImpl(bsType));
        } else{
                throw new UnsupportedOperationException("Unsupported requested proof token: " + local);
        }
    }

    @Override
    public String getProofTokenType() {
        return tokenType;
    }

    @Override
    public void setProofTokenType(String proofTokenType) {
        if (! (proofTokenType.equalsIgnoreCase(RequestedProofToken.BINARY_SECRET_TYPE)
            || proofTokenType.equalsIgnoreCase(RequestedProofToken.COMPUTED_KEY_TYPE)
            || proofTokenType.equalsIgnoreCase(RequestedProofToken.ENCRYPTED_KEY_TYPE)
            || proofTokenType.equalsIgnoreCase(RequestedProofToken.CUSTOM_TYPE)
            || proofTokenType.equalsIgnoreCase(RequestedProofToken.TOKEN_REF_TYPE)
            ))
            // make this a WSTrustException?
        throw new RuntimeException("Invalid tokenType");
        tokenType = proofTokenType;
    }

    @Override
    public void setSecurityTokenReference(SecurityTokenReference reference) {
        if (reference != null) {
            str = reference;
            JAXBElement<SecurityTokenReferenceType> strElement=
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
    public void setComputedKey(URI computedKey) {
        if (computedKey != null) {
            String ckString = computedKey.toString();
            if (!(ckString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getCKHASHalgorithmURI()) ||
                    (ckString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getCKPSHA1algorithmURI())))) {
                throw new RuntimeException("Invalid computedKeyURI");
            }
            this.computedKey = computedKey;
            JAXBElement<String> ckElement=
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
    public void setBinarySecret(BinarySecret secret) {
       if (secret != null) {
            this.secret = secret;
            JAXBElement<BinarySecretType> bsElement=
                    (new ObjectFactory()).createBinarySecret((BinarySecretType)secret);
            setAny(bsElement);
        }
        setProofTokenType(RequestedProofToken.BINARY_SECRET_TYPE);
    }

    @Override
    public BinarySecret getBinarySecret() {
        return secret;
    }

    public static RequestedProofTokenType fromElement(org.w3c.dom.Element element)
        throws WSTrustException {
        try {
            JAXBContext jc =
                JAXBContext.newInstance("com.sun.xml.ws.security.trust.impl.wssx.elements");
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (RequestedProofTokenType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new WSTrustException(ex.getMessage(), ex);
        }
    }

}
