/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.verifier;

import com.sun.xml.ws.security.opt.impl.incoming.TargetResolverImpl;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.impl.policy.PolicyAlternatives;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.spi.PolicyVerifier;

/**
 *
 * @author vbkumarjayanti
 */
public class PolicyVerifierFactory {

    /**
     *
     * @param servicePolicy the policy for the Service
     * @return a Concrete PolicyVerifier which can be used to verify the policy of the service
     */
    public static PolicyVerifier createVerifier(SecurityPolicy servicePolicy, ProcessingContext ctx) {
        TargetResolver targetResolver = new TargetResolverImpl(ctx);
        if (servicePolicy instanceof MessagePolicy) {
            return new MessagePolicyVerifier(ctx, targetResolver);
        }else if (servicePolicy instanceof PolicyAlternatives){
            return new PolicyAlternativesVerifier(ctx, targetResolver);
        }
        return null;
    }
}
