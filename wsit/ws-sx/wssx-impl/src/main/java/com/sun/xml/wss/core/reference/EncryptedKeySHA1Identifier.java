/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.core.reference;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.SecurityHeaderException;

import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.wss.impl.misc.Base64;

import jakarta.xml.soap.SOAPElement;

import org.w3c.dom.Document;

import java.util.logging.Level;

/**
 *
 * @author Ashutosh Shahi
 */
public class EncryptedKeySHA1Identifier extends KeyIdentifier{
    
    /**Defaults*/
    private String encodingType = MessageConstants.BASE64_ENCODING_NS;
    
    private String valueType = MessageConstants.EncryptedKeyIdentifier_NS;
    
    /**
     * Creates an "empty" KeyIdentifier element with default encoding type
     * and default value type.
     */
    public EncryptedKeySHA1Identifier(Document doc) throws XWSSecurityException {
        super(doc);
        // Set default attributes
        setAttribute("EncodingType", encodingType);
        setAttribute("ValueType", valueType);
    }
    
    public EncryptedKeySHA1Identifier(SOAPElement element) 
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
}
