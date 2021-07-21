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
 * $Id: SubjectConfirmation.java,v 1.2 2010-10-21 15:38:00 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml11.jaxb20;

import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.security.core.dsig.KeyInfoType;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.SubjectConfirmationData;
import com.sun.xml.wss.saml.internal.saml11.jaxb20.SubjectConfirmationType;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Element;
import java.util.logging.Logger;

import java.security.PublicKey;

import javax.xml.bind.JAXBContext;

/**
 * The <code>SubjectConfirmation</code> element specifies a subject by specifying data that
 * authenticates the subject.
 */
public class SubjectConfirmation extends com.sun.xml.wss.saml.internal.saml11.jaxb20.SubjectConfirmationType
        implements com.sun.xml.wss.saml.SubjectConfirmation {
    
    protected PublicKey keyInfoKeyValue = null;    
            
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    public SubjectConfirmation(){
        
    }
    @SuppressWarnings("unchecked")
    public void setConfirmationMethod(List confirmationMethod) {
        this.confirmationMethod = confirmationMethod;
    }
    
    /**
     * From scratch constructor for a single confirmation method.
     *
     * @param confirmationMethod A URI (String) that identifies a protocol used
     *        to authenticate a <code>Subject</code>. Please refer to
     *        <code>draft-sstc-core-25</code> Section 7 for a list of URIs
     *        identifying common authentication protocols.
     */
    @SuppressWarnings("unchecked")
    public SubjectConfirmation(java.lang.String confirmationMethod) {
        
        List cm = new LinkedList();
        cm.add(confirmationMethod);
        setConfirmationMethod(cm);
    }
    
    /**
     * Constructs a subject confirmation element from an existing
     * XML block.
     *
     * @param element a DOM Element representing the
     *        <code>SubjectConfirmation</code> object.
     * @throws SAMLException
     */
    public static SubjectConfirmationType fromElement(org.w3c.dom.Element element)
    throws SAMLException {
        try {
            JAXBContext jc = SAMLJAXBUtil.getJAXBContext();
                    
            javax.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (SubjectConfirmationType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }
    
    /**
     * Constructs an <code>SubjectConfirmation</code> instance.
     *
     * @param confirmationMethods A set of <code>confirmationMethods</code>
     *        each of which is a URI (String) that identifies a protocol
     *        used to authenticate a <code>Subject</code>. Please refer to
     *        <code>draft-sstc-core-25</code> Section 7 for
     *        a list of URIs identifying common authentication protocols.
     * @param subjectConfirmationData Additional authentication information to
     *        be used by a specific authentication protocol. Can be passed as
     *        null if there is no <code>subjectConfirmationData</code> for the
     *        <code>SubjectConfirmation</code> object.
     * @param keyInfo An XML signature element that specifies a cryptographic
     *        key held by the <code>Subject</code>.
     * @exception SAMLException if the input data is invalid or
     *            <code>confirmationMethods</code> is empty.
     */
    public SubjectConfirmation(
            List confirmationMethods, Element subjectConfirmationData,
            Element keyInfo) throws SAMLException {
        
        JAXBContext jc = null;
        javax.xml.bind.Unmarshaller u = null;
        
        //Unmarshal to JAXB KeyInfo Object and set it
        try {
            jc = SAMLJAXBUtil.getJAXBContext();;
            u = jc.createUnmarshaller();
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
        
        try {
            if ( keyInfo != null) {
                setKeyInfo((KeyInfoType)((JAXBElement)u.unmarshal(keyInfo)).getValue());
            }
            if ( subjectConfirmationData != null) {
                setSubjectConfirmationData((SubjectConfirmationType)((JAXBElement)u.unmarshal(subjectConfirmationData)).getValue());
            }
        } catch (Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
        setConfirmationMethod(confirmationMethods);
    }     
    
    public SubjectConfirmation(SubjectConfirmationType subConfType){      
        setSubjectConfirmationData(subConfType.getSubjectConfirmationData());        
        setConfirmationMethod(subConfType.getConfirmationMethod());
    }
       
    public Object getSubjectConfirmationDataForSAML11() {
        return super.getSubjectConfirmationData();
    }
    
    public SubjectConfirmationData getSubjectConfirmationDataForSAML20() {
        throw new UnsupportedOperationException("Not supported for SAML 1.1");
    }

    public NameID getNameId() {
        throw new UnsupportedOperationException("Not supported for SAML 1.1");
    }
    
}
