/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
    
    public Action createAction(Element actionElement) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Action(actionElement);
    }
    
    @Override
    public Action createAction(String action, String namespace) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Action(action, namespace);
    }
    
    @Override
    public Advice createAdvice(List assertionidreference, List assertion, List otherelement) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Advice(assertionidreference, assertion, otherelement);
    }
    
    @Override
    public AnyType createAnyType() {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public Assertion createAssertion(org.w3c.dom.Element element) throws SAMLException {
        return com.sun.xml.wss.saml.assertion.saml11.jaxb20.Assertion.fromElement(element);
    }
    
    @Override
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
    @Override
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
    
    
    @Override
    public Assertion createAssertion(
            String ID,
            NameID issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            Subject subject,
            List statements) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    @Override
    public Assertion createAssertion(
            String ID,
            NameID issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            Subject subject,
            List statements,JAXBContext jcc) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AssertionIDReference createAssertionIDReference() {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AssertionIDRef createAssertionIDRef() {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AssertionIDReference createAssertionIDReference(String id) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AssertionIDRef createAssertionIDRef(String id) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public Attribute createAttribute(String name, String nameSpace, List values) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Attribute(name, nameSpace, values);
    }
    
    @Override
    public Attribute createAttribute(String name, List values) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AttributeDesignator createAttributeDesignator(String name, String nameSpace) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AttributeDesignator( name, nameSpace);
    }
    
    @Override
    public AttributeStatement createAttributeStatement(Subject subj, List attr) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AttributeStatement(
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Subject)subj, attr);
    }
    
    @Override
    public AttributeStatement createAttributeStatement(List attr) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AudienceRestrictionCondition createAudienceRestrictionCondition(List audience) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AudienceRestrictionCondition(audience);
    }
    
    @Override
    public AudienceRestriction createAudienceRestriction(List audience) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AuthenticationStatement createAuthenticationStatement(
            String authMethod, GregorianCalendar authInstant, Subject subject,
            SubjectLocality subjectLocality, List authorityBinding) {
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AuthenticationStatement(
                authMethod,
                authInstant,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Subject)subject,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectLocality)subjectLocality,
                authorityBinding);
    }
    
    @Override
    public AuthnStatement createAuthnStatement(
            GregorianCalendar authInstant, SubjectLocality subjectLocality, AuthnContext authnContext, 
            String sessionIndex, GregorianCalendar sessionNotOnOrAfter) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AuthorityBinding createAuthorityBinding(QName authKind, String location, String binding) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AuthorityBinding(
                authKind, location, binding);
    }
    
    @Override
    public AuthnContext createAuthnContext() {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public AuthnContext createAuthnContext(String authContextClassref, String authenticatingAuthority) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
        
    @Override
    public AuthorizationDecisionStatement createAuthorizationDecisionStatement(
            Subject subject, String resource, String decision, List action, Evidence evidence) {
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AuthorizationDecisionStatement(
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Subject)subject,
                resource,
                decision,
                action,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.Evidence)evidence
                );
    }
    
    @Override
    public AuthnDecisionStatement createAuthnDecisionStatement(
            String resource, String decision, List action, Evidence evidence) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    @Override
    public Conditions createConditions() {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Conditions();
    }
    
    @Override
    public Conditions createConditions(
            GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter,
            List condition,
            List arc,
            List doNotCacheCnd) {
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Conditions(
                notBefore, notOnOrAfter, condition, arc, doNotCacheCnd);
    }
    
    @Override
    public Conditions createConditions(
            GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter,
            List condition,
            List ar,
            List oneTimeUse,
            List proxyRestriction) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public DoNotCacheCondition createDoNotCacheCondition() {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.DoNotCacheCondition();
    }
    
    @Override
    public OneTimeUse createOneTimeUse() {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public Evidence createEvidence(List assertionIDRef, List assertion) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Evidence( assertionIDRef, assertion);
    }
    
    @Override
    public NameIdentifier createNameIdentifier(String name, String nameQualifier, String format) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.NameIdentifier( name, nameQualifier, format);
    }
    
    @Override
    public NameID createNameID(String name, String nameQualifier, String format) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public Subject createSubject(NameIdentifier nameIdentifier, SubjectConfirmation subjectConfirmation) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.Subject(
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.NameIdentifier)nameIdentifier,
                (com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectConfirmation)subjectConfirmation);
    }
    
    @Override
    public Subject createSubject(NameID nameID, SubjectConfirmation subjectConfirmation) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public SubjectConfirmation createSubjectConfirmation(String confirmationMethod) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectConfirmation(confirmationMethod);
    }
    
    @Override
    public SubjectConfirmation createSubjectConfirmation(NameID nameID, String method) {
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public SubjectConfirmation createSubjectConfirmation(
            List confirmationMethods, Element subjectConfirmationData,
            Element keyInfo) throws SAMLException {
        
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectConfirmation(confirmationMethods, subjectConfirmationData, keyInfo);
    }
    
    @Override
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
    
    
    @Override
    public SubjectConfirmation createSubjectConfirmation(
            NameID nameID, SubjectConfirmationData subjectConfirmationData,
            String confirmationMethod) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public SubjectConfirmation createSubjectConfirmation(
            NameID nameID, KeyInfoConfirmationData subjectConfirmationData,
            String confirmationMethod) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public SubjectConfirmationData createSubjectConfirmationData(
            String address, String inResponseTo, GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter, String recipient, Element keyInfo) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public SubjectConfirmationData createSubjectConfirmationData(
            String address, String inResponseTo, GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter, String recipient, KeyInfo keyInfo) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public KeyInfoConfirmationData createKeyInfoConfirmationData(Element keyInfo) {
        
        throw new UnsupportedOperationException("Not Supported for SAML1.1");
    }
    
    @Override
    public SubjectLocality createSubjectLocality() {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectLocality();
    }
    
    @Override
    public SubjectLocality createSubjectLocality(String ipAddress, String dnsAddress) {
        return new com.sun.xml.wss.saml.assertion.saml11.jaxb20.SubjectLocality(ipAddress, dnsAddress);
    }
    
    @Override
    public Assertion createAssertion(XMLStreamReader reader) throws SAMLException {
        try {
            Element samlElement = SAMLUtil.createSAMLAssertion(reader);
            Assertion samlAssertion =
                    com.sun.xml.wss.saml.assertion.saml11.jaxb20.Assertion.fromElement(samlElement);
            return samlAssertion;
        } catch (XWSSecurityException | XMLStreamException ex) {
            throw new SAMLException(ex);
        }
    }    
}
