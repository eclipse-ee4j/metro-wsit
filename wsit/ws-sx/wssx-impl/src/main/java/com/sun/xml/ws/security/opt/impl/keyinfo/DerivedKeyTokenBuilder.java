/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.api.reference.DirectReference;
import com.sun.xml.ws.security.DerivedKeyToken;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.SecurityContextTokenInfo;
import com.sun.xml.ws.security.impl.DerivedKeyTokenImpl;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import com.sun.xml.wss.impl.AlgorithmSuite;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.mls.DerivedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.IssuedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SecureConversationTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SymmetricKeyBinding;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy.UsernameTokenBinding;
import java.security.Key;
import java.util.logging.Level;
import com.sun.xml.wss.logging.impl.opt.token.LogStringsMessages;

import javax.crypto.SecretKey;
import jakarta.xml.bind.JAXBElement;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class DerivedKeyTokenBuilder extends TokenBuilder {
    
    private DerivedTokenKeyBinding dtk = null;
    /** Creates a new instance of DerivedKeyTokenBuilder */
    public DerivedKeyTokenBuilder(JAXBFilterProcessingContext context,DerivedTokenKeyBinding dtk) {
        super(context);
        this.dtk = dtk;
    }
    /**
     * identifies the suitable key binding and obtains the keys from them
     * calculates the derived key and sets it in security header
     * reruens a BuilderResult with all token details set in it
     * @return BuilderResult
     * @throws com.sun.xml.wss.XWSSecurityException
     */
    public BuilderResult process() throws XWSSecurityException {
        
        String algorithm = null;
        WSSPolicy originalKeyBinding = dtk.getOriginalKeyBinding();
        AlgorithmSuite algSuite = context.getAlgorithmSuite();
        BuilderResult dktResult = new BuilderResult();
                
        if(algSuite != null){
            algorithm = algSuite.getEncryptionAlgorithm();
            if(logger.isLoggable(Level.FINEST)){
                logger.log(Level.FINEST, "Algorithm used for Derived Keys: "+algorithm);
            }
        } else{
            throw new XWSSecurityException("Internal Error: Algorithm Suite is not set in context");
        }
        //The offset and length to be used for DKT
        long offset = 0; // Default 0
        long length = SecurityUtil.getLengthFromAlgorithm(algorithm);
        
        WSSPolicy policy = (WSSPolicy)context.getSecurityPolicy();
        if(length == 32 && PolicyTypeUtil.signaturePolicy(policy)){
            length = 24;
        }
        String dpTokenID = "";
        byte[] secret =null;
        BuilderResult result = null;
        // findbugs :correctness error, will lead to NPE if result was accessed later.
        /*if (PolicyTypeUtil.x509CertificateBinding(originalKeyBinding)) {
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1805_DERIVEDKEYS_WITH_ASYMMETRICBINDING_UNSUPPORTED());
            //throw new XWSSecurityException("Asymmetric Binding with DerivedKeys under X509Token Policy Not Yet Supported");
        } else*/
        UsernameTokenBinding utb;
        if(PolicyTypeUtil.usernameTokenBinding(originalKeyBinding)){
          if ( context.getusernameTokenBinding() != null) {
                utb = context.getusernameTokenBinding();
                context.setUsernameTokenBinding(null);
            } else{
                throw new XWSSecurityException("Internal Error: UsernameToken Binding not set on context");
            }
           UsernameTokenBuilder br = new UsernameTokenBuilder(context,utb);
           result = br.process();
           SecretKey key  = utb.getSecretKey();
           if(key == null){
               throw new XWSSecurityException("Key obtained from the username token binding is null");
           }
           byte[] tempSecret = key.getEncoded();
           secret = new byte[16];
           for(int i =0;i<=15;i++){
               secret[i] = tempSecret[i];
           }
           
        } else if ( PolicyTypeUtil.symmetricKeyBinding(originalKeyBinding)) {
            //SymmetricKeyBinding skb = (SymmetricKeyBinding)originalKeyBinding;
            SymmetricKeyBinding skb = null;
            if ( context.getSymmetricKeyBinding() != null) {
                skb = context.getSymmetricKeyBinding();
                context.setSymmetricKeyBinding(null);
            } else{
                throw new XWSSecurityException("Internal Error: SymmetricBinding not set on context");
            }
            String dataEncAlgo = SecurityUtil.getDataEncryptionAlgo(context);
            
            String keyAlgo = skb.getKeyAlgorithm();
            if(keyAlgo == null || "".equals(keyAlgo)){
                if(context.getAlgorithmSuite() != null)
                    keyAlgo = context.getAlgorithmSuite().getAsymmetricKeyAlgorithm();
            }
            SymmetricTokenBuilder stb = new SymmetricTokenBuilder(skb,context,dataEncAlgo,keyAlgo);
            result = stb.process();
            Key originalKey = result.getDataProtectionKey();
            secret = originalKey.getEncoded();
            if(logger.isLoggable(Level.FINEST)){
                logger.log(Level.FINEST, "SymmetricBinding under Derived Keys");
                logger.log(Level.FINEST, "DataEncryption Algorithm:"+dataEncAlgo);
                logger.log(Level.FINEST, "Key Algorithm:"+keyAlgo);
            }
        } else if (PolicyTypeUtil.secureConversationTokenKeyBinding(originalKeyBinding)) {
            SecureConversationTokenKeyBinding skb = (SecureConversationTokenKeyBinding)originalKeyBinding;
            SCTBuilder builder = new SCTBuilder(context, (SecureConversationTokenKeyBinding) originalKeyBinding);
            result = builder.process();
            IssuedTokenContext ictx = context.getSecureConversationContext();
            com.sun.xml.ws.security.SecurityContextToken sct =(com.sun.xml.ws.security.SecurityContextToken)ictx.getSecurityToken();
            if(sct.getInstance() != null){
                if(context.isExpired()){
                    secret = ictx.getProofKey();
                }else{
                    SecurityContextTokenInfo sctInstanceInfo = ictx.getSecurityContextTokenInfo();
                    //secret = context.getSecureConversationContext().getProofKey();
                    if(sctInstanceInfo != null){
                        secret = sctInstanceInfo.getInstanceSecret(sct.getInstance());
                    }else {
                        secret = ictx.getProofKey();
                    }                    
                }
            }else{
                secret = ictx.getProofKey();
            }
            if(logger.isLoggable(Level.FINEST)){
                logger.log(Level.FINEST, "SecureConversation token binding under Derived Keys");
            }
        }else if( PolicyTypeUtil.issuedTokenKeyBinding(originalKeyBinding)) {
            IssuedTokenBuilder itb = new IssuedTokenBuilder(context,(IssuedTokenKeyBinding)originalKeyBinding);
            result = itb.process();
            Key originalKey = result.getDataProtectionKey();
            //ignore derived key when issuedtoken is public key
            if (context.getTrustContext().getProofKey() == null) {
                dktResult.setDataProtectionKey(originalKey);
                //keyinfo
                dktResult.setKeyInfo(result.getKeyInfo());
                return dktResult;
            }
            
            secret = originalKey.getEncoded();
            dpTokenID = result.getDPTokenId();
            
            if(logger.isLoggable(Level.FINEST)){
                logger.log(Level.FINEST, "Issued Token Binding token binding under Derived Keys");
            }
        } else{
            if(originalKeyBinding != null){
                throw new XWSSecurityException("Unsupported Key Binding:" + originalKeyBinding);
            } else{
                throw new XWSSecurityException("Internal Error: Null original key binding");
            }
        }
        
        DerivedKeyToken dkt = new DerivedKeyTokenImpl(offset, length, secret);
        Key dataKey = null;
        try{
            String jceAlgo = SecurityUtil.getSecretKeyAlgorithm(algorithm);
            dataKey = dkt.generateSymmetricKey(jceAlgo);
        } catch(Exception e){
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1806_ERROR_GENERATING_SYMMETRIC_KEY(),e);
            throw new XWSSecurityException(e);
        }
        SecurityTokenReferenceType str = null;
        Object strObj = result.getKeyInfo().getContent().get(0);
        if(strObj instanceof JAXBElement){
            str = (SecurityTokenReferenceType) ((JAXBElement)strObj).getValue();
        }else{
            str = (SecurityTokenReferenceType)strObj;
        }
        if(str instanceof SecurityTokenReference){
           str = elementFactory.createSecurityTokenReference(((SecurityTokenReference)str).getReference());
        }
        DerivedKey dk = null;
        if(dpTokenID.length() == 0){
            dk = elementFactory.createDerivedKey(dtk.getUUID(),algorithm,dkt.getNonce(),dkt.getOffset(),dkt.getLength(),dkt.getLabel(),str, context.getSecurityPolicyVersion());
        }else{
            dk = elementFactory.createDerivedKey(dtk.getUUID(),algorithm,dkt.getNonce(),dkt.getOffset(),dkt.getLength(),dkt.getLabel(),str,dpTokenID, context.getSecurityPolicyVersion());
        }
        DirectReference dr = elementFactory.createDirectReference();
        dr.setURI("#"+dk.getId());
        SecurityTokenReference str2 = buildSTR(context.generateID(),dr);
        context.getSecurityHeader().add(dk);
        //Construct the STR for Encryption or Signature
        buildKeyInfo(str2);
        dktResult.setKeyInfo(super.keyInfo);
        dktResult.setDataProtectionKey(dataKey);
        return dktResult;
    }
}
