/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.DeliveryAssurance;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import java.util.Collection;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceException;

/**
 * <sunc:AllowDuplicates />
 */
/**
 * Proprietary assertion that works with WS-RM v1.0 (WSRM200502) and enables 
 * "At Least Once" message delivery:
 * <p />
 * Each message is to be delivered at least once, or else an error MUST be raised 
 * by the RM Source and/or RM Destination. The requirement on an RM Source is that 
 * it SHOULD retry transmission of every message sent by the Application Source 
 * until it receives an acknowledgement from the RM Destination. The requirement 
 * on the RM Destination is that it SHOULD retry the transfer to the Application 
 * Destination of any message that it accepts from the RM Source, until that message 
 * has been successfully delivered. There is no requirement for the RM Destination 
 * to apply duplicate message filtering.
 * <p />
 * NOTE: this assertion has currently no effect, we treat the case the same way as 
 * "Exactly Once" delivery mode.
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class AllowDuplicatesAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = RmAssertionNamespace.METRO_200603.getQName("AllowDuplicates");
    
    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
            return new AllowDuplicatesAssertion(data, assertionParameters);
        }
    };
    
    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }
    
    public AllowDuplicatesAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);
    }

    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        if (builder.getProtocolVersion() != RmProtocolVersion.WSRM200502) {
            throw new WebServiceException(LocalizationMessages.WSRM_1001_ASSERTION_NOT_COMPATIBLE_WITH_RM_VERSION(NAME, builder.getProtocolVersion()));
        }

        return builder.deliveryAssurance(DeliveryAssurance.AT_LEAST_ONCE);
    }
    
    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200502 == version;
    }
}
