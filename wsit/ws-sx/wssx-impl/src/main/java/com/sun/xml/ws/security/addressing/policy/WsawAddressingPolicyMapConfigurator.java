/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.addressing.policy;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;

import com.sun.xml.ws.policy.subject.WsdlBindingSubject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import jakarta.xml.ws.soap.AddressingFeature;

/**
 * Generate an wsaw:UsingAddressing policy assertion and updates the PolicyMap if AddressingFeature is enabled.
 * This is done in WSIT just for backwards compatibility of WSIT for interoperability with old clients.
 * JAX-WS generates wsam:Addressing assertion for the same when Addressing is enabled.
 *
 *
 * @author Rama Pulavarthi
 */
public class WsawAddressingPolicyMapConfigurator implements PolicyMapConfigurator {

    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(WsawAddressingPolicyMapConfigurator.class);

    private static final class AddressingAssertion extends PolicyAssertion {

        AddressingAssertion(AssertionData assertionData) {
            super(assertionData, null);
        }
    }

    public WsawAddressingPolicyMapConfigurator() {
    }

    /**
     * Puts an addressing policy into the PolicyMap if the addressing feature was set.
     */
    @Override
    public Collection<PolicySubject> update(final PolicyMap policyMap, final SEIModel model, final WSBinding wsBinding)
            throws PolicyException {
        LOGGER.entering(policyMap, model, wsBinding);

        Collection<PolicySubject> subjects = new ArrayList<>();
        if (policyMap != null) {
            final AddressingFeature addressingFeature = wsBinding.getFeature(AddressingFeature.class);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("addressingFeature = " + addressingFeature);
            }
            if ((addressingFeature != null) && addressingFeature.isEnabled()) {
                //add wsaw:UsingAddressing for WSIT compatibility.
                addWsawUsingAddressingForCompatibility(subjects, policyMap, model, addressingFeature);

            }
        } // endif policy map not null
        LOGGER.exiting(subjects);
        return subjects;
    }


    private void addWsawUsingAddressingForCompatibility(Collection<PolicySubject> subjects, PolicyMap policyMap, SEIModel model, AddressingFeature addressingFeature) throws PolicyException {
        final AddressingVersion addressingVersion = AddressingVersion.fromFeature(addressingFeature);
        final QName usingAddressing = new QName(addressingVersion.policyNsUri, "UsingAddressing");
        final PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(model.getServiceQName(), model.getPortName());
        final Policy existingPolicy = policyMap.getEndpointEffectivePolicy(endpointKey);
        if ((existingPolicy == null) || !existingPolicy.contains(usingAddressing)) {
            final QName bindingName = model.getBoundPortTypeName();
            final WsdlBindingSubject wsdlSubject = WsdlBindingSubject.createBindingSubject(bindingName);
            final Policy addressingPolicy = createWsawAddressingPolicy(bindingName, usingAddressing, addressingFeature.isRequired());
            final PolicySubject addressingPolicySubject = new PolicySubject(wsdlSubject, addressingPolicy);
            subjects.add(addressingPolicySubject);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Added addressing policy with ID \"" + addressingPolicy.getIdOrName() + "\" to binding element \"" + bindingName + "\"");
            }
        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Addressing policy exists already, doing nothing");
            }
        }
    }

    /**
     * Create a policy with an WSAW UsingAddressing assertion.
     *
     * @param bindingName   The wsdl:binding element name. Used to generate a (locally) unique ID for the policy.
     * @param assertionName The fully qualified name of the addressing policy assertion.
     * @param isRequired    True, if the addressing feature was set to required, false otherwise.
     * @return A policy that contains one policy assertion that corresponds to the given assertion name.
     */
    private Policy createWsawAddressingPolicy(final QName bindingName, final QName assertionName, final boolean isRequired) {
        final ArrayList<AssertionSet> assertionSets = new ArrayList<>(1);
        final ArrayList<PolicyAssertion> assertions = new ArrayList<>(1);
        final AssertionData addressingData =
                AssertionData.createAssertionData(assertionName);
        if (!isRequired) {
            addressingData.setOptionalAttribute(true);
        }
        assertions.add(new AddressingAssertion(addressingData));
        assertionSets.add(AssertionSet.createAssertionSet(assertions));
        return Policy.createPolicy(null, bindingName.getLocalPart() + "_Wsaw_Addressing_Policy", assertionSets);
    }
}
