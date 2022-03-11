/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.net200702;

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
 * Assertion which replaces inactivity timeout attribute of WS-RMP v1.0 RMAssertion.
 * The same assertion is used by .Net framework which could simplify the interoperability.
 *
 * <pre>{@code
 * <netrmp:InactivityTimeout Milliseconds="600000" xmlns:netrmp="http://schemas.microsoft.com/ws-rx/wsrmp/200702"/>
 * }</pre>
 */
public class InactivityTimeoutAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = RmAssertionNamespace.MICROSOFT_200702.getQName("InactivityTimeout");
    private static final QName MILISECONDS_ATTRIBUTE_QNAME = new QName("", "Milliseconds");

    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        @Override
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative){
            return new InactivityTimeoutAssertion(data, assertionParameters);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    private final long timeout;

    public InactivityTimeoutAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);

        timeout = Long.parseLong(data.getAttributeValue(MILISECONDS_ATTRIBUTE_QNAME));
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        return builder.sequenceInactivityTimeout(timeout);
    }

    @Override
    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200702 == version;
    }
}
