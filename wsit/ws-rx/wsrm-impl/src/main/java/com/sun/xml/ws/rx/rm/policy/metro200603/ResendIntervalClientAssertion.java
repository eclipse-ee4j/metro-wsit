/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.metro200603;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.SimpleAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import com.sun.xml.ws.rx.rm.policy.RmConfigurator;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 * <sunc:ResendInterval Milliseconds="..." />
 */
/**
 * Specifies how long the RM Source will wait after transmitting a message and
 * before retransmitting the message. If omitted, value from
 * {@link ReliableMessagingFeature#DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL}
 * is used. Specified in milliseconds.
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class ResendIntervalClientAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = RmAssertionNamespace.METRO_CLIENT_200603.getQName("ResendInterval");
    private static final QName MILLISECONDS_ATTRIBUTE_QNAME = new QName("", "Milliseconds");

    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative){
            return new ResendIntervalClientAssertion(data, assertionParameters);
        }
    };
    
    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    private final long interval;
    
    public ResendIntervalClientAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);
        
        interval = Long.parseLong(super.getAttributeValue(MILLISECONDS_ATTRIBUTE_QNAME));
    }
   
    public long getInterval() {
        return interval;
    }

    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        return builder.messageRetransmissionInterval(interval);
    }

    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200502 == version;
    }
}
