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
 * $Id: KeyInfoStrategy.java,v 1.2 2010-10-21 15:37:29 snajper Exp $
 */

package com.sun.xml.wss.impl.keyinfo;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.XWSSecurityException;

import com.sun.xml.wss.core.SecurityTokenReference;
import com.sun.xml.wss.core.KeyInfoHeaderBlock;

import java.security.cert.X509Certificate;

/**
 * The interface for different KeyInfo Schemes
 * @author XWS Security team
 * @author K.Venugopal@sun.com
 */
public abstract class KeyInfoStrategy {

    public static KeyInfoStrategy getInstance(String strategy) {
        //TODO: For now.
        if(MessageConstants.KEY_INDETIFIER_TYPE == strategy || MessageConstants.KEY_INDETIFIER_TYPE.equals(strategy)){
            return new KeyIdentifierStrategy();
        }else if(MessageConstants.THUMB_PRINT_TYPE == strategy || MessageConstants.THUMB_PRINT_TYPE.equals(strategy)){
            return new KeyIdentifierStrategy(KeyIdentifierStrategy.THUMBPRINT);
        }else if(MessageConstants.EK_SHA1_TYPE == strategy || MessageConstants.EK_SHA1_TYPE.equals(strategy)){
            return new KeyIdentifierStrategy(KeyIdentifierStrategy.ENCRYPTEDKEYSHA1);
        }else if(MessageConstants.KEY_NAME_TYPE == strategy || MessageConstants.KEY_NAME_TYPE.equals(strategy)){
            return new KeyNameStrategy();
        }else if(MessageConstants.DIRECT_REFERENCE_TYPE == strategy || MessageConstants.DIRECT_REFERENCE_TYPE.equals(strategy)){
            return new DirectReferenceStrategy();
        }else if(MessageConstants.X509_ISSUER_TYPE == strategy || MessageConstants.X509_ISSUER_TYPE.equals(strategy)){
            return new X509IssuerSerialStrategy();
        }else if (MessageConstants.BINARY_SECRET == strategy || MessageConstants.BINARY_SECRET.equals(strategy)) {
            return new BinarySecretStrategy();
        }
        return null;
    }

    /**
     * insert the Key Information into a ds:KeyInfo using the
     * appropriate scheme
     *
     * @param keyInfo
     *    the KeyInfo block into which the Key Information has to be inserted.
     * @param secureMsg the SecurableSoapMessage
     * @param x509TokenId value of the &lt;xwss:X509Token&gt;/@id in config file
     * @throws XWSSecurityException
     *     if there was a problem in inserting the key information
     */
    public abstract void insertKey(KeyInfoHeaderBlock keyInfo,SecurableSoapMessage secureMsg,
    String x509TokenId)throws XWSSecurityException;

    /**
     * insert the Key Information into a SecurityTokenReference using the
     * appropriate scheme
     *
     * @param tokenRef
     *    the SecurityTokenReference into which the Key Information
     *    has to be inserted.
     * @param secureMsg the SecurableSoapMessage
     * @throws XWSSecurityException
     *     if there was a problem in inserting the key information
     */
    public abstract void insertKey(SecurityTokenReference tokenRef, SecurableSoapMessage secureMsg)
    throws XWSSecurityException;

    /**
     * Sets the certificate corresponding to the security operation
     */
    public abstract void setCertificate(X509Certificate cert);

    public abstract String getAlias();
}
