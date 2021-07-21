/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import java.util.Collection;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceException;

/**
 * <sunc:Ordered />
 */
/**
  * Proprietary assertion that works with WS-RM v1.0 (WSRM200502) and enables
 * "In Order" message delivery:
 * <p>
 * Messages from each individual Sequence are to be delivered in the same order
 * they have been sent by the Application Source. The requirement on an RM Source
 * is that it MUST ensure that the ordinal position of each message in the Sequence
 * (as indicated by a message Sequence number) is consistent with the order in
 * which the messages have been sent from the Application Source. The requirement
 * on the RM Destination is that it MUST deliver received messages for each Sequence
 * in the order indicated by the message numbering. This DeliveryAssurance can be
 * used in combination with any of the AtLeastOnce, AtMostOnce or ExactlyOnce
 * assertions, and the requirements of those assertions MUST also be met. In
 * particular if the AtLeastOnce or ExactlyOnce assertion applies and the RM
 * Destination detects a gap in the Sequence then the RM Destination MUST NOT
 * deliver any subsequent messages from that Sequence until the missing messages
 * are received or until the Sequence is closed.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class OrderedDeliveryAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = RmAssertionNamespace.METRO_200603.getQName("Ordered");
    
    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative){
            return new OrderedDeliveryAssertion(data, assertionParameters);
        }
    };
    
    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    public OrderedDeliveryAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);
    }

    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        if (builder.getProtocolVersion() != RmProtocolVersion.WSRM200502) {
            throw new WebServiceException(LocalizationMessages.WSRM_1001_ASSERTION_NOT_COMPATIBLE_WITH_RM_VERSION(NAME, builder.getProtocolVersion()));
        }

        return builder.enableOrderedDelivery();
    }

    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200502 == version;
    }
}
