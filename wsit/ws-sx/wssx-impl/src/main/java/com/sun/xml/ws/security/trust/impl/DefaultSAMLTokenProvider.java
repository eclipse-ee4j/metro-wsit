/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl;

import com.sun.xml.ws.api.security.trust.Status;
import com.sun.xml.ws.api.security.trust.STSAttributeProvider;
import com.sun.xml.ws.api.security.trust.STSTokenProvider;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;
import com.sun.xml.ws.security.trust.logging.LogStringsMessages;
import com.sun.xml.ws.security.trust.util.WSTrustUtil;

import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.saml.Advice;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.AttributeStatement;
import com.sun.xml.wss.saml.AudienceRestriction;
import com.sun.xml.wss.saml.AudienceRestrictionCondition;
import com.sun.xml.wss.saml.AuthenticationStatement;
import com.sun.xml.wss.saml.AuthnContext;
import com.sun.xml.wss.saml.AuthnStatement;
import com.sun.xml.wss.saml.Conditions;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.NameIdentifier;
import com.sun.xml.wss.saml.SAMLAssertionFactory;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.SubjectConfirmation;
import com.sun.xml.wss.saml.KeyInfoConfirmationData;
import com.sun.xml.wss.saml.util.SAMLUtil;

import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.KeyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.util.XmlFactory;
import com.sun.xml.wss.WSITXMLFactory;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** 
 *
 * @author Jiandong Guo
 */
public class DefaultSAMLTokenProvider implements STSTokenProvider {
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);

    protected static final String SAML_HOLDER_OF_KEY_1_0 = "urn:oasis:names:tc:SAML:1.0:cm:holder-of-key";
    protected static final String SAML_HOLDER_OF_KEY_2_0 = "urn:oasis:names:tc:SAML:2.0:cm:holder-of-key";
    protected static final String SAML_BEARER_1_0 = "urn:oasis:names:tc:SAML:1.0:cm:bearer";
    protected static final String SAML_BEARER_2_0 = "urn:oasis:names:tc:SAML:2.0:cm:bearer";
    protected static final String SAML_SENDER_VOUCHES_1_0 = "urn:oasis:names:tc:SAML:1.0:cm:sender-vouches";
    protected static final String SAML_SENDER_VOUCHES_2_0 = "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches";
    
    public void generateToken(IssuedTokenContext ctx) throws WSTrustException {
           
        String issuer = ctx.getTokenIssuer();
        String appliesTo = ctx.getAppliesTo();
        String tokenType = ctx.getTokenType(); 
        String keyType = ctx.getKeyType();
        int tokenLifeSpan = (int)(ctx.getExpirationTime().getTime() - ctx.getCreationTime().getTime());
        String confirMethod = (String)ctx.getOtherProperties().get(IssuedTokenContext.CONFIRMATION_METHOD);
        @SuppressWarnings("unchecked") Map<QName, List<String>> claimedAttrs = (Map<QName, List<String>>) ctx.getOtherProperties().get(IssuedTokenContext.CLAIMED_ATTRUBUTES);
        WSTrustVersion wstVer = (WSTrustVersion)ctx.getOtherProperties().get(IssuedTokenContext.WS_TRUST_VERSION);
       // WSTrustElementFactory eleFac = WSTrustElementFactory.newInstance(wstVer);
        
        // Create the KeyInfo for SubjectConfirmation
        final KeyInfo keyInfo = createKeyInfo(ctx);
        
        // Create AssertionID
        final String assertionId = "uuid-" + UUID.randomUUID().toString();
        
        // Create SAML assertion and the reference to the SAML assertion
        Assertion assertion = null;
        SecurityTokenReference samlReference = null;
        if (WSTrustConstants.SAML10_ASSERTION_TOKEN_TYPE.equals(tokenType)||
            WSTrustConstants.SAML11_ASSERTION_TOKEN_TYPE.equals(tokenType)){
            assertion = createSAML11Assertion(wstVer, tokenLifeSpan, confirMethod, assertionId, issuer, appliesTo, keyInfo, claimedAttrs, keyType);
            samlReference = WSTrustUtil.createSecurityTokenReference(assertionId, MessageConstants.WSSE_SAML_KEY_IDENTIFIER_VALUE_TYPE);
        } else if (WSTrustConstants.SAML20_ASSERTION_TOKEN_TYPE.equals(tokenType)||
                   WSTrustConstants.SAML20_WSS_TOKEN_TYPE.equals(tokenType)){
            String authnCtx = (String)ctx.getOtherProperties().get(IssuedTokenContext.AUTHN_CONTEXT);
            assertion = createSAML20Assertion(wstVer, tokenLifeSpan, confirMethod, assertionId, issuer, appliesTo, keyInfo, claimedAttrs, keyType, authnCtx);
            samlReference = WSTrustUtil.createSecurityTokenReference(assertionId, MessageConstants.WSSE_SAML_v2_0_KEY_IDENTIFIER_VALUE_TYPE);

            //set TokenType attribute for the STR as required in wss 1.1 saml token profile
            samlReference.setTokenType(WSTrustConstants.SAML20_WSS_TOKEN_TYPE);
        } else{
            log.log(Level.SEVERE, LogStringsMessages.WST_0031_UNSUPPORTED_TOKEN_TYPE(tokenType, appliesTo));
            throw new WSTrustException(LogStringsMessages.WST_0031_UNSUPPORTED_TOKEN_TYPE(tokenType, appliesTo));
        }
            
        // Get the STS's certificate and private key
        final X509Certificate stsCert = (X509Certificate)ctx.getOtherProperties().get(IssuedTokenContext.STS_CERTIFICATE);
        final PrivateKey stsPrivKey = (PrivateKey)ctx.getOtherProperties().get(IssuedTokenContext.STS_PRIVATE_KEY);
            
        // Sign the assertion with STS's private key
        Element signedAssertion = null;
        try{            
            signedAssertion = assertion.sign(stsCert, stsPrivKey, true, ctx.getSignatureAlgorithm(), ctx.getCanonicalizationAlgorithm());            
            //signedAssertion = assertion.sign(stsCert, stsPrivKey, true);            
            //signedAssertion = assertion.sign(stsCert, stsPrivKey);
        }catch (SAMLException ex){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
            throw new WSTrustException(
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
        }

        // put the SAML assertion and the references in the context
        ctx.setSecurityToken(new GenericToken(signedAssertion));
        ctx.setAttachedSecurityTokenReference(samlReference);
        ctx.setUnAttachedSecurityTokenReference(samlReference);
    }

    @SuppressWarnings("UnusedAssignment")
    public void isValideToken(IssuedTokenContext ctx) throws WSTrustException {
        WSTrustVersion wstVer = (WSTrustVersion)ctx.getOtherProperties().get(IssuedTokenContext.WS_TRUST_VERSION);
        WSTrustElementFactory eleFac = WSTrustElementFactory.newInstance(wstVer);
        
        // Get the token to be validated 
        Token token = ctx.getTarget();
        
        // Validate the token and create the Status
        // Only for SAML tokens for now: verify the signature and check 
        // the time stamp
        Element element = eleFac.toElement(token.getTokenValue());
        
        String code = wstVer.getValidStatusCodeURI();
        String reason = "The Trust service successfully validate the input";
        
        // Check if it is an SAML assertion
        if (!isSAMLAssertion(element)){
            code = wstVer.getInvalidStatusCodeURI();
            reason = "The Trust service did not successfully validate the input";
        }
        
        //==============================
        // validate the SAML asserttion
        //==============================
        
        // Get the STS's certificate and private key
        final X509Certificate stsCert = (X509Certificate)ctx.getOtherProperties().get(IssuedTokenContext.STS_CERTIFICATE);
       
       try{
            boolean isValid = true;

            // Verify the signature of the SAML assertion
            isValid = SAMLUtil.verifySignature(element, stsCert.getPublicKey());
        
            // validate time in Conditions
            isValid = SAMLUtil.validateTimeInConditionsStatement(element);
           
            if (!isValid){
                 code = wstVer.getInvalidStatusCodeURI();
                 reason = "The Trust service did not successfully validate the input";
            }
        }catch (XWSSecurityException ex){
            throw new WSTrustException(ex.getMessage());
        }
        
        // Create the Status
        Status status = eleFac.createStatus(code, reason);
        
        // Get TokenType
        String tokenType = ctx.getTokenType();
        if (!wstVer.getValidateStatuesTokenType().equals(tokenType)){
            // Todo: create a token of the required type
        }
        
        // populate the IssuedTokenContext
        ctx.getOtherProperties().put(IssuedTokenContext.STATUS, status);
    }

    public void renewToken(IssuedTokenContext ctx) throws WSTrustException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void invalidateToken(IssuedTokenContext ctx) throws WSTrustException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected Assertion createSAML11Assertion(final WSTrustVersion wstVer, final int lifeSpan, String confirMethod, final String assertionId, final String issuer, final String appliesTo, final KeyInfo keyInfo, final Map<QName, List<String>> claimedAttrs, String keyType) throws WSTrustException{
        Assertion assertion = null;
        try{
            final SAMLAssertionFactory samlFac = SAMLAssertionFactory.newInstance(SAMLAssertionFactory.SAML1_1);
            
            final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
            final GregorianCalendar issuerInst = new GregorianCalendar(utcTimeZone);
            final GregorianCalendar notOnOrAfter = new GregorianCalendar(utcTimeZone);
            notOnOrAfter.add(Calendar.MILLISECOND, lifeSpan);
            
            List<AudienceRestrictionCondition> arc = null;
            if (appliesTo != null){
                arc = new ArrayList<AudienceRestrictionCondition>();
                List<String> au = new ArrayList<String>();
                au.add(appliesTo);
                arc.add(samlFac.createAudienceRestrictionCondition(au));
            }
            final List<String> confirmMethods = new ArrayList<String>();
            Element keyInfoEle = null;
            if (keyType.equals(wstVer.getBearerKeyTypeURI())){
                confirMethod = SAML_BEARER_1_0;
            }else{
                if (confirMethod == null){
                    confirMethod = SAML_HOLDER_OF_KEY_1_0;
                }
                if (keyInfo != null){
                    keyInfoEle = keyInfo.getElement();
                }
            }
            confirmMethods.add(confirMethod);
            
            final SubjectConfirmation subjectConfirm = samlFac.createSubjectConfirmation(
                    confirmMethods, null, keyInfoEle);
            final Conditions conditions =
                    samlFac.createConditions(issuerInst, notOnOrAfter, null, arc, null);
            final Advice advice = samlFac.createAdvice(null, null, null);
            
            com.sun.xml.wss.saml.Subject subj = null;
            //final List<Attribute> attrs = new ArrayList<Attribute>();
            QName idName = null;
            String id = null;
            String idNS = null;
            final Set<Map.Entry<QName, List<String>>> entries = claimedAttrs.entrySet();
            for(Map.Entry<QName, List<String>> entry : entries){
                final QName attrKey = entry.getKey();
                final List<String> values = entry.getValue();
                if (values != null){
                    if ("ActAs".equals(attrKey.getLocalPart())){
                         if (values.size() > 0){
                            id = values.get(0);
                        }else{
                            id = null;
                        }
                        idNS = attrKey.getNamespaceURI();
                        idName = attrKey;

                        break;
                    } else if (STSAttributeProvider.NAME_IDENTIFIER.equals(attrKey.getLocalPart()) && subj == null){
                        if (values.size() > 0){
                            id = values.get(0);
                        }
                        idNS = attrKey.getNamespaceURI();
                        idName = attrKey;
                    }//else{
                       // final Attribute attr = samlFac.createAttribute(attrKey.getLocalPart(), attrKey.getNamespaceURI(), values);
                        //attrs.add(attr);
                    //}
                }
            }
            NameIdentifier nameId = null;
            if (idName != null && id != null){
                nameId = samlFac.createNameIdentifier(id, idNS, null);
                claimedAttrs.remove(idName);
            }
            subj = samlFac.createSubject(nameId, subjectConfirm);
            final List<Object> statements = new ArrayList<Object>();
           //if (attrs.isEmpty()){
            if (claimedAttrs.isEmpty()){
                final AuthenticationStatement statement = samlFac.createAuthenticationStatement(null, issuerInst, subj, null, null);
                statements.add(statement); 
            }else{
                final AttributeStatement statement = samlFac.createAttributeStatement(subj, null);
                statements.add(statement);
            }
            assertion =
                    samlFac.createAssertion(assertionId, issuer, issuerInst, conditions, advice, statements);
            if (!claimedAttrs.isEmpty()){
                return WSTrustUtil.addSamlAttributes(assertion, claimedAttrs);
            }
        }catch(SAMLException ex){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
            throw new WSTrustException(
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
        }catch(XWSSecurityException ex){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
            throw new WSTrustException(
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
        }
        
        return assertion;
    }
    
    protected Assertion createSAML20Assertion(final WSTrustVersion wstVer, final int lifeSpan, String confirMethod, final String assertionId, final String issuer, final String appliesTo, final KeyInfo keyInfo, final  Map<QName, List<String>> claimedAttrs, String keyType, String authnCtx) throws WSTrustException{
        Assertion assertion = null;
        try{
            final SAMLAssertionFactory samlFac = SAMLAssertionFactory.newInstance(SAMLAssertionFactory.SAML2_0);
            
            // Create Conditions
            final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
            final GregorianCalendar issueInst = new GregorianCalendar(utcTimeZone);
            final GregorianCalendar notOnOrAfter = new GregorianCalendar(utcTimeZone);
            notOnOrAfter.add(Calendar.MILLISECOND, lifeSpan);
            
            List<AudienceRestriction> arc = null;
            if (appliesTo != null){
                arc = new ArrayList<AudienceRestriction>();
                List<String> au = new ArrayList<String>();
                au.add(appliesTo);
                arc.add(samlFac.createAudienceRestriction(au));
            }
            KeyInfoConfirmationData keyInfoConfData = null;
            if (keyType.equals(wstVer.getBearerKeyTypeURI())){
                confirMethod = SAML_BEARER_2_0;
            }else{
                if (confirMethod == null){
                    confirMethod = SAML_HOLDER_OF_KEY_2_0;
                }
                if (keyInfo != null){
                    keyInfoConfData = samlFac.createKeyInfoConfirmationData(keyInfo.getElement());
                }
            }         
            
            final Conditions conditions = samlFac.createConditions(issueInst, notOnOrAfter, null, arc, null, null);
               
            final SubjectConfirmation subjectConfirm = samlFac.createSubjectConfirmation(
                    null, keyInfoConfData, confirMethod);
            
            com.sun.xml.wss.saml.Subject subj = null;
            //final List<Attribute> attrs = new ArrayList<Attribute>();
            QName idName = null;
            String id = null;
            String idNS = null;
            final Set<Map.Entry<QName, List<String>>> entries = claimedAttrs.entrySet();
            for(Map.Entry<QName, List<String>> entry : entries){
                final QName attrKey = entry.getKey();
                final List<String> values = entry.getValue();
                if (values != null){
                    if ("ActAs".equals(attrKey.getLocalPart())){
                        if (values.size() > 0){
                            id = values.get(0);
                        }else{
                            id = null;
                        }
                        idNS = attrKey.getNamespaceURI();
                        idName = attrKey;

                        break;
                    } else if (STSAttributeProvider.NAME_IDENTIFIER.equals(attrKey.getLocalPart()) && subj == null){
                        if (values.size() > 0){
                            id = values.get(0);
                        }
                        idNS = attrKey.getNamespaceURI();
                        idName = attrKey;
                    }
                    //else{
                      //  final Attribute attr = samlFac.createAttribute(attrKey.getLocalPart(), attrKey.getNamespaceURI(), values);
                      //  attrs.add(attr);
                    //}
                }
            }

            NameID nameId = null;
            if (idName != null && id != null){
                nameId = samlFac.createNameID(id, idNS, null);
                claimedAttrs.remove(idName);
            }
            subj = samlFac.createSubject(nameId, subjectConfirm);
        
            final List<Object> statements = new ArrayList<Object>();
            //if (attrs.isEmpty()){
            if (claimedAttrs.isEmpty()){
                AuthnContext ctx = samlFac.createAuthnContext(authnCtx, null);
                final AuthnStatement statement = samlFac.createAuthnStatement(issueInst, null, ctx, null, null);
                statements.add(statement); 
            }else{
                final AttributeStatement statement = samlFac.createAttributeStatement(null);
                statements.add(statement);
            }
            
            final NameID issuerID = samlFac.createNameID(issuer, null, null);
            
            // Create Assertion
            assertion =
                    samlFac.createAssertion(assertionId, issuerID, issueInst, conditions, null, null, statements);
            if (!claimedAttrs.isEmpty()){
                assertion = WSTrustUtil.addSamlAttributes(assertion, claimedAttrs);
            }
            ((com.sun.xml.wss.saml.assertion.saml20.jaxb20.Assertion)assertion).setSubject((com.sun.xml.wss.saml.internal.saml20.jaxb20.SubjectType)subj);
        }catch(SAMLException ex){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
            throw new WSTrustException(
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
        }catch(XWSSecurityException ex){
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
            throw new WSTrustException(
                    LogStringsMessages.WST_0032_ERROR_CREATING_SAML_ASSERTION(), ex);
        }
        
        return assertion;
    }
     
    private KeyInfo createKeyInfo(final IssuedTokenContext ctx)throws WSTrustException{
        Element kiEle = (Element)ctx.getOtherProperties().get("ConfirmationKeyInfo");
        if (kiEle != null && "KeyInfo".equals(kiEle.getLocalName())){
            try{
                return new KeyInfo(kiEle, null);
            }catch(org.apache.xml.security.exceptions.XMLSecurityException ex){
                log.log(Level.SEVERE, LogStringsMessages.WST_0034_UNABLE_GET_CLIENT_CERT(), ex);
                throw new WSTrustException(LogStringsMessages.WST_0034_UNABLE_GET_CLIENT_CERT(), ex);
            }
        }
        final DocumentBuilderFactory docFactory = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
        
        Document doc = null;
        try{
            doc = docFactory.newDocumentBuilder().newDocument();
        }catch(ParserConfigurationException ex){
            log.log(Level.SEVERE, 
                    LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
            throw new WSTrustException(LogStringsMessages.WST_0039_ERROR_CREATING_DOCFACTORY(), ex);
        }
        
        final String appliesTo = ctx.getAppliesTo();
        final KeyInfo keyInfo = new KeyInfo(doc);
        if (kiEle != null){
            keyInfo.addUnknownElement(kiEle);
            return keyInfo;
        }
        String keyType = ctx.getKeyType();
        WSTrustVersion wstVer = (WSTrustVersion)ctx.getOtherProperties().get(IssuedTokenContext.WS_TRUST_VERSION);
        if (wstVer.getSymmetricKeyTypeURI().equals(keyType)){
            final byte[] key = ctx.getProofKey();
            try{
                final EncryptedKey encKey = WSTrustUtil.encryptKey(doc, key, (X509Certificate)ctx.getOtherProperties().get(IssuedTokenContext.TARGET_SERVICE_CERTIFICATE), null);
                 keyInfo.add(encKey);
            } catch (Exception ex) {
                 log.log(Level.SEVERE,
                            LogStringsMessages.WST_0040_ERROR_ENCRYPT_PROOFKEY(appliesTo), ex);
                 throw new WSTrustException(LogStringsMessages.WST_0040_ERROR_ENCRYPT_PROOFKEY(appliesTo), ex);
            }
        }else if(wstVer.getPublicKeyTypeURI().equals(keyType)){
            final X509Data x509data = new X509Data(doc);
            try{
                x509data.addCertificate(ctx.getRequestorCertificate());
            }catch(org.apache.xml.security.exceptions.XMLSecurityException ex){
                log.log(Level.SEVERE, LogStringsMessages.WST_0034_UNABLE_GET_CLIENT_CERT(), ex);
                throw new WSTrustException(LogStringsMessages.WST_0034_UNABLE_GET_CLIENT_CERT(), ex);
            }
            keyInfo.add(x509data);
        }
        
        return keyInfo;
    }
    
    private boolean isSAMLAssertion(Element token){
        if (token.getLocalName().equals("Assertion") && 
            (token.getNamespaceURI().equals(WSTrustConstants.SAML10_ASSERTION_TOKEN_TYPE) ||
             token.getNamespaceURI().equals(WSTrustConstants.SAML20_ASSERTION_TOKEN_TYPE))){
            return true;
        }
        
        return false;
    }
}
