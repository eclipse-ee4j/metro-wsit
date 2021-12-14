/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy;

import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import java.util.List;


/**
 *
 * @author vbkumarjayanti
 */
public class PolicyAlternatives implements SecurityPolicy {

    private final List<MessagePolicy> policyAlternatives;

    public PolicyAlternatives(List<MessagePolicy> policies) {
        //TODO: store an immutable list internally.
        this.policyAlternatives = policies;
    }
    @Override
    public String getType() {
        return PolicyTypeUtil.SEC_POLICY_ALTERNATIVES_TYPE;
    }

    public final List<MessagePolicy> getSecurityPolicy() {
        return this.policyAlternatives;
    }

     /**
     * @return true if empty
     */
    public boolean isEmpty() {
        if (policyAlternatives == null) {
            return true;
        }

        for (MessagePolicy m : policyAlternatives) {
            if (!m.isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
