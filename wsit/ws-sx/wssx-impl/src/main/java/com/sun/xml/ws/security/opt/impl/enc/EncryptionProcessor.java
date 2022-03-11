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
 * EncryptionProcessor.java
 *
 * Created on August 1, 2006, 3:30 PM
 */

package com.sun.xml.ws.security.opt.impl.enc;

import com.sun.xml.security.core.xenc.EncryptedKeyType;
import com.sun.xml.security.core.xenc.ReferenceList;
import com.sun.xml.security.core.xenc.ReferenceType;
import com.sun.xml.ws.security.opt.api.EncryptedKey;
import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import com.sun.xml.ws.security.opt.impl.util.WSSElementFactory;
import com.sun.xml.ws.security.opt.impl.message.ETHandler;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.ws.security.opt.crypto.dsig.keyinfo.KeyInfo;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.keyinfo.KeyInfoStrategy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.DerivedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.impl.policy.mls.EncryptionTarget;
import com.sun.xml.wss.impl.policy.mls.SymmetricKeyBinding;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy.FeatureBinding;
import com.sun.xml.wss.logging.impl.opt.crypto.LogStringsMessages;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBElement;

 /*
  * @author K.Venugopal@sun.com
  */
public class EncryptionProcessor {
    private static byte[] crlf = null;
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN,
            LogDomainConstants.IMPL_OPT_CRYPTO_DOMAIN_BUNDLE);
    static{
        crlf =  "\r\n".getBytes(StandardCharsets.US_ASCII);
    }
    /** Creates a new instance of EncryptionProcessor */
    public EncryptionProcessor() {
    }
    /**
     * performs encryption
     * @param context JAXBFilterProcessingContext
     */
    public void process(JAXBFilterProcessingContext context) throws XWSSecurityException{
        boolean ekRefList = false;
        String referenceType = null;
        String x509TokenId = null;
        WSSElementFactory elementFactory = new WSSElementFactory(context.getSOAPVersion());
        X509Certificate _x509Cert = null;
        KeyInfoStrategy keyInfoStrategy =  null;
        String symmetricKeyName = null;
        AuthenticationTokenPolicy.X509CertificateBinding certificateBinding = null;
        ((NamespaceContextEx)context.getNamespaceContext()).addEncryptionNS();
        ((NamespaceContextEx)context.getNamespaceContext()).addSignatureNS();
        ReferenceList dataRefList = null;
        EncryptedKeyType ekt = null;
        WSSPolicy wssPolicy = (WSSPolicy)context.getSecurityPolicy();
        EncryptionPolicy.FeatureBinding featureBinding =(EncryptionPolicy.FeatureBinding)  wssPolicy.getFeatureBinding();
        WSSPolicy keyBinding = (WSSPolicy)wssPolicy.getKeyBinding();
        EncryptedKey ek = null;
        KeyInfo edKeyInfo = null;


        if(logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST, LogStringsMessages.WSS_1952_ENCRYPTION_KEYBINDING_VALUE(keyBinding));
        }

        if(PolicyTypeUtil.derivedTokenKeyBinding(keyBinding)){
            DerivedTokenKeyBinding dtk = (DerivedTokenKeyBinding)keyBinding.clone();
            WSSPolicy originalKeyBinding = dtk.getOriginalKeyBinding();

            if (PolicyTypeUtil.x509CertificateBinding(originalKeyBinding)){
                AuthenticationTokenPolicy.X509CertificateBinding ckBindingClone =
                        (AuthenticationTokenPolicy.X509CertificateBinding)originalKeyBinding.clone();
                //create a symmetric key binding and set it as original key binding of dkt
                SymmetricKeyBinding skb = new SymmetricKeyBinding();
                skb.setKeyBinding(ckBindingClone);
                // set the x509 binding as key binding of symmetric binding
                dtk.setOriginalKeyBinding(skb);
                //keyBinding = dtk;
                EncryptionPolicy ep = (EncryptionPolicy)wssPolicy.clone();
                ep.setKeyBinding(dtk);
                context.setSecurityPolicy(ep);
                wssPolicy = ep;
            }
        }

        TokenProcessor tp = new TokenProcessor((EncryptionPolicy) wssPolicy, context);
        BuilderResult tokenInfo = tp.process();
        Key dataEncKey = null;
        Key dkEncKey = null;
        dataEncKey = tokenInfo.getDataProtectionKey();
        ek = tokenInfo.getEncryptedKey();
        ArrayList targets =  featureBinding.getTargetBindings();
        Iterator targetItr = targets.iterator();

        ETHandler edBuilder =  new ETHandler(context.getSOAPVersion());
        EncryptionPolicy.FeatureBinding  binding = (FeatureBinding) wssPolicy.getFeatureBinding();
        dataRefList = new ReferenceList();

        if(ek == null || binding.getUseStandAloneRefList()){
            edKeyInfo = tokenInfo.getKeyInfo();
        }

        boolean refAdded = false;
        while (targetItr.hasNext()) {
            EncryptionTarget target = (EncryptionTarget)targetItr.next();
            boolean contentOnly = target.getContentOnly();
            //target.getDataEncryptionAlgorithm();
            //target.getCipherReferenceTransforms();//TODO support this

            List edList = edBuilder.buildEDList( (EncryptionPolicy)wssPolicy,target ,context, dataEncKey,edKeyInfo);
            for(int i =0;i< edList.size();i++){
                JAXBElement<ReferenceType> rt = elementFactory.createDataReference((SecurityElement)edList.get(i));
                dataRefList.getDataReferenceOrKeyReference().add(rt);

                refAdded = true;
            }
        }
        if(refAdded){
            if(ek == null || (binding.getUseStandAloneRefList())){
                context.getSecurityHeader().add(elementFactory.createGSHeaderElement(dataRefList));
            }else{
                ek.setReferenceList(dataRefList);
            }
        }
    }
    /**
     *
     */
    private void checkBSP5607(String elemName, String uri, boolean contentOnly) throws XWSSecurityException {
        // BSP: 5607
        if (!contentOnly && (MessageConstants.SOAP_1_1_NS.equalsIgnoreCase(uri) || MessageConstants.SOAP_1_2_NS.equalsIgnoreCase(uri))
                && ("Header".equalsIgnoreCase(elemName) || "Envelope".equalsIgnoreCase(elemName) || "Body".equalsIgnoreCase(elemName))) {
            logger.log(Level.SEVERE,LogStringsMessages.WSS_1918_ILLEGAL_ENCRYPTION_TARGET(uri, elemName));
            throw new XWSSecurityException("Encryption of SOAP " + elemName + " is not allowed"); // BSP 5607
        }
    }
}
