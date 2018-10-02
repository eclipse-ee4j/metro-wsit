/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.addressing.policy;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;

/**
 * This Policy extension configures the WSDLModel with AddressingFeature when
 * wsaw:UsingAddressing assertion is present in the PolicyMap.
 *
 * This class exists in WSIT to provide functionality for backwards compatibility with previously generated
 * wsaw:UsingAddressing assertion.
 *
 * @author Rama Pulavarthi
 */
public class WsawAddressingFeatureConfigurator implements PolicyFeatureConfigurator{

    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(WsawAddressingFeatureConfigurator.class);

    private static final QName WSAW_ADDRESSING_ASSERTION =
        new QName(AddressingVersion.W3C.policyNsUri, "UsingAddressing");

    /**
     * Creates a new instance of WsawAddressingFeatureConfigurator
     */
    public WsawAddressingFeatureConfigurator() {
    }

    /**
     * process addressing policy assertions and if found and are not optional then addressing is enabled on the
     * {@link com.sun.xml.ws.api.model.wsdl.WSDLBoundPortType}
     *
     * @param key Key that identifies the endpoint scope
     * @param policyMap must be non-null
     * @return The list of features
     * @throws PolicyException If retrieving the policy triggered an exception
     */
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        LOGGER.entering(key, policyMap);
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if ((key != null) && (policyMap != null)) {
            final Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (null != policy && policy.contains(WSAW_ADDRESSING_ASSERTION)) {
                final Iterator<AssertionSet> assertions = policy.iterator();
                while (assertions.hasNext()) {
                    final AssertionSet assertionSet = assertions.next();
                    final Iterator<PolicyAssertion> policyAssertion = assertionSet.iterator();
                    while (policyAssertion.hasNext()) {
                        final PolicyAssertion assertion = policyAssertion.next();
                        if (assertion.getName().equals(WSAW_ADDRESSING_ASSERTION)) {
                            final WebServiceFeature feature = new AddressingFeature(true, !assertion.isOptional());
                            features.add(feature);
                            if (LOGGER.isLoggable(Level.FINE)) {
                                LOGGER.fine("Added addressing feature \"" + feature + "\" to element \"" + key + "\"");
                            }
                        } // end-if non optional wsa assertion found
                    } // next assertion
                } // next alternative
            } // end-if policy contains wsa assertion
        }
        LOGGER.exiting(features);
        return features;
    }
}
