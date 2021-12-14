/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import com.sun.xml.ws.security.opt.api.SecurityHeaderElement;
import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.ws.security.opt.impl.message.GSHeaderElement;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.mls.IssuedTokenKeyBinding;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.crypto.spec.SecretKeySpec;

import com.sun.xml.wss.impl.policy.mls.KeyBindingBase;
import jakarta.xml.bind.JAXBElement;
import org.w3c.dom.Element;
import com.sun.xml.wss.logging.impl.opt.token.LogStringsMessages;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class IssuedTokenBuilder extends TokenBuilder {
    private IssuedTokenKeyBinding ikb = null;
    /** Creates a new instance of IssuedTokenBuilder */
    public IssuedTokenBuilder(JAXBFilterProcessingContext context,IssuedTokenKeyBinding kb) {
        super(context);
        this.ikb = kb;
    }
    /**
     *
     * @return BuilderResult
     */
    @Override
    @SuppressWarnings("unchecked")
    public BuilderResult process() throws XWSSecurityException {
        BuilderResult itkbResult = new BuilderResult();
        byte[] proofKey = context.getTrustContext().getProofKey();
        Key dataProtectionKey = null;
        SecurityTokenReferenceType str = null;
        Key cacheKey = null;        
        //For Encryption proofKey will be null.
        if (proofKey == null) {
             KeyPair keyPair = context.getTrustContext().getProofKeyPair();
             if (keyPair == null){
                X509Certificate cert =
                        context.getTrustContext().getRequestorCertificate();
                if (cert == null){
                    logger.log(Level.SEVERE, LogStringsMessages.WSS_1823_KEY_PAIR_PROOF_KEY_NULL_ISSUEDTOKEN());
                    throw new XWSSecurityException(
                        "Proof Key and RSA KeyPair for Supporting token (KeyValueToken or RsaToken) are both null for Issued Token");
                }else{
                    dataProtectionKey = context.getSecurityEnvironment().getPrivateKey(context.getExtraneousProperties(), cert);
                    cacheKey = cert.getPublicKey();
                }
            }else{
                dataProtectionKey = keyPair.getPrivate();
                cacheKey = keyPair.getPublic();
            }
        }else{
            String secretKeyAlg = "AES";
            if (context.getAlgorithmSuite() != null) {
                secretKeyAlg = SecurityUtil.getSecretKeyAlgorithm(context.getAlgorithmSuite().getEncryptionAlgorithm());
            }
            //TODO: assuming proofkey is a byte array in case of Trust as well
            dataProtectionKey = new SecretKeySpec(proofKey, secretKeyAlg);
            cacheKey = dataProtectionKey;
            //SecurityUtil.updateSamlVsKeyCache(str, context, dataProtectionKey);
        }
        
        SecurityHeaderElement issuedTokenElement = null;
        GenericToken issuedToken = (GenericToken)context.getTrustContext().getSecurityToken();
        if(issuedToken != null){
            issuedTokenElement = issuedToken.getElement();
            if(issuedTokenElement == null){
                Element element = (Element)issuedToken.getTokenValue();
                issuedTokenElement = new GSHeaderElement(element);
                issuedTokenElement.setId(issuedToken.getId());
                itkbResult.setDPTokenId(issuedToken.getId());
            }
            String tokId = issuedTokenElement.getId();
            if ("".equals(tokId) &&  MessageConstants.ENCRYPTED_DATA_LNAME.equals(issuedTokenElement.getLocalPart())) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1808_ID_NOTSET_ENCRYPTED_ISSUEDTOKEN());
                throw new XWSSecurityException("ID attribute not set");
            }
            context.getTokenCache().put(ikb.getUUID(), issuedTokenElement);
            
            HashMap sentSamlKeys = (HashMap) context.getExtraneousProperty(MessageConstants.STORED_SAML_KEYS);
            if(sentSamlKeys == null){
                sentSamlKeys = new HashMap();
            }
            sentSamlKeys.put(tokId, dataProtectionKey);
            context.setExtraneousProperty(MessageConstants.STORED_SAML_KEYS, sentSamlKeys);
        }
        String itType = ikb.getIncludeToken();
        boolean includeToken = (KeyBindingBase.INCLUDE_ALWAYS.equals(itType) ||
                                KeyBindingBase.INCLUDE_ALWAYS_TO_RECIPIENT.equals(itType) ||
                                KeyBindingBase.INCLUDE_ALWAYS_VER2.equals(itType) ||
                                KeyBindingBase.INCLUDE_ALWAYS_TO_RECIPIENT_VER2.equals(itType)
                                );
        
        if (includeToken) {
            str = (SecurityTokenReferenceType)context.getTrustContext().
                    getAttachedSecurityTokenReference();
        }else{
            str = (SecurityTokenReferenceType)context.getTrustContext().
                    getUnAttachedSecurityTokenReference();
        }
        
        if (issuedToken != null && includeToken) {
            if( context.getSecurityHeader().getChildElement(issuedTokenElement.getId()) == null){
                context.getSecurityHeader().add(issuedTokenElement);
            }
        }

        ((NamespaceContextEx)context.getNamespaceContext()).addWSS11NS();
        keyInfo = new KeyInfo();
        JAXBElement je = new com.sun.xml.ws.security.secext10.ObjectFactory().createSecurityTokenReference(str);
        List strList = Collections.singletonList(je);
        keyInfo.setContent(strList);
        if(str != null)
            SecurityUtil.updateSamlVsKeyCache(str, context, cacheKey);
        itkbResult.setDataProtectionKey(dataProtectionKey);
        itkbResult.setKeyInfo(keyInfo);
        return itkbResult;
    }
}
