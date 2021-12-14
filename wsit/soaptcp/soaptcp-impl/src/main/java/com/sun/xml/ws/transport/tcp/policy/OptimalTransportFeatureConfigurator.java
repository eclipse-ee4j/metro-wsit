/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.policy;

import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.ws.api.transport.tcp.SelectOptimalTransportFeature;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceFeature;

/**
 * {@link PolicyFeatureConfigurator}, which will transform SOAP/TCP policy
 * assertions to features on corresponding ports.
 *
 * @author Alexey Stashok
 */
public class OptimalTransportFeatureConfigurator implements PolicyFeatureConfigurator {

    private static final QName ENABLED = new QName("enabled");
    private static final Logger LOGGER = Logger.getLogger(OptimalTransportFeatureConfigurator.class);

    /**
     * process optimized transport policy assertions
     * {@link WSDLPort}
     *
     * @param key Key that identifies the endpoint scope
     * @param policyMap must be non-null
     * @return The list of features
     * @throws PolicyException If retrieving the policy triggered an exception
     */
    @Override
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        final Collection<WebServiceFeature> features = new LinkedList<>();
        if ((key != null) && (policyMap != null)) {
            Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (policy != null) {
                for (AssertionSet alternative : policy) {
                    for (PolicyAssertion assertion : alternative) {
                        if (assertion.getName().equals(com.sun.xml.ws.transport.tcp.wsit.TCPConstants.SELECT_OPTIMAL_TRANSPORT_ASSERTION)) {
                            boolean isEnabled = true;
                            String value = assertion.getAttributeValue(ENABLED);
                            if (value != null) {
                                value = value.trim();
                                isEnabled = Boolean.valueOf(value) || value.equalsIgnoreCase("yes");
                            }

                            features.add(new SelectOptimalTransportFeature(isEnabled));
                        }
                    }
                }
            } // end-if policy not null
        }

        return features;
    }
}
