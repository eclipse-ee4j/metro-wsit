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
 * $Id: RequestSecurityTokenImpl.java,v 1.2 2010-10-21 15:37:05 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import java.util.List;

import java.net.URI;

import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.impl.bindings.PolicyReference;
import jakarta.xml.bind.JAXBElement;

import com.sun.xml.ws.security.trust.util.WSTrustUtil;
import com.sun.xml.ws.api.security.trust.WSTrustException;

import com.sun.xml.ws.security.trust.elements.*;
import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.WSTrustVersion;

import com.sun.xml.ws.security.trust.impl.wssx.bindings.AllowPostdatingType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.BinaryExchangeType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.CancelTargetType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestSecurityTokenType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.LifetimeType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.EntropyType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ClaimsType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.DelegateToType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.EncryptionType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.OnBehalfOfType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ObjectFactory;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ParticipantsType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ProofEncryptionType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RenewTargetType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RenewingType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.SecondaryParametersType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.SignChallengeType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.UseKeyType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ValidateTargetType;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of the RequestSecurityToken interface.
 *
 * @author Manveen Kaur
 */
public class RequestSecurityTokenImpl  extends RequestSecurityTokenType
        implements RequestSecurityToken {
    
    private Claims claims = null;
    private Participants participants = null;
    private URI tokenType = null;
    
    private URI requestType = null;
    
    private long keySize = 0;
    private URI keyType = null;
    private URI computedKeyAlgorithm = null;
    
    private URI signWith = null;
    private URI encryptWith = null;
    private URI keyWrapAlgorithm = null;
    private URI authenticationType = null;
    private URI signatureAlgorithm = null;
    private URI encryptionAlgorithm = null;
    private URI canonAlgorithm = null;
    
    private Lifetime lifetime = null;
    private Entropy entropy = null;
    private AppliesTo appliesTo = null;
    private OnBehalfOf obo = null;
    private ActAs actAs = null;
    private SignChallenge signChallenge = null;
    private Encryption encryption = null;
    private UseKey useKey = null;
    private DelegateTo delegateTo = null;
    private RenewTarget renewTarget = null;
    private CancelTarget cancelTarget = null;
    private ValidateTarget validateTarget = null;
    
    private AllowPostdating apd = null;
    private BinaryExchange binaryExchange = null;
    private Issuer issuer = null;
    private Renewing renewable = null;
    private ProofEncryption proofEncryption = null;
    
    private boolean forwardable = true;
    private boolean delegatable = false;
    
    private Policy policy = null;
    private PolicyReference policyRef = null;
    
    private SecondaryParameters sp = null;
    private List<Object> extendedElements = new ArrayList<>();
    
    public RequestSecurityTokenImpl() {
        setRequestType(URI.create(WSTrustVersion.WS_TRUST_13.getIssueRequestTypeURI()));
    }
    
    public RequestSecurityTokenImpl(URI tokenType, URI requestType) {
        setTokenType(tokenType);
        setRequestType(requestType);
    }
    
    public RequestSecurityTokenImpl(URI tokenType, URI requestType,
            URI context, AppliesTo scopes,
            Claims claims, Entropy entropy,
            Lifetime lt, URI algorithm) {
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
        if (entropy !=null)
            setEntropy(entropy);
        if (lt!=null)
            setLifetime(lt);
        if (algorithm !=null)
            setComputedKeyAlgorithm(algorithm);
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
    public String getContext() {
        return super.getContext();
    }
    
    @Override
    public void setClaims(Claims claims) {
        this.claims = claims;
        JAXBElement<ClaimsType> cElement =
                (new ObjectFactory()).createClaims((ClaimsType)claims);
        getAny().add(cElement);
    }
    
    @Override
    public Claims getClaims() {
        return claims;
    }
    
    @Override
    public void setCancelTarget(CancelTarget cTarget) {
        this.cancelTarget = cTarget;
        JAXBElement<CancelTargetType> ctElement =
                (new ObjectFactory()).createCancelTarget((CancelTargetType)cTarget);
        getAny().add(ctElement);
    }
    
    @Override
    public CancelTarget getCancelTarget() {
        return cancelTarget;
    }
    
    @Override
    public void setRenewTarget(RenewTarget target) {
        this.renewTarget = target;
        JAXBElement<RenewTargetType> rElement =
                (new ObjectFactory()).createRenewTarget((RenewTargetType)target);
        getAny().add(rElement);
    }
    
    @Override
    public RenewTarget getRenewTarget() {
        return renewTarget;
    }
    
     @Override
     public final void setValidateTarget(final ValidateTarget target) {
        this.validateTarget = target;
        JAXBElement<ValidateTargetType> vtElement =
                (new ObjectFactory()).createValidateTarget((ValidateTargetType)target);
        getAny().add(vtElement);
    }
    
    @Override
    public ValidateTarget getValidateTarget() {
       return validateTarget;
    }
    
    @Override
    public void setParticipants(Participants participants) {
        this.participants = participants;
        JAXBElement<ParticipantsType> rElement =
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
    public void setTokenType(URI tokenType) {
        if (tokenType != null) {
            this.tokenType = tokenType;
            JAXBElement<String> ttElement =
                    (new ObjectFactory()).createTokenType(tokenType.toString());
            getAny().add(ttElement);
        }
    }
    
    @Override
    public URI getRequestType() {
        return requestType;
    }
    
    @Override
    public void setRequestType(URI requestType) {
        if (requestType == null) {
            throw new RuntimeException("RequestType cannot be null");
        }
        String rtString = requestType.toString();
        if (!rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getIssueRequestTypeURI())
        && !rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getCancelRequestTypeURI())
        && !rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getKeyExchangeRequestTypeURI())
        && !rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getRenewRequestTypeURI())
        && !rtString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getValidateRequestTypeURI())) {
            throw new RuntimeException("Invalid Request Type specified");
        }
        this.requestType = requestType;
        JAXBElement<String> rtElement =
                (new ObjectFactory()).createRequestType(rtString);
        getAny().add(rtElement);
    }
    
    @Override
    public Lifetime getLifetime() {
        return lifetime;
    }
    
    @Override
    public void setLifetime(Lifetime lifetime) {
        this.lifetime = lifetime;
        JAXBElement<LifetimeType> ltElement =
                (new ObjectFactory()).createLifetime((LifetimeType)lifetime);
        getAny().add(ltElement);
    }
    
     @Override
     public SecondaryParameters getSecondaryParameters() {
        return sp;
    }
    
    @Override
    public void setSecondaryParameters(SecondaryParameters sp) {
        this.sp = sp;
        JAXBElement<SecondaryParametersType> spElement =
                (new ObjectFactory()).createSecondaryParameters((SecondaryParametersType)sp);
        getAny().add(spElement);
    }
    
    @Override
    public Entropy getEntropy() {
        return entropy;
    }
    
    @Override
    public void setEntropy(Entropy entropy) {
        this.entropy = entropy;
        JAXBElement<EntropyType> etElement =
                (new ObjectFactory()).createEntropy((EntropyType)entropy);
        getAny().add(etElement);
    }
    
    @Override
    public void setAppliesTo(AppliesTo appliesTo) {
        getAny().add(appliesTo);
        this.appliesTo = appliesTo;
    }
    
    @Override
    public AppliesTo getAppliesTo() {
        return appliesTo;
    }
    
    @Override
    public void setOnBehalfOf(OnBehalfOf onBehalfOf) {
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
    public void setActAs(ActAs actAs) {
        this.actAs = actAs;

        //Create ActAs element
        Document doc = WSTrustUtil.newDocument();
        Element actAsElement = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200802", "wst14:ActAs");
        actAsElement.setAttribute("xmlns:wst14", "http://docs.oasis-open.org/ws-sx/ws-trust/200802");
        doc.appendChild(actAsElement);
        actAsElement.appendChild(doc.importNode(WSTrustElementFactory.newInstance(WSTrustVersion.WS_TRUST_13).toElement(actAs.getAny()), true));
        getAny().add(actAsElement);
    }

    @Override
    public ActAs getActAs() {
        return this.actAs;
    }
    
    @Override
    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
//        JAXBElement<EndpointReferenceImpl> eprType =
//                (new com.sun.xml.ws.security.trust.impl.wssx.bindings.ObjectFactory()).createIssuer((EndpointReferenceImpl)issuer);
//        getAny().add(eprType);
    }
    
    @Override
    public Issuer getIssuer() {
        return issuer;
    }
    
    @Override
    public void setRenewable(Renewing renew) {
        renewable = renew;
        JAXBElement<RenewingType> renewType =
                (new ObjectFactory()).createRenewing((RenewingType)renew);
        getAny().add(renewType);
    }
    
    @Override
    public Renewing getRenewable() {
        return renewable;
    }
    
    @Override
    public void setSignChallenge(SignChallenge challenge) {
        signChallenge = challenge;
        JAXBElement<SignChallengeType> challengeType =
                (new ObjectFactory()).createSignChallenge((SignChallengeType)challenge);
        getAny().add(challengeType);
    }
    
    @Override
    public SignChallenge getSignChallenge() {
        return signChallenge;
    }
    
    @Override
    public void setBinaryExchange(BinaryExchange exchange) {
        binaryExchange = exchange;
        JAXBElement<BinaryExchangeType> exchangeType =
                (new ObjectFactory()).createBinaryExchange((BinaryExchangeType)exchange);
        getAny().add(exchangeType);
    }
    
    @Override
    public BinaryExchange getBinaryExchange() {
        return binaryExchange;
    }
    
    @Override
    public void setAuthenticationType(URI uri) {
        this.authenticationType = uri;
        JAXBElement<String> atElement =
                (new ObjectFactory()).createAuthenticationType(uri.toString());
        getAny().add(atElement);
    }
    
    @Override
    public URI getAuthenticationType() {
        return authenticationType;
    }
    
    @Override
    public void setKeyType(URI keytype) {
        
        //if (keytype == null || ! (keytype.toString().equalsIgnoreCase(RequestSecurityToken.PUBLIC_KEY_TYPE)
       // || keytype.toString().equalsIgnoreCase(RequestSecurityToken.SYMMETRIC_KEY_TYPE) )){
          //  throw new WSTrustException("Invalid KeyType");
       // }
       // else {
            this.keyType = keytype;
            JAXBElement<String> ktElement =
                    (new ObjectFactory()).createKeyType(keyType.toString());
            getAny().add(ktElement);
       // }
    }
    
    @Override
    public URI getKeyType() {
        return keyType;
    }
    
    @Override
    public void setKeySize(long size) {
        keySize = size;
        JAXBElement<Long> ksElement =  (new ObjectFactory()).createKeySize(size);
        getAny().add(ksElement);
    }
    
    @Override
    public long getKeySize() {
        return keySize;
    }
    
    @Override
    public void setSignatureAlgorithm(URI algorithm) {
        signatureAlgorithm = algorithm;
        JAXBElement<String> signElement =
                (new ObjectFactory()).createSignatureAlgorithm(algorithm.toString());
        getAny().add(signElement);
    }
    
    @Override
    public URI getSignatureAlgorithm() {
        return signatureAlgorithm;
    }
    
    @Override
    public void setEncryptionAlgorithm(URI algorithm) {
        encryptionAlgorithm = algorithm;
        JAXBElement<String> encElement =
                (new ObjectFactory()).createEncryptionAlgorithm(algorithm.toString());
        getAny().add(encElement);
    }
    
    @Override
    public URI getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }
    
    @Override
    public void setCanonicalizationAlgorithm(URI algorithm) {
        canonAlgorithm = algorithm;
        JAXBElement<String> canonElement =
                (new ObjectFactory()).createCanonicalizationAlgorithm(algorithm.toString());
        getAny().add(canonElement);
    }
    
    @Override
    public URI getCanonicalizationAlgorithm() {
        return canonAlgorithm;
    }
    
    @Override
    public void setUseKey(UseKey useKey) {
        this.useKey = useKey;
        JAXBElement<UseKeyType> ukElement =
                (new ObjectFactory()).createUseKey((UseKeyType)useKey);
        getAny().add(ukElement);
    }
    
    @Override
    public UseKey getUseKey() {
        return useKey;
    }
    
    @Override
    public void setProofEncryption(ProofEncryption proofEncryption) {
        this.proofEncryption = proofEncryption;
        JAXBElement<ProofEncryptionType> proofElement =
                (new ObjectFactory()).createProofEncryption((ProofEncryptionType)proofEncryption);
        getAny().add(proofElement);
    }
    
    @Override
    public ProofEncryption getProofEncryption() {
        return proofEncryption;
    }
    
    @Override
    public void setComputedKeyAlgorithm(URI algorithm) {
        if (algorithm != null) {
            String ckaString = algorithm.toString();
            if (!ckaString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getCKHASHalgorithmURI())
            && !ckaString.equalsIgnoreCase(WSTrustVersion.WS_TRUST_13.getCKPSHA1algorithmURI())) {
                throw new RuntimeException("Invalid Computed Key Algorithm specified");
            }
            computedKeyAlgorithm = algorithm;
            JAXBElement<String> ckaElement =
                    (new ObjectFactory()).createComputedKeyAlgorithm(ckaString);
            getAny().add(ckaElement);
        }
    }
    
    @Override
    public URI getComputedKeyAlgorithm() {
        return computedKeyAlgorithm;
    }
    
    @Override
    public void setEncryption(Encryption enc) {
        this.encryption = enc;
        JAXBElement<EncryptionType> encElement =
                (new ObjectFactory()).createEncryption((EncryptionType)enc);
        getAny().add(encElement);
    }
    
    @Override
    public Encryption getEncryption() {
        return encryption;
    }
    
    @Override
    public void setSignWith(URI algorithm) {
        signWith = algorithm;
        JAXBElement<String> sElement =  (new ObjectFactory()).createSignWith(algorithm.toString());
        getAny().add(sElement);
    }
    
    @Override
    public URI getSignWith() {
        return signWith;
    }
    
    @Override
    public void setEncryptWith(URI algorithm) {
        encryptWith = algorithm;
        JAXBElement<String> sElement =  (new ObjectFactory()).createEncryptWith(algorithm.toString());
        getAny().add(sElement);
    }
    
    @Override
    public URI getEncryptWith() {
        return encryptWith;
    }
    
    @Override
    public void setKeyWrapAlgorithm(URI algorithm) {
        keyWrapAlgorithm = algorithm;
        JAXBElement<String> keyWrapElement =
                (new ObjectFactory()).createKeyWrapAlgorithm(algorithm.toString());
        getAny().add(keyWrapElement);
    }
    
    @Override
    public URI getKeyWrapAlgorithm() {
        return keyWrapAlgorithm;
    }
    
    @Override
    public void setDelegateTo(DelegateTo to) {
        this.delegateTo = to;
        JAXBElement<DelegateToType> dtElement =
                (new ObjectFactory()).createDelegateTo((DelegateToType)to);
        getAny().add(dtElement);
    }
    
    @Override
    public DelegateTo getDelegateTo() {
        return delegateTo;
    }
    
    @Override
    public void setForwardable(boolean flag) {
        forwardable = flag;
        JAXBElement<Boolean> forward =
                (new ObjectFactory()).createForwardable(flag);
        getAny().add(forward);
    }
    
    @Override
    public boolean getForwardable() {
        return forwardable;
    }
    
    @Override
    public void setDelegatable(boolean flag) {
        delegatable = flag;
        JAXBElement<Boolean> del =
                (new ObjectFactory()).createDelegatable(flag);
        getAny().add(del);
    }
    
    @Override
    public  boolean getDelegatable() {
        return delegatable;
    }
    
    @Override
    public void setPolicy(Policy policy) {
        this.policy = policy;
        getAny().add(policy);
    }
    
    @Override
    public Policy getPolicy() {
        return policy;
    }
    
    @Override
    public void setPolicyReference(PolicyReference policyRef) {
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
    public void setAllowPostdating(AllowPostdating allowPostdating) {
        apd = allowPostdating;
        JAXBElement<AllowPostdatingType> allowPd =
                (new ObjectFactory()).createAllowPostdating((AllowPostdatingType)apd);
        getAny().add(allowPd);
    }
    
    public RequestSecurityTokenImpl(RequestSecurityTokenType rstType)
    throws Exception {
        this.context = rstType.getContext();
        List<Object> list = rstType.getAny();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i) instanceof AppliesTo){
                setAppliesTo((AppliesTo)list.get(i));
                continue;
            }
            if (list.get(i) instanceof JAXBElement){
                JAXBElement obj = (JAXBElement)list.get(i);

                String local = obj.getName().getLocalPart();
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
                    LifetimeType ltType = (LifetimeType)obj.getValue();
                    setLifetime(new LifetimeImpl(ltType.getCreated(), ltType.getExpires()));
                } else if (local.equalsIgnoreCase("Entropy")){
                    EntropyType eType = (EntropyType)obj.getValue();
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
                    BinaryExchangeType bcType = (BinaryExchangeType)obj.getValue();
                    setBinaryExchange(new BinaryExchangeImpl(bcType));
                } else if (local.equalsIgnoreCase("Issuer")){
//                    EndpointReferenceImpl isType = (EndpointReferenceImpl)obj.getValue();
//                    setIssuer(new IssuerImpl(isType));
                } else if (local.equalsIgnoreCase("Claims")){
                    ClaimsType cType = (ClaimsType)obj.getValue();
                    setClaims(new ClaimsImpl(cType));
                } else if (local.equalsIgnoreCase("Participants")){
                    ParticipantsType psType = (ParticipantsType)obj.getValue();
                    setParticipants(new ParticipantsImpl(psType));
                } else if (local.equalsIgnoreCase("Renewing")){
                    setRenewable(new RenewingImpl());
                } else if (local.equalsIgnoreCase("ProofEncryption")){
                    ProofEncryptionType peType = (ProofEncryptionType)obj.getValue();
                    setProofEncryption(new ProofEncryptionImpl(peType));
                } else if (local.equalsIgnoreCase("Policy")){
                    setPolicy((Policy)obj.getValue());
                } else if (local.equalsIgnoreCase("PolicyReference")){
                    setPolicyReference((PolicyReference)obj.getValue());
                } else if (local.equalsIgnoreCase("AppliesTo")){
                    setAppliesTo((AppliesTo)obj.getValue());
                } else if (local.equalsIgnoreCase("OnBehalfOf")){
                    OnBehalfOfType oboType = (OnBehalfOfType)obj.getValue();
                    setOnBehalfOf(new OnBehalfOfImpl(oboType));
                } else if (local.equalsIgnoreCase("Encryption")){
                    EncryptionType encType = (EncryptionType)obj.getValue();
                    setEncryption(new EncryptionImpl(encType));
                } else if (local.equalsIgnoreCase("UseKey")){
                    UseKeyType ukType = (UseKeyType)obj.getValue();
                    setUseKey(new UseKeyImpl(ukType));
                } else if (local.equalsIgnoreCase("DelegateTo")){
                    DelegateToType dtType  = (DelegateToType)obj.getValue();
                    setDelegateTo(new DelegateToImpl(dtType));
                } else if (local.equalsIgnoreCase("RenewTarget")){
                    RenewTargetType rtType = (RenewTargetType)obj.getValue();
                    setRenewTarget(new RenewTargetImpl(rtType));
                } else if (local.equalsIgnoreCase("CancelTarget")){
                    CancelTargetType ctType = (CancelTargetType)obj.getValue();
                    setCancelTarget(new CancelTargetImpl(ctType));
                } else if (local.equalsIgnoreCase("ValidateTarget")){
                    ValidateTargetType vtType = (ValidateTargetType)obj.getValue();
                    setValidateTarget(new ValidateTargetImpl(vtType));
                } else if (local.equalsIgnoreCase("AppliesTo")) {
                    setAppliesTo((AppliesTo)obj.getValue());
                } else if (local.equalsIgnoreCase("SecondaryParameters")){
                    setSecondaryParameters(new SecondaryParametersImpl((SecondaryParametersType)obj.getValue()));
                }else{
                    getAny().add(list.get(i));
                    extendedElements.add(list.get(i));
                }
            }else{
                Object obj = list.get(i);
                if ((obj instanceof Element) && (((Element)obj).getLocalName().equals("ActAs"))){
                    setActAs(new ActAsImpl((Element)obj));
                }else{
                    getAny().add(list.get(i));
                    extendedElements.add(list.get(i));
                }
            }
        }
    }

    @Override
    public List<Object> getExtensionElements() {
        return extendedElements;
    }
}
