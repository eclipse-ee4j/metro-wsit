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

/**
 *
 */
public class SecureConversationTokenKeyBinding extends KeyBindingBase {

    /** Creates a new instance of IssuedTokenKeyBinding */
    public SecureConversationTokenKeyBinding() {
        setPolicyIdentifier(PolicyTypeUtil.SECURE_CONVERSATION_TOKEN_KEY_BINDING);
    }

    @Override
    public Object clone() {
        SecureConversationTokenKeyBinding itb = new SecureConversationTokenKeyBinding();
        //itb.setPolicyToken(this.getPolicyToken());
        itb.setUUID(this.getUUID());
        itb.setIncludeToken(this.getIncludeToken());
        itb.setPolicyTokenFlag(this.policyTokenWasSet());
        return itb;
    }

    @Override
    public boolean equals(WSSPolicy policy) {
        if ( !PolicyTypeUtil.secureConversationTokenKeyBinding(policy)) {
            return false;
        }

        //TODO: Check the contents of IssuedTokenContext
        return true;
    }

    @Override
    public boolean equalsIgnoreTargets(WSSPolicy policy) {
        return equals(policy);
    }

    @Override
    public String getType() {
        return PolicyTypeUtil.SECURE_CONVERSATION_TOKEN_KEY_BINDING;
    }

}
