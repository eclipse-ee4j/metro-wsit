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
 * $Id: SubjectConfirmationData.java,v 1.2 2010-10-21 15:38:04 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.SubjectConfirmationDataType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import com.sun.xml.wss.util.DateUtils;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import java.security.PublicKey;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBContext;
import org.w3c.dom.Element;

/**
 * The <code>SubjectConfirmationData</code> element specifies a subject by specifying data that
 * authenticates the subject.
 */
public class SubjectConfirmationData extends SubjectConfirmationDataType
        implements com.sun.xml.wss.saml.SubjectConfirmationData {

    protected PublicKey keyInfoKeyValue = null;
    private Date notBeforeDate = null;
    private Date notOnOrAfterDate = null;
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);


    public SubjectConfirmationData(){

    }

    /**
     * Constructs a subject confirmation element from an existing
     * XML block.
     *
     * @param element a DOM Element representing the
     *        <code>SubjectConfirmationData</code> object.
     */
    public static SubjectConfirmationDataType fromElement(org.w3c.dom.Element element)
    throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();

            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (SubjectConfirmationDataType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    /**
     * Constructs an <code>SubjectConfirmationData</code> instance.
     *
     * @param keyInfo An XML signature element that specifies a cryptographic
     *        key held by the <code>Subject</code>.
     * @exception SAMLException if the input data is invalid or
     *            <code>confirmationMethods</code> is empty.
     */
    public SubjectConfirmationData(
        String address, String inResponseTo, GregorianCalendar notBefore,
        GregorianCalendar notOnOrAfter, String recipient, Element keyInfo) throws SAMLException {

        JAXBContext jc = null;
        jakarta.xml.bind.Unmarshaller u = null;

        //Unmarshal to JAXB KeyInfo Object and set it
        try {
            jc = SAML20JAXBUtil.getJAXBContext();
           u = jc.createUnmarshaller();
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }

//        try {
//            if ( keyInfo != null) {
//                setKeyInfo((KeyInfoType)((JAXBElement)u.unmarshal(keyInfo)).getValue());
//            }
//            if ( SubjectConfirmationDataData != null) {
//                setSubjectConfirmationDataData((SubjectConfirmationDataType)((JAXBElement)u.unmarshal(SubjectConfirmationDataData)).getValue());
//            }
//        } catch (Exception ex) {
//            // log here
//            throw new SAMLException(ex);
//        }
        setAddress(address);
        setInResponseTo(inResponseTo);
        if ( notBefore != null) {
            try {
                DatatypeFactory factory = DatatypeFactory.newInstance();
                setNotBefore(factory.newXMLGregorianCalendar(notBefore));
            }catch ( DatatypeConfigurationException ex ) {
                //ignore
            }
        }

        if ( notOnOrAfter != null) {
            try {
                DatatypeFactory factory = DatatypeFactory.newInstance();
                setNotOnOrAfter(factory.newXMLGregorianCalendar(notOnOrAfter));
            }catch ( DatatypeConfigurationException ex ) {
                //ignore
            }
        }

        setRecipient(recipient);

        try {
            if (keyInfo != null) {
                //this.getContent().add(keyInfo);
                this.getContent().add(((JAXBElement) u.unmarshal(keyInfo)).getValue());
            }
        } catch (Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
    }

    public SubjectConfirmationData(SubjectConfirmationDataType subConfDataType){
        setAddress(subConfDataType.getAddress());
        setInResponseTo(subConfDataType.getInResponseTo());
        setNotBefore(subConfDataType.getNotBefore());
        setNotOnOrAfter(subConfDataType.getNotOnOrAfter());
        setRecipient(subConfDataType.getRecipient());
    }

    @Override
    public Date getNotBeforeDate() {
        if (notBeforeDate == null) {
            if (super.getNotBefore() != null) {
                try {
                    notBeforeDate = DateUtils.stringToDate(super.getNotBefore().toString());
                } catch (ParseException ex) {
                   log.log(Level.SEVERE, LogStringsMessages.WSS_0430_SAML_GET_NOT_BEFORE_DATE_OR_GET_NOT_ON_OR_AFTER_DATE_PARSE_FAILED(), ex);
                }
            }
        }
        return notBeforeDate;
    }

    @Override
    public Date getNotOnOrAfterDate() {
        if (notOnOrAfterDate == null) {
            if (super.getNotOnOrAfter() != null) {
                try {
                    notOnOrAfterDate = DateUtils.stringToDate(super.getNotOnOrAfter().toString());
                } catch (ParseException ex) {
                   log.log(Level.SEVERE,LogStringsMessages.WSS_0430_SAML_GET_NOT_BEFORE_DATE_OR_GET_NOT_ON_OR_AFTER_DATE_PARSE_FAILED(), ex);
                }
            }
        }
        return notOnOrAfterDate;
    }
}
