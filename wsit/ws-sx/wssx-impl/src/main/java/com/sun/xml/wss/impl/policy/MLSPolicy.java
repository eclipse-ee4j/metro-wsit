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
 * $Id: MLSPolicy.java,v 1.2 2010-10-21 15:37:33 snajper Exp $
 */

package com.sun.xml.wss.impl.policy;

/**
 * Represents a base class for Message Level Security (MLS) Policies.
 * Any MLSPolicy can be epxressed as being composed of one or both of
 * two SecurityPolicy components called FeatureBinding and KeyBinding.
 * This generic structure for an MLSPolicy allows for representing complex,
 * concrete Message Level Security Policies.
 */
public abstract class MLSPolicy implements SecurityPolicy {

    protected boolean readonly = false;

    protected MLSPolicy() {}

    /**
     * Get FeatureBinding component
     * @return FeatureBinding component of this MLSPolicy
     * @exception PolicyGenerationException if a FeatureBinding component is invalid for this MLSPolicy
     */
    public abstract MLSPolicy getFeatureBinding () throws PolicyGenerationException;

    /**
     * Get KeyBinding component
     * @return KeyBinding component of this MLSPolicy
     * @exception PolicyGenerationException if a KeyBinding component is invalid for this MLSPolicy
     */
    public abstract MLSPolicy getKeyBinding () throws PolicyGenerationException;


    /**
     * @param readonly set the readonly status of the policy.
     *
     */
    public void isReadOnly(boolean readonly) throws PolicyGenerationException {
        this.readonly = readonly;
        MLSPolicy featureBinding = getFeatureBinding();
        if ( featureBinding != null ) {
            featureBinding.isReadOnly(readonly);
        }

        MLSPolicy keybinding = getKeyBinding();
        if ( keybinding != null ) {
            keybinding.isReadOnly(readonly);
        }
    }

    /**
     * @return true if policy is readonly.
     *
     */
    public boolean isReadOnly() {
        return readonly;
    }

}
