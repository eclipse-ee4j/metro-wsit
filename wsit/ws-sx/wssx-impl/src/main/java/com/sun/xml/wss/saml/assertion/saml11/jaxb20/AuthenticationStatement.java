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
 * $Id: AuthenticationStatement.java,v 1.2 2010-10-21 15:38:00 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

import com.sun.xml.wss.saml.AuthorityBinding;
import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AuthenticationStatementType;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.AuthorityBindingType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import com.sun.xml.wss.util.DateUtils;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.w3c.dom.Element;
import java.util.List;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBContext;

/**
 * The <code>AuthenticationStatement</code> element supplies a
 * statement by the issuer that its subject was authenticated by a
 * particular means at a particular time. The
 * <code>AuthenticationStatement</code> element is of type
 * <code>AuthenticationStatementType</code>, which extends the
 * <code>SubjectStatementAbstractType</code> with the additional element and
 * attributes.
 */
public class AuthenticationStatement extends AuthenticationStatementType
        implements com.sun.xml.wss.saml.AuthenticationStatement {
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    private List<AuthorityBinding> authorityBindingList = null;
    private Date instantDate = null;
    /**
     *Default constructor
     */
    protected AuthenticationStatement() {
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
    public static AuthenticationStatementType fromElement(Element element) throws SAMLException {
        try {
            JAXBContext jc = SAMLJAXBUtil.getJAXBContext();
                    
            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (AuthenticationStatementType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void setAuthorityBinding(List authorityBinding) {
        this.authorityBinding = authorityBinding;
    }
    
    /**
     * Constructor for authentication statement
     *
     * @param authMethod (optional) A String specifies the type of authentication
     *        that took place.
     * @param authInstant (optional) A GregorianCalendar specifies the time at which the
     *        authentication that took place.
     * @param subject (required) A Subject object
     * @param subjectLocality (optional) A <code>SubjectLocality</code> object.
     * @param authorityBinding (optional) A List of <code>AuthorityBinding</code>
     *        objects.
     */
    public AuthenticationStatement(
            String authMethod, GregorianCalendar authInstant, Subject subject,
            SubjectLocality subjectLocality, List authorityBinding) {
        
        if ( authMethod != null)
            setAuthenticationMethod(authMethod);
        
        if ( authInstant != null) {
            try {
                DatatypeFactory factory = DatatypeFactory.newInstance();
                setAuthenticationInstant(factory.newXMLGregorianCalendar(authInstant));
            }catch ( DatatypeConfigurationException ex ) {
                //ignore
            }
        }
        
        if ( subject != null)
            setSubject(subject);
        
        if ( subjectLocality != null)
            setSubjectLocality(subjectLocality);
        
        if ( authorityBinding != null)
            setAuthorityBinding(authorityBinding);
    }
    
    public AuthenticationStatement(AuthenticationStatementType authStmtType) {
        setAuthenticationMethod(authStmtType.getAuthenticationMethod());
        setAuthenticationInstant(authStmtType.getAuthenticationInstant());
        if(authStmtType.getSubject() != null){
            Subject subj = new Subject(authStmtType.getSubject());
            setSubject(subj);
        }
        setSubjectLocality(authStmtType.getSubjectLocality());
        setAuthorityBinding(authStmtType.getAuthorityBinding());
    }
    
    public Date getAuthenticationInstantDate(){
        if(instantDate != null){
            return instantDate;
        }        
        try {
            if(super.getAuthenticationInstant() != null){
                instantDate = DateUtils.stringToDate(super.getAuthenticationInstant().toString());
            }
        } catch (ParseException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        return instantDate;
    }

    
    @Override
    public String getAuthenticationMethod(){
        return super.getAuthenticationMethod();
    }
    
    public List<AuthorityBinding> getAuthorityBindingList(){
        if(authorityBindingList != null){
            authorityBindingList = new ArrayList<AuthorityBinding>();
        }else{
            return authorityBindingList;
        }
        Iterator it = super.getAuthorityBinding().iterator();
        while(it.hasNext()){
            com.sun.xml.wss.saml.assertion.saml11.jaxb20.AuthorityBinding obj = 
                    new com.sun.xml.wss.saml.assertion.saml11.jaxb20.AuthorityBinding((AuthorityBindingType)it.next());
            authorityBindingList.add(obj);            
        }
        return authorityBindingList;
    }        

    public String getSubjectLocalityIPAddress() {
        if(super.getSubjectLocality() != null){
            return super.getSubjectLocality().getIPAddress();
        }
        return null;
    }

    public String getSubjectLocalityDNSAddress() {
        if(super.getSubjectLocality() != null){
            return super.getSubjectLocality().getDNSAddress();
        }
        return null;
    }

    public Subject getSubject() {
        return (Subject)super.getSubject();
    }
}
