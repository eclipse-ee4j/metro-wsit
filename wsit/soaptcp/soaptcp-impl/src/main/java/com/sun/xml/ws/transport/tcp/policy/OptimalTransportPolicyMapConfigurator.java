/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.policy;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.SimpleAssertion;
import com.sun.xml.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.ws.api.transport.tcp.SelectOptimalTransportFeature;
import com.sun.xml.ws.transport.tcp.wsit.TCPConstants;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;

/**
 *
 * @author Alexey Stashok
 * @author Marek Potociar
 */
public class OptimalTransportPolicyMapConfigurator implements PolicyMapConfigurator {

    private static final Logger LOGGER = Logger.getLogger(OptimalTransportPolicyMapConfigurator.class);

    public Collection<PolicySubject> update(PolicyMap policyMap, SEIModel model, WSBinding wsBinding) throws PolicyException {
        final Collection<PolicySubject> subjects = new LinkedList<PolicySubject>();

        try {
            LOGGER.entering(policyMap, model, wsBinding);

            updateOptimalTransportSettings(subjects, wsBinding, model, policyMap);

            return subjects;
            // TODO : update map with RM policy based on RM feature

        } finally {
            LOGGER.exiting(subjects);
        }
    }

    private void updateOptimalTransportSettings(Collection<PolicySubject> subjects, WSBinding wsBinding, SEIModel model, PolicyMap policyMap) throws PolicyException, IllegalArgumentException {
        final SelectOptimalTransportFeature optimalTransportFeature = wsBinding.getFeature(SelectOptimalTransportFeature.class);
        if (optimalTransportFeature == null || !optimalTransportFeature.isEnabled()) {
            return;
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            // TODO L10N
            LOGGER.finest(String.format("Make Optimal transport feature enabled on service '%s', port '%s'", model.getServiceQName(), model.getPortName()));
        }

        final PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(model.getServiceQName(), model.getPortName());
        final Policy existingPolicy = (policyMap != null) ? policyMap.getEndpointEffectivePolicy(endpointKey) : null;
        if ((existingPolicy == null) || !existingPolicy.contains(TCPConstants.SELECT_OPTIMAL_TRANSPORT_ASSERTION)) {
            final Policy otPolicy = createOptimalTransportPolicy(model.getBoundPortTypeName());
            final WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject(model.getBoundPortTypeName());
            final PolicySubject subject = new PolicySubject(wsdlSubject, otPolicy);
            subjects.add(subject);
            if (LOGGER.isLoggable(Level.FINE)) {
                // TODO L10N
                LOGGER.fine(String.format("Added Optimal transport policy with ID '%s' to binding element '%s'", otPolicy.getIdOrName(), model.getBoundPortTypeName()));
            }
        } else if (LOGGER.isLoggable(Level.FINE)) {
            // TODO L10N
            LOGGER.fine("Make Optimal transport assertion is already present in the endpoint policy");
        }
    }

    /**
     * Create a policy with an OptimalTransport assertion.
     *
     * @param bindingName The wsdl:binding element name. Used to generate a (locally) unique ID for the policy.
     * @return A policy that contains one policy assertion that corresponds to the given assertion name.
     */
    private Policy createOptimalTransportPolicy(final QName bindingName) {
        return Policy.createPolicy(null, bindingName.getLocalPart() + "_OptimalTransport_Policy", Arrays.asList(new AssertionSet[]{
                    AssertionSet.createAssertionSet(Arrays.asList(new PolicyAssertion[]{new OptimalTransportAssertion()}))
                }));
    }

    public static class OptimalTransportAssertion extends SimpleAssertion {

        public OptimalTransportAssertion() {
            this(AssertionData.createAssertionData(
                    TCPConstants.SELECT_OPTIMAL_TRANSPORT_ASSERTION), null);
        }

        public OptimalTransportAssertion(AssertionData data,
                Collection<? extends PolicyAssertion> assertionParameters) {
            super(data, assertionParameters);
        }
    }
}
