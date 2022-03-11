/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.spi_impl;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import com.sun.xml.ws.rx.rm.policy.net200502.RmFlowControlAssertion;
import com.sun.xml.ws.rx.rm.policy.wsrm200502.Rm10Assertion;
import com.sun.xml.ws.rx.rm.policy.wsrm200702.DeliveryAssuranceAssertion;
import com.sun.xml.ws.rx.rm.policy.wsrm200702.Rm11Assertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.AckRequestIntervalClientAssertion;
import com.sun.xml.ws.rx.rm.policy.net200702.AcknowledgementIntervalAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.AllowDuplicatesAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.CloseTimeoutClientAssertion;
import com.sun.xml.ws.rx.rm.policy.net200702.InactivityTimeoutAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.OrderedDeliveryAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.ResendIntervalClientAssertion;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.rm.policy.metro200702.AckRequestIntervalAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.CloseSequenceTimeoutAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.MaintenanceTaskPeriodAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.MaxConcurrentSessionsAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.PersistentAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.RetransmissionConfigAssertion;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class RmAssertionCreator implements PolicyAssertionCreator {

    private static final Map<QName, AssertionInstantiator> instantiationMap = new HashMap<>();
    static {
        // metro200603
        instantiationMap.put(AckRequestIntervalClientAssertion.NAME, AckRequestIntervalClientAssertion.getInstantiator());
        instantiationMap.put(AllowDuplicatesAssertion.NAME, AllowDuplicatesAssertion.getInstantiator());
        instantiationMap.put(CloseTimeoutClientAssertion.NAME, CloseTimeoutClientAssertion.getInstantiator());
        instantiationMap.put(OrderedDeliveryAssertion.NAME, OrderedDeliveryAssertion.getInstantiator());
        instantiationMap.put(ResendIntervalClientAssertion.NAME, ResendIntervalClientAssertion.getInstantiator());

        // metro200702
        instantiationMap.put(AckRequestIntervalAssertion.NAME, AckRequestIntervalAssertion.getInstantiator());
        instantiationMap.put(CloseSequenceTimeoutAssertion.NAME, CloseSequenceTimeoutAssertion.getInstantiator());
        instantiationMap.put(MaintenanceTaskPeriodAssertion.NAME, MaintenanceTaskPeriodAssertion.getInstantiator());
        instantiationMap.put(MaxConcurrentSessionsAssertion.NAME, MaxConcurrentSessionsAssertion.getInstantiator());
        instantiationMap.put(PersistentAssertion.NAME, PersistentAssertion.getInstantiator());
        instantiationMap.put(RetransmissionConfigAssertion.NAME, RetransmissionConfigAssertion.getInstantiator());

        // net200502
        instantiationMap.put(RmFlowControlAssertion.NAME, RmFlowControlAssertion.getInstantiator());

        // net200702
        instantiationMap.put(AcknowledgementIntervalAssertion.NAME, AcknowledgementIntervalAssertion.getInstantiator());
        instantiationMap.put(InactivityTimeoutAssertion.NAME, InactivityTimeoutAssertion.getInstantiator());

        // wsrm200502
        instantiationMap.put(Rm10Assertion.NAME, Rm10Assertion.getInstantiator());

        // wsrm200702
        instantiationMap.put(DeliveryAssuranceAssertion.NAME, DeliveryAssuranceAssertion.getInstantiator());
        instantiationMap.put(Rm11Assertion.NAME, Rm11Assertion.getInstantiator());

    }

    private static final List<String> SUPPORTED_DOMAINS = Collections.unmodifiableList(RmAssertionNamespace.namespacesList());

    @Override
    public String[] getSupportedDomainNamespaceURIs() {
        return SUPPORTED_DOMAINS.toArray(new String[0]);
    }

    @Override
    public PolicyAssertion createAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative, PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
        AssertionInstantiator instantiator = instantiationMap.get(data.getName());
        if (instantiator != null) {
            return instantiator.newInstance(data, assertionParameters, nestedAlternative);
        } else {
            return defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, null);
        }
    }
}
