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
 * $Id: EncryptionPolicy.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import java.util.Iterator;
import java.util.ArrayList;

import com.sun.xml.wss.impl.policy.MLSPolicy;
import com.sun.xml.wss.impl.PolicyTypeUtil;


/**
 *  Objects of this class  represent a policy for Encrypting parts of a SOAP Message. The
 *  Message Parts to be encrypted and the Data Encryption Algorithm to be used are
 *  captured as FeatureBindings of this Policy. The exact Key to be used is to be represented
 *  as a distinct KeyBinding for this policy instance.
 *
 *  Allowed KeyBindings for an EncryptionPolicy include the following :
 * <UL>
 *   <LI>AuthenticationTokenPolicy.X509CertificateBinding
 *   <LI>AuthenticationTokenPolicy.SAMLAssertionBinding
 *   <LI>SymmetricKeyBinding
 * </UL>
 */
public class EncryptionPolicy extends WSSKeyBindingExtension {
    
    /*
     * Feature Bindings
     *
     * (1) EncryptionPolicy.FeatureBinding
     *
     * Key Bindings
     *
     * (1) X509CertificateBinding
     * (2) SymmetricKeyBinding
     * (3) SAMLAssertionBinding
     */
    
    /**
     * default constructor
     */
    public EncryptionPolicy() {
        setPolicyIdentifier(PolicyTypeUtil.ENCRYPTION_POLICY_TYPE);
        this._featureBinding = new FeatureBinding();
    }
    
    /**
     * Equals operator
     * @param policy <code>WSSPolicy</code> to be compared for equality
     * @return true if the policy is equal to this policy
     */
    @Override
    public boolean equals(WSSPolicy policy) {
        boolean _assert = false;
        
        try {
            return equalsIgnoreTargets(policy);
            /*EncryptionPolicy sPolicy = (EncryptionPolicy) policy;
             
            _assert = ((WSSPolicy) getFeatureBinding()).equals (
                         (WSSPolicy) sPolicy.getFeatureBinding()) &&
            getKeyBinding().equals ((WSSPolicy) sPolicy.getKeyBinding());
             */
        } catch (Exception cce) {}
        
        return _assert;
    }
    
    /*
     * Equality comparision ignoring the Targets
     * @param policy the policy to be compared for equality
     * @return true if the argument policy is equal to this
     */
    @Override
    public boolean equalsIgnoreTargets(WSSPolicy policy) {
        boolean _assert = false;
        
        try {
            if(PolicyTypeUtil.encryptionPolicy(policy))
                return true;
            
            //EncryptionPolicy sPolicy = (EncryptionPolicy) policy;
            //TODO : Uncomment it
            //_assert = getKeyBinding().equals((WSSPolicy) sPolicy.getKeyBinding());
        } catch (Exception cce) {}
        
        return _assert;
    }
    
    /**
     * clone operator
     * @return a clone of this EncryptionPolicy
     */
    @Override
    public Object clone() {
        EncryptionPolicy ePolicy = new EncryptionPolicy();
        
        try {
            WSSPolicy fBinding = (WSSPolicy) getFeatureBinding();
            WSSPolicy kBinding = (WSSPolicy) getKeyBinding();
            
            if (fBinding != null)
                ePolicy.setFeatureBinding((MLSPolicy)fBinding.clone());
            
            if (kBinding != null)
                ePolicy.setKeyBinding((MLSPolicy)kBinding.clone());
        } catch (Exception e) {}
        
        return ePolicy;
    }
    
    /**
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.ENCRYPTION_POLICY_TYPE;
    }
    
    /**
     * A class representing FeatureBindings for an EncryptionPolicy
     * The FeatureBinding would contain information about the MessageParts
     * to be Encrypted, The data encryption algorithm to be used.
     */
    public static class FeatureBinding extends WSSPolicy {
        
        /*
         * Feature Bindings
         *
         * (1) SignaturePolicy
         * (2) EncryptionPolicy
         * (3) AuthenticationTokenPolicy
         *
         * Key Bindings
         *
         * (1) X509CertificateBinding
         * (2) SymmetricKeyBinding
         * (3) SAMLAssertionBinding
         */
        
        String _dataEncryptionAlgorithm = "";
        ArrayList _targets = new ArrayList();
        boolean standAloneRefList = false;
        boolean targetIsIssuedToken = false;
        boolean targetIsSignature = false;
        
        /**
         *default constructor
         */
        public FeatureBinding() {
            setPolicyIdentifier(PolicyTypeUtil.ENCRYPTION_POLICY_FEATUREBINDING_TYPE);
        }
        
        /**
         * @return the DataEncryptionAlgorithm
         */
        public String getDataEncryptionAlgorithm() {
            return _dataEncryptionAlgorithm;
        }
        
        /**
         * set the DataEncryptionAlgorithm to be used
         * @param algorithm the DataEncryptionAlgorithm
         */
        public void setDataEncryptionAlgorithm(String algorithm) {
            if ( isReadOnly() ) {
                throw new RuntimeException("Can not set DateEncryptionAlgorithm : Policy is ReadOnly");
            }
            this._dataEncryptionAlgorithm = algorithm;
        }
        
        /**
         * @return Target collection
         */
        public ArrayList getTargetBindings() {
            return _targets;
        }
        
        /**
         * @param target EncryptionTarget
         */
        @SuppressWarnings("unchecked")
        public void addTargetBinding(EncryptionTarget target) {
            if ( isReadOnly() ) {
                throw new RuntimeException("Can not add Target : Policy is ReadOnly");
            }
            _targets.add(target);
        }
        
        /*
         * @param target Target
         */
        @SuppressWarnings("unchecked")
        public void addTargetBinding(Target target) {
            if ( isReadOnly() ) {
                throw new RuntimeException("Can not add Target : Policy is ReadOnly");
            }
            _targets.add(new EncryptionTarget(target));
        }
        
        /**
         * @param targets ArrayList of all targets to be removed
         */
        @SuppressWarnings("unchecked")
        public void removeTargetBindings(ArrayList targets) {
            if ( isReadOnly() ) {
                throw new RuntimeException("Can not remove Target : Policy is ReadOnly");
            }
            _targets.removeAll(targets);
        }
        
        /**
         * Equals operator
         * @return true if the binding is equal to this Encryption Policy
         */
        @Override
        public boolean equals(WSSPolicy policy) {
            
            try {
                FeatureBinding fBinding = (FeatureBinding) policy;
                boolean b1 = _targets.equals(fBinding.getTargetBindings());
                if (!b1) return false;
            } catch (Exception e) {}
            
            return true;
        }
        
       /*
        * Equality comparision ignoring the Targets
        * @param policy the policy to be compared for equality
        * @return true if the argument policy is equal to this
        */
        @Override
        public boolean equalsIgnoreTargets(WSSPolicy policy) {
            return true;
        }
        
        /**
         * clone operator
         * @return a clone of this EncryptionPolicy.FeatureBinding
         */
        @Override
        @SuppressWarnings("unchecked")
        public Object clone(){
            FeatureBinding fBinding = new FeatureBinding();
            
            try {
                ArrayList list = new ArrayList();
                
                Iterator i = getTargetBindings().iterator();
                while (i.hasNext()) list.add(((EncryptionTarget)i.next()).clone());
                
                fBinding.getTargetBindings().addAll(list);
                
                WSSPolicy kBinding = (WSSPolicy)getKeyBinding();
                fBinding.setDataEncryptionAlgorithm(this.getDataEncryptionAlgorithm());
                if (kBinding != null)
                    fBinding.setKeyBinding((MLSPolicy)kBinding.clone());
            } catch (Exception e) {}
            
            fBinding.encryptsIssuedToken(this.encryptsIssuedToken());
            fBinding.encryptsSignature(this.encryptsSignature());
            return fBinding;
        }
        
        /**
         * @return the type of the policy
         */
        @Override
        public String getType() {
            return PolicyTypeUtil.ENCRYPTION_POLICY_FEATUREBINDING_TYPE;
        }
        
        public boolean encryptsIssuedToken() {
            return targetIsIssuedToken;
        }
        
        public void encryptsIssuedToken(boolean flag) {
            targetIsIssuedToken = flag;
        }
        
        public boolean encryptsSignature() {
            return targetIsSignature;
        }
        public void encryptsSignature(boolean flag) {
            targetIsSignature = flag;
        }
        public boolean getUseStandAloneRefList(){
            return standAloneRefList;
        }
        
        public void setUseStandAloneRefList(boolean value){
            this.standAloneRefList = value;
        }
    }
}

