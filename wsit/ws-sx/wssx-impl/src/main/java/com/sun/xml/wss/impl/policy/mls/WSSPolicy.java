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
 * $Id: WSSPolicy.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.policy.MLSPolicy;


/**
 * Represents a base class for SOAP Message Security Policies.
 * Any WSSPolicy can be epxressed as being composed of one or both of
 * two SecurityPolicy components called FeatureBinding and KeyBinding.
 * This generic structure for a WSSPolicy allows for representing complex,
 * concrete WSS Policy Instances.
 *
 * For example, A SignaturePolicy can have a SAMLAssertion as its KeyBinding.
 * The SAMLAssertionBinding can in turn have a KeyBinding which is a PrivateKeyBinding.
 * The PrivateKeyBinding would contain a PrivateKey corresponding to the PublicKey
 * contained in the SAML Assertion of the SAMLAssertionBinding. Such a SignaturePolicy
 * instance can then be used by the XWS-Runtime to sign Message parts of an outgoing
 * SOAP Message. The MessageParts to be signed are inturn identified by the FeatureBinding
 * component of the SignaturePolicy.
 *
 */
public abstract class WSSPolicy extends MLSPolicy implements Cloneable {
    protected String UUID;
    protected String _policyIdentifier;
    
    protected MLSPolicy _keyBinding= null;
    protected MLSPolicy _featureBinding= null;
    
    protected boolean _isOptional = false;
    
    protected boolean bsp = false;
    
    
    /**
     *Default constructor
     */
    public WSSPolicy () {}
    
    
    
    /**
     * @return MLSPolicy the FeatureBinding associated with this WSSPolicy, null otherwise
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    @Override
    public MLSPolicy getFeatureBinding () {
        return _featureBinding;
    }
    
    /**
     * @return MLSPolicy the KeyBinding associated with this WSSPolicy, null otherwise
     *
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    @Override
    public MLSPolicy getKeyBinding () {
        return _keyBinding;
    }
    
    /**
     * set the FeatureBinding for this WSSPolicy
     * @param policy the FeatureBinding to be set for this WSSPolicy
     */
    public void setFeatureBinding (MLSPolicy policy) {
        if ( isReadOnly () ) {
            throw new RuntimeException ("Can not set FeatureBinding : Policy is Readonly");
        }
        
        this._featureBinding = policy;
    }
    
    /**
     * set the KeyBinding for this WSSPolicy
     * @param policy the KeyBinding to be set for this WSSPolicy
     */
    public void setKeyBinding (MLSPolicy policy) {
        if ( isReadOnly () ) {
            throw new RuntimeException ("Can not set KeyBinding : Policy is Readonly");
        }
        
        this._keyBinding = policy;
    }
    
    /*
     *@param pi the policy identifier
     */
    public void setPolicyIdentifier (String pi) {
        if ( isReadOnly () ) {
            throw new RuntimeException ("Can not set PolicyIdentifier : Policy is Readonly");
        }
        
        this._policyIdentifier = pi;
    }
    
    /*
     *@return policy identifier
     */
    public String getPolicyIdentifier () {
        return _policyIdentifier;
    }
    
    /**
     *@return unique policy identifier associated with this policy
     */
    public String getUUID () {
        return UUID;
    }
    
    /**
     * set a unique policy identifier for this WSSPolicy
     */
    public void setUUID (String uuid) {
        if ( isReadOnly () ) {
            throw new RuntimeException ("Can not set UUID : Policy is Readonly");
        }
        
        this.UUID = uuid;
    }
    
    /*
     * @return true if-requirement-is-optional
     */
    public boolean isOptional () {
        return this._isOptional;
    }
    
    /*
     * @param isOptional parameter to indicate if this requirement is optional
     */
    public void isOptional (boolean isOptional) {
        if ( isReadOnly () ) {
            throw new RuntimeException ("Can not set Optional Requirement flag : Policy is Readonly");
        }
        
        this._isOptional = isOptional;
    }
    
    
    //TODO: we are not making any validity checks before creating KeyBindings.
    
    /**
     * clone operatror
     * @return a clone of this WSSPolicy
     *
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    @Override
    public abstract Object clone ();
    
    /**
     * equals operator
     *
     * @return true if the argument policy is the same as this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     * @see PrivateKeyBinding
     * @see SymmetricKeyBinding
     */
    public abstract boolean equals (WSSPolicy policy);
    
    /*
     * @return true if the argument policy is the same as this WSSPolicy ignoring Target bindings.
     *
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     * @see PrivateKeyBinding
     * @see SymmetricKeyBinding
     */
    public abstract boolean equalsIgnoreTargets (WSSPolicy policy);
    
    /*
     * Sets whether Basic Security Profile restrictions should be enforced as part
     * of this policy.
     */
    public void isBSP (boolean flag) {
        bsp = flag;
    }
    
    /*
     * @return true if BSP restrictions will be enforced.
     */
    public boolean isBSP () {
        return bsp;
    }
    
}
