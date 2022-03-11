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
 * $Id: DirectReferenceStrategy.java,v 1.2 2010-10-21 15:37:29 snajper Exp $
 */

package com.sun.xml.wss.impl.keyinfo;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.XWSSecurityException;

import java.security.cert.X509Certificate;

//import com.sun.xml.wss.impl.filter.FilterParameterConstants;
import com.sun.xml.wss.core.reference.DirectReference;
import com.sun.xml.wss.core.KeyInfoHeaderBlock;
import com.sun.xml.wss.core.SecurityTokenReference;
import com.sun.xml.wss.logging.LogStringsMessages;

public class DirectReferenceStrategy extends KeyInfoStrategy {

    X509Certificate cert = null;

    String alias = null;
    boolean forSigning;

    String samlAssertionId = null;

    protected static final Logger log =
        Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    public DirectReferenceStrategy(){

    }
    public DirectReferenceStrategy(String samlAssertionId) {
        this.samlAssertionId = samlAssertionId;
        this.cert = null;
        this.alias = null;
        this.forSigning = false;
    }

    public DirectReferenceStrategy(String alias, boolean forSigning) {
        this.alias = alias;
        this.forSigning = forSigning;
        this.samlAssertionId = null;
        this.cert = null;
    }

    @Override
    public void insertKey(
        SecurityTokenReference tokenRef, SecurableSoapMessage secureMsg)
        throws XWSSecurityException {
        DirectReference ref = getDirectReference(secureMsg, null, null);
        tokenRef.setReference(ref);
    }


    @Override
    public void insertKey(
        KeyInfoHeaderBlock keyInfo,
        SecurableSoapMessage secureMsg,
        String x509TokenId)
        throws XWSSecurityException {

        Document ownerDoc = keyInfo.getOwnerDocument();
        SecurityTokenReference tokenRef = new SecurityTokenReference(ownerDoc);
        DirectReference ref = getDirectReference(secureMsg, x509TokenId, null);
        tokenRef.setReference(ref);
        keyInfo.addSecurityTokenReference(tokenRef);
    }

    public void insertKey(
         KeyInfoHeaderBlock keyInfo,
         SecurableSoapMessage secureMsg,
     String x509TokenId, String valueType)
     throws XWSSecurityException {

         Document ownerDoc = keyInfo.getOwnerDocument();
         SecurityTokenReference tokenRef = new SecurityTokenReference(ownerDoc);
         DirectReference ref = getDirectReference(secureMsg, x509TokenId, valueType);
         tokenRef.setReference(ref);
         keyInfo.addSecurityTokenReference(tokenRef);
        }

    @Override
    public void setCertificate(X509Certificate cert) {
        this.cert = cert;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    private DirectReference getDirectReference(
        SecurableSoapMessage secureMsg,
        String x509TokenId, String valueType)
        throws XWSSecurityException {

        DirectReference ref = new DirectReference();

        if (samlAssertionId != null) {
            String uri = "#" + samlAssertionId;
            ref.setURI(uri);
            ref.setValueType(MessageConstants.WSSE_SAML_v1_1_VALUE_TYPE);

        } else  {
            // create a certificate token
            if (cert == null) {
                log.log(
                        Level.SEVERE,
                        LogStringsMessages.WSS_0185_FILTERPARAMETER_NOT_SET( "subjectkeyidentifier"),
                        new Object[] { "subjectkeyidentifier"});
                throw new XWSSecurityException(
                        "No certificate specified and no default found.");
            }
            if(x509TokenId == null){
                throw new XWSSecurityException("WSU ID is null");
            }
            String uri = "#" + x509TokenId;
            ref.setURI(uri);
            if(valueType==null||valueType.equals("")){
                valueType = MessageConstants.X509v3_NS;
            }
            ref.setValueType(valueType);
        }
        return ref;
    }
}
