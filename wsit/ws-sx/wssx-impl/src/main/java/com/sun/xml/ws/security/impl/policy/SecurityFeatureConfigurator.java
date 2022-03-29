/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.api.ha.StickyFeature;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceFeature;

/**
 * Policy WS feature configurator implementation for the security domain
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class SecurityFeatureConfigurator implements PolicyFeatureConfigurator {

    public SecurityFeatureConfigurator() {}

    /*
     * Empty marker class that is used to tell JAX-WS RI that client session
     * stickiness should be enabled.
     * <p />
     * This is used whenever we detect that NonceManager or SC was enabled.
     *
     */
    public static final class SecurityStickyFeature extends WebServiceFeature implements StickyFeature {
        public static final String ID = SecurityStickyFeature.class.getName();

        private boolean nonceManagerUsed;
        private boolean scUsed;

        public SecurityStickyFeature() {}

        @Override
        public String getID() {
            return ID;
        }

        public boolean isNonceManagerUsed() {
            return nonceManagerUsed;
        }

        public void nonceManagerUsed() {
            this.nonceManagerUsed = true;
        }

        public boolean isScUsed() {
            return scUsed;
        }

        public void scUsed() {
            this.scUsed = true;
        }
    }

    @Override
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        SecurityStickyFeature stickyFeature = null;
        final Collection<WebServiceFeature> features = new LinkedList<>();
        if ((key != null) && (policyMap != null)) {
            Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (policy != null) {
                for (AssertionSet alternative : policy) {
                    stickyFeature = resolveStickiness(alternative.iterator(), stickyFeature);

                    List<WebServiceFeature> singleAlternativeFeatures;
                    singleAlternativeFeatures = getFeatures(alternative);
                    if (!singleAlternativeFeatures.isEmpty()) {
                        features.addAll(singleAlternativeFeatures);
                    }
                }
            } // end-if policy not null
        }

        if (stickyFeature != null) {
           features.add(stickyFeature);
        }

        return features;
    }

    /**
     * NonceManager is used when there is sp:UsernameToken assertion in Policy of the Service with DigestAuthentication enabled.
     * SC feature is enabled by having a sp:SecureConversationToken in the Policy of the Service.
     */
    private static final String SC_LOCAL_NAME = "SecureConversationToken";
    private static final String DIGEST_PASSWORD_LOCAL_NAME = "HashPassword";
    private static final String NONCE_LOCAL_NAME = "Nonce";
    private static final Set<QName> STICKINESS_ENABLERS = Collections.unmodifiableSet(new HashSet(Arrays.asList(new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, SC_LOCAL_NAME),
            new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, SC_LOCAL_NAME),
            new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, DIGEST_PASSWORD_LOCAL_NAME),
            new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, DIGEST_PASSWORD_LOCAL_NAME),
            new QName(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri, NONCE_LOCAL_NAME),
            new QName(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri, NONCE_LOCAL_NAME))));

    /**
     *
     */
    private SecurityStickyFeature resolveStickiness(Iterator<PolicyAssertion> assertions, SecurityStickyFeature currentFeature) {
        while(assertions.hasNext()) {
            final PolicyAssertion assertion = assertions.next();
            if (STICKINESS_ENABLERS.contains(assertion.getName())) {
                if (currentFeature == null) {
                    currentFeature = new SecurityStickyFeature();
                }

                if (SC_LOCAL_NAME.equals(assertion.getName().getLocalPart())) {
                    currentFeature.scUsed();
                }

                if (NONCE_LOCAL_NAME.equals(assertion.getName().getLocalPart()) ||
                        DIGEST_PASSWORD_LOCAL_NAME.equals(assertion.getName().getLocalPart())) {
                    currentFeature.nonceManagerUsed();
                }
            }

            if (assertion.hasParameters()) {
                currentFeature = resolveStickiness(assertion.getParametersIterator(), currentFeature);
            }

            if (assertion.hasNestedPolicy()) {
                currentFeature = resolveStickiness(assertion.getNestedPolicy().getAssertionSet().iterator(), currentFeature);
            }
        }

        return currentFeature;
    }

    private List<WebServiceFeature> getFeatures(AssertionSet alternative) {
        // method will be useful in the future with unified config; do nothing for now
        return Collections.emptyList();
    }
}
