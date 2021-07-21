/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.spi_impl;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.policy.RmConfigurator;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

/**
 *
 */
public class RmFeatureConfigurator implements PolicyFeatureConfigurator {
    // TODO implement PolicyMapConfigurator as well

    private static final Logger LOGGER = Logger.getLogger(RmFeatureConfigurator.class);

    /**
     * Process WS-RM policy assertions and if found and is not optional then RM is enabled on the
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
                    WebServiceFeature feature;
                    feature = getRmFeature(alternative);
                    if (feature != null) {
                        features.add(feature);
                    }
                }
            } // end-if policy not null
        }
        return features;

    }

    private ReliableMessagingFeature getRmFeature(AssertionSet alternative) throws PolicyException {
        ReliableMessagingFeatureBuilder rmFeatureBuilder = null;
        for (RmProtocolVersion rmv : RmProtocolVersion.values()) {
            if (isPresentAndMandatory(alternative, rmv.rmAssertionName)) {
                rmFeatureBuilder = new ReliableMessagingFeatureBuilder(rmv);
                break;
            }
        }

        if (rmFeatureBuilder == null) {
            return null;
        }

        for (PolicyAssertion assertion : alternative) {
            if (assertion instanceof RmConfigurator) {
                final RmConfigurator rmAssertion = RmConfigurator.class.cast(assertion);
                if (!rmAssertion.isCompatibleWith(rmFeatureBuilder.getProtocolVersion())) {
                    LOGGER.warning(LocalizationMessages.WSRM_1009_INCONSISTENCIES_IN_POLICY(rmAssertion.getName(), rmFeatureBuilder.getProtocolVersion()));
                    // TODO replace warning with exception in Metro >2.0:
                    // throw new WebServiceException(/*message*/);
                }
                rmFeatureBuilder = rmAssertion.update(rmFeatureBuilder);
            }
        } // next assertion
        // next assertion
        return rmFeatureBuilder.build();
    }

    private Collection<PolicyAssertion> getAssertionsWithName(AssertionSet alternative, QName name) throws PolicyException {
        Collection<PolicyAssertion> assertions = alternative.get(name);
        if (assertions.size() > 1) {
            throw LOGGER.logSevereException(new PolicyException(
                    LocalizationMessages.WSRM_1008_DUPLICATE_ASSERTION_IN_POLICY(assertions.size(), name)));
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
