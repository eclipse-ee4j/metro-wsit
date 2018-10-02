/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * TokenProcessor.java
 *
 * Created on September 8, 2006, 10:44 AM
 */

package com.sun.xml.ws.security.opt.impl.dsig;

import org.apache.xml.security.encryption.XMLCipher;
import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.ws.security.opt.api.keyinfo.TokenBuilder;
import com.sun.xml.ws.security.opt.impl.keyinfo.DerivedKeyTokenBuilder;
import com.sun.xml.ws.security.opt.impl.keyinfo.IssuedTokenBuilder;
import com.sun.xml.ws.security.opt.impl.keyinfo.KerberosTokenBuilder;
import com.sun.xml.ws.security.opt.impl.keyinfo.SCTBuilder;
import com.sun.xml.ws.security.opt.impl.keyinfo.SamlTokenBuilder;
import com.sun.xml.ws.security.opt.impl.keyinfo.SymmetricTokenBuilder;
import com.sun.xml.ws.security.opt.impl.keyinfo.X509TokenBuilder;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.IssuedTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SecureConversationTokenKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.opt.impl.keyinfo.KeyValueTokenBuilder;
import com.sun.xml.ws.security.opt.impl.keyinfo.UsernameTokenBuilder;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.mls.PrivateKeyBinding;
import com.sun.xml.wss.impl.policy.mls.SymmetricKeyBinding;
import com.sun.xml.wss.impl.policy.mls.DerivedTokenKeyBinding;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.AlgorithmSuite;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;
import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * TokenProcessor for Signature. Looks at the keyBinding and 
 * polulates BuilderResult with appropriate key and KeyInfo
 * @author Ashutosh.Shahi@sun.com
 */

public class TokenProcessor {
    
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);
    
    private Key  signingKey = null;
    //private KeyInfo siKI = null;
    private TokenBuilder builder = null;
    private WSSPolicy keyBinding = null;
    //private SignaturePolicy sp = null;
    private JAXBFilterProcessingContext context = null;
    
    /**
     * Creates a new instance of TokenProcessor
     * @param sp SignaturePolicy
     * @param context the ProcessingContext
     */
    public TokenProcessor(SignaturePolicy sp,JAXBFilterProcessingContext context) {
        //this.sp = sp;
        this.context = context;
        this.keyBinding = (WSSPolicy)sp.getKeyBinding();
    }
    
    /**
     * process the keyBinding and populate BuilderResult with appropriate key and KeyInfo
     * @return <CODE>BuilderResult</CODE> populated with appropriate values
     * @throws com.sun.xml.wss.XWSSecurityException 
     */
    public BuilderResult process()
    throws XWSSecurityException{
        
        String keyEncAlgo = XMLCipher.RSA_v1dot5;  //<--Harcoding of Algo
        String dataEncAlgo = MessageConstants.TRIPLE_DES_BLOCK_ENCRYPTION;
        
        AlgorithmSuite algSuite = context.getAlgorithmSuite();
        String tmp = null;
        if(algSuite != null){
            tmp = algSuite.getAsymmetricKeyAlgorithm();
        }
        if(tmp != null && !"".equals(tmp)){
            keyEncAlgo = tmp;
        }
        if(algSuite != null){
            tmp = algSuite.getEncryptionAlgorithm();
        }
        if(tmp != null && !"".equals(tmp)){
            dataEncAlgo = tmp;
        }
        
        if (PolicyTypeUtil.usernameTokenBinding(keyBinding)) {
            AuthenticationTokenPolicy.UsernameTokenBinding usernameTokenBinding = null;
            if ( context.getusernameTokenBinding() != null ) {
                usernameTokenBinding  = context.getusernameTokenBinding();
                context.setUsernameTokenBinding(null);
            } else {
                usernameTokenBinding =(AuthenticationTokenPolicy.UsernameTokenBinding)keyBinding;
            }      
            signingKey = usernameTokenBinding.getSecretKey();
            builder = new UsernameTokenBuilder(context,usernameTokenBinding);
            BuilderResult untResult = builder.process();            
            untResult.setDataProtectionKey(signingKey);
            return untResult;
            
        } else if(PolicyTypeUtil.x509CertificateBinding(keyBinding)) {
            AuthenticationTokenPolicy.X509CertificateBinding certificateBinding = null;
            if ( context.getX509CertificateBinding() != null) {
                certificateBinding  = context.getX509CertificateBinding();
                context.setX509CertificateBinding(null);
            } else {
                certificateBinding  =(AuthenticationTokenPolicy.X509CertificateBinding)keyBinding;
            }
            
            PrivateKeyBinding privKBinding  = (PrivateKeyBinding)certificateBinding.getKeyBinding();
            signingKey = privKBinding.getPrivateKey();
            
            builder = new X509TokenBuilder(context,certificateBinding);
            BuilderResult xtbResult = builder.process();
            
            xtbResult.setDataProtectionKey(signingKey);
            return xtbResult;
        } else if(PolicyTypeUtil.kerberosTokenBinding(keyBinding)){
            AuthenticationTokenPolicy.KerberosTokenBinding krbBinding = null;
            if(context.getKerberosTokenBinding() != null){
                krbBinding = context.getKerberosTokenBinding();
                context.setKerberosTokenBinding(null);
            } else{
                krbBinding = (AuthenticationTokenPolicy.KerberosTokenBinding)keyBinding;
            }
            
            signingKey = krbBinding.getSecretKey();
            builder = new KerberosTokenBuilder(context, krbBinding);
            BuilderResult ktbResult = builder.process();
            ktbResult.setDataProtectionKey(signingKey);
            
            return ktbResult;
        } else if (PolicyTypeUtil.symmetricKeyBinding(keyBinding)) {
            SymmetricKeyBinding skb = null;
            if ( context.getSymmetricKeyBinding() != null) {
                skb = context.getSymmetricKeyBinding();
                context.setSymmetricKeyBinding(null);
            } else {
                skb = (SymmetricKeyBinding)keyBinding;
            }
            
            builder = new SymmetricTokenBuilder(skb, context, dataEncAlgo,keyEncAlgo);
            BuilderResult skbResult = builder.process();
            return skbResult;
        }  else if ( PolicyTypeUtil.derivedTokenKeyBinding(keyBinding)) {
            DerivedTokenKeyBinding dtk = (DerivedTokenKeyBinding)keyBinding;
            ((NamespaceContextEx)context.getNamespaceContext()).addSCNS();
            builder = new DerivedKeyTokenBuilder(context, dtk);
            BuilderResult dtkResult = builder.process();
            return dtkResult;
        }  else if ( PolicyTypeUtil.issuedTokenKeyBinding(keyBinding)) {
            IssuedTokenBuilder itb = new IssuedTokenBuilder(context,(IssuedTokenKeyBinding)keyBinding);
            BuilderResult itbResult = itb.process();
            return itbResult;
        } else if (PolicyTypeUtil.secureConversationTokenKeyBinding(keyBinding)) {
            ((NamespaceContextEx)context.getNamespaceContext()).addSCNS();
            SCTBuilder sctBuilder = new SCTBuilder(context,(SecureConversationTokenKeyBinding)keyBinding);
            BuilderResult sctResult = sctBuilder.process();
            return sctResult;
        } else if (PolicyTypeUtil.samlTokenPolicy(keyBinding)) {
            ((NamespaceContextEx)context.getNamespaceContext()).addSAMLNS();
            SamlTokenBuilder stb = new SamlTokenBuilder(context,(AuthenticationTokenPolicy.SAMLAssertionBinding)keyBinding,true);
            return stb.process();
        } else if (PolicyTypeUtil.keyValueTokenBinding(keyBinding)) {
            ((NamespaceContextEx)context.getNamespaceContext()).addSAMLNS();            
            KeyValueTokenBuilder sctBuilder = new KeyValueTokenBuilder(context,(AuthenticationTokenPolicy.KeyValueTokenBinding)keyBinding);
            BuilderResult kvtResult = sctBuilder.process();
            return kvtResult;            
         } else{
            logger.log(Level.SEVERE, LogStringsMessages.WSS_1703_UNSUPPORTED_KEYBINDING_SIGNATUREPOLICY(keyBinding));
            throw new UnsupportedOperationException("Unsupported Key Binding"+keyBinding);
            
        }
    }
    
}
