/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * Assertion which replaces acknowledgement interval attribute of WS-RMP v1.0 RMAssertion.
 * The same assertion is used by .Net framework which could simplify the interoperability.
 *
 * <pre>
 * <netrmp:AcknowledgementInterval Milliseconds="200" xmlns:netrmp="http://schemas.microsoft.com/ws-rx/wsrmp/200702"/>
 * </pre>
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class AcknowledgementIntervalAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = RmAssertionNamespace.MICROSOFT_200702.getQName("AcknowledgementInterval");
    private static final QName MILISECONDS_ATTRIBUTE_QNAME = new QName("", "Milliseconds");

    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative){
            return new AcknowledgementIntervalAssertion(data, assertionParameters);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    private final long timeout;

    public AcknowledgementIntervalAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);

        timeout = Long.parseLong(data.getAttributeValue(MILISECONDS_ATTRIBUTE_QNAME));
    }

    public long getTimeout() {
        return timeout;
    }

    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        return builder.acknowledgementTransmissionInterval(timeout);
    }

    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200702 == version;
    }
}
