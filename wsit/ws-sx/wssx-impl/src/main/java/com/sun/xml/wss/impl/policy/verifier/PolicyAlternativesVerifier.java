/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.verifier;

import com.sun.xml.ws.security.spi.AlternativeSelector;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.impl.PolicyViolationException;
import com.sun.xml.wss.impl.policy.PolicyAlternatives;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.spi.PolicyVerifier;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 *
 * @author vbkumarjayanti
 */
public class PolicyAlternativesVerifier implements PolicyVerifier {
    private ProcessingContext ctx = null;

    private static final  Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /** Creates a new instance of MessagePolicyVerifier */
    public PolicyAlternativesVerifier(ProcessingContext ctx, TargetResolver targetResolver) {
        this.ctx = ctx;
        //this.targetResolver = targetResolver;
    }

    @Override
    public void verifyPolicy(SecurityPolicy recvdPolicy, SecurityPolicy configPolicy) throws PolicyViolationException {
        PolicyAlternatives confPolicies = (PolicyAlternatives)configPolicy;

        List<MessagePolicy> mps = confPolicies.getSecurityPolicy();
        if (mps.size() == 1) {
            PolicyVerifier verifier = PolicyVerifierFactory.createVerifier(mps.get(0), ctx);
            verifier.verifyPolicy(recvdPolicy, mps.get(0));
            if (mps.get(0).getPolicyAlternativeId() != null) {
                ctx.getExtraneousProperties().put(POLICY_ALTERNATIVE_ID,mps.get(0).getPolicyAlternativeId());
            }
        } else {
           //do policy verification
           // try with an AlternativeSelector first
           //AlternativeSelector selector = new  DefaultAlternativeSelector();
           AlternativeSelector selector = findAlternativesSelector(mps);
           MessagePolicy toVerify = selector.selectAlternative(ctx, mps, recvdPolicy);
           //TODO: the PolicyVerifier.verifyPolicy() method expects the toVerify argument to be
           //passed again. since that interface is a legacy interface,  not changing it
           //right now.
            if (toVerify != null) {
                PolicyVerifier verifier = PolicyVerifierFactory.createVerifier(toVerify, ctx);
                verifier.verifyPolicy(recvdPolicy, toVerify);
                if (toVerify.getPolicyAlternativeId() != null) {
                   ctx.getExtraneousProperties().put(POLICY_ALTERNATIVE_ID,toVerify.getPolicyAlternativeId());
                }
            } else {
                //unsupported
               throw new UnsupportedOperationException(
                       "Cannot verify the request against the configured PolicyAlternatives in the WebService");
            }

        }

    }

    private AlternativeSelector findAlternativesSelector(List<MessagePolicy> alternatives) {
        ServiceLoader<AlternativeSelector> alternativeSelectorLoader = ServiceLoader.load(AlternativeSelector.class);
        //not clear from javadoc if null is returned ever or an RT exception thrown when it does not find
        // the services definitions.
        if (alternativeSelectorLoader == null) {
            if (alternatives.size() == 2) {
                return new UsernameOrSAMLAlternativeSelector();
            } else {
                throw new UnsupportedOperationException("No AlternativeSelector accepts the policy alternatives combination.");
            }
        }
        Iterator<AlternativeSelector> alternativeSelectorIterator = alternativeSelectorLoader.iterator();

        while (alternativeSelectorIterator.hasNext()) {
            AlternativeSelector selector = alternativeSelectorIterator.next();
            if (selector.supportsAlternatives(alternatives)) {
                return selector;
            }
        }

        throw new UnsupportedOperationException("No AlternativeSelector accepts the policy alternatives combination.");
    }


}
