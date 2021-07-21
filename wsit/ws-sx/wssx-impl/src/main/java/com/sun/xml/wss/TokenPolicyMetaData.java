/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss;


import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.KeyBindingBase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Provides Meta Data about the token Policy.
 * Can be used to obtain WS-SecurityPolicy related Meta-Data associated with the Token.
 * The meta-data is generally used to disambiguate the exact action to be performed inside 
 * a specific callback or validator. For example the Policy Meta-Data can be used to decide
 * what certificate/username to return.
 */
public class TokenPolicyMetaData {

    public static final String TOKEN_POLICY = "token.policy";
    private AuthenticationTokenPolicy tokenPolicy = null;

    /**
     *
     * @param runtimeProperties the runtime Properties of an XWSS CallbackHandler
     */
    public TokenPolicyMetaData(Map runtimeProperties) {
        this.tokenPolicy = (AuthenticationTokenPolicy) runtimeProperties.get(TOKEN_POLICY);
    }

    /**
     * @return &lt;sp:Issuer&gt;wsa:EndpointReferenceType&lt;/sp:Issuer&gt;, null if not specified policy 
     */
    public String getIssuer() {
        if (tokenPolicy == null) {
            return null;
        }
        KeyBindingBase kb = (KeyBindingBase) tokenPolicy.getFeatureBinding();
        return kb.getIssuer();
    }

    /**
     * @return &lt;wst:Claims Dialect="..."&gt; ... &lt;/wst:Claims&gt;, null if not specified in policy
     */
    public Element getClaims() throws XWSSecurityException{

        if (tokenPolicy == null) {
            return null;
        }
        KeyBindingBase kb = (KeyBindingBase) tokenPolicy.getFeatureBinding();
        Element claimsElement = null;
        byte[] claimBytes = kb.getClaims();
        if (claimBytes != null) {
            try {
                DocumentBuilderFactory dbf = WSITXMLFactory.createDocumentBuilderFactory(WSITXMLFactory.DISABLE_SECURE_PROCESSING);
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new ByteArrayInputStream(claimBytes));
                claimsElement = (Element) doc.getElementsByTagNameNS("*", "Claims").item(0);
            } catch (SAXException ex) {
                Logger.getLogger(TokenPolicyMetaData.class.getName()).log(Level.SEVERE, null, ex);
                throw new XWSSecurityException(ex);
            } catch (IOException ex) {
                Logger.getLogger(TokenPolicyMetaData.class.getName()).log(Level.SEVERE, null, ex);
                throw new XWSSecurityException(ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(TokenPolicyMetaData.class.getName()).log(Level.SEVERE, null, ex);
                throw new XWSSecurityException(ex);
            }
        }
        return claimsElement;
    }
}
