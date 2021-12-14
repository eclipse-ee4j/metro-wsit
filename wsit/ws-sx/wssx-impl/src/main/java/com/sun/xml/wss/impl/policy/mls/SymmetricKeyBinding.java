/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SymmetricKeyBinding.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.policy.MLSPolicy;
import com.sun.xml.wss.impl.MessageConstants;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.sun.xml.wss.impl.PolicyTypeUtil;

/**
 * A policy representing a SymmetricKey that can be used as the
 * KeyBinding for a SignaturePolicy or an EncryptionPolicy.
 */
public class SymmetricKeyBinding extends KeyBindingBase {
    
    /*
     * Feature Binding
     * Key Binding
     */
    
    String _keyAlgorithm  = MessageConstants._EMPTY;
    
    String _keyIdentifier = MessageConstants._EMPTY;
    
    String _certAlias = MessageConstants._EMPTY;
    
    boolean _useReceivedSecret = false;
    
    SecretKey _secretKey  = null;
    private boolean isEKSHA1 = false;
    
    /**
     * Default constructor
     */
    public SymmetricKeyBinding() {
        setPolicyIdentifier(PolicyTypeUtil.SYMMETRIC_KEY_TYPE);
    }
    
    /**
     * @param keyIdentifier identifier for Key
     * @param keyAlgorithm Key Algorithm
     */
    public SymmetricKeyBinding(String keyIdentifier, String keyAlgorithm) {
        this();
        
        this._keyIdentifier = keyIdentifier;
        this._keyAlgorithm = keyAlgorithm;
    }
    
    /**
     * set the key identifier for the symmetric key
     */
    public void setKeyIdentifier(String keyIdentifier) {
        this._keyIdentifier = keyIdentifier;
    }
    
    /**
     * @return key identifier for the symmetric key
     */
    public String getKeyIdentifier() {
        return this._keyIdentifier;
    }
    
    public void setCertAlias(String certAlias) {
        this._certAlias = certAlias;
    }
    
    public String getCertAlias() {
        return this._certAlias;
    }
    
    public void setUseReceivedSecret(boolean useReceivedSecret) {
        this._useReceivedSecret = useReceivedSecret;
    }
    
    public boolean getUseReceivedSecret() {
        return this._useReceivedSecret;
    }
    
    /**
     * set the Key Algorithm of the Symmetric Key
     */
    public void setKeyAlgorithm(String keyAlgorithm) {
        this._keyAlgorithm = keyAlgorithm;
    }
    
    /**
     * @return keyAlgorithm for the Symmetric Key
     */
    public String getKeyAlgorithm() {
        return this._keyAlgorithm;
    }
    
    /**
     * Set the symmetric key
     * @param secretKey the SecretKey
     */
    public void setSecretKey(SecretKey secretKey) {
        this._secretKey = secretKey;
    }
    
    /**
     * @return SecretKey the symmetric key
     */
    public SecretKey getSecretKey() {
        return this._secretKey;
    }

    /**
     * Create and set the KeyBinding for this WSSPolicy to an X509CertificateBinding
     * @return a new X509CertificateBinding as a KeyBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newX509CertificateKeyBinding () {
        if ( isReadOnly () ) {
            throw new RuntimeException ("Can not create X509CertificateKeyBinding : Policy is Readonly");
        }
        this._keyBinding = new AuthenticationTokenPolicy.X509CertificateBinding ();
        return _keyBinding;
    }
   public boolean usesEKSHA1KeyBinding(){
       return isEKSHA1;
   }
   public void usesEKSHA1KeyBinding(boolean value){
       isEKSHA1= value;
   }
    /**
     * @param policy the policy to be compared for equality
     * @return true if the argument policy is equal to this
     */
    @Override
    public boolean equals(WSSPolicy policy) {
        
        boolean assrt = false;
        
        try {
            SymmetricKeyBinding skBinding = (SymmetricKeyBinding) policy;
            
            boolean b1 = _keyIdentifier.equals("") ? true : _keyIdentifier.equals(skBinding.getKeyIdentifier());
            
            boolean b2 = _keyAlgorithm.equals("") ? true : _keyAlgorithm.equals(skBinding.getKeyAlgorithm());
            
            boolean b3 = _certAlias.equals("") ? true : _certAlias.equals(skBinding.getCertAlias());
            
            boolean b4 = (_useReceivedSecret == false) ? true : (_useReceivedSecret == skBinding.getUseReceivedSecret());
            boolean b5 = (this._keyBinding.equals(policy._keyBinding));
            boolean b6 = this.isEKSHA1 == skBinding.usesEKSHA1KeyBinding();
            assrt = b1 && b2 && b3 && b4 && b5 && b6;
        } catch (Exception e) {}
        
        return assrt;
    }
    
    /*
     * Equality comparision ignoring the Targets
     * @param policy the policy to be compared for equality
     * @return true if the argument policy is equal to this
     */
    @Override
    public boolean equalsIgnoreTargets(WSSPolicy binding) {
        return equals(binding);
    }
    
    /**
     * Clone operator
     * @return clone of this policy
     */
    @Override
    public Object clone(){
        SymmetricKeyBinding skBinding = new SymmetricKeyBinding();
        
        try {
            skBinding.setUUID(this.getUUID());
            skBinding.setKeyIdentifier(_keyIdentifier);
            skBinding.setKeyAlgorithm(_keyAlgorithm);
            skBinding.setCertAlias(_certAlias);
            skBinding.setUseReceivedSecret(_useReceivedSecret);
            skBinding.usesEKSHA1KeyBinding(this.isEKSHA1);
            SecretKeySpec ky0 = (SecretKeySpec) _secretKey;
            if (ky0 != null) {
                SecretKeySpec key = new SecretKeySpec(ky0.getEncoded(), ky0.getAlgorithm());
                skBinding.setSecretKey(key);
            }

            if (this._keyBinding != null) {
                if(this._keyBinding instanceof AuthenticationTokenPolicy.UsernameTokenBinding){
                    skBinding.setKeyBinding((AuthenticationTokenPolicy.UsernameTokenBinding)
                            ((AuthenticationTokenPolicy.UsernameTokenBinding)this._keyBinding).clone());
                }else if (this._keyBinding instanceof AuthenticationTokenPolicy.X509CertificateBinding) {
                    skBinding.setKeyBinding((AuthenticationTokenPolicy.X509CertificateBinding)
                        ((AuthenticationTokenPolicy.X509CertificateBinding)this._keyBinding).clone());
                } else if(this._keyBinding instanceof AuthenticationTokenPolicy.KerberosTokenBinding){
                    skBinding.setKeyBinding((AuthenticationTokenPolicy.KerberosTokenBinding)
                        ((AuthenticationTokenPolicy.KerberosTokenBinding)this._keyBinding).clone());
                }
            }

        } catch (Exception e) {
            // log
        }
        
        return skBinding;
    }
    
    /**
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.SYMMETRIC_KEY_TYPE;
    }
    
    public String toString(){
        return PolicyTypeUtil.SYMMETRIC_KEY_TYPE+"::"+getKeyAlgorithm()+"::"+_keyIdentifier;
    }    
}

