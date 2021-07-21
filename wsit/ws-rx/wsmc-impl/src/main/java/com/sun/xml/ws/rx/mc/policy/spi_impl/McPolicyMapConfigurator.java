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
import com.sun.xml.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.ws.rx.mc.api.MakeConnectionSupportedFeature;
import com.sun.xml.ws.rx.mc.policy.wsmc200702.MakeConnectionSupportedAssertion;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;

/**
 *
 */
public class McPolicyMapConfigurator implements PolicyMapConfigurator {

    private static final Logger LOGGER = Logger.getLogger(McPolicyMapConfigurator.class);

    public Collection<PolicySubject> update(PolicyMap policyMap, SEIModel model, WSBinding wsBinding) throws PolicyException {
        final Collection<PolicySubject> subjects = new LinkedList<PolicySubject>();

        try {
            LOGGER.entering(policyMap, model, wsBinding);

            updateMakeConnectionSettings(subjects, wsBinding, model, policyMap);

            return subjects;
        } finally {
            LOGGER.exiting(subjects);
        }
    }

    /**
     * Create a policy with an MCSupported assertion.
     *
     * @param bindingName The wsdl:binding element name. Used to generate a (locally) unique ID for the policy.
     * @return A policy that contains one policy assertion that corresponds to the given assertion name.
     */
    private Policy createMakeConnectionPolicy(final QName bindingName) {
        return Policy.createPolicy(null, bindingName.getLocalPart() + "_MCSupported_Policy", Arrays.asList(new AssertionSet[]{
                    AssertionSet.createAssertionSet(Arrays.asList(new PolicyAssertion[]{new MakeConnectionSupportedAssertion()}))
                }));
    }

    private void updateMakeConnectionSettings(Collection<PolicySubject> subjects, WSBinding wsBinding, SEIModel model, PolicyMap policyMap) throws PolicyException, IllegalArgumentException {
        final MakeConnectionSupportedFeature mcFeature = wsBinding.getFeature(MakeConnectionSupportedFeature.class);
        if (mcFeature == null || !mcFeature.isEnabled()) {
            return;
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(String.format("Make connection feature enabled on service '%s', port '%s'", model.getServiceQName(), model.getPortName()));
        }

        final PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(model.getServiceQName(), model.getPortName());
        final Policy existingPolicy = (policyMap != null) ? policyMap.getEndpointEffectivePolicy(endpointKey) : null;
        if ((existingPolicy == null) || !existingPolicy.contains(MakeConnectionSupportedAssertion.NAME)) {
            final Policy mcPolicy = createMakeConnectionPolicy(model.getBoundPortTypeName());
            final WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject(model.getBoundPortTypeName());
            final PolicySubject subject = new PolicySubject(wsdlSubject, mcPolicy);
            subjects.add(subject);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(String.format("Added make connection policy with ID '%s' to binding element '%s'", mcPolicy.getIdOrName(), model.getBoundPortTypeName()));
            }
        } else if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Make connection assertion is already present in the endpoint policy");
        }
    }
}
