/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SAMLAssertion1_1FactoryImpl.java
 *
 * Created on August 18, 2005, 12:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.sun.xml.wss.saml.impl;


import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.wss.XWSSecurityException;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import com.sun.xml.wss.saml.*;
import com.sun.xml.wss.saml.util.SAMLUtil;
import jakarta.xml.bind.JAXBContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;



/**
 *
 * @author root
 */
public class SAMLAssertion2_1FactoryImpl extends SAMLAssertionFactory {
    
    
    /** Creates a new instance of SAMLAssertion1_1FactoryImpl */
    public SAMLAssertion2_1FactoryImpl() {
    }
    
    public Action createAction(Element actionElement) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Action(actionElement);
    }
    
    public Action createAction(String action, String namespace) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Action(action, namespace);
    }
    
    public Advice createAdvice(List assertionidreference, List assertion, List otherelement) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Advice(assertionidreference, assertion, otherelement);
    }
    
    public AnyType createAnyType() throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public Assertion createAssertion(org.w3c.dom.Element element) throws SAMLException {
        return com.sun.xml.wss.saml.assertion.saml11.jaxb20.Assertion.fromElement(element);
    }
    
    public Assertion createAssertion(
            String assertionID,
            java.lang.String issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            List statements) throws SAMLException {
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Assertion(
                assertionID, issuer, issueInstant,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Conditions)conditions,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Advice)advice,
                statements);
    }
    public Assertion createAssertion(
            String assertionID,
            java.lang.String issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            List statements,JAXBContext jcc) throws SAMLException {
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Assertion(
                assertionID, issuer, issueInstant,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Conditions)conditions,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Advice)advice,
                statements,jcc);
    }
    
    
    public Assertion createAssertion(
            String ID,
            NameID issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            Subject subject,
            List statements) throws SAMLException {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    public Assertion createAssertion(
            String ID,
            NameID issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            Subject subject,
            List statements,JAXBContext jcc) throws SAMLException {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AssertionIDReference createAssertionIDReference() throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AssertionIDRef createAssertionIDRef() throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AssertionIDReference createAssertionIDReference(String id) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AssertionIDRef createAssertionIDRef(String id) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public Attribute createAttribute(String name, String nameSpace, List values) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Attribute(name, nameSpace, values);
    }
    
    public Attribute createAttribute(String name, List values) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AttributeDesignator createAttributeDesignator(String name, String nameSpace) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AttributeDesignator( name, nameSpace);
    }
    
    public AttributeStatement createAttributeStatement(Subject subj, List attr) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AttributeStatement(
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Subject)subj, attr);
    }
    
    public AttributeStatement createAttributeStatement(List attr) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AudienceRestrictionCondition createAudienceRestrictionCondition(List audience) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AudienceRestrictionCondition(audience);
    }
    
    public AudienceRestriction createAudienceRestriction(List audience) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AuthenticationStatement createAuthenticationStatement(
            String authMethod, GregorianCalendar authInstant, Subject subject,
            SubjectLocality subjectLocality, List authorityBinding) throws SAMLException{
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AuthenticationStatement(
                authMethod,
                authInstant,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Subject)subject,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectLocality)subjectLocality,
                authorityBinding);
    }
    
    public AuthnStatement createAuthnStatement(
            GregorianCalendar authInstant, SubjectLocality subjectLocality, AuthnContext authnContext, 
            String sessionIndex, GregorianCalendar sessionNotOnOrAfter) throws SAMLException{
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AuthorityBinding createAuthorityBinding(QName authKind, String location, String binding) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AuthorityBinding(
                authKind, location, binding);
    }
    
    public AuthnContext createAuthnContext() throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public AuthnContext createAuthnContext(String authContextClassref, String authenticatingAuthority) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
        
    public AuthorizationDecisionStatement createAuthorizationDecisionStatement(
            Subject subject, String resource, String decision, List action, Evidence evidence) throws SAMLException{
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AuthorizationDecisionStatement(
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Subject)subject,
                resource,
                decision,
                action,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Evidence)evidence
                );
    }
    
    public AuthnDecisionStatement createAuthnDecisionStatement(
            String resource, String decision, List action, Evidence evidence) throws SAMLException{
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    public Conditions createConditions() throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Conditions();
    }
    
    public Conditions createConditions(
            GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter,
            List condition,
            List arc,
            List doNotCacheCnd) throws SAMLException{
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Conditions(
                notBefore, notOnOrAfter, condition, arc, doNotCacheCnd);
    }
    
    public Conditions createConditions(
            GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter,
            List condition,
            List ar,
            List oneTimeUse,
            List proxyRestriction) throws SAMLException{
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public DoNotCacheCondition createDoNotCacheCondition() throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.DoNotCacheCondition();
    }
    
    public OneTimeUse createOneTimeUse() throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public Evidence createEvidence(List assertionIDRef, List assertion) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Evidence( assertionIDRef, assertion);
    }
    
    public NameIdentifier createNameIdentifier(String name, String nameQualifier, String format) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.NameIdentifier( name, nameQualifier, format);
    }
    
    public NameID createNameID(String name, String nameQualifier, String format) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public Subject createSubject(NameIdentifier nameIdentifier, SubjectConfirmation subjectConfirmation) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Subject(
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.NameIdentifier)nameIdentifier,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectConfirmation)subjectConfirmation);
    }
    
    public Subject createSubject(NameID nameID, SubjectConfirmation subjectConfirmation) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public SubjectConfirmation createSubjectConfirmation(String confirmationMethod) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectConfirmation(confirmationMethod);
    }
    
    public SubjectConfirmation createSubjectConfirmation(NameID nameID, String method) throws SAMLException{
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public SubjectConfirmation createSubjectConfirmation(
            List confirmationMethods, Element subjectConfirmationData,
            Element keyInfo) throws SAMLException {
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectConfirmation(confirmationMethods, subjectConfirmationData, keyInfo);
    }
    
    public SubjectConfirmation createSubjectConfirmation(
            List confirmationMethods,SubjectConfirmationData scd,KeyInfo keyInfo) throws SAMLException {
        com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectConfirmation sc = new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectConfirmation();
        
        try {
            if ( keyInfo != null) {
                sc.setKeyInfo(keyInfo);
            }
            if ( scd != null) {
                sc.setSubjectConfirmationData(scd);
            }
        } catch (Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
        sc.setConfirmationMethod(confirmationMethods);
        return sc;
    }
    
    
    public SubjectConfirmation createSubjectConfirmation(
            NameID nameID, SubjectConfirmationData subjectConfirmationData,
            String confirmationMethod) throws SAMLException {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public SubjectConfirmation createSubjectConfirmation(
            NameID nameID, KeyInfoConfirmationData subjectConfirmationData,
            String confirmationMethod) throws SAMLException {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public SubjectConfirmationData createSubjectConfirmationData(
            String address, String inResponseTo, GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter, String recipient, Element keyInfo) throws SAMLException{
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public SubjectConfirmationData createSubjectConfirmationData(
            String address, String inResponseTo, GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter, String recipient, KeyInfo keyInfo) throws SAMLException{
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public KeyInfoConfirmationData createKeyInfoConfirmationData(Element keyInfo) throws SAMLException{
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    public SubjectLocality createSubjectLocality() throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectLocality();
    }
    
    public SubjectLocality createSubjectLocality(String ipAddress, String dnsAddress) throws SAMLException{
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectLocality(ipAddress, dnsAddress);
    }
    
    public Assertion createAssertion(XMLStreamReader reader) throws SAMLException {
        try {
            Element samlElement = SAMLUtil.createSAMLAssertion(reader);
            Assertion samlAssertion = 
                    (Assertion)com.sun.xml.wss.saml.assertion.saml11.jaxb20.Assertion.fromElement(samlElement);
            return samlAssertion;
        } catch (XWSSecurityException ex) {
            throw new SAMLException(ex);
        } catch (XMLStreamException ex) {
            throw new SAMLException(ex);
        }
    }    
}
