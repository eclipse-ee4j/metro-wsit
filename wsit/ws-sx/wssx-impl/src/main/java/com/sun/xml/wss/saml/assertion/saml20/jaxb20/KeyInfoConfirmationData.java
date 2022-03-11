/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.security.core.dsig.KeyInfoType;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import com.sun.xml.wss.saml.util.SAMLJAXBUtil;
import com.sun.xml.wss.util.DateUtils;
import java.security.PublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import org.w3c.dom.Element;

/**
 *
 * @author root
 */
public class KeyInfoConfirmationData extends com.sun.xml.wss.saml.internal.saml20.jaxb20.KeyInfoConfirmationDataType
        implements com.sun.xml.wss.saml.KeyInfoConfirmationData {

    protected PublicKey keyInfoKeyValue = null;
   // public static KeyInfoType keyInfo = null;

    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
     * Constructs a KeyInfoConfirmationData element from an existing
     * XML block.
     *
     * @param element a DOM Element representing the
     *        <code>KeyInfoConfirmationData</code> object.
     */
    public static KeyInfoConfirmationData fromElement(org.w3c.dom.Element element)
    throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();

            jakarta.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (KeyInfoConfirmationData)u.unmarshal(element);
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

    public KeyInfoConfirmationData(Element keyInfo) throws SAMLException {

        JAXBContext jc = null;
        jakarta.xml.bind.Unmarshaller u = null;


        //Unmarshal to JAXB KeyInfo Object and set it
        try {
            jc = SAMLJAXBUtil.getJAXBContext();
            u = jc.createUnmarshaller();
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }

        try {
            if ( keyInfo != null) {
                this.setKeyInfo(((KeyInfoType)((JAXBElement)u.unmarshal(keyInfo)).getValue()));
            }
        } catch (Exception ex) {
            // log here
            throw new SAMLException(ex);
        }
    }

    public void setKeyInfo(KeyInfoType value) {
        //this.keyInfo = value;
         this.getContent().add(value);
    }

    @Override
    public Date getNotBeforeDate() {
        if(super.getNotBefore() != null){
            Date getNotBeforeDate = null;
            try {
                getNotBeforeDate = DateUtils.stringToDate(super.getNotBefore().toString());
            } catch (ParseException ex) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0430_SAML_GET_NOT_BEFORE_DATE_OR_GET_NOT_ON_OR_AFTER_DATE_PARSE_FAILED(), ex);
            }
            return getNotBeforeDate;
        }
        return null;
    }

    @Override
    public Date getNotOnOrAfterDate() {
        if(super.getNotBefore() != null){
            Date getNotBeforeDate = null;
            try {
                getNotBeforeDate = DateUtils.stringToDate(super.getNotOnOrAfter().toString());
            } catch (ParseException ex) {
                log.log(Level.SEVERE,LogStringsMessages.WSS_0430_SAML_GET_NOT_BEFORE_DATE_OR_GET_NOT_ON_OR_AFTER_DATE_PARSE_FAILED(), ex);
            }
            return getNotBeforeDate;
        }
        return null;
    }
}
