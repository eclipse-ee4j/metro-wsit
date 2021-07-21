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
 * $Id: EncryptedDataHeaderBlock.java,v 1.2 2010-10-21 15:37:11 snajper Exp $
 */

package com.sun.xml.wss.core;

import java.util.logging.Level;

import com.sun.xml.wss.impl.XMLUtil;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.XWSSecurityException;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;

/**
 * Corresponds to Schema definition for EncryptedData. 
 * Schema definition for EncryptedData is as follows:
 * <p>
 * <pre>{@code
 * <xmp>
 * <element name='EncryptedData' type='xenc:EncryptedDataType'/>
 * <complexType name='EncryptedDataType'>
 *     <complexContent>
 *         <extension base='xenc:EncryptedType'/>
 *     </complexContent>
 * </complexType>
 * </xmp>
 *}</pre>
 * @author Vishal Mahajan
 */
public class EncryptedDataHeaderBlock extends EncryptedTypeHeaderBlock {

            
    /**
     * Create an empty EncryptedData element.
     *
     * @throws XWSSecurityException
     *     If there is problem creating an EncryptedData element.
     */
    public EncryptedDataHeaderBlock() throws XWSSecurityException {
        try {
            setSOAPElement(
                getSoapFactory().createElement(
                    MessageConstants.ENCRYPTED_DATA_LNAME,
                    MessageConstants.XENC_PREFIX,
                    MessageConstants.XENC_NS));
            addNamespaceDeclaration(
                MessageConstants.XENC_PREFIX,
                MessageConstants.XENC_NS);
        } catch (SOAPException e) {
            log.log(Level.SEVERE, "WSS0345.error.creating.edhb", e.getMessage());
            throw new XWSSecurityException(e);
        }
    }
    
    

    /**
     * @throws XWSSecurityException
     *     If there is problem in initializing EncryptedData element.
     */
    public EncryptedDataHeaderBlock(SOAPElement element)
        throws XWSSecurityException {

        setSOAPElement(element);

        if (!(element.getLocalName().equals(
                  MessageConstants.ENCRYPTED_DATA_LNAME) &&
              XMLUtil.inEncryptionNS(element))) {
            log.log(Level.SEVERE, "WSS0346.error.creating.edhb", element.getTagName());  
            throw new XWSSecurityException("Invalid EncryptedData passed");
        }
        initializeEncryptedType(element);
    }

    public static SecurityHeaderBlock fromSoapElement(SOAPElement element)
        throws XWSSecurityException {
        return SecurityHeaderBlockImpl.fromSoapElement(
            element, EncryptedDataHeaderBlock.class);
    }

    public SOAPElement getAsSoapElement() throws XWSSecurityException {
        if (updateRequired) {
            removeContents();
            try {
                addTextNode("\n    ");
                if (encryptionMethod != null) {
                    addChildElement(encryptionMethod);
                    addTextNode("\n    ");
                }
                if (keyInfo != null) {
                    addChildElement(keyInfo.getAsSoapElement());
                    addTextNode("\n    ");
                }
                if (cipherData == null) {
                    log.log(Level.SEVERE, 
                            "WSS0347.missing.cipher.data");
                    throw new XWSSecurityException(
                        "CipherData is not present inside EncryptedType");
                }
                addChildElement(cipherData);
                addTextNode("\n    ");
                if (encryptionProperties != null) {
                    addChildElement(encryptionProperties);
                    addTextNode("\n    ");
                }
            } catch (SOAPException e) {
                log.log(Level.SEVERE, "WSS0345.error.creating.edhb", e.getMessage());
                throw new XWSSecurityException(e);
            }
        }
        return super.getAsSoapElement();
    }  
        
}
