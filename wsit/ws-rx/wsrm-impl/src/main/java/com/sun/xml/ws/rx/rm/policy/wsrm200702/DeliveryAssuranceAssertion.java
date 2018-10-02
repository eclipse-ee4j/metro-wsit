/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.wsrm200702;

import com.sun.istack.logging.Logger;
import java.util.Collection;
import javax.xml.namespace.QName;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.ComplexAssertion;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.DeliveryAssurance;

/**
 * <wsrmp:DeliveryAssurance>
 *   <wsp:Policy>
 *     [ <wsrmp:ExactlyOnce/> |
 *       <wsrmp:AtLeastOnce/> |
 *       <wsrmp:AtMostOnce/> ]
 *     <wsrmp:InOrder/> ?
 *   </wsp:Policy>
 * </wsrmp:DeliveryAssurance>
 */
/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class DeliveryAssuranceAssertion extends ComplexAssertion {

    private static final Logger LOGGER = Logger.getLogger(DeliveryAssuranceAssertion.class);
    private static final QName EXACTLY_ONCE_QNAME = RmAssertionNamespace.WSRMP_200702.getQName("ExactlyOnce");
    private static final QName AT_LEAST_ONCE_QNAME = RmAssertionNamespace.WSRMP_200702.getQName("AtLeastOnce");
    private static final QName AT_MOST_ONCE_QNAME = RmAssertionNamespace.WSRMP_200702.getQName("AtMostOnce");
    private static final QName IN_ORDER_QNAME = RmAssertionNamespace.WSRMP_200702.getQName("InOrder");
    public static final QName NAME = RmAssertionNamespace.WSRMP_200702.getQName("DeliveryAssurance");
    private static AssertionInstantiator instantiator = new AssertionInstantiator() {

        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
            return new DeliveryAssuranceAssertion(data, assertionParameters, nestedAlternative);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }
    private final DeliveryAssurance deliveryAssurance;
    private final boolean orderedDelivery;

    private DeliveryAssuranceAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
        super(data, assertionParameters, nestedAlternative);

        DeliveryAssurance _deliveryAssurance = null;
        boolean _orderedDelivery = false;

        if (nestedAlternative != null) {
            for (PolicyAssertion nestedAssertion : nestedAlternative) {

                if (EXACTLY_ONCE_QNAME.equals(nestedAssertion.getName())) {
                    _deliveryAssurance = evaluateDeliveryAssurance(_deliveryAssurance == null, DeliveryAssurance.EXACTLY_ONCE, data);
                } else if (AT_LEAST_ONCE_QNAME.equals(nestedAssertion.getName())) {
                    _deliveryAssurance = evaluateDeliveryAssurance(_deliveryAssurance == null, DeliveryAssurance.AT_LEAST_ONCE, data);
                } else if (AT_MOST_ONCE_QNAME.equals(nestedAssertion.getName())) {
                    _deliveryAssurance = evaluateDeliveryAssurance(_deliveryAssurance == null, DeliveryAssurance.AT_MOST_ONCE, data);
                } else if (IN_ORDER_QNAME.equals(nestedAssertion.getName())) {
                    _orderedDelivery = true;
                }
            }
        }
        deliveryAssurance = (_deliveryAssurance == null) ? DeliveryAssurance.getDefault() : _deliveryAssurance;
        orderedDelivery = _orderedDelivery;
    }

    public DeliveryAssurance getDeliveryAssurance() {
        return deliveryAssurance;
    }

    public boolean isOrderedDelivery() {
        return orderedDelivery;
    }

    private DeliveryAssurance evaluateDeliveryAssurance(boolean successCondition, DeliveryAssurance daOnSuccess, AssertionData data) throws AssertionCreationException {
        if (successCondition) {
            return daOnSuccess;
        } else {
            throw LOGGER.logSevereException(new AssertionCreationException(data, LocalizationMessages.WSRM_1003_MUTLIPLE_DA_TYPES_IN_POLICY()));
        }
    }
}
