/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.policy.spi_impl;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.ws.rx.mc.api.MakeConnectionSupportedFeature;
import com.sun.xml.ws.rx.mc.localization.LocalizationMessages;
import com.sun.xml.ws.rx.mc.policy.wsmc200702.MakeConnectionSupportedAssertion;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceFeature;

/**
 *
 */
public class McFeatureConfigurator implements PolicyFeatureConfigurator {
    // TODO implement PolicyMapConfigurator as well

    private static final Logger LOGGER = Logger.getLogger(McFeatureConfigurator.class);

    /**
     * Process WS-MC policy assertions and if found and is not optional then MC is enabled on the
     * {@link WSDLPort}
     *
     * @param key Key that identifies the endpoint scope
     * @param policyMap must not be {@code null}
     * @return The list of features
     * @throws PolicyException If retrieving the policy triggered an exception
     */
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if ((key != null) && (policyMap != null)) {
            Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (policy != null) {
                for (AssertionSet alternative : policy) {
                    MakeConnectionSupportedFeature feature = translateIntoMakeConnectionFeature(alternative);
                    if (feature != null) {
                        features.add(feature);
                    }
                }
            } // end-if policy not null
        }
        return features;

    }

    private MakeConnectionSupportedFeature translateIntoMakeConnectionFeature(AssertionSet alternative) throws PolicyException {
        if (isPresentAndMandatory(alternative, MakeConnectionSupportedAssertion.NAME)) {
            return new MakeConnectionSupportedFeature();
        } // end-if MC assertion is present and not optional
        return null;
    }

    private Collection<PolicyAssertion> getAssertionsWithName(AssertionSet alternative, QName name) throws PolicyException {
        Collection<PolicyAssertion> assertions = alternative.get(name);
        if (assertions.size() > 1) {
            throw LOGGER.logSevereException(new PolicyException(
                    LocalizationMessages.WSMC_0122_DUPLICATE_ASSERTION_IN_POLICY(assertions.size(), name)));
        }
        return assertions;
    }

    private boolean isPresentAndMandatory(AssertionSet alternative, QName assertionName) throws PolicyException {
        Collection<PolicyAssertion> assertions;

        assertions = getAssertionsWithName(alternative, assertionName);
        for (PolicyAssertion assertion : assertions) {
            if (!assertion.isOptional()) {
                return true;
            }
        }

        return false;
    }
}
