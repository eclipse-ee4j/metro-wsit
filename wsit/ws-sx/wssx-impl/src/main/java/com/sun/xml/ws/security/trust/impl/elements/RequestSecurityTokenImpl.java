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
 * $Id: RequestSecurityTokenImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import java.net.URISyntaxException;
import java.util.List;

import java.net.URI;

import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.impl.bindings.PolicyReference;
import jakarta.xml.bind.JAXBElement;

import com.sun.xml.ws.security.trust.elements.*;

import com.sun.xml.ws.security.trust.impl.bindings.AllowPostdatingType;
import com.sun.xml.ws.security.trust.impl.bindings.BinaryExchangeType;
import com.sun.xml.ws.security.trust.impl.bindings.CancelTargetType;
import com.sun.xml.ws.security.trust.impl.bindings.RequestSecurityTokenType;
import com.sun.xml.ws.security.trust.impl.bindings.LifetimeType;
import com.sun.xml.ws.security.trust.impl.bindings.EntropyType;
import com.sun.xml.ws.security.trust.impl.bindings.ClaimsType;
import com.sun.xml.ws.security.trust.impl.bindings.DelegateToType;
import com.sun.xml.ws.security.trust.impl.bindings.EncryptionType;
import com.sun.xml.ws.security.trust.impl.bindings.OnBehalfOfType;
import com.sun.xml.ws.security.trust.impl.bindings.ObjectFactory;
import com.sun.xml.ws.security.trust.impl.bindings.ParticipantsType;
import com.sun.xml.ws.security.trust.impl.bindings.ProofEncryptionType;
import com.sun.xml.ws.security.trust.impl.bindings.RenewTargetType;
import com.sun.xml.ws.security.trust.impl.bindings.RenewingType;
import com.sun.xml.ws.security.trust.impl.bindings.SignChallengeType;
import com.sun.xml.ws.security.trust.impl.bindings.UseKeyType;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.istack.NotNull;
import com.sun.xml.ws.security.trust.WSTrustVersion;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import java.util.ArrayList;

/**
 * Implementation of the RequestSecurityToken interface.
 *
 * @author Manveen Kaur
 */
public class RequestSecurityTokenImpl  extends RequestSecurityTokenType
        implements RequestSecurityToken {
    
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);
    
    private Claims claims = null;
    private Participants participants = null;
    private URI tokenType = null;
    
    private URI requestType = null;
    
    private long keySize = 0;
    private URI keyType = null;
    private URI computedKeyAlgorithm = null;
    
    private URI signWith = null;
    private URI encryptWith = null;
    private URI authenticationType = null;
    private URI signatureAlgorithm = null;
    private URI encryptionAlgorithm = null;
    private URI canonAlgorithm = null;
    
    private Lifetime lifetime = null;
    private Entropy entropy = null;
    private AppliesTo appliesTo = null;
    private OnBehalfOf obo = null;
    private SignChallenge signChallenge = null;
    private Encryption encryption = null;
    private UseKey useKey = null;
    private DelegateTo delegateTo = null;
    private RenewTarget renewTarget = null;
    private CancelTarget cancelTarget = null;
    
    private AllowPostdating apd = null;
    private BinaryExchange binaryExchange = null;
    private Issuer issuer = null;
    private Renewing renewable = null;
    private ProofEncryption proofEncryption = null;
    
    private boolean forwardable = true;
    private boolean delegatable = false;
    
    private Policy policy = null;
    private PolicyReference policyRef = null;
    
    List<Object> extendedElements = new ArrayList<>();
    
    public RequestSecurityTokenImpl() {
        setRequestType(URI.create(WSTrustVersion.WS_TRUST_10.getIssueRequestTypeURI()));
    }
    
    public RequestSecurityTokenImpl(URI tokenType, URI requestType) {
        setTokenType(tokenType);
        setRequestType(requestType);
    }
    
    public RequestSecurityTokenImpl(URI tokenType, URI requestType,
            URI context, AppliesTo scopes,
            Claims claims, Entropy entropy,
            Lifetime lifetime, URI algorithm) {
        setTokenType(tokenType);
        setRequestType(requestType);
        if (context != null) {
            setContext(context.toString());
        }
        if (scopes != null) {
            setAppliesTo(scopes);
        }
        if (claims != null) {
            setClaims(claims);
        }
        if (entropy !=null) {
            setEntropy(entropy);
        }
        if (lifetime!=null) {
            setLifetime(lifetime);
        }
        if (algorithm !=null) {
            setComputedKeyAlgorithm(algorithm);
        }
    }
    
    public RequestSecurityTokenImpl(URI tokenType, URI requestType, URI context,
            RenewTarget target, AllowPostdating apd, Renewing renewingInfo) {
        setTokenType(tokenType);
        setRequestType(requestType);
        if (context != null) {
            setContext(context.toString());
        }
        if (context != null) {
            setContext(context.toString());
        }
        if (target != null) {
            setRenewTarget(target);
        }
        if (apd != null) {
            setAllowPostdating(apd);
        }
        if (renewingInfo != null) {
            setRenewable(renewingInfo);
        }
    }
    
    public RequestSecurityTokenImpl(URI tokenType, URI requestType, CancelTarget cancel) {
        setTokenType(tokenType);
        setRequestType(requestType);
        setCancelTarget(cancel);
    }
    
    @Override
    public final void setClaims(final Claims claims) {
        this.claims = claims;
        final JAXBElement<ClaimsType> cElement =
                (new ObjectFactory()).createClaims((ClaimsType)claims);
        getAny().add(cElement);
    }
    
    @Override
    public Claims getClaims() {
        return claims;
    }
    
    @Override
    public final void setCancelTarget(final CancelTarget cTarget) {
        this.cancelTarget = cTarget;
        final JAXBElement<CancelTargetType> ctElement =
                (new ObjectFactory()).createCancelTarget((CancelTargetType)cTarget);
        getAny().add(ctElement);
    }
    
    @Override
    public CancelTarget getCancelTarget() {
        return cancelTarget;
    }
    
    @Override
    public final void setRenewTarget(final RenewTarget target) {
        this.renewTarget = target;
        final JAXBElement<RenewTargetType> rElement =
                (new ObjectFactory()).createRenewTarget((RenewTargetType)target);
        getAny().add(rElement);
    }
    
    @Override
    public RenewTarget getRenewTarget() {
        return renewTarget;
    }
    
    @Override
    public final void setValidateTarget(final ValidateTarget target) {
        throw new UnsupportedOperationException("Unsupported operation: setValidateTarget");
    }
    
    @Override
    public ValidateTarget getValidateTarget() {
       throw new UnsupportedOperationException("Unsupported operation: getValidateTarget");
    }
    
    @Override
    public final void setParticipants(final Participants participants) {
        this.participants = participants;
        final JAXBElement<ParticipantsType> rElement =
                (new ObjectFactory()).createParticipants((ParticipantsType)participants);
        getAny().add(rElement);
    }
    
    @Override
    public Participants getParticipants() {
        return participants;
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
    public void setSecondaryParameters(SecondaryParameters sp){
         throw new UnsupportedOperationException("Unsupported operations!");
    }
    
    @Override
    public SecondaryParameters getSecondaryParameters(){
         throw new UnsupportedOperationException("Unsupported operations!");
    }
    
    @Override
    public URI getRequestType() {
        return requestType;
    }
    
    @Override
    public final void setRequestType(@NotNull final URI requestType) {
        
        final String rtString = requestType.toString();
        if (!rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getIssueRequestTypeURI())
        && !rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getCancelRequestTypeURI())
        && !rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getKeyExchangeRequestTypeURI())
        && !rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getRenewRequestTypeURI())
        && !rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_10.getValidateRequestTypeURI())) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0024_INVALID_REQUEST_TYPE(rtString));
            throw new RuntimeException(LogStringsMessages.WST_0024_INVALID_REQUEST_TYPE(rtString));
        }
        this.requestType = requestType;
        final JAXBElement<String> rtElement =
                (new ObjectFactory()).createRequestType(rtString);
        getAny().add(rtElement);
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
 
        final JAXBElement<OnBehalfOfType> oboElement =
                (new ObjectFactory()).createOnBehalfOf((OnBehalfOfType)onBehalfOf);
        getAny().add(oboElement);
    }
    
    @Override
    public OnBehalfOf getOnBehalfOf() {
        return obo;
    }

    @Override
    public void setActAs(ActAs actAs){
        throw new UnsupportedOperationException("Unsupported operation: setActAs");
    }
    
    @Override
    public ActAs getActAs(){
        return null;
    }
    
    @Override
    public final void setIssuer(final Issuer issuer) {
        this.issuer = issuer;
        /*JAXBElement<EndpointReferenceImpl> eprType =
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
    public final void setKeyType(@NotNull final URI keytype) {
        
       // if (! (keytype.toString().equalsIgnoreCase(RequestSecurityToken.PUBLIC_KEY_TYPE)
       // || keytype.toString().equalsIgnoreCase(RequestSecurityToken.SYMMETRIC_KEY_TYPE) )){
          //  log.log(Level.SEVERE,
                 //   LogStringsMessages.WST_0025_INVALID_KEY_TYPE(keytype.toString(), null));
          //  throw new WSTrustException(LogStringsMessages.WST_0025_INVALID_KEY_TYPE(keytype.toString(), null));
       // } else {
            this.keyType = keytype;
            final JAXBElement<String> ktElement =
                    (new ObjectFactory()).createKeyType(keyType.toString());
            getAny().add(ktElement);
       // }
    }
    
    @Override
    public URI getKeyType() {
        return keyType;
    }
    
    @Override
    public final void setKeySize(final long size) {
        keySize = size;
        final JAXBElement<Long> ksElement =  (new ObjectFactory()).createKeySize(size);
        getAny().add(ksElement);
    }
    
    @Override
    public long getKeySize() {
        return keySize;
    }
    
    @Override
    public final void setSignatureAlgorithm(final URI algorithm) {
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
    public final void setEncryptionAlgorithm(final URI algorithm) {
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
    public final void setCanonicalizationAlgorithm(final URI algorithm) {
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
                log.log(Level.SEVERE,
                        LogStringsMessages.WST_0026_INVALID_CK_ALGORITHM(ckaString));
                throw new RuntimeException(LogStringsMessages.WST_0026_INVALID_CK_ALGORITHM(ckaString));
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
    public final void setEncryptWith(final URI algorithm) {
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
    public final void setDelegateTo(final DelegateTo delegateTo) {
        this.delegateTo = delegateTo;
        final JAXBElement<DelegateToType> dtElement =
                (new ObjectFactory()).createDelegateTo((DelegateToType)delegateTo);
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
        delegatable = flag;
        final JAXBElement<Boolean> del =
                (new ObjectFactory()).createDelegatable(flag);
        getAny().add(del);
    }
    
    @Override
    public  boolean getDelegatable() {
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
    
    public RequestSecurityTokenImpl(final RequestSecurityTokenType rstType)
    throws URISyntaxException,WSTrustException {
        this.context = rstType.getContext();
        final List<Object> list = rstType.getAny();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i) instanceof AppliesTo){
                setAppliesTo((AppliesTo)list.get(i));
                continue;
            }
            if (list.get(i) instanceof JAXBElement){
                final JAXBElement obj = (JAXBElement)list.get(i);
                
                final String local = obj.getName().getLocalPart();
                if (local.equalsIgnoreCase("RequestType")) {
                    setRequestType(new URI((String)obj.getValue()));
                } else if (local.equalsIgnoreCase("KeySize")) {
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
                    setLifetime(new LifetimeImpl(ltType.getCreated(), ltType.getExpires()));
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
                }  else if (local.equalsIgnoreCase("SignChallenge")){
                    setSignChallenge(new SignChallengeImpl());
                }else if (local.equalsIgnoreCase("BinaryExchange")){
                    final BinaryExchangeType bcType = (BinaryExchangeType)obj.getValue();
                    setBinaryExchange(new BinaryExchangeImpl(bcType));
                } else if (local.equalsIgnoreCase("Issuer")){
                   // EndpointReferenceImpl isType = (EndpointReferenceImpl)obj.getValue();
                   // setIssuer(new IssuerImpl(isType));
                } else if (local.equalsIgnoreCase("Claims")){
                    final ClaimsType cType = (ClaimsType)obj.getValue();
                    setClaims(new ClaimsImpl(cType));
                } else if (local.equalsIgnoreCase("Participants")){
                    final ParticipantsType psType = (ParticipantsType)obj.getValue();
                    setParticipants(new ParticipantsImpl(psType));
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
                    final OnBehalfOfType oboType = (OnBehalfOfType)obj.getValue();
                    setOnBehalfOf(new OnBehalfOfImpl(oboType));
                } else if (local.equalsIgnoreCase("Encryption")){
                    final EncryptionType encType = (EncryptionType)obj.getValue();
                    setEncryption(new EncryptionImpl(encType));
                } else if (local.equalsIgnoreCase("UseKey")){
                    final UseKeyType ukType = (UseKeyType)obj.getValue();
                    setUseKey(new UseKeyImpl(ukType));
                } else if (local.equalsIgnoreCase("DelegateTo")){
                    final DelegateToType dtType  = (DelegateToType)obj.getValue();
                    setDelegateTo(new DelegateToImpl(dtType));
                } else if (local.equalsIgnoreCase("RenewTarget")){
                    final RenewTargetType rtType = (RenewTargetType)obj.getValue();
                    setRenewTarget(new RenewTargetImpl(rtType));
                } else if (local.equalsIgnoreCase("CancelTarget")){
                    final CancelTargetType ctType = (CancelTargetType)obj.getValue();
                    setCancelTarget(new CancelTargetImpl(ctType));
                } else if (local.equalsIgnoreCase("AppliesTo")) {
                    setAppliesTo((AppliesTo)obj.getValue());
                }else{
                    getAny().add(list.get(i));
                    extendedElements.add(list.get(i));
                }
            }else{
                getAny().add(list.get(i));
                extendedElements.add(list.get(i));
            }
        }
    }

    @Override
    public List<Object> getExtensionElements() {
        return extendedElements;
    }
}
