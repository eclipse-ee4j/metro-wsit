/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl.wssx;

import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;

import com.sun.xml.ws.security.trust.elements.AllowPostdating;
import com.sun.xml.ws.security.trust.elements.ActAs;
import com.sun.xml.ws.security.trust.elements.BinarySecret;
import com.sun.xml.ws.security.trust.elements.BaseSTSRequest;
import com.sun.xml.ws.security.trust.elements.BaseSTSResponse;
import com.sun.xml.ws.security.trust.elements.CancelTarget;
import com.sun.xml.ws.api.security.trust.Claims;
import com.sun.xml.ws.security.trust.elements.Entropy;
import com.sun.xml.ws.security.trust.elements.IssuedTokens;
import com.sun.xml.ws.security.trust.elements.Lifetime;
import com.sun.xml.ws.security.trust.elements.OnBehalfOf;
import com.sun.xml.ws.security.trust.elements.RenewTarget;
import com.sun.xml.ws.security.trust.elements.Renewing;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponseCollection;
import com.sun.xml.ws.security.trust.elements.RequestedProofToken;
import com.sun.xml.ws.security.trust.elements.RequestedAttachedReference;
import com.sun.xml.ws.security.trust.elements.RequestedUnattachedReference;
import com.sun.xml.ws.security.trust.elements.RequestSecurityToken;
import com.sun.xml.ws.security.trust.elements.RequestedSecurityToken;
import com.sun.xml.ws.security.trust.elements.SecondaryParameters;
import com.sun.xml.ws.api.security.trust.Status;
import com.sun.xml.ws.security.trust.elements.UseKey;
import com.sun.xml.ws.security.trust.elements.ValidateTarget;

import com.sun.xml.ws.security.trust.impl.elements.str.DirectReferenceImpl;
import com.sun.xml.ws.security.trust.impl.elements.str.SecurityTokenReferenceImpl;
import com.sun.xml.ws.security.trust.impl.elements.str.KeyIdentifierImpl;

import com.sun.xml.ws.security.trust.impl.wssx.elements.ActAsImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.BinarySecretImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.CancelTargetImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.ClaimsImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.EntropyImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.IssuedTokensImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.LifetimeImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.OnBehalfOfImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestSecurityTokenResponseImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestSecurityTokenResponseCollectionImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestedProofTokenImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestedAttachedReferenceImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestedUnattachedReferenceImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestSecurityTokenImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestedSecurityTokenImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RequestedTokenCancelledImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.SecondaryParametersImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.StatusImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.UseKeyImpl;
import com.sun.xml.ws.security.trust.impl.wssx.elements.ValidateTargetImpl;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.BinarySecretType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.EntropyType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestSecurityTokenType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestSecurityTokenResponseType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.RequestSecurityTokenResponseCollectionType;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.ObjectFactory;
import com.sun.xml.ws.policy.impl.bindings.AppliesTo;

import com.sun.xml.ws.security.trust.elements.str.DirectReference;
import com.sun.xml.ws.security.trust.elements.str.KeyIdentifier;
import com.sun.xml.ws.security.EncryptedKey;
import com.sun.xml.ws.security.trust.elements.str.Reference;
import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.SecurityContextToken;
import com.sun.xml.ws.security.wsu10.AttributedDateTime;

import java.net.URI;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import jakarta.xml.bind.JAXBElement;

import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.util.WSTrustUtil;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.impl.wssx.elements.RenewTargetImpl;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;
import java.util.logging.Level;

import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.JAXBException;

import jakarta.xml.bind.PropertyException;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import com.sun.xml.wss.impl.MessageConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class WSTrustElementFactoryImpl extends WSTrustElementFactory {
     private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);
     
    public WSTrustElementFactoryImpl(){
        
    }
    
    /**
     * Create an RST for Issue from the given arguments
     * Any of the arguments can be null since they are all optional, but one of tokenType and AppliesTo must be present
     */
    public  RequestSecurityToken createRSTForIssue(URI tokenType, URI requestType, URI context, AppliesTo scopes,
            Claims claims, Entropy entropy, Lifetime lt) throws WSTrustException {
       // if (tokenType==null || scopes==null)
         //   throw new WSTrustException("TokenType and AppliesTo cannot be both null");
        RequestSecurityToken rst = new RequestSecurityTokenImpl(tokenType, requestType, context, scopes, claims, entropy, lt, null);
        return rst;
    }
    
    /**
     * Create an RSTR for Issue from the given arguments. TokenType should be Issue.
     * Any of the arguments can be null since they are all optional, but one of RequestedSecurityToken or RequestedProofToken should be returned
     */
    public  RequestSecurityTokenResponse createRSTRForIssue(URI tokenType, URI context, RequestedSecurityToken token, AppliesTo scopes, RequestedAttachedReference attachedReference, RequestedUnattachedReference unattachedReference, RequestedProofToken proofToken, Entropy entropy, Lifetime lt) throws WSTrustException {
        return new RequestSecurityTokenResponseImpl(tokenType, context, token, scopes,
                attachedReference, unattachedReference, proofToken, entropy, lt, null);
    }
    
    /**
     * Create  a collection of RequestSecurityTokenResponse(s)
     */
    public  RequestSecurityTokenResponseCollection createRSTRCollectionForIssue(URI tokenType, URI context, RequestedSecurityToken token, AppliesTo scopes, RequestedAttachedReference attached, RequestedUnattachedReference unattached, RequestedProofToken proofToken, Entropy entropy, Lifetime lt) throws WSTrustException {
        return new RequestSecurityTokenResponseCollectionImpl(tokenType, context, token, scopes, attached, unattached, proofToken, entropy, lt);
    }
    
    public RequestSecurityTokenResponseCollection createRSTRCollectionForIssue(List rstrs) throws WSTrustException {
         //RequestSecurityTokenResponseCollection rstrc = new RequestSecurityTokenResponseCollectionImpl();
        RequestSecurityTokenResponseCollectionImpl rstrc = new RequestSecurityTokenResponseCollectionImpl();
         for (int i = 0; i < rstrs.size(); i++) {
            rstrc.addRequestSecurityTokenResponse((RequestSecurityTokenResponse)rstrs.get(i));
        }
         return rstrc;
     }
    
     /**
     * Create an RSTR for Renew from the given arguments. TokenType should be Issue.
     * Any of the arguments can be null since they are all optional, but one of RequestedSecurityToken or RequestedProofToken should be returned
     */
    public  RequestSecurityTokenResponse createRSTRForRenew(URI tokenType, final URI context, RequestedSecurityToken token, final RequestedAttachedReference attachedReference, final RequestedUnattachedReference unattachedRef, final RequestedProofToken proofToken, final Entropy entropy, final Lifetime lifetime) throws WSTrustException {
        final RequestSecurityTokenResponse rstr =
                new RequestSecurityTokenResponseImpl(tokenType, context, token, null, attachedReference, unattachedRef, proofToken, entropy, lifetime, null);
        return rstr;
    }
    
    /**
     * Create a wst:IssuedTokens object
     */
    public  IssuedTokens createIssuedTokens(RequestSecurityTokenResponseCollection issuedTokens) {
        return new IssuedTokensImpl(issuedTokens);
    }
    
    /**
     * Create an Entropy with a BinarySecret
     */
    public Entropy createEntropy(BinarySecret secret) {
        return new EntropyImpl(secret);
    }
    
    /**
     * Create an Entropy with an xenc:EncryptedKey
     */
    public  Entropy createEntropy(EncryptedKey key) {
        return new EntropyImpl(key);
    }
    
    public BinarySecret createBinarySecret(byte[] rawValue, String type) {
        return new BinarySecretImpl(rawValue, type);
    }

    public BinarySecret createBinarySecret(Element elem) throws WSTrustException {
        return new BinarySecretImpl(BinarySecretImpl.fromElement(elem));
    }
    
    public Claims createClaims(Element elem)throws WSTrustException {
        return new ClaimsImpl(ClaimsImpl.fromElement(elem));
    }

     public Claims createClaims(Claims claims) throws WSTrustException {
        ClaimsImpl newClaims = new ClaimsImpl();
        if (claims != null){
            newClaims.setDialect(claims.getDialect());
            newClaims.getAny().addAll(claims.getAny());
            newClaims.getOtherAttributes().putAll(claims.getOtherAttributes());
        }

        return newClaims;
    }

    public Claims createClaims() throws WSTrustException {
        return new ClaimsImpl();
    }
    
    public Status createStatus(String code, String reason){
        return new StatusImpl(code, reason);
    }
    
    
    /**
     * Create a Lifetime.
     */
    public Lifetime createLifetime(AttributedDateTime created,  AttributedDateTime expires) {
        return new LifetimeImpl(created, expires);
    }
    
    public OnBehalfOf createOnBehalfOf(Token oboToken){
         return new OnBehalfOfImpl(oboToken);
    }

    public ActAs createActAs(Token actAsToken){
        return new ActAsImpl(actAsToken);
    }
    
    /**
     * Create a RequestedSecurityToken.
     */
    public RequestedSecurityToken createRequestedSecurityToken(Token token) {
        return new RequestedSecurityTokenImpl(token);
    }
    
     /**
     * Create a RequestedSecurityToken.
     */
    public RequestedSecurityToken createRequestedSecurityToken() {
        return new RequestedSecurityTokenImpl();
    }
    
    public DirectReference createDirectReference(String valueType, String uri){
        return new DirectReferenceImpl(valueType, uri);
    }
   
   public KeyIdentifier createKeyIdentifier(String valueType, String encodingType){
       return new KeyIdentifierImpl(valueType, encodingType);
   }
   
    public SecurityTokenReference createSecurityTokenReference(Reference ref){
        return new SecurityTokenReferenceImpl(ref);
    }
    /**
     * Create a RequestedAttachedReference.
     */
    public RequestedAttachedReference createRequestedAttachedReference(SecurityTokenReference str) {
        return new RequestedAttachedReferenceImpl(str);
    }
    
    /**
     * Create a RequestedUnattachedReference.
     */
    public RequestedUnattachedReference createRequestedUnattachedReference(SecurityTokenReference str) {
        return new RequestedUnattachedReferenceImpl(str);
    }
    
    /**
     * Create a RequestedProofToken.
     */
    public RequestedProofToken createRequestedProofToken() {
        return new RequestedProofTokenImpl();
    }
    
    
    /**
     *Create an RST for a Renewal Request
     */
    public  RequestSecurityToken createRSTForRenew(URI tokenType, URI requestType, URI context, RenewTarget target, AllowPostdating apd, Renewing renewingInfo) {
        return new RequestSecurityTokenImpl(tokenType, requestType, context, target, apd, renewingInfo);
    }
    
    public RenewTarget createRenewTarget(final SecurityTokenReference str){
        return new RenewTargetImpl(str);
    }
    
    public CancelTarget createCancelTarget(SecurityTokenReference str){
        return new CancelTargetImpl(str);
    }
    
    public ValidateTarget createValidateTarget(Token token){
         return new ValidateTargetImpl(token);
    }
    
    public SecondaryParameters createSecondaryParameters(){
        return new SecondaryParametersImpl();
    }
    
    public UseKey createUseKey(Token token, String sig){
        UseKey useKey = new UseKeyImpl(token);
        if (sig != null){
            useKey.setSignatureID(URI.create(sig));
        }
        
        return useKey;
    }
    
    /**
     *Create an RST for Token Cancellation
     */
    public  RequestSecurityToken createRSTForCancel(URI requestType, CancelTarget target) {
        return new RequestSecurityTokenImpl(null, requestType, target);
    }
    
    /**
     *Create an RSTR for a Successful Token Cancellation
     */
    public  RequestSecurityTokenResponse createRSTRForCancel() {
        RequestSecurityTokenResponse rstr =  new RequestSecurityTokenResponseImpl();
        rstr.setRequestedTokenCancelled(new RequestedTokenCancelledImpl());
        
        return rstr;
    }
    
    /**
     *Create an RST for Token Validation
     *<p>
     *TODO: Not clear from Spec whether the Token to be validated is ever sent ?
     *TODO: There is a mention of special case where a SOAPEnvelope may be specified as
     * a security token if the requestor desires the envelope to be validated.
     *</p>
     */
    public  RequestSecurityToken createRSTForValidate(URI tokenType, URI requestType) {
        return new RequestSecurityTokenImpl(tokenType, requestType);
    }
    
    /**
     * create an RSTR for validate request.
     */
    public  RequestSecurityTokenResponse createRSTRForValidate(URI tokenType, RequestedSecurityToken token, Status status) {
        return new RequestSecurityTokenResponseImpl(tokenType, null, token, null, null, null, null, null, null, status);
    }
    
    /**
     * Create an Empty RST
     */
    public RequestSecurityToken createRST() {
        return new RequestSecurityTokenImpl();
    }
    
    /**
     * Create an Empty RSTR
     */
    public RequestSecurityTokenResponse createRSTR() {
        return new RequestSecurityTokenResponseImpl();
    }

    public RequestSecurityTokenResponseCollection createRSTRC(List<RequestSecurityTokenResponse> rstrs){
        RequestSecurityTokenResponseCollection rstrc = new RequestSecurityTokenResponseCollectionImpl();
        //rstrc.getRequestSecurityTokenResponses().addAll(rstrs);        
        
        for (int i = 0; i < rstrs.size(); i++) {
            ((RequestSecurityTokenResponseCollectionImpl)rstrc).addRequestSecurityTokenResponse(rstrs.get(i));
        }
        return rstrc;
    }
    
    
    /**
     * create an RST from a Source
     */
    public RequestSecurityToken createRSTFrom(Source src) {
        try {           
            jakarta.xml.bind.Unmarshaller u = getContext(WSTrustVersion.WS_TRUST_13).createUnmarshaller();
            JAXBElement<RequestSecurityTokenType> rstType = u.unmarshal(src, RequestSecurityTokenType.class);
            RequestSecurityTokenType type = rstType.getValue();
            return new RequestSecurityTokenImpl(type);
        } catch ( Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /**
     * create an RST from DOM Element
     */
    public  RequestSecurityToken createRSTFrom(Element elem) {
        try {
            jakarta.xml.bind.Unmarshaller u = getContext(WSTrustVersion.WS_TRUST_13).createUnmarshaller();
            JAXBElement<RequestSecurityTokenType> rstType = u.unmarshal(elem, RequestSecurityTokenType.class);
            RequestSecurityTokenType type = rstType.getValue();
            return new RequestSecurityTokenImpl(type);
        } catch ( Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /**
     * create an RSTR from a Source
     */
    public  RequestSecurityTokenResponse createRSTRFrom(Source src) {
        try {
            jakarta.xml.bind.Unmarshaller u = getContext(WSTrustVersion.WS_TRUST_13).createUnmarshaller();
            JAXBElement<RequestSecurityTokenResponseType> rstType = u.unmarshal(src, RequestSecurityTokenResponseType.class);
            RequestSecurityTokenResponseType type = rstType.getValue();
            return new RequestSecurityTokenResponseImpl(type);
        } catch ( Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /**
     * create an RSTR from DOM Element
     */
    public  RequestSecurityTokenResponse createRSTRFrom(Element elem) {
        try {
            jakarta.xml.bind.Unmarshaller u = getContext(WSTrustVersion.WS_TRUST_13).createUnmarshaller();
            JAXBElement<RequestSecurityTokenResponseType> rstType = u.unmarshal(elem, RequestSecurityTokenResponseType.class);
            RequestSecurityTokenResponseType type = rstType.getValue();
            return new RequestSecurityTokenResponseImpl(type);
        } catch ( Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Create RSTR Collection from Source
     */
    public  RequestSecurityTokenResponseCollection createRSTRCollectionFrom(Source src) {
         try {
            jakarta.xml.bind.Unmarshaller u = getContext(WSTrustVersion.WS_TRUST_13).createUnmarshaller();
            JAXBElement<RequestSecurityTokenResponseCollectionType> rstrcType = u.unmarshal(src, RequestSecurityTokenResponseCollectionType.class);
            RequestSecurityTokenResponseCollectionType type = rstrcType.getValue();
            return new RequestSecurityTokenResponseCollectionImpl(type);
        } catch ( Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Create RSTR Collection from Element
     */
    public  RequestSecurityTokenResponseCollection createRSTRCollectionFrom(Element elem) {
        checkElement(elem);
        try {
            jakarta.xml.bind.Unmarshaller u = getContext(WSTrustVersion.WS_TRUST_13).createUnmarshaller();
            JAXBElement<RequestSecurityTokenResponseCollectionType> rstrcType = u.unmarshal(elem, RequestSecurityTokenResponseCollectionType.class);
            RequestSecurityTokenResponseCollectionType type = rstrcType.getValue();
            return new RequestSecurityTokenResponseCollectionImpl(type);
        } catch ( Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    
    /**
     * create an RST from JAXBElement
     * <p>
     * NOTE: an STS Implementor can call
     * <PRE>
     * JAXBElement&lt;RequestSecurityTokenType&gt; elem=
     * ObjectFactory.createRequestSecurityToken(&lt;JAXBBean for RST&gt;)
     * </PRE>
     * The JAXBBean for RST is the one generated from the ws-trust.xsd schema
     * The default implementation expects the packagename of the generated JAXB Beans to be fixed.
     * </p>
     */
    public RequestSecurityToken createRSTFrom(JAXBElement elem) {
        try {
            RequestSecurityTokenType type = (RequestSecurityTokenType)elem.getValue();
            return new RequestSecurityTokenImpl(type);
        } catch (Exception e) {
            throw new RuntimeException("There was a problem while creating RST from JAXBElement", e);
        }
    }
    
    /**
     * create an RSTR from JAXBElement
     * <p>
     * NOTE: an STS Implementor can call
     * <PRE>
     * JAXBElement&lt;RequestSecurityTokenResponseType&gt; elem=
     * ObjectFactory.createRequestSecurityTokenResponse(&lt;JAXBBean for RSTR&gt;);
     * </PRE>
     * The &lt;JAXBBean for RSTR&gt; is the one generated from the ws-trust.xsd schema
     * The default implementation expects the packagename of the generated JAXB Beans to be fixed.
     * </p>
     */
    public  RequestSecurityTokenResponse createRSTRFrom(JAXBElement elem) {
        try {
            RequestSecurityTokenResponseType type = (RequestSecurityTokenResponseType)elem.getValue();
            return new RequestSecurityTokenResponseImpl(type);
        } catch (Exception e) {
            throw new RuntimeException("There was a problem while creating RSTR from JAXBElement", e);
        }
    }
    /**
     * create an RSTR Collection from JAXBElement
     * <p>
     * NOTE: an STS Implementor can call
     * <PRE>
     * JAXBElement&lt;RequestSecurityTokenResponseCollectionType&gt; elem=
     * ObjectFactory.createRequestSecurityTokenResponseCollection(&lt;JAXBBean for RSTR Collection&gt;
     * </PRE>
     * The &lt;JAXBBean for RSTR Collection&gt; is the one generated from the ws-trust.xsd schema
     * The default implementation expects the packagename of the generated JAXB Beans to be fixed.
     * </p>
     */
    public  RequestSecurityTokenResponseCollection createRSTRCollectionFrom(JAXBElement elem) {
        try {
            RequestSecurityTokenResponseCollectionType type = (RequestSecurityTokenResponseCollectionType)elem.getValue();
            return new RequestSecurityTokenResponseCollectionImpl(type);
        } catch (Exception e) {
            throw new RuntimeException("There was a problem while creating RSTRCollection from JAXBElement", e);
        }
    }
    
     public Object createResponseFrom(JAXBElement elem){
        String local = elem.getName().getLocalPart();
        if (local.equalsIgnoreCase("RequestSecurityTokenResponseType")) {
           return createRSTRFrom(elem);
        }else{
            return createRSTRCollectionFrom(elem);
        }
    }
    
     public SecurityTokenReference createSecurityTokenReference(JAXBElement elem){
          try {
            SecurityTokenReferenceType type = (SecurityTokenReferenceType)elem.getValue();
            return new SecurityTokenReferenceImpl(type);
        } catch (Exception e) {
            throw new RuntimeException("There was a problem while creating STR from JAXBElement", e);
        }
     }

     public SecurityContextToken createSecurityContextToken(final URI identifier, final String instance, final String wsuId){
        throw new UnsupportedOperationException("this operation is not supported");
     }

     public JAXBElement toJAXBElement(final BaseSTSRequest request) {
        if (request instanceof RequestSecurityToken){
            return toJAXBElement((RequestSecurityToken)request);
        }

        return null;
    }

    public JAXBElement toJAXBElement(final BaseSTSResponse response) {
        if (response instanceof RequestSecurityTokenResponse){
            return toJAXBElement((RequestSecurityTokenResponse)response);
        }
        
        if (response instanceof RequestSecurityTokenResponseCollection){
            return toJAXBElement((RequestSecurityTokenResponseCollection)response);
        }

        return null;
    }
    
     /**
     * convert an SecurityTokenReference to a JAXBElement
     */
     public JAXBElement toJAXBElement(SecurityTokenReference str){
         JAXBElement<SecurityTokenReferenceType> strElement =
            (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)str);
        return strElement;
     }
    
    /**
     * convert an RST to a JAXBElement
     */
    public JAXBElement toJAXBElement(RequestSecurityToken rst) {
        JAXBElement<RequestSecurityTokenType> rstElement=
                (new ObjectFactory()).createRequestSecurityToken((RequestSecurityTokenType)rst);
        return rstElement;
    }
    
    /**
     * convert an RSTR to a JAXBElement
     */
    public  JAXBElement toJAXBElement(RequestSecurityTokenResponse rstr) {
        JAXBElement<RequestSecurityTokenResponseType> rstElement=
                (new ObjectFactory()).createRequestSecurityTokenResponse((RequestSecurityTokenResponseType)rstr);
        return rstElement;
    }
    
    
    /**
     * convert a Entropy to a JAXBElement
     */
    public  JAXBElement toJAXBElement(Entropy entropy) {
        JAXBElement<EntropyType> etElement=
                (new ObjectFactory()).createEntropy((EntropyType)entropy);
        return etElement;
    }
    
    /**
     * convert an RSTR Collection to a JAXBElement
     */
    public  JAXBElement toJAXBElement(RequestSecurityTokenResponseCollection rstrCollection) {
        JAXBElement<RequestSecurityTokenResponseCollectionType> rstElement=
                (new ObjectFactory()).createRequestSecurityTokenResponseCollection((RequestSecurityTokenResponseCollectionType)rstrCollection);
        return rstElement;
    }

     public Source toSource(final BaseSTSRequest request) {
        if (request instanceof RequestSecurityToken){
            return toSource((RequestSecurityToken)request);
        }

        return null;
    }

    public Source toSource(final BaseSTSResponse response) {
        if (response instanceof RequestSecurityTokenResponse){
            return toSource((RequestSecurityTokenResponse)response);
        }
        
        if (response instanceof RequestSecurityTokenResponseCollection){
            return toSource((RequestSecurityTokenResponseCollection)response);
        }

        return null;
    }
    
    /**
     * Marshal an RST to a Source.
     * <p>
     * Note: Useful for Dispatch Client implementations
     * </p>
     */
    public Source toSource(RequestSecurityToken rst) {
        return new DOMSource(toElement(rst));
    }
    
    /**
     * Marshal an RSTR to a Source
     * <p>
     * Note: Useful for STS implementations which are JAXWS Providers
     * </p>
     */
    public Source toSource(RequestSecurityTokenResponse rstr) {
        return new DOMSource(toElement(rstr));
    }
    
    /**
     * Marshal an RSTR Collection to a Source
     * <p>
     * Note: Useful for STS implementations which are JAXWS Providers
     * </p>
     */
    public  Source toSource(RequestSecurityTokenResponseCollection rstrCollection) {
         return new DOMSource(toElement(rstrCollection));
    }

     public Element toElement(final BaseSTSRequest request) {
        if (request instanceof RequestSecurityToken){
            return toElement((RequestSecurityToken)request);
        }

        return null;
    }

    public Element toElement(final BaseSTSResponse response) {
        if (response instanceof RequestSecurityTokenResponse){
            return toElement((RequestSecurityTokenResponse)response);
        }
        
        if (response instanceof RequestSecurityTokenResponseCollection){
            return toElement((RequestSecurityTokenResponseCollection)response);
        }

        return null;
    }
    
    /**
     * Marshal an RST to a DOM Element.
     * <p>
     * Note: Useful for Dispatch Client implementations
     * </p>
     */
    public Element toElement(RequestSecurityToken rst) {
        try {
            Document doc = WSTrustUtil.newDocument();
            
            //jakarta.xml.bind.Marshaller marshaller = getContext(WSTrustVersion.WS_TRUST_13).createMarshaller();
            JAXBElement<RequestSecurityTokenType> rstElement =  (new ObjectFactory()).createRequestSecurityToken((RequestSecurityTokenType)rst);
            getMarshaller().marshal(rstElement, doc);
            return doc.getDocumentElement();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    
    
    /**
     * Marshal an RSTR to DOM Element
     * <p>
     * Note: Useful for STS implementations which are JAXWS Providers
     * </p>
     */
    public Element toElement(RequestSecurityTokenResponse rstr) {
        try {
            Document doc = WSTrustUtil.newDocument();
            
            //jakarta.xml.bind.Marshaller marshaller = getContext(WSTrustVersion.WS_TRUST_13).createMarshaller();
            JAXBElement<RequestSecurityTokenResponseType> rstrElement =  (new ObjectFactory()).createRequestSecurityTokenResponse((RequestSecurityTokenResponseType)rstr);
            getMarshaller().marshal(rstrElement, doc);
            return doc.getDocumentElement();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
     public Element toElement(RequestSecurityTokenResponse rstr, Document doc) {
        try { 
           // jakarta.xml.bind.Marshaller marshaller = getContext(WSTrustVersion.WS_TRUST_13).createMarshaller();
           JAXBElement<RequestSecurityTokenResponseType> rstrElement =  (new ObjectFactory()).createRequestSecurityTokenResponse((RequestSecurityTokenResponseType)rstr);
           getMarshaller().marshal(rstrElement, doc);
           return doc.getDocumentElement();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Marshal an RSTR Collection to a DOM Element
     * <p>
     * Note: Useful for STS implementations which are JAXWS Providers
     * </p>
     */
    public  Element toElement(RequestSecurityTokenResponseCollection rstrCollection) {
        try {
            Document doc = WSTrustUtil.newDocument();
           // jakarta.xml.bind.Marshaller marshaller = getContext(WSTrustVersion.WS_TRUST_13).createMarshaller();
            JAXBElement<RequestSecurityTokenResponseCollectionType> rstrElement =
                    (new ObjectFactory()).createRequestSecurityTokenResponseCollection((RequestSecurityTokenResponseCollectionType)rstrCollection);                                    
            getMarshaller().marshal(rstrElement, doc);   

            return doc.getDocumentElement();                        
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    public Element toElement(BinarySecret bs){
        try {
            Document doc = WSTrustUtil.newDocument();
            
            //jakarta.xml.bind.Marshaller marshaller = getContext(WSTrustVersion.WS_TRUST_13).createMarshaller();
            JAXBElement<BinarySecretType> bsElement =
                    (new ObjectFactory()).createBinarySecret((BinarySecretType)bs);
            getMarshaller().marshal(bsElement, doc);
            return doc.getDocumentElement();            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Marshal an STR to a DOM Element.
     * <p>
     * Note: Useful for Dispatch Client implementations
     * </p>
     */
    public Element toElement(SecurityTokenReference str, Document doc) {
        try {
            if(doc == null){
                doc = WSTrustUtil.newDocument();
            }
            
            //jakarta.xml.bind.Marshaller marshaller = getContext(WSTrustVersion.WS_TRUST_13).createMarshaller();
            JAXBElement<SecurityTokenReferenceType> strElement =  (new com.sun.xml.ws.security.secext10.ObjectFactory()).createSecurityTokenReference((SecurityTokenReferenceType)str);
            getMarshaller().marshal(strElement, doc);
            return doc.getDocumentElement();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Marshal an BinarySecret to a DOM Element.
     * <p>
     * Note: Useful for Dispatch Client implementations
     * </p>
     */
    public Element toElement(BinarySecret bs, Document doc) {
        try {
            if(doc == null){
                doc = WSTrustUtil.newDocument();
            }
            
            //jakarta.xml.bind.Marshaller marshaller = getContext(WSTrustVersion.WS_TRUST_13).createMarshaller();
            JAXBElement<BinarySecretType> bsElement =
                    (new ObjectFactory()).createBinarySecret((BinarySecretType)bs);
            getMarshaller().marshal(bsElement, doc);
            return doc.getDocumentElement();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
     
    public Marshaller getMarshaller(){
         try {
            Marshaller marshaller = getContext(WSTrustVersion.WS_TRUST_13).createMarshaller();
            marshaller.setProperty("org.glassfish.jaxb.runtime.marshaller.namespacePrefixMapper", new com.sun.xml.ws.security.trust.util.TrustNamespacePrefixMapper());
        
            return marshaller;
         } catch( PropertyException e ) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0003_ERROR_CREATING_WSTRUSTFACT(), e);
            throw new RuntimeException(
                    LogStringsMessages.WST_0003_ERROR_CREATING_WSTRUSTFACT(), e);
        } catch (JAXBException jbe) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0003_ERROR_CREATING_WSTRUSTFACT(), jbe);
            throw new RuntimeException(
                    LogStringsMessages.WST_0003_ERROR_CREATING_WSTRUSTFACT(), jbe);
        }
    }

    private void checkElement(Element elem) {
        if (elem != null && elem.getLocalName().equalsIgnoreCase("Fault")) {
            try {
                QName qname = null;
                Map<String,Object> faultMap = null;
                Set<String> subcodeValues = new LinkedHashSet<String>();

                if (elem.getNamespaceURI().equals(MessageConstants.SOAP_1_1_NS)) {
                    faultMap = getFaultCodeAndReasonForSOAP1_1(elem);
                    String codeText = (String) faultMap.get("CodeText");
                    String reasonText = (String) faultMap.get("ReasonText");
                    codeText = codeText.substring(codeText.indexOf(":") + 1);
                    qname = new QName(MessageConstants.SOAP_1_1_NS, codeText);
                    throw new jakarta.xml.ws.soap.SOAPFaultException(SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault(reasonText, qname));
                } else if (elem.getNamespaceURI().equals(MessageConstants.SOAP_1_2_NS)) {

                    faultMap = getFaultCodeAndReasonForSOAP1_2(elem, "Code", subcodeValues);
                    String codeText = (String) faultMap.get("CodeText");
                    String reasonText = (String) faultMap.get("ReasonText");

                    codeText = codeText.substring(codeText.indexOf(":") + 1);
                    qname = new QName(MessageConstants.SOAP_1_2_NS, codeText);
                    SOAPFault fault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createFault(reasonText, qname);


                    List<String> subcodesList = new ArrayList<String>(subcodeValues);
                    Collections.reverse(subcodesList);
                    if (!subcodesList.isEmpty()) {
                        for (String subCodeValue : subcodesList) {
                            subCodeValue = subCodeValue.substring(subCodeValue.indexOf(":") + 1);
                            QName subcodeqname = new QName(MessageConstants.SOAP_1_2_NS, subCodeValue);
                            fault.appendFaultSubcode(subcodeqname);
                        }
                    }
                    throw new jakarta.xml.ws.soap.SOAPFaultException(fault);

                }
            } catch (SOAPException se) {
                throw new RuntimeException(se.getMessage());
            }
        }
    }

    private Map getFaultCodeAndReasonForSOAP1_1(Element elem) {
        Map<String,String> faultMap = new HashMap(2);
        Node reasonNode = null;
        String reasonText = null;        
        String codeText = null;
        NodeList nodes = elem.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            if("faultcode".equals(node.getLocalName())) {
                codeText = node.getTextContent();
            }
            else if("faultstring".equals(node.getLocalName())) {
                reasonText = node.getTextContent();
            }
        }
        faultMap.put("CodeText", codeText);
        faultMap.put("ReasonText", reasonText);
        return faultMap;

    }

    private Map getFaultCodeAndReasonForSOAP1_2(Element elem, String codeString, Set<String> subcodeValues) {
        Map<String,Object> faultMap = new HashMap();
        Node reasonNode = null;
        String reasonText = null;
        Node codeNode = null;
        String codeText = null;


        NodeList nodes = elem.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            
            if (codeString.equals(node.getLocalName())) {
                codeNode = node;
                NodeList subNodes = codeNode.getChildNodes();
                for (int j = 0; j < subNodes.getLength(); j++) {
                    Node subNode = subNodes.item(j);
                    if (subNode.getNodeType() == Node.TEXT_NODE) {
                        continue;
                    }
                    if ("Value".equals(subNode.getLocalName())) {
                        codeText = subNode.getTextContent();
                    }
                    if ("Subcode".equals(subNode.getLocalName())) {
                        Map subcodeMap = getFaultCodeAndReasonForSOAP1_2((Element) node, "Subcode",subcodeValues);

                        subcodeValues.add((String) subcodeMap.get("CodeText"));
                    }
                }
            } else if ("Reason".equals(node.getLocalName())) {
                reasonNode = node;
                NodeList subNodes = reasonNode.getChildNodes();
                for (int j = 0; j < subNodes.getLength(); j++) {
                    Node subNode = subNodes.item(j);
                    if (subNode.getNodeType() == Node.TEXT_NODE) {
                        continue;
                    }
                    if ("Text".equals(subNode.getLocalName())) {
                        reasonText = subNode.getTextContent();
                    }
                }
            }

        }
        faultMap.put("CodeText", codeText);
        faultMap.put("ReasonText", reasonText);
        return faultMap;

    }
}
