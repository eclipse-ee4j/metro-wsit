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
import com.sun.xml.ws.security.opt.api.SecurityElement;
import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.api.reference.DirectReference;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.SecurityContextTokenInfo;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.mls.KeyBindingBase;
import com.sun.xml.wss.impl.policy.mls.SecureConversationTokenKeyBinding;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import java.security.Key;
import java.util.logging.Level;
import javax.crypto.spec.SecretKeySpec;
import com.sun.xml.wss.logging.impl.opt.token.LogStringsMessages;
/**
 *
 * @author K.Venugopal@sun.com
 */
public class SCTBuilder extends TokenBuilder{
    private SecureConversationTokenKeyBinding sctBinding = null;
    /** Creates a new instance of SCTBuilder */
    public SCTBuilder(JAXBFilterProcessingContext context,SecureConversationTokenKeyBinding kb) {
        super(context);
        this.sctBinding = kb;      
    }
    /**
     * 
     * @return BuilderResult
     */
    @Override
    public BuilderResult process() throws XWSSecurityException {
        BuilderResult sctResult = new BuilderResult();       
        String dataEncAlgo = SecurityUtil.getDataEncryptionAlgo(context);       
        String sctPolicyId = sctBinding.getUUID();
        //Look for SCT in TokenCache
        SecurityElement sct = context.getSecurityHeader().getChildElement(sctPolicyId);
        IssuedTokenContext ictx = context.getSecureConversationContext();
        String sctVersion = sctBinding.getIncludeToken();
        boolean includeToken = (KeyBindingBase.INCLUDE_ALWAYS.equals( sctVersion) ||
                                KeyBindingBase.INCLUDE_ALWAYS_TO_RECIPIENT.equals( sctVersion) ||
                                KeyBindingBase.INCLUDE_ALWAYS_VER2.equals( sctVersion) ||
                                KeyBindingBase.INCLUDE_ALWAYS_TO_RECIPIENT_VER2.equals( sctVersion)
                                );
        com.sun.xml.ws.security.SecurityContextToken sct1 = null;
        if (sct == null) {
            sct1 =(com.sun.xml.ws.security.SecurityContextToken)ictx.getSecurityToken();
            if (sct1 == null) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1809_SCT_NOT_FOUND());
                throw new XWSSecurityException("SecureConversation Token not Found");
            }
            sct  = context.getSecurityHeader().getChildElement(sct1.getWsuId());
            if(sct == null){
                sct1 = com.sun.xml.wss.impl.misc.SecurityUtil.getSCT(sct1, context.getSOAPVersion());
                if(includeToken){
                    if(context.getSecurityPolicyVersion().equals(MessageConstants.SECURITYPOLICY_12_NS)){
                        context.getSecurityHeader().add((SecurityContextToken13)sct1);
                    }else{
                        context.getSecurityHeader().add((SecurityContextToken)sct1);
                    }
                } 
                if(context.getSecurityPolicyVersion().equals(MessageConstants.SECURITYPOLICY_12_NS)){
                    sct = (SecurityContextToken13)sct1;
                }else{
                    sct = (SecurityContextToken)sct1;
                }                
            }
            //Add ext elements;
        }
   
        String sctWsuId = sct.getId();
        if (sctWsuId == null) {
            sct.setId(context.generateID());
            sctWsuId = sct.getId();
        }               
        Key dataProtectionKey = null;       
        DirectReference directRef = elementFactory.createDirectReference();
        if(includeToken){
            directRef.setURI("#"+sctWsuId);
        } else{
            directRef.setURI(sct1.getIdentifier().toString());  
        }       
        if (!KeyBindingBase.INCLUDE_ALWAYS_TO_RECIPIENT.equals(sctBinding.getIncludeToken()) ||
                !KeyBindingBase.INCLUDE_ALWAYS.equals(sctBinding.getIncludeToken())) {
            if(context.getSecurityPolicyVersion().equals(MessageConstants.SECURITYPOLICY_12_NS)){
                directRef.setValueType(MessageConstants.SCT_13_VALUETYPE);                
            }else{
                directRef.setValueType(MessageConstants.SCT_VALUETYPE);                
            }
        }
     
        if(sct1.getInstance() != null && !context.isExpired()){
            ((com.sun.xml.ws.security.opt.impl.reference.DirectReference)directRef).setAttribute(
                    context.getWSSCVersion(context.getSecurityPolicyVersion()), "Instance", sct1.getInstance());
        }   
        byte[] proofKey = null;    
        if(sct1.getInstance() != null){
            if(context.isExpired()){
                proofKey = ictx.getProofKey();
            }else{
               if(ictx.getSecurityContextTokenInfo() != null){
                SecurityContextTokenInfo sctInstanceInfo = ictx.getSecurityContextTokenInfo();
                 proofKey = sctInstanceInfo.getInstanceSecret(sct1.getInstance());
               } else {
                   proofKey = ictx.getProofKey();
               }
            }
        }else{
            proofKey = ictx.getProofKey();
        }
        String jceAlgo = SecurityUtil.getSecretKeyAlgorithm(dataEncAlgo);
        //dataProtectionKey = new SecretKeySpec(ictx.getProofKey(), jceAlgo);
        dataProtectionKey = new SecretKeySpec(proofKey, jceAlgo);
        buildKeyInfo(directRef,context.generateID());
        sctResult.setKeyInfo(super.keyInfo);
        sctResult.setDataProtectionKey(dataProtectionKey);
        return sctResult;
    }
}
