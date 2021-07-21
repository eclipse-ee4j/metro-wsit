/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.metro200702;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.SimpleAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import com.sun.xml.ws.rx.rm.policy.RmConfigurator;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 * <metro:MaintenanceTaskPeriod Milliseconds="..." />
 */
/**
 * Specifies the period (in milliseconds) of a sequence maintenance task execution. 
 * Sequence maintenance task takes care of terminating inactive sequences and 
 * removing the terminated sequences from the sequence repository.
 *
 */
public class MaintenanceTaskPeriodAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = RmAssertionNamespace.METRO_200702.getQName("MaintenanceTaskPeriod");
    private static final QName MILLISECONDS_ATTRIBUTE_QNAME = new QName("", "Milliseconds");

    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
            return new MaintenanceTaskPeriodAssertion(data, assertionParameters);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    private final long interval;

    private MaintenanceTaskPeriodAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);

        interval = Long.parseLong(super.getAttributeValue(MILLISECONDS_ATTRIBUTE_QNAME));
    }

    public long getInterval() {
        return interval;
    }

    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        return builder.sequenceMaintenancePeriod(interval);
    }

    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200702 == version;
    }
}
