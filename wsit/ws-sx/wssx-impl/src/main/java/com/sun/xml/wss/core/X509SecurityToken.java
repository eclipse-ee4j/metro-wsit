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
 * $Id: X509SecurityToken.java,v 1.2 2010-10-21 15:37:12 snajper Exp $
 */

package com.sun.xml.wss.core;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.SOAPElement;

import org.w3c.dom.Document;

import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurityTokenException;
import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;

import com.sun.xml.ws.security.Token;

/**
 * An  X509 v3 certificate BinarySecurityToken.
 *
 * @author Manveen Kaur
 * @author Edwin Goei
 */
public class X509SecurityToken extends BinarySecurityToken implements Token {

    private static Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    private X509Certificate cert;

    public X509SecurityToken(
        Document document,
        X509Certificate cert,
        String wsuId, String valueType)
        throws SecurityTokenException {

        super(document, wsuId, valueType);
        this.cert = cert;
        //checkCertVersion();
    }

    public X509SecurityToken(Document document, X509Certificate cert)
        throws SecurityTokenException {
        super(document, null, MessageConstants.X509v3_NS);
        this.cert = cert;
        //checkCertVersion();
    }

     public X509SecurityToken(Document document, X509Certificate cert, String valueType) throws SecurityTokenException {
            super(document, null, valueType);

        this.cert = cert;
        //checkCertVersion();
    }

    public X509SecurityToken(SOAPElement tokenElement, boolean isBSP)
        throws XWSSecurityException {
        super(tokenElement, isBSP);
        if (!(tokenElement.getLocalName().equals(
                  MessageConstants.WSSE_BINARY_SECURITY_TOKEN_LNAME) &&
              XMLUtil.inWsseNS(tokenElement))) {
            log.log(Level.SEVERE, "WSS0391.error.creating.X509SecurityToken", tokenElement.getTagName());
            throw new XWSSecurityException(
                "BinarySecurityToken expected, found " +
                tokenElement.getTagName());
        }
    }

   public X509SecurityToken(SOAPElement tokenElement) throws XWSSecurityException {
        this(tokenElement, false);
    }

    public X509Certificate getCertificate() throws XWSSecurityException {

        if (cert == null) {

            byte[] data;
            String encodedData = XMLUtil.getFullTextFromChildren(this);
            try {
                data = Base64.decode(encodedData);
            } catch (Base64DecodingException bde) {
                log.log(Level.SEVERE, "WSS0301.unableto.decode.data");
                throw new SecurityTokenException("Unable to decode data", bde);
            }
            try {
                CertificateFactory certFact = CertificateFactory.getInstance("X.509");
                cert = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(data));
            } catch (Exception e) {
                log.log(Level.SEVERE, "WSS0302.unableto.create.x509cert");
                throw new XWSSecurityException(
                    "Unable to create X509Certificate from data");
            }
        }
        //checkCertVersion();
        return cert;
    }

    public static SecurityHeaderBlock fromSoapElement(SOAPElement element)
        throws XWSSecurityException {
        return SecurityHeaderBlockImpl.fromSoapElement(
            element, X509SecurityToken.class);
    }

    @Override
    public String getTextValue() throws XWSSecurityException {

        if (encodedText == null) {
            byte[] rawBytes;
            try {
                rawBytes = cert.getEncoded();
                setRawValue(rawBytes);
            } catch (CertificateEncodingException e) {
                log.log(
                    Level.SEVERE,"WSS0303.unableto.get.encoded.x509cert");
                throw new XWSSecurityException (
                    "Unable to get encoded representation of X509Certificate",
                    e);
            }
        }
        return encodedText;
    }

    private void checkCertVersion() throws SecurityTokenException {
        if (cert.getVersion() != 3||cert.getVersion() !=1) {
            log.log(Level.SEVERE,
                    "WSS0392.invalid.X509cert.version",
                    Integer.toString(cert.getVersion()));
            throw new SecurityTokenException(
                "Expected Version 1 or 3 Certificate, found Version " +
                cert.getVersion());
        }
    }

    // Token interface methods
    @Override
    public String getType() {
        return MessageConstants.X509_TOKEN_NS;
    }

    @Override
    public Object getTokenValue() {
        try {
            return getCertificate();
        } catch (XWSSecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
}
