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
 * $Id: SignaturePolicy.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.MessageConstants;
import java.util.Iterator;
import java.util.ArrayList;

import com.sun.xml.wss.impl.policy.MLSPolicy;
import com.sun.xml.wss.impl.PolicyTypeUtil;

import javax.xml.crypto.dsig.DigestMethod;

/**
 *  Objects of this class  represent a policy for Signing parts of a SOAP Message. The
 *  Message Parts to be signed and the Canonicalization Algorithm to be used for the ds:SignedInfo are
 *  captured as FeatureBindings of this Policy. The exact Key to be used is to be represented
 *  as a distinct KeyBinding for this policy instance.
 *  The SignatureMethod for the signature is obtained as the keyAlgorithm
 *  on the corresponding KeyBinding associated with this SignaturePolicy
 *
 *  Allowed KeyBindings for a SignaturePolicy include the following :
 * <UL>
 *   <LI>AuthenticationTokenPolicy.X509CertificateBinding
 *   <LI>AuthenticationTokenPolicy.SAMLAssertionBinding
 *   <LI>SymmetricKeyBinding
 * </UL>
 */
public class SignaturePolicy extends WSSKeyBindingExtension {

    /*
     * Feature Bindings
     *
     * (1) SignaturePolicy.FeatureBinding
     *
     * Key Bindings
     *
     * (1) X509KeyBinding
     * (2) SymmetricKeyBinding
     * (3) SAMLAssertionBinding
     */

    /**
     *Default constructor
     */
    public SignaturePolicy() {
        setPolicyIdentifier(PolicyTypeUtil.SIGNATURE_POLICY_TYPE);
        this._featureBinding = new FeatureBinding();
    }

    /**
     * clone operator
     * @return a clone of this SignaturePolicy
     */
    @Override
    public Object clone() {
        SignaturePolicy policy = new SignaturePolicy();

        try {
            WSSPolicy fBinding = (WSSPolicy) getFeatureBinding();
            WSSPolicy kBinding = (WSSPolicy) getKeyBinding();

            if (fBinding != null)
                policy.setFeatureBinding((MLSPolicy)fBinding.clone());

            if (kBinding != null)
                policy.setKeyBinding((MLSPolicy)kBinding.clone());
        } catch (Exception e) {}

        return policy;
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
            //TODO :: Uncomment;
            /*SignaturePolicy sPolicy = (SignaturePolicy) policy;

            _assert = ((WSSPolicy) getFeatureBinding()).equals(
            (WSSPolicy) sPolicy.getFeatureBinding()) &&
            getKeyBinding().equals((WSSPolicy) sPolicy.getKeyBinding());*/
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
            if(!(PolicyTypeUtil.signaturePolicy(policy))) return false;
            SignaturePolicy sPolicy = (SignaturePolicy) policy;
            _assert = ((WSSPolicy) getFeatureBinding()).equalsIgnoreTargets(
                    (WSSPolicy) sPolicy.getFeatureBinding()) ;
            //TODO : Un comment later;
            //&&   getKeyBinding().equals((WSSPolicy) sPolicy.getKeyBinding());
        } catch (Exception cce) {}

        return _assert;
    }

    /**
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.SIGNATURE_POLICY_TYPE;
    }

    /**
     * A class representing FeatureBindings for a SignaturePolicy
     * The FeatureBinding would contain information about the MessageParts
     * to be Signed, and the CanonicalizationMethod.
     * The SignatureMethod for the signature is obtained as the keyAlgorithm
     * on the corresponding KeyBinding associated with this SignaturePolicy
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
         * (1) X509KeyBinding
         * (2) SymmetricKeyBinding
         * (3) SAMLAssertionBinding
         */

        String _canonicalizationAlgorithm = "";

        private SignatureTarget timestamp = null;

        final ArrayList _targets = new ArrayList();

        private boolean isEndorsingSignature = false;

        private boolean isPrimarySignature = false;

        private boolean disableInclusivePrefix = false;

        /**
         * Default constructor
         */
        public FeatureBinding() {
            setPolicyIdentifier(PolicyTypeUtil.SIGNATURE_POLICY_FEATUREBINDING_TYPE);
        }

        /**
         * Constructor
         * @param canonicalization algorithm
         */
        public FeatureBinding(String canonicalization) {
            this();

            this._canonicalizationAlgorithm = canonicalization;
        }

        /**
         * @return Canonicalization Algorithm for the ds:SignedInfo
         */
        public String getCanonicalizationAlgorithm() {
            return _canonicalizationAlgorithm;
        }

        /**
         * set the Canonicalization Algorithm for the ds:SignedInfo
         * @param canonicalization Canonicalization Algorithm
         */
        public void setCanonicalizationAlgorithm(String canonicalization) {
            if ( isReadOnly()) {
                throw new RuntimeException("Can not set CanonicalizationAlgorithm : Policy is ReadOnly");
            }

            if (isBSP() &&
                canonicalization != MessageConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS) {
                throw new RuntimeException("Does not meet BSP requirement: 5404. C14n algorithm must be exc-c14n");
            }
            this._canonicalizationAlgorithm = canonicalization;
        }

        /*
         * @return true if a Timestamp Reference is to be included in the Signature
         */
        public boolean includeTimestamp() {
        return timestamp != null ? true: false;
        }

        /*
         * indicate whether to include a Timestamp Reference in the Signature
         * @param includeTimestamp
         */
        @SuppressWarnings("unchecked")
        public void includeTimestamp(boolean include) {
            if ( isReadOnly()) {
                throw new RuntimeException("Can not set includeTimestamp Flag : Policy is ReadOnly");
            }

            if (include) {
                if(timestamp == null) {
                    timestamp = new SignatureTarget();
                    timestamp.setType("qname");
                    timestamp.setValue(MessageConstants.TIMESTAMP_QNAME);
                    timestamp.setDigestAlgorithm(DigestMethod.SHA1);
                    _targets.add(0, timestamp);
                }
            } else {
                if (timestamp != null) {
                    int idx = _targets.indexOf(timestamp);
                    _targets.remove(idx);
                    timestamp = null;
                }
            }
        }

        public void isEndorsingSignature(boolean isEndorsingSignature){
            this.isEndorsingSignature = isEndorsingSignature;
        }

        public boolean isEndorsingSignature(){
            return this.isEndorsingSignature;
        }

        public void isPrimarySignature(boolean isPrimarySignature){
            this.isPrimarySignature = isPrimarySignature;
        }

        public boolean isPrimarySignature(){
            return this.isPrimarySignature;
        }

        public boolean getDisableInclusivePrefix(){
            return this.disableInclusivePrefix;
        }

        public void setDisbaleInclusivePrefix(boolean disableInclusivePrefix){
            this.disableInclusivePrefix = disableInclusivePrefix;
        }

        /**
         * @return collection of target bindings
         */
        public ArrayList getTargetBindings() {
            return _targets;
        }

        /**
         * Add target to the list of targets for this FeatureBinding
         * @param target SignatureTarget
         */
        @SuppressWarnings("unchecked")
        public void addTargetBinding(SignatureTarget target) {

            if ( isReadOnly()) {
                throw new RuntimeException("Can not add Target : Policy is ReadOnly");
            }
            _targets.add(target);
        }

        /*
         * Add target to the list of targets for this FeatureBinding
         * @param target Target to be added
         */
        public void addTargetBinding(Target target) {
            addTargetBinding(new SignatureTarget(target));
            //if ( isReadOnly()) {
            //    throw new RuntimeException("Can not add Target : Policy is ReadOnly");
            //}

            //_targets.add(new SignatureTarget(target));
        }

        /**
         * @param targets ArrayList of targets to be removed
         */
        @SuppressWarnings("unchecked")
        public void removeTargetBindings(ArrayList targets) {
            if ( isReadOnly()) {
                throw new RuntimeException("Can not remove Target : Policy is ReadOnly");
            }

            _targets.removeAll(targets);
        }

        /**
         * Equals operator
         * @param binding <code>WSSPolicy</code> to be compared for equality
         * @return true if the binding is equal to this policy
         */
        @Override
        public boolean equals(WSSPolicy binding) {

            try {
                if (!PolicyTypeUtil.signaturePolicyFeatureBinding(binding))
                    return false;
                FeatureBinding policy = (FeatureBinding) binding;

                boolean b1 = _canonicalizationAlgorithm.equals("") ? true :
                    _canonicalizationAlgorithm.equals(policy.getCanonicalizationAlgorithm());
                if (!b1) return false;

                boolean b2 = _targets.equals(policy.getTargetBindings());
                if (!b2) return false;

            } catch (Exception e) {}

            return true;
        }

        /*
         * Equality comparision ignoring the Targets
         * @param binding the binding to be compared for equality
         * @return true if the argument binding is equal to this
         */
        @Override
        public boolean equalsIgnoreTargets(WSSPolicy binding) {

            boolean assrt = false;

            if (!PolicyTypeUtil.signaturePolicyFeatureBinding(binding)){
                return false;
            }

            try {
                FeatureBinding policy = (FeatureBinding) binding;
                assrt = _canonicalizationAlgorithm.equals(policy.getCanonicalizationAlgorithm());
            } catch (Exception e) {}

            return assrt;
        }

        /**
         * @return a clone of this SignaturePolicy.FeatureBinding
         */
        @Override
        public Object clone() {
            FeatureBinding binding = new FeatureBinding();

            try {
                WSSPolicy kBinding = (WSSPolicy) getKeyBinding();
                WSSPolicy fBinding = (WSSPolicy) getFeatureBinding();

                if (fBinding != null)
                    binding.setFeatureBinding((MLSPolicy) fBinding.clone());

                if (kBinding != null)
                    binding.setKeyBinding((MLSPolicy) kBinding.clone());

                binding.setCanonicalizationAlgorithm(getCanonicalizationAlgorithm());

                Iterator i = getTargetBindings().iterator();
                while (i.hasNext()) {
                    SignatureTarget target = (SignatureTarget) i.next();
                    binding.addTargetBinding((SignatureTarget)target.clone());
                }
            } catch (Exception e) {}

            return binding;
        }

        /**
         * @return the type of the policy
         */
        @Override
        public String getType() {
            return PolicyTypeUtil.SIGNATURE_POLICY_FEATUREBINDING_TYPE;
        }

    }
}

