/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.keyinfo;

import com.sun.xml.ws.security.opt.api.keyinfo.BuilderResult;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import java.util.logging.Level;
import com.sun.xml.wss.logging.impl.opt.token.LogStringsMessages;
import java.security.Key;
import java.security.KeyPair;

/**
 *
 * @author shyam.rao@sun.com
 */
public class KeyValueTokenBuilder extends TokenBuilder{
    
    AuthenticationTokenPolicy.KeyValueTokenBinding binding = null;
    /** Creates a new instance of X509TokenBuilder */
    public KeyValueTokenBuilder(JAXBFilterProcessingContext context, AuthenticationTokenPolicy.KeyValueTokenBinding binding) {
        super(context);
        this.binding = binding;
    }
    
    /**
     * 
     * @return BuilderResult
     * @throws com.sun.xml.wss.XWSSecurityException
     */
    public BuilderResult process() throws XWSSecurityException{
                
        String referenceType = binding.getReferenceType();
        if(logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST, LogStringsMessages.WSS_1851_REFERENCETYPE_X_509_TOKEN(referenceType));
        }
        Key dataProtectionKey = null;
        BuilderResult result = new BuilderResult();
        KeyPair keyPair = (KeyPair)context.getExtraneousProperties().get("UseKey-RSAKeyPair");
        /*if(keyPair == null){
            KeyPairGenerator kpg;            
            try{
                kpg = KeyPairGenerator.getInstance("RSA");
                //RSAKeyGenParameterSpec rsaSpec = new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F0);
                //kpg.initialize(rsaSpec);                
            }catch (NoSuchAlgorithmException ex){
                throw new XWSSecurityException("Unable to create key pairs in Security Layer for KeyValueToken/RsaToken policy", ex);
            }
            //catch (InvalidAlgorithmParameterException ex){
            //    throw new XWSSecurityException("Unable to create key pairs in Security Layer for KeyValueToken/RsaToken policy", ex);
            //}
            kpg.initialize(512);
            keyPair = kpg.generateKeyPair();
            if(keyPair == null){
                throw new XWSSecurityException("RSA keypair is not generated/set for supporting token (KeyValueToken or RsaToken).");
            }
        }*/
        if (keyPair != null){
            dataProtectionKey = keyPair.getPrivate();
            if (dataProtectionKey == null) {
                //log here
                throw new XWSSecurityException("PrivateKey null inside PrivateKeyBinding set for KeyValueToken/RsaToken Policy ");
            }
            buildKeyInfo(keyPair.getPublic());
            result.setDataProtectionKey(dataProtectionKey);
            result.setKeyInfo(keyInfo);
        }
        return result;
    }    
}
