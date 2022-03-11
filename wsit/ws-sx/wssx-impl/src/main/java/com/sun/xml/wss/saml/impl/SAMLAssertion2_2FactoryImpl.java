/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.saml.impl;
//import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
//import com.sun.xml.wss.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.wss.XWSSecurityException;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

import com.sun.xml.wss.saml.Action;
import com.sun.xml.wss.saml.Advice;
import com.sun.xml.wss.saml.AnyType;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.AssertionIDRef;
import com.sun.xml.wss.saml.AssertionIDReference;
import com.sun.xml.wss.saml.Attribute;
import com.sun.xml.wss.saml.AttributeDesignator;
import com.sun.xml.wss.saml.AttributeStatement;
import com.sun.xml.wss.saml.AudienceRestriction;
import com.sun.xml.wss.saml.AudienceRestrictionCondition;
import com.sun.xml.wss.saml.AuthenticationStatement;
import com.sun.xml.wss.saml.AuthnContext;
import com.sun.xml.wss.saml.AuthnDecisionStatement;
import com.sun.xml.wss.saml.AuthnStatement;
import com.sun.xml.wss.saml.AuthorityBinding;
import com.sun.xml.wss.saml.AuthorizationDecisionStatement;
import com.sun.xml.wss.saml.Conditions;
import com.sun.xml.wss.saml.DoNotCacheCondition;
import com.sun.xml.wss.saml.Evidence;
import com.sun.xml.wss.saml.KeyInfoConfirmationData;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.NameIdentifier;
import com.sun.xml.wss.saml.OneTimeUse;
import com.sun.xml.wss.saml.SAMLAssertionFactory;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.Subject;
import com.sun.xml.wss.saml.SubjectConfirmation;
import com.sun.xml.wss.saml.SubjectConfirmationData;
import com.sun.xml.wss.saml.SubjectLocality;
import org.w3c.dom.Element;
import com.sun.xml.wss.saml.util.SAMLUtil;
import jakarta.xml.bind.JAXBContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author root
 */
public class SAMLAssertion2_2FactoryImpl extends SAMLAssertionFactory {
    DatatypeFactory dataTypeFac = null;

    /** Creates a new instance of SAMLAssertion2_2FactoryImpl */
    public SAMLAssertion2_2FactoryImpl() {
        try{
            dataTypeFac = DatatypeFactory.newInstance();
        }catch ( DatatypeConfigurationException ex ) {
            //ignore
        }

    }

    public Action createAction(Element actionElement) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Action(actionElement);
    }

    @Override
    public Action createAction(String action, String namespace) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Action(action, namespace);
    }

    @Override
    public Advice createAdvice(List assertionidreference, List assertion, List otherelement) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Advice(assertionidreference, assertion, otherelement);
    }

    @Override
    public AnyType createAnyType() {
        return null;
    }

    @Override
    public Assertion createAssertion(org.w3c.dom.Element element) throws SAMLException {
        return com.sun.xml.wss.saml.assertion.saml20.jaxb20.Assertion.fromElement(element);
    }

    @Override
    public Assertion createAssertion(
            String assertionID,
            java.lang.String issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            List statements) {

        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }
    @Override
    public Assertion createAssertion(
            String assertionID,
            java.lang.String issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            List statements,JAXBContext jcc) {

        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public Assertion createAssertion(
            String ID,
            NameID issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            Subject subject,
            List statements) throws SAMLException {

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Assertion(
                ID,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.NameID)issuer,
                issueInstant,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.Conditions)conditions,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.Advice)advice,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.Subject)subject,
                statements);
    }
    @Override
    public Assertion createAssertion(
            String ID,
            NameID issuer,
            GregorianCalendar issueInstant,
            Conditions conditions,
            Advice advice,
            Subject subject,
            List statements,JAXBContext jcc) throws SAMLException {

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Assertion(
                ID,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.NameID)issuer,
                issueInstant,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.Conditions)conditions,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.Advice)advice,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.Subject)subject,
                statements,jcc);
    }


    @Override
    public AssertionIDReference createAssertionIDReference() {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }
    @Override
    public AssertionIDRef createAssertionIDRef() {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public AssertionIDReference createAssertionIDReference(String id) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public AssertionIDRef createAssertionIDRef(String id) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public Attribute createAttribute(String name, String nameSpace, List values) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Attribute(name, nameSpace, values);
    }

    @Override
    public Attribute createAttribute(String name, List values) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Attribute(name, values);
    }

    @Override
    public AttributeDesignator createAttributeDesignator(String name, String nameSpace) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public AttributeStatement createAttributeStatement(Subject subj, List attr) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public AttributeStatement createAttributeStatement(List attr) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.AttributeStatement(attr);
    }

    @Override
    public AudienceRestrictionCondition createAudienceRestrictionCondition(List audience) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public AudienceRestriction createAudienceRestriction(List audience) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.AudienceRestriction(audience);
    }

    @Override
    public AuthenticationStatement createAuthenticationStatement(
            String authMethod, GregorianCalendar authInstant, Subject subject,
            SubjectLocality subjectLocality, List authorityBinding) {

        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public AuthnStatement createAuthnStatement(
            GregorianCalendar authInstant, SubjectLocality subjectLocality, AuthnContext authnContext,
            String sessionIndex, GregorianCalendar sessionNotOnOrAfter ) {

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.AuthnStatement(
                authInstant,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectLocality)subjectLocality,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.AuthnContext)authnContext,
                sessionIndex,
                sessionNotOnOrAfter);
    }

    @Override
    public AuthorityBinding createAuthorityBinding(QName authKind, String location, String binding) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public AuthnContext createAuthnContext() {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.AuthnContext();
    }

    @Override
    public AuthnContext createAuthnContext(String authContextClassref, String authenticatingAuthority) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.AuthnContext(authContextClassref, authenticatingAuthority);
    }

    @Override
    public AuthorizationDecisionStatement createAuthorizationDecisionStatement(
            Subject subject, String resource, String decision, List action, Evidence evidence) {

        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public AuthnDecisionStatement createAuthnDecisionStatement(
            String resource, String decision, List action, Evidence evidence) {

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.AuthzDecisionStatement(
                resource,
                decision,
                action,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.Evidence)evidence
                );
    }

    @Override
    public Conditions createConditions() {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Conditions();
    }

    @Override
    public Conditions createConditions(
            GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter,
            List condition,
            List arc,
            List doNotCacheCnd) {

        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public Conditions createConditions(
            GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter,
            List condition,
            List ar,
            List oneTimeUse,
            List proxyRestriction) {

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Conditions(
                notBefore, notOnOrAfter, condition, ar, oneTimeUse, proxyRestriction);
    }


    @Override
    public DoNotCacheCondition createDoNotCacheCondition() {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public OneTimeUse createOneTimeUse() {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.OneTimeUse();
    }

    @Override
    public Evidence createEvidence(List assertionIDRef, List assertion) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Evidence( assertionIDRef, assertion);
    }

    @Override
    public NameIdentifier createNameIdentifier(String name, String nameQualifier, String format) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public NameID createNameID(String name, String nameQualifier, String format) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.NameID( name, nameQualifier, format);
    }

    @Override
    public Subject createSubject(NameIdentifier nameIdentifier, SubjectConfirmation subjectConfirmation) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public Subject createSubject(NameID nameID, SubjectConfirmation subjectConfirmation) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.Subject(
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.NameID)nameID,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectConfirmation)subjectConfirmation);
    }

    @Override
    public SubjectConfirmation createSubjectConfirmation(String confirmationMethod) {
        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public SubjectConfirmation createSubjectConfirmation(NameID nameID, String method) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectConfirmation(
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.NameID)nameID,
                method);
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
            List confirmationMethods, Element subjectConfirmationData,
            Element keyInfo) {

        throw new UnsupportedOperationException("Not Supported for SAML2.0");
    }

    @Override
    public SubjectConfirmation createSubjectConfirmation(
            NameID nameID, SubjectConfirmationData subjectConfirmationData,
            String confirmationMethod) throws SAMLException {

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectConfirmation(
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.NameID)nameID,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectConfirmationData)subjectConfirmationData,
                confirmationMethod);
    }

    @Override
    public SubjectConfirmation createSubjectConfirmation(
            NameID nameID, KeyInfoConfirmationData keyInfoConfirmationData,
            String confirmationMethod) throws SAMLException {

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectConfirmation(
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.NameID)nameID,
                (com.sun.xml.wss.saml.assertion.saml20.jaxb20.KeyInfoConfirmationData)keyInfoConfirmationData,
                confirmationMethod);
    }

    @Override
    public SubjectConfirmationData createSubjectConfirmationData(
            String address, String inResponseTo, GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter, String recipient, Element keyInfo) throws SAMLException{

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectConfirmationData(
                address, inResponseTo, notBefore, notOnOrAfter, recipient,
                keyInfo);
    }

    @Override
    public SubjectConfirmationData createSubjectConfirmationData(
            String address, String inResponseTo, GregorianCalendar notBefore,
            GregorianCalendar notOnOrAfter, String recipient, KeyInfo keyInfo) {
        com.sun.xml.wss.saml.internal.saml20.jaxb20.SubjectConfirmationDataType scd = new com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectConfirmationData();
        scd.setAddress(address);
        scd.setInResponseTo(inResponseTo);
        if ( notBefore != null) {
            scd.setNotBefore(dataTypeFac.newXMLGregorianCalendar(notBefore));
        }

        if ( notOnOrAfter != null) {
            scd.setNotOnOrAfter(dataTypeFac.newXMLGregorianCalendar(notOnOrAfter));
        }

        scd.setRecipient(recipient);

        if (keyInfo != null){
            scd.getContent().add(keyInfo);
        }
        return (SubjectConfirmationData)scd;
    }

    @Override
    public KeyInfoConfirmationData createKeyInfoConfirmationData(Element keyInfo) throws SAMLException{

        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.KeyInfoConfirmationData(keyInfo);
    }

    @Override
    public SubjectLocality createSubjectLocality() {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectLocality();
    }

    @Override
    public SubjectLocality createSubjectLocality(String ipAddress, String dnsAddress) {
        return new com.sun.xml.wss.saml.assertion.saml20.jaxb20.SubjectLocality(ipAddress, dnsAddress);
    }

    @Override
    public Assertion createAssertion(XMLStreamReader reader) throws SAMLException {
        try {
            Element samlElement = SAMLUtil.createSAMLAssertion(reader);
            return com.sun.xml.wss.saml.assertion.saml20.jaxb20.Assertion.fromElement(samlElement);
        } catch (XWSSecurityException | XMLStreamException ex) {
            throw new SAMLException(ex);
        }
    }
}
