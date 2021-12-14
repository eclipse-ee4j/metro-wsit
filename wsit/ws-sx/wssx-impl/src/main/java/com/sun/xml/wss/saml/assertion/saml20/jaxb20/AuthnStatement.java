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
 * $Id: AuthnStatement.java,v 1.2 2010-10-21 15:38:03 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.AuthnStatementType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import com.sun.xml.wss.util.DateUtils;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.w3c.dom.Element;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;

/**
 * The <code>AuthnStatement</code> element supplies a
 * statement by the issuer that its subject was authenticated by a
 * particular means at a particular time. The
 * <code>AuthnStatement</code> element is of type
 * <code>AuthnStatementType</code>, which extends the
 * <code>SubjectStatementAbstractType</code> with the additional element and
 * attributes.
 */
public class AuthnStatement extends AuthnStatementType
        implements com.sun.xml.wss.saml.AuthnStatement {
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);    
    private Date authnInstantDate = null;
    private Date sessionDate = null;
    
    /**
     *Default constructor
     */
    protected AuthnStatement() {
        super();
    }
    
    /**
     * This constructor builds an authentication statement element from an
     * existing XML block.
     *
     * @param element representing a DOM tree element.
     * @exception SAMLException if there is an error in the sender or in the
     *            element definition.
     */
    public static AuthnStatementType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();
                    
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AuthnStatementType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
    
    
//    private void setAuthnContext(AuthnContext authnContext) {
//        this.authnContext = authnContext;
//    }
    
    /**
     * Constructor for authentication statement
     *
     * @param authInstant A GregorianCalendar specifies the time at which the
     *        authentication that took place.
     * @param subjectLocality A <code>SubjectLocality</code> object.
     */
    public AuthnStatement(
            GregorianCalendar authInstant, SubjectLocality subjectLocality, 
            AuthnContext authnContext, String sessionIndex, GregorianCalendar sessionNotOnOrAfter) {
                
        if ( authInstant != null) {
            try {
                DatatypeFactory factory = DatatypeFactory.newInstance();
                setAuthnInstant(factory.newXMLGregorianCalendar(authInstant));
            }catch ( DatatypeConfigurationException ex ) {
                //ignore
            }
        }
        
        if ( subjectLocality != null)
            setSubjectLocality(subjectLocality);
        
        if ( authnContext != null)
            setAuthnContext(authnContext);
        
        if(sessionIndex != null)
            setSessionIndex(sessionIndex);
        
        if ( sessionNotOnOrAfter != null) {
            try {
                DatatypeFactory factory = DatatypeFactory.newInstance();
                setSessionNotOnOrAfter(factory.newXMLGregorianCalendar(sessionNotOnOrAfter));
            }catch ( DatatypeConfigurationException ex ) {
                //ignore
            }
        }
    }
    
    public AuthnStatement(AuthnStatementType authStmtType) {
        setAuthnInstant(authStmtType.getAuthnInstant());
        setAuthnContext(authStmtType.getAuthnContext());
        setSubjectLocality(authStmtType.getSubjectLocality());        
        setSessionIndex(authStmtType.getSessionIndex());
        setSessionNotOnOrAfter(authStmtType.getSessionNotOnOrAfter());
    }

    @Override
    public Date getAuthnInstantDate() {
        if(authnInstantDate != null){
            return authnInstantDate;
        } 
        try {
            if(super.getAuthnInstant() != null){
                authnInstantDate = DateUtils.stringToDate(super.getAuthnInstant().toString());
            }
        } catch (ParseException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0429_SAML_AUTH_INSTANT_OR_SESSION_PARSE_FAILED(), ex);
        }
        return authnInstantDate;
    }

    @Override
    public Date getSessionNotOnOrAfterDate() {
        if(sessionDate != null){
            return sessionDate;
        }
        try {
            if(super.getSessionNotOnOrAfter() != null){
                sessionDate = DateUtils.stringToDate(super.getSessionNotOnOrAfter().toString());
            }
        } catch (ParseException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0429_SAML_AUTH_INSTANT_OR_SESSION_PARSE_FAILED(), ex);
        }
        return sessionDate;
    }

    @Override
    public String getSubjectLocalityAddress() {
        if(super.getSubjectLocality() != null){
            return super.getSubjectLocality().getAddress();
        }
        return null;
    }

    @Override
    public String getSubjectLocalityDNSName() {
        if(super.getSubjectLocality() != null){
            return super.getSubjectLocality().getDNSName();
        }
        return null;
    }

    @Override
    public String getAuthnContextClassRef() {
        Iterator it = super.getAuthnContext().getContent().iterator();        
        while(it.hasNext()){
            Object obj = it.next();
            if(obj instanceof JAXBElement){
                JAXBElement element = (JAXBElement)obj;
                if(element.getName().getLocalPart().equalsIgnoreCase("AuthnContextClassRef")){                    
                    return element.getValue().toString();
                }
            }
        }
        return null;
    }

    @Override
    public String getAuthenticatingAuthority() {
        Iterator it = super.getAuthnContext().getContent().iterator();        
        while(it.hasNext()){
            Object obj = it.next();
            if(obj instanceof JAXBElement){
                JAXBElement element = (JAXBElement)obj;
                if(element.getName().getLocalPart().equalsIgnoreCase("AuthenticatingAuthority")){
                    return element.getValue().toString();
                }
            }
        }
        return null;
    }
    
    @Override
    public String getSessionIndex(){
        if(super.getSessionIndex() != null){
            return super.getSessionIndex();
        }
        return null;
    }
}
