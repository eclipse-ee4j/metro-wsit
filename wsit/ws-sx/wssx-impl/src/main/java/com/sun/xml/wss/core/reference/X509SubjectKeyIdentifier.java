/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: X509SubjectKeyIdentifier.java,v 1.2 2010-10-21 15:37:14 snajper Exp $
 */

package com.sun.xml.wss.core.reference;

import java.security.cert.X509Certificate;
import java.util.logging.Level;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Document;

import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurityHeaderException;
import com.sun.xml.wss.XWSSecurityException;
import java.io.IOException;


/**
 * @author Vishal Mahajan
 * @author Manveen Kaur
 */
public class X509SubjectKeyIdentifier extends KeyIdentifier {

    /** Defaults */
    private String encodingType = MessageConstants.BASE64_ENCODING_NS;

    private String valueType = MessageConstants.X509SubjectKeyIdentifier_NS;
    
    private X509Certificate cert = null;

    /**
     * Creates an "empty" KeyIdentifier element with default encoding type
     * and default value type.
     */
    public X509SubjectKeyIdentifier(Document doc) throws XWSSecurityException {
        super(doc);
        // Set default attributes
        setAttribute("EncodingType", encodingType);
        setAttribute("ValueType", valueType);
    }

    public X509SubjectKeyIdentifier(SOAPElement element) 
        throws XWSSecurityException {
        super(element);
    }


    public byte[] getDecodedBase64EncodedValue() throws XWSSecurityException {
        try {
            return Base64.decode(getReferenceValue());
        } catch (Base64DecodingException e) {
            log.log(Level.SEVERE, "WSS0144.unableto.decode.base64.data",
                new Object[] {e.getMessage()});
            throw new SecurityHeaderException(
                "Unable to decode Base64 encoded data",
                e);
        }
    }

    /**
     * @return the SubjectKeyIdentifier from cert or null if cert does not
     *         contain one
     */
    public static byte[] getSubjectKeyIdentifier(X509Certificate cert) 
       throws XWSSecurityException {
        KeyIdentifierSPI spi = KeyIdentifierSPI.getInstance();
        if (spi != null) {
            try {
                return spi.getSubjectKeyIdentifier(cert);
            } catch (KeyIdentifierSPI.KeyIdentifierSPIException ex) {
                throw new XWSSecurityException(ex);
            }
        }
        //todo : log here
        throw new XWSSecurityException("Could not locate SPI class for KeyIdentifierSPI");
    }
    
    public void setCertificate(X509Certificate cert){
        this.cert = cert;
    }
 
    public X509Certificate getCertificate(){
        return cert;
    }
} 
