/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.core;

import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import com.sun.xml.ws.security.Token;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;
import java.security.Key;
import javax.crypto.SecretKey;
import jakarta.xml.soap.SOAPElement;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.Iterator;
import javax.xml.namespace.QName;

/**
 *
 * @author root
 */

public class EncryptedKeyToken extends SecurityHeaderBlockImpl implements SecurityToken, Token {

    EncryptedKey encryptedKey = null;
    SOAPElement elem = null;
    /** Creates a new instance of EncryptedKeyToken */
    public EncryptedKeyToken(SOAPElement elem) {
        this.elem = elem;
    }

    public Key getSecretKey(Key privKey, String dataEncAlgo) throws XWSSecurityException {
        try {
            XMLCipher xmlc = null;
            String algorithm = null;
            if(elem != null){
                NodeList nl = elem.getElementsByTagNameNS(MessageConstants.XENC_NS, "EncryptionMethod");
                if (nl != null)
                    algorithm = ((Element)nl.item(0)).getAttribute("Algorithm");
                xmlc = XMLCipher.getInstance(algorithm);
                xmlc.init(XMLCipher.UNWRAP_MODE, privKey);
                if ( encryptedKey == null)
                    encryptedKey = xmlc.loadEncryptedKey(elem);
            }
            if (xmlc == null){
                throw new XWSSecurityException("XMLCipher is null while getting SecretKey from EncryptedKey");
            }
            return (SecretKey) xmlc.decryptKey(encryptedKey, dataEncAlgo);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new XWSSecurityException("Error while getting SecretKey from EncryptedKey");
        }
    }

    @Override
    public SOAPElement getAsSoapElement() {
        //throw new UnsupportedOperationException("Not supported");
        if(elem != null)
            return elem;
        else
           throw new UnsupportedOperationException("Not supported");
    }


    @Override
    public String getId(){
        try {
            return elem.getAttribute("Id");
        } catch (Exception ex) {
            throw new RuntimeException("Error while extracting ID");
        }
    }

    public KeyInfoHeaderBlock getKeyInfo() {
         try {
            if (encryptedKey != null) {
                return  new KeyInfoHeaderBlock(encryptedKey.getKeyInfo());
            } else{
                Iterator iter = elem.getChildElements(new QName(MessageConstants.DSIG_NS,"KeyInfo"));
                Element keyInfoElem = null;
                if(iter.hasNext()){
                    keyInfoElem = (Element)iter.next();
                }
                KeyInfo keyInfo = new KeyInfo(keyInfoElem, "MessageConstants.DSIG_NS");
                return new KeyInfoHeaderBlock(keyInfo);
            }
        } catch (XWSSecurityException | XMLSecurityException ex) {
            throw new XWSSecurityRuntimeException("Error while extracting KeyInfo", ex);
        }
    }

     @Override
     public String getType() {
        return MessageConstants.XENC_ENCRYPTED_KEY_QNAME;
    }

    @Override
    public Object getTokenValue() {
        return this;
    }
}
