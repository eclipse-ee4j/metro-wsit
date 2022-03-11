/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SubjectConfirmation.java,v 1.2 2010-10-21 15:38:04 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.SubjectConfirmationType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import java.util.logging.Logger;


import java.security.PublicKey;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBContext;

/**
 * The <code>SubjectConfirmation</code> element specifies a subject by specifying data that
 * authenticates the subject.
 */
public class SubjectConfirmation extends SubjectConfirmationType
        implements com.sun.xml.wss.saml.SubjectConfirmation {

    protected PublicKey keyInfoKeyValue = null;

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    public SubjectConfirmation(){

    }

/**
     * From scratch constructor for a single confirmation method.
     *
     */
    public SubjectConfirmation(NameID nameID, java.lang.String method) {

//        List cm = new LinkedList();
//        cm.add(method);
        setNameID(nameID);
        setMethod(method);
    }

    /**
     * Constructs a subject confirmation element from an existing
     * XML block.
     *
     * @param element a DOM Element representing the
     *        <code>SubjectConfirmation</code> object.
     */
    public static SubjectConfirmationType fromElement(org.w3c.dom.Element element)
    throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();

            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (SubjectConfirmationType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    /**
     * Constructs an <code>SubjectConfirmation</code> instance.
     *
     * @param subjectConfirmationData Additional authentication information to
     *        be used by a specific authentication protocol. Can be passed as
     *        null if there is no <code>subjectConfirmationData</code> for the
     *        <code>SubjectConfirmation</code> object.
     * @exception SAMLException if the input data is invalid or
     *            <code>confirmationMethods</code> is empty.
     */
    public SubjectConfirmation(
            NameID nameID, SubjectConfirmationData subjectConfirmationData,
            java.lang.String confirmationMethod) throws SAMLException {

       // JAXBContext jc = null;
        //jakarta.xml.bind.Unmarshaller u = null;

        //Unmarshal to JAXB KeyInfo Object and set it
       // try {
        //    jc = SAML20JAXBUtil.getJAXBContext();
         //   u = jc.createUnmarshaller();
        //} catch ( Exception ex) {
         //   throw new SAMLException(ex.getMessage());
        //}

//        try {
//            if ( keyInfo != null) {
//                setKeyInfo((KeyInfoType)((JAXBElement)u.unmarshal(keyInfo)).getValue());
//            }
//            if ( subjectConfirmationData != null) {
//                setSubjectConfirmationData((SubjectConfirmationType)((JAXBElement)u.unmarshal(subjectConfirmationData)).getValue());
//            }
//        } catch (Exception ex) {
//            // log here
//            throw new SAMLException(ex);
//        }
        setNameID(nameID);
        if ( subjectConfirmationData != null)
            setSubjectConfirmationData(subjectConfirmationData);
        setMethod(confirmationMethod);
    }

     public SubjectConfirmation(
            NameID nameID, KeyInfoConfirmationData keyInfoConfirmationData,
            java.lang.String confirmationMethod) throws SAMLException {

        setNameID(nameID);
        if (keyInfoConfirmationData != null)
            setSubjectConfirmationData(keyInfoConfirmationData);
        setMethod(confirmationMethod);
    }


    public SubjectConfirmation(SubjectConfirmationType subConfType){
        if(subConfType.getNameID() != null){
            NameID nameId = new NameID(subConfType.getNameID());
            setNameID(nameId);
        }
        if(subConfType.getSubjectConfirmationData() != null){
            SubjectConfirmationData subConData = new SubjectConfirmationData(subConfType.getSubjectConfirmationData());
            setSubjectConfirmationData(subConData);
        }
        setMethod(subConfType.getMethod());
    }

    @Override
    public List<String> getConfirmationMethod() {
         List<String> confirmMethods = new ArrayList<>();
         confirmMethods.add(super.getMethod());
        return confirmMethods;
    }

    @Override
    public Object getSubjectConfirmationDataForSAML11() {
        throw new UnsupportedOperationException("Not supported for SAML 2.0");
    }
    @Override
    public SubjectConfirmationData getSubjectConfirmationDataForSAML20() {
        return (SubjectConfirmationData) super.getSubjectConfirmationData();
    }

    @Override
    public NameID getNameId() {
        return (NameID) super.getNameID();
    }
}
