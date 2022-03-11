/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.MLSPolicy;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;

/**
 *
 * @author abhijit.das@Sun.COM
 */
public abstract class WSSFeatureBindingExtension extends WSSPolicy {
    
    /** Creates a new instance of WSSFeatureBindingExtension */
    public WSSFeatureBindingExtension() {
    }
    
    /**
     * Create and set the FeatureBinding for this WSSPolicy to a UsernameTokenBinding
     * @return a new UsernameTokenBinding as a FeatureBinding for this WSSPolicy
     * @exception PolicyGenerationException if UsernameTokenBinding is not a valid FeatureBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newUsernameTokenFeatureBinding () throws PolicyGenerationException {
        if ( isReadOnly () ) {
            throw new RuntimeException (
                    "Can not create a feature binding of UsernameToken type for ReadOnly " + _policyIdentifier);
        }
        
        if ((PolicyTypeUtil.SIGNATURE_POLICY_TYPE == _policyIdentifier) ||
                (PolicyTypeUtil.ENCRYPTION_POLICY_TYPE == _policyIdentifier)) {
            throw new PolicyGenerationException (
                    "Can not create a feature binding of UsernameToken type for " + _policyIdentifier);
        }
        
        this._featureBinding = new AuthenticationTokenPolicy.UsernameTokenBinding ();
        return _featureBinding;
    }
    
    
    /**
     * Create and set the FeatureBinding for this WSSPolicy to an X509CertificateBinding
     * @return a new X509CertificateBinding as a FeatureBinding for this WSSPolicy
     * @exception PolicyGenerationException if X509CertificateBinding is not a valid FeatureBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newX509CertificateFeatureBinding () throws PolicyGenerationException {
        if ( isReadOnly () ) {
            throw new RuntimeException (
                    "Can not create a feature binding of X509Certificate type for ReadOnly " + _policyIdentifier);
        }
        
        if ((PolicyTypeUtil.SIGNATURE_POLICY_TYPE == _policyIdentifier) ||
                (PolicyTypeUtil.ENCRYPTION_POLICY_TYPE == _policyIdentifier)) {
            throw new PolicyGenerationException (
                    "Can not create a feature binding of X509Certificate type for " + _policyIdentifier);
        }
        
        this._featureBinding = new AuthenticationTokenPolicy.X509CertificateBinding ();
        return _featureBinding;
    }
    
    /**
     * Create and set the FeatureBinding for this WSSPolicy to a SAMLAssertionBinding
     * @return a new SAMLAssertionBinding as a FeatureBinding for this WSSPolicy
     * @exception PolicyGenerationException if SAMLAssertionBinding is not a valid FeatureBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newSAMLAssertionFeatureBinding () throws PolicyGenerationException {
        if ( isReadOnly () ) {
            throw new RuntimeException (
                    "Can not create a feature binding of SAMLAssertion type for ReadOnly " + _policyIdentifier);
        }
        
        if ((PolicyTypeUtil.SIGNATURE_POLICY_TYPE == _policyIdentifier) ||
                (PolicyTypeUtil.ENCRYPTION_POLICY_TYPE == _policyIdentifier)) {
            throw new PolicyGenerationException (
                    "Can not create a feature binding of SAMLAssertion type for " + _policyIdentifier);
        }
        
        this._featureBinding = new AuthenticationTokenPolicy.SAMLAssertionBinding ();
        return _featureBinding;
    }
    
    
    /*
      Create and set the FeatureBinding for this WSSPolicy to a TimestampPolicy
      @return a new TimestampPolicy as a FeatureBinding for this WSSPolicy
     * @exception PolicyGenerationException, if TimestampPolicy is not a valid FeatureBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    /*public MLSPolicy newTimestampFeatureBinding () throws PolicyGenerationException {
        if ( isReadOnly () ) {
            throw new RuntimeException (
                    "Can not create a feature binding of Timestamp type for ReadOnly " + _policyIdentifier);
        }
        
        if (!(_policyIdentifier == PolicyTypeUtil.USERNAMETOKEN_TYPE) &&
                !(_policyIdentifier == PolicyTypeUtil.SIGNATURE_POLICY_FEATUREBINDING_TYPE))
            throw new PolicyGenerationException (
                    "Can not create a feature binding of Timestamp type for " + _policyIdentifier);
        
        this._featureBinding = new TimestampPolicy ();
        return _featureBinding;
    }*/
    
}
