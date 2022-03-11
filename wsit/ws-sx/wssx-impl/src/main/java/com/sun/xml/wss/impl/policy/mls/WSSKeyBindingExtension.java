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

import com.sun.xml.wss.impl.policy.MLSPolicy;

/**
 *
 * @author abhijit.das@Sun.COM
 */
public abstract class WSSKeyBindingExtension extends WSSPolicy {

    /** Creates a new instance of WSSKeyBindingExtension */
    public WSSKeyBindingExtension() {
    }


    /**
     * Create and set the KeyBinding for this WSSPolicy to an X509CertificateBinding
     * @return a new X509CertificateBinding as a KeyBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newX509CertificateKeyBinding() {
        if ( isReadOnly() ) {
            throw new RuntimeException("Can not create X509CertificateKeyBinding : Policy is Readonly");
        }
        this._keyBinding = new AuthenticationTokenPolicy.X509CertificateBinding();
        return _keyBinding;
    }

    /**
     * Create and set the KeyBinding for this WSSPolicy to a SAMLAssertionBinding
     * @return a new SAMLAssertionBinding as a KeyBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newSAMLAssertionKeyBinding() {
        if ( isReadOnly() ) {
            throw new RuntimeException("Can not create SAMLAssertionKeyBinding : Policy is Readonly");
        }

        this._keyBinding = new AuthenticationTokenPolicy.SAMLAssertionBinding();
        return _keyBinding;
    }

    /**
     * Create and set the KeyBinding for this WSSPolicy to a SymmetricKeyBinding
     * @return a new SymmetricKeyBinding as a KeyBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newSymmetricKeyBinding() {
        if ( isReadOnly() ) {
            throw new RuntimeException("Can not create SymmetricKeyBinding : Policy is Readonly");
        }

        this._keyBinding = new SymmetricKeyBinding();
        return _keyBinding;
    }


    /**
     * Create and set the KeyBinding for this WSSPolicy to a DerivedTokenKeyBinding
     * @return a new DerivedTokenKeyBinding as a KeyBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newDerivedTokenKeyBinding() {
        if ( isReadOnly() ) {
            throw new RuntimeException("Can not create DerivedTokenKeyBinding : Policy is Readonly");
        }

        this._keyBinding = new DerivedTokenKeyBinding();
        return _keyBinding;
    }


    /**
     * Create and set the KeyBinding for this WSSPolicy to a IssuedTokenKeyBinding
     * @return a new IssuedTokenKeyBinding as a KeyBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newIssuedTokenKeyBinding() {
        if ( isReadOnly() ) {
            throw new RuntimeException("Can not create IssuedTokenKeyBinding : Policy is Readonly");
        }

        this._keyBinding = new IssuedTokenKeyBinding();
        return _keyBinding;
    }

    /**
     * Create and set the KeyBinding for this WSSPolicy to a IssuedTokenKeyBinding
     * @return a new IssuedTokenKeyBinding as a KeyBinding for this WSSPolicy
     * @see SignaturePolicy
     * @see EncryptionPolicy
     * @see AuthenticationTokenPolicy
     */
    public MLSPolicy newSecureConversationTokenKeyBinding() {
        if ( isReadOnly() ) {
            throw new RuntimeException("Can not create SecureConversationKeyBinding : Policy is Readonly");
        }

        this._keyBinding = new SecureConversationTokenKeyBinding();
        return _keyBinding;
    }

    public MLSPolicy newUsernameTokenBindingKeyBinding(){
        if ( isReadOnly() ) {
            throw new RuntimeException("Can not create SAMLAssertionKeyBinding : Policy is Readonly");
        }
        this._keyBinding = new AuthenticationTokenPolicy.UsernameTokenBinding();
        return _keyBinding;
    }
}
