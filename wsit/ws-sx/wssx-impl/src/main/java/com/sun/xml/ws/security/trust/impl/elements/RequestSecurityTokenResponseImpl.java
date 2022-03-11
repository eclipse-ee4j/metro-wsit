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
 * $Id: RequestSecurityTokenResponseImpl.java,v 1.2 2010-10-21 15:36:55 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import java.net.URISyntaxException;
import java.util.List;

import java.net.URI;

import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.impl.bindings.PolicyReference;
import com.sun.xml.ws.security.trust.elements.AllowPostdating;
import com.sun.xml.ws.security.trust.elements.Authenticator;
import com.sun.xml.ws.security.trust.elements.BinaryExchange;
import com.sun.xml.ws.security.trust.elements.DelegateTo;
import com.sun.xml.ws.security.trust.elements.Encryption;
import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.elements.Issuer;
import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.xml.ws.security.trust.elements.OnBehalfOf;
import com.sun.xml.ws.security.trust.elements.ProofEncryption;
import com.sun.xml.ws.security.trust.elements.Renewing;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;
import com.sun.xml.ws.security.trust.elements.RequestedAttachedReference;
import com.sun.xml.ws.security.trust.elements.RequestedProofToken;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestedTokenCancelled;
import com.sun.xml.ws.security.trust.elements.RequestedUnattachedReference;
import com.sun.xml.ws.security.trust.elements.SignChallenge;
import com.sun.xml.ws.security.trust.elements.SignChallengeResponse;
import com.sun.xml.ws.security.trust.elements.UseKey;
import jakarta.xml.bind.JAXBElement;

import com.sun.xml.ws.api.security.trust.WSTrustException;

import com.sun.xml.ws.api.security.trust.Status;

import com.sun.xml.ws.security.trust.impl.bindings.AllowPostdatingType;
import com.sun.xml.ws.security.trust.impl.bindings.AuthenticatorType;
import com.sun.xml.ws.security.trust.impl.bindings.BinaryExchangeType;
import com.sun.xml.ws.security.trust.impl.bindings.DelegateToType;
import com.sun.xml.ws.security.trust.impl.bindings.EncryptionType;
import com.sun.xml.ws.security.trust.impl.bindings.RequestSecurityTokenResponseType;
import com.sun.xml.ws.security.trust.impl.bindings.EntropyType;
import com.sun.xml.ws.security.trust.impl.bindings.LifetimeType;
import com.sun.xml.ws.security.trust.impl.bindings.RequestedReferenceType;
import com.sun.xml.ws.security.trust.impl.bindings.RequestedProofTokenType;
import com.sun.xml.ws.security.trust.impl.bindings.RequestedSecurityTokenType;
import com.sun.xml.ws.security.trust.impl.bindings.RequestedTokenCancelledType;

import com.sun.xml.ws.security.trust.impl.bindings.ObjectFactory;
import com.sun.xml.ws.security.trust.impl.bindings.ProofEncryptionType;
import com.sun.xml.ws.security.trust.impl.bindings.RenewingType;
import com.sun.xml.ws.security.trust.impl.bindings.SignChallengeType;
import com.sun.xml.ws.security.trust.impl.bindings.StatusType;
import com.sun.xml.ws.security.trust.impl.bindings.UseKeyType;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.istack.NotNull;
import com.sun.xml.ws.security.trust.WSTrustVersion;

import com.sun.xml.ws.security.trust.impl.WSTrustVersion10;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;

/**
 * Implementation of a RequestSecurityTokenResponse.
 *
 * @author Manveen Kaur
 */
public class RequestSecurityTokenResponseImpl extends RequestSecurityTokenResponseType implements RequestSecurityTokenResponse {

    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);

    private URI tokenType = null;

    private long keySize = 0;

    private URI keyType = null;
    private URI computedKeyAlgorithm = null;
    private URI signatureAlgorithm = null;
    private URI encryptionAlgorithm = null;
    private URI canonAlgorithm = null;

    private Lifetime lifetime = null;
    private Entropy entropy = null;
    private AppliesTo appliesTo = null;
    private Authenticator authenticator = null;
    private UseKey useKey = null;
    private ProofEncryption proofEncryption = null;
    private Encryption encryption = null;
    private DelegateTo delegateTo = null;

    private OnBehalfOf obo = null;
    private RequestedSecurityToken requestedSecToken = null;
    private RequestedProofToken requestedProofToken = null;
    private RequestedAttachedReference requestedAttachedReference = null;
    private RequestedUnattachedReference requestedUnattachedReference = null;

    private URI signWith = null;
    private URI encryptWith = null;
    private URI authenticationType = null;

    private SignChallenge signChallenge = null;
    private SignChallengeResponse signChallengeRes = null;

    private boolean forwardable = true;
    private boolean delegatable = false;

    private Issuer issuer = null;
    private Renewing renewable = null;

    private BinaryExchange binaryExchange = null;
    private AllowPostdating apd = null;
    private Status status = null;

    private Policy policy = null;
    private PolicyReference policyRef = null;

    private RequestedTokenCancelled rtc = null;

    public RequestSecurityTokenResponseImpl() {
        // default empty constructor
    }

    public RequestSecurityTokenResponseImpl(URI tokenType,
            URI context,
            RequestedSecurityToken token,
            AppliesTo scopes,
            RequestedAttachedReference attached,
            RequestedUnattachedReference unattached,
            RequestedProofToken proofToken,
            Entropy entropy,
            Lifetime lifetime,
            Status status) {

        setTokenType(tokenType);
        if (context != null) { setContext(context.toString()); }
        if (token != null) { setRequestedSecurityToken(token); }
        if (attached!= null) { setRequestedAttachedReference(attached); }
        if (unattached!= null) { setRequestedUnattachedReference(unattached); }
        if (scopes != null) { setAppliesTo(scopes); }
        if (proofToken != null) { setRequestedProofToken(proofToken); }
        if (entropy != null) { setEntropy(entropy); }
        if (lifetime != null) { setLifetime(lifetime); }
        if (status != null) { setStatus(status); }
    }

    @Override
    public URI getTokenType() {
        return tokenType;
    }

    @Override
    public final void setTokenType(final URI tokenType) {
        if (tokenType != null) {
            this.tokenType = tokenType;
            final JAXBElement<String> ttElement =
                    (new ObjectFactory()).createTokenType(tokenType.toString());
            getAny().add(ttElement);
        }
    }

    @Override
    public Lifetime getLifetime() {
        return lifetime;
    }

    @Override
    public final void setLifetime(final Lifetime lifetime) {
        this.lifetime = lifetime;
        final JAXBElement<LifetimeType> ltElement =
                (new ObjectFactory()).createLifetime((LifetimeType)lifetime);
        getAny().add(ltElement);
    }

    @Override
    public RequestedTokenCancelled getRequestedTokenCancelled() {
        return this.rtc;
    }

    @Override
    public final void setRequestedTokenCancelled(final RequestedTokenCancelled rtc) {
        this.rtc = rtc;
        final JAXBElement<RequestedTokenCancelledType> rtcElement =
                (new ObjectFactory()).createRequestedTokenCancelled((RequestedTokenCancelledType)rtc);
        getAny().add(rtcElement);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public final void setStatus(final Status status) {
        this.status = status;
        final JAXBElement<StatusType> sElement =
                (new ObjectFactory()).createStatus((StatusType)status);
        getAny().add(sElement);
    }

    @Override
    public Entropy getEntropy() {
        return entropy;
    }

    @Override
    public final void setEntropy(final Entropy entropy) {
        this.entropy = entropy;
        final JAXBElement<EntropyType> etElement =
                (new ObjectFactory()).createEntropy((EntropyType)entropy);
        getAny().add(etElement);
    }

    @Override
    public final void setAppliesTo(final AppliesTo appliesTo) {
        getAny().add(appliesTo);
        this.appliesTo = appliesTo;
    }

    @Override
    public AppliesTo getAppliesTo() {
        return appliesTo;
    }

    @Override
    public final void setOnBehalfOf(final OnBehalfOf onBehalfOf) {
        obo = onBehalfOf;
    }

    @Override
    public OnBehalfOf getOnBehalfOf() {
        return obo;
    }

    @Override
    public final void setIssuer(final Issuer issuer) {
        this.issuer = issuer;
       /* JAXBElement<EndpointReferenceImpl> eprType =
                (new com.sun.xml.ws.security.trust.impl.bindings.ObjectFactory()).createIssuer((EndpointReferenceImpl)issuer);
        getAny().add(eprType);*/
    }

    @Override
    public Issuer getIssuer() {
        return issuer;
    }

    @Override
    public final void setRenewable(final Renewing renew) {
        renewable = renew;
        final JAXBElement<RenewingType> renewType =
                (new ObjectFactory()).createRenewing((RenewingType)renew);
        getAny().add(renewType);
    }

    @Override
    public Renewing getRenewable() {
        return renewable;
    }

    @Override
    public final void setSignChallenge(final SignChallenge challenge) {
        signChallenge = challenge;
        final JAXBElement<SignChallengeType> challengeType =
                (new ObjectFactory()).createSignChallenge((SignChallengeType)challenge);
        getAny().add(challengeType);
    }

    @Override
    public SignChallenge getSignChallenge() {
        return signChallenge;
    }

    @Override
    public final void setBinaryExchange(final BinaryExchange exchange) {
        binaryExchange = exchange;
        final JAXBElement<BinaryExchangeType> exchangeType =
                (new ObjectFactory()).createBinaryExchange((BinaryExchangeType)exchange);
        getAny().add(exchangeType);
    }

    @Override
    public BinaryExchange getBinaryExchange() {
        return binaryExchange;
    }

    @Override
    public final void setAuthenticationType(final URI uri) {
        this.authenticationType = uri;
        final JAXBElement<String> atElement =
                (new ObjectFactory()).createAuthenticationType(uri.toString());
        getAny().add(atElement);
    }

    @Override
    public URI getAuthenticationType() {
        return authenticationType;
    }

    @Override
    public final void setKeyType(@NotNull final URI keytype) throws WSTrustException {

        WSTrustVersion wstVer = new WSTrustVersion10();
        if (! (keytype.toString().equalsIgnoreCase(wstVer.getSymmetricKeyTypeURI())
               || keytype.toString().equalsIgnoreCase(wstVer.getPublicKeyTypeURI())
               || keytype.toString().equalsIgnoreCase(wstVer.getBearerKeyTypeURI()) )){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0025_INVALID_KEY_TYPE(keytype.toString(), null));
            throw new WSTrustException(LogStringsMessages.WST_0025_INVALID_KEY_TYPE(keytype.toString(), null));
        } else {
            this.keyType = keytype;
            final JAXBElement<String> ktElement =
                    (new ObjectFactory()).createKeyType(keyType.toString());
            getAny().add(ktElement);
        }
    }

    @Override
    public URI getKeyType() {
        return keyType;
    }

    @Override
    public final void setKeySize(@NotNull final long size) {
        keySize = size;
        final JAXBElement<Long> ksElement =  (new ObjectFactory()).createKeySize(size);
        getAny().add(ksElement);
    }

    @Override
    public long getKeySize() {
        return keySize;
    }

    @Override
    public final void setSignatureAlgorithm(@NotNull final URI algorithm) {
        signatureAlgorithm = algorithm;
        final JAXBElement<String> signElement =
                (new ObjectFactory()).createSignatureAlgorithm(algorithm.toString());
        getAny().add(signElement);
    }

    @Override
    public URI getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    @Override
    public final void setEncryptionAlgorithm(@NotNull final URI algorithm) {
        encryptionAlgorithm = algorithm;
        final JAXBElement<String> encElement =
                (new ObjectFactory()).createEncryptionAlgorithm(algorithm.toString());
        getAny().add(encElement);
    }

    @Override
    public URI getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    @Override
    public final void setCanonicalizationAlgorithm(@NotNull final URI algorithm) {
        canonAlgorithm = algorithm;
        final JAXBElement<String> canonElement =
                (new ObjectFactory()).createCanonicalizationAlgorithm(algorithm.toString());
        getAny().add(canonElement);
    }

    @Override
    public URI getCanonicalizationAlgorithm() {
        return canonAlgorithm;
    }

    @Override
    public final void setUseKey(final UseKey useKey) {
        this.useKey = useKey;
        final JAXBElement<UseKeyType> ukElement =
                (new ObjectFactory()).createUseKey((UseKeyType)useKey);
        getAny().add(ukElement);
    }

    @Override
    public UseKey getUseKey() {
        return useKey;
    }

    @Override
    public final void setProofEncryption(final ProofEncryption proofEncryption) {
        this.proofEncryption = proofEncryption;
        final JAXBElement<ProofEncryptionType> proofElement =
                (new ObjectFactory()).createProofEncryption((ProofEncryptionType)proofEncryption);
        getAny().add(proofElement);
    }

    @Override
    public ProofEncryption getProofEncryption() {
        return proofEncryption;
    }

    @Override
    public final void setComputedKeyAlgorithm(@NotNull final URI algorithm) {
        if (algorithm != null) {
            final String ckaString = algorithm.toString();
            if (!ckaString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getCKHASHalgorithmURI())
            && !ckaString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getCKPSHA1algorithmURI())) {
                throw new RuntimeException("Invalid Computed Key Algorithm specified");
            }
            computedKeyAlgorithm = algorithm;
            final JAXBElement<String> ckaElement =
                    (new ObjectFactory()).createComputedKeyAlgorithm(ckaString);
            getAny().add(ckaElement);
        }
    }

    @Override
    public URI getComputedKeyAlgorithm() {
        return computedKeyAlgorithm;
    }

    @Override
    public final void setEncryption(final Encryption enc) {
        this.encryption = enc;
        final JAXBElement<EncryptionType> encElement =
                (new ObjectFactory()).createEncryption((EncryptionType)enc);
        getAny().add(encElement);
    }

    @Override
    public Encryption getEncryption() {
        return encryption;
    }

    @Override
    public final void setSignWith(final URI algorithm) {
        signWith = algorithm;
        final JAXBElement<String> sElement =  (new ObjectFactory()).createSignWith(algorithm.toString());
        getAny().add(sElement);
    }

    @Override
    public URI getSignWith() {
        return signWith;
    }

    @Override
    public final void setEncryptWith(@NotNull final URI algorithm) {
        encryptWith = algorithm;
        final JAXBElement<String> sElement =  (new ObjectFactory()).createEncryptWith(algorithm.toString());
        getAny().add(sElement);
    }

    @Override
    public URI getEncryptWith() {
        return encryptWith;
    }

    @Override
    public void setKeyWrapAlgorithm(URI algorithm) {
        throw new UnsupportedOperationException("KeyWrapAlgorithm element in WS-Trust Standard version(1.0) is not supported");
    }

    @Override
    public URI getKeyWrapAlgorithm() {
        throw new UnsupportedOperationException("KeyWrapAlgorithm element in WS-Trust Standard version(1.0) is not supported");
    }

    @Override
    public final void setDelegateTo(final DelegateTo to) {
        this.delegateTo = to;
        final JAXBElement<DelegateToType> dtElement =
                (new ObjectFactory()).createDelegateTo((DelegateToType)to);
        getAny().add(dtElement);
    }

    @Override
    public DelegateTo getDelegateTo() {
        return delegateTo;
    }

    @Override
    public final void setForwardable(final boolean flag) {
        forwardable = flag;
        final JAXBElement<Boolean> forward =
                (new ObjectFactory()).createForwardable(flag);
        getAny().add(forward);
    }

    @Override
    public boolean getForwardable() {
        return forwardable;
    }

    @Override
    public final void setDelegatable(final boolean flag) {
        this.delegatable = flag;
        final JAXBElement<Boolean> del =
                (new ObjectFactory()).createDelegatable(flag);
        getAny().add(del);
    }

    @Override
    public boolean getDelegatable() {
        return delegatable;
    }

    @Override
    public final void setPolicy(final Policy policy) {
        this.policy = policy;
        getAny().add(policy);
    }

    @Override
    public Policy getPolicy() {
        return policy;
    }

    @Override
    public final void setPolicyReference(final PolicyReference policyRef) {
        this.policyRef = policyRef;
        getAny().add(policyRef);
    }

    @Override
    public PolicyReference getPolicyReference() {
        return policyRef;
    }

    @Override
    public AllowPostdating getAllowPostdating() {
        return apd;
    }

    @Override
    public final void setAllowPostdating(final AllowPostdating allowPostdating) {
        apd = allowPostdating;
        final JAXBElement<AllowPostdatingType> allowPd =
                (new ObjectFactory()).createAllowPostdating((AllowPostdatingType)apd);
        getAny().add(allowPd);
    }

    @Override
    public final void setSignChallengeResponse(final SignChallengeResponse challenge) {
        signChallengeRes = challenge;
        final JAXBElement<SignChallengeType> challengeType =
                (new ObjectFactory()).createSignChallengeResponse((SignChallengeType)challenge);
        getAny().add(challengeType);
    }

    @Override
    public SignChallengeResponse getSignChallengeResponse() {
        return signChallengeRes;
    }

    @Override
    public final void setAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
        final JAXBElement<AuthenticatorType> authType =
                (new ObjectFactory()).createAuthenticator((AuthenticatorType)authenticator);
        getAny().add(authType);
    }

    @Override
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    @Override
    public final void setRequestedProofToken(final RequestedProofToken proofToken) {
        requestedProofToken = proofToken;
        final JAXBElement<RequestedProofTokenType> pElement =  (new ObjectFactory()).
                createRequestedProofToken((RequestedProofTokenType)proofToken);
        getAny().add(pElement);
    }

    @Override
    public RequestedProofToken getRequestedProofToken() {
        return requestedProofToken;
    }

    @Override
    public final void setRequestedSecurityToken(final RequestedSecurityToken securityToken) {
        requestedSecToken = securityToken;
        final JAXBElement<RequestedSecurityTokenType> rstElement =  (new ObjectFactory()).
                createRequestedSecurityToken((RequestedSecurityTokenType)securityToken);

        getAny().add(rstElement);
    }

    @Override
    public RequestedSecurityToken getRequestedSecurityToken() {
        return requestedSecToken;
    }

    @Override
    public final void setRequestedAttachedReference(final RequestedAttachedReference reference) {
        requestedAttachedReference = reference;
        final JAXBElement<RequestedReferenceType> raElement =  (new ObjectFactory()).
                createRequestedAttachedReference((RequestedReferenceType)reference);
        getAny().add(raElement);
    }

    @Override
    public RequestedAttachedReference getRequestedAttachedReference() {
        return requestedAttachedReference;
    }

    @Override
    public final void setRequestedUnattachedReference(final RequestedUnattachedReference reference) {
        requestedUnattachedReference = reference;
        final JAXBElement<RequestedReferenceType> raElement =  (new ObjectFactory()).
                createRequestedUnattachedReference((RequestedReferenceType)reference);
        getAny().add(raElement);
    }

    @Override
    public RequestedUnattachedReference getRequestedUnattachedReference() {
        return requestedUnattachedReference;
    }

    public RequestSecurityTokenResponseImpl(RequestSecurityTokenResponseType rstrType)
    throws URISyntaxException,WSTrustException {

        this.context = rstrType.getContext();
        final List<Object> list = rstrType.getAny();
        for (int i = 0; i < list.size(); i++) {

            if(list.get(i) instanceof AppliesTo){
                setAppliesTo((AppliesTo)list.get(i));
                continue;
            }

            Object object = list.get(i);
            if (!(object instanceof JAXBElement)){
                getAny().add(object);
            } else {
                JAXBElement obj = (JAXBElement)list.get(i);
                final String local = obj.getName().getLocalPart();
                if (local.equalsIgnoreCase("KeySize")) {
                    setKeySize((Long)obj.getValue());
                } else if (local.equalsIgnoreCase("KeyType")){
                    setKeyType(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("ComputedKeyAlgorithm")){
                    setComputedKeyAlgorithm(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("TokenType")){
                    setTokenType(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("AuthenticationType")){
                    setAuthenticationType(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("Lifetime")){
                    final LifetimeType ltType = (LifetimeType)obj.getValue();
                    setLifetime(new LifetimeImpl(ltType));
                } else if (local.equalsIgnoreCase("Entropy")){
                    final EntropyType eType = (EntropyType)obj.getValue();
                    setEntropy(new EntropyImpl(eType));
                } else if (local.equalsIgnoreCase("Forwardable")){
                    setForwardable((Boolean)obj.getValue());
                } else if (local.equalsIgnoreCase("Delegatable")){
                    setDelegatable((Boolean)obj.getValue());
                } else if (local.equalsIgnoreCase("SignWith")){
                    setSignWith(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("EncryptWith")){
                    setEncryptWith(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("SignatureAlgorithm")){
                    setSignatureAlgorithm(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("EncryptionAlgorithm")){
                    setEncryptionAlgorithm(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("CanonicalizationAlgorithm")){
                    setCanonicalizationAlgorithm(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("AllowPostdating")){
                    setAllowPostdating(new AllowPostdatingImpl());
                } else if (local.equalsIgnoreCase("SignChallenge")){
                    setSignChallenge(new SignChallengeImpl());
                } else if (local.equalsIgnoreCase("SignChallengeResponse")){
                     setSignChallengeResponse(new SignChallengeResponseImpl());
                } else if (local.equalsIgnoreCase("BinaryExchange")){
                    final BinaryExchangeType bcType = (BinaryExchangeType)obj.getValue();
                    setBinaryExchange(new BinaryExchangeImpl(bcType));
                } else if (local.equalsIgnoreCase("Issuer")){
                    /* EndpointReferenceImpl isType = (EndpointReferenceImpl)obj.getValue();
                    setIssuer(new IssuerImpl(isType));*/
                } else if (local.equalsIgnoreCase("Authenticator")){
                    final AuthenticatorType aType = (AuthenticatorType)obj.getValue();
                    setAuthenticator(new AuthenticatorImpl(aType));
                } else if (local.equalsIgnoreCase("Renewing")){
                    setRenewable(new RenewingImpl());
                } else if (local.equalsIgnoreCase("ProofEncryption")){
                    final ProofEncryptionType peType = (ProofEncryptionType)obj.getValue();
                    setProofEncryption(new ProofEncryptionImpl(peType));
                } else if (local.equalsIgnoreCase("Policy")){
                    setPolicy((Policy)obj.getValue());
                } else if (local.equalsIgnoreCase("PolicyReference")){
                    setPolicyReference((PolicyReference)obj.getValue());
                } else if (local.equalsIgnoreCase("AppliesTo")){
                    setAppliesTo((AppliesTo)obj.getValue());
                } else if (local.equalsIgnoreCase("OnBehalfOf")){
                    this.obo = (OnBehalfOf)obj.getValue();
                } else if (local.equalsIgnoreCase("Encryption")){
                    final EncryptionType encType = (EncryptionType)obj.getValue();
                    setEncryption(new EncryptionImpl(encType));
                } else if (local.equalsIgnoreCase("UseKey")){
                    final UseKeyType ukType = (UseKeyType)obj.getValue();
                    setUseKey(new UseKeyImpl(ukType));
                } else if (local.equalsIgnoreCase("Status")){
                    final StatusType sType = (StatusType)obj.getValue();
                    setStatus(new StatusImpl(sType));
                } else if (local.equalsIgnoreCase("DelegateTo")){
                    final DelegateToType dtType  = (DelegateToType)obj.getValue();
                    setDelegateTo(new DelegateToImpl(dtType));
                } else if (local.equalsIgnoreCase("RequestedProofToken")){
                    final RequestedProofTokenType rptType = (RequestedProofTokenType)obj.getValue();
                    setRequestedProofToken(new RequestedProofTokenImpl(rptType));
                } else if (local.equalsIgnoreCase("RequestedSecurityToken")){
                    final RequestedSecurityTokenType rdstType = (RequestedSecurityTokenType)obj.getValue();
                    setRequestedSecurityToken(new RequestedSecurityTokenImpl(rdstType));
                } else if (local.equalsIgnoreCase("RequestedAttachedReference")){
                    final RequestedReferenceType rarType = (RequestedReferenceType)obj.getValue();
                    setRequestedAttachedReference(new RequestedAttachedReferenceImpl(rarType));
                } else if (local.equalsIgnoreCase("RequestedUnattachedReference")){
                    final RequestedReferenceType rarType = (RequestedReferenceType)obj.getValue();
                    setRequestedUnattachedReference(new RequestedUnattachedReferenceImpl(rarType));
                } else if (local.equalsIgnoreCase("RequestedTokenCancelled")){
                    setRequestedTokenCancelled(new RequestedTokenCancelledImpl());
                } else {
                    getAny().add(obj.getValue());
                }
            }
        }
    }

}

