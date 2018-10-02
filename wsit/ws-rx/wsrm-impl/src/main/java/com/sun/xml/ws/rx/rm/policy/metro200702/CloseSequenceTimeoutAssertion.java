/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * <metro:CloseSequenceTimeout Milliseconds="..." />
 */
/**
 * By default, the call to proxy.close() will not return until all messages have
 * been acknowledged. RM close timeout is the interval (in milliseconds) that the
 * client runtime will block waiting for a call to close() to return. If there are
 * still unacknowledged messages after this interval is reached, and the call to
 * close has returned, an error will be logged about messages being lost.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */

public class CloseSequenceTimeoutAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = RmAssertionNamespace.METRO_200702.getQName("CloseSequenceTimeout");
    private static final QName MILLISECONDS_ATTRIBUTE_QNAME = new QName("", "Milliseconds");

    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
            return new CloseSequenceTimeoutAssertion(data, assertionParameters);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    private final long interval;

    private CloseSequenceTimeoutAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);

        interval = Long.parseLong(super.getAttributeValue(MILLISECONDS_ATTRIBUTE_QNAME));
    }

    public long getInterval() {
        return interval;
    }

    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        return builder.closeSequenceOperationTimeout(interval);
    }

    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200702 == version;
    }
}
