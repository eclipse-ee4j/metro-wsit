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
 * $Id: PrivateKeyBinding.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.MessageConstants;
import java.security.PrivateKey;
import java.security.KeyFactory;

import com.sun.xml.wss.impl.PolicyTypeUtil;

/**
 * Objects of this class act as KeyBindings for AuthenticationTokens such
 * as AuthenticationTokenPolicy.X509CertificateBinding and
 * AuthenticationTokenPolicy.SAMLAssertionBinding. When associated with an
 * AuthenticationToken they represent the PrivateKey associated with the
 * AuthenticationToken.
 */
public class PrivateKeyBinding extends WSSPolicy {

    /*
     * Feature Bindings
     * Key Bindings
     */

    /* this keyalgorithm is not used by our impl */
    String _keyAlgorithm   = MessageConstants._EMPTY;
    String _keyIdentifier  = MessageConstants._EMPTY;

    PrivateKey _privateKey = null;

    /**
     * Default constructor
     */
    public PrivateKeyBinding() {
        setPolicyIdentifier(PolicyTypeUtil.PRIVATEKEY_BINDING_TYPE);
    }

    /**
     * Constructor
     * @param keyIdentifier identifier for the Private Key
     * @param keyAlgorithm  identified for the Key Algorithm
     */
    public PrivateKeyBinding(String keyIdentifier, String keyAlgorithm) {
        this();

        this._keyIdentifier = keyIdentifier;
        this._keyAlgorithm = keyAlgorithm;
    }

    /**
     * set the keyIdentifier for the Private Key
     * @param keyIdentifier Key Identifier for the Private Key
     */
    public void setKeyIdentifier(String keyIdentifier) {
        this._keyIdentifier = keyIdentifier;
    }

    /**
     * @return key identifier for the Private Key
     */
    public String getKeyIdentifier() {
        return this._keyIdentifier;
    }

    /**
     * set the KeyAlgorithm of this Private Key.
     *
     * Implementation Note: This KeyAlgorithm is not used by XWS-Runtime,
     * refer setKeyAlgorithm on X509CertificateBinding, SAMLAssertionBinding,
     * and SymmetricKeyBinding instead.
     * @param keyAlgorithm  KeyAlgorithm of this Private Key
     */
    public void setKeyAlgorithm(String keyAlgorithm) {
        this._keyAlgorithm = keyAlgorithm;
    }

    /**
     * @return KeyAlgorithm of this Private Key
     */
    public String getKeyAlgorithm() {
        return this._keyAlgorithm;
    }

    /**
     * set the private key instance
     * @param privateKey PrivateKey for this PrivateKeyBinding
     */
    public void setPrivateKey(PrivateKey privateKey) {
        this._privateKey = privateKey;
    }

    /**
     * @return PrivateKey associated with this PrivateKeyBinding
     */
    public PrivateKey getPrivateKey() {
        return this._privateKey;
    }

    /**
     * equality operator
     * @param binding the Policy to be checked for equality
     * @return true if the argument binding is equal to this PrivateKeyBinding.
     */
    @Override
    public boolean equals(WSSPolicy binding) {

        try {
            if (!PolicyTypeUtil.privateKeyBinding(binding))
                return false;

            PrivateKeyBinding policy = (PrivateKeyBinding) binding;

            boolean b1 = _keyIdentifier.equals("") ? true : _keyIdentifier.equals(policy.getKeyIdentifier());
            if (!b1) return false;
            boolean b2 = _keyAlgorithm.equals("") ? true : _keyAlgorithm.equals(policy.getKeyAlgorithm());
            if (!b2) return false;
        } catch (Exception e) {}

        return true;
    }

    /*
     * equality operator ignoring Target bindings
     */
    @Override
    public boolean equalsIgnoreTargets(WSSPolicy binding) {
        return equals(binding);
    }

    /**
     * clone operator
     * @return a clone of this PrivateKeyBinding
     */
    @Override
    public Object clone(){
        PrivateKeyBinding pkBinding = new PrivateKeyBinding();

        try {
            pkBinding.setKeyAlgorithm(_keyAlgorithm);
            pkBinding.setKeyIdentifier(_keyIdentifier);

            KeyFactory factory = KeyFactory.getInstance(_privateKey.getAlgorithm());
            pkBinding.setPrivateKey((PrivateKey)factory.translateKey(_privateKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return pkBinding;
    }

    /**
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.PRIVATEKEY_BINDING_TYPE;
    }

    public String toString(){
        return PolicyTypeUtil.PRIVATEKEY_BINDING_TYPE+"::"+getKeyAlgorithm()+"::"+_keyIdentifier;
    }
}

