/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.wsrm200702;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import java.util.Collection;
import javax.xml.namespace.QName;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.ComplexAssertion;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import com.sun.xml.ws.rx.rm.policy.RmConfigurator;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.DeliveryAssurance;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.SecurityBinding;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import jakarta.xml.ws.WebServiceException;

/**
 * <pre>{@code
 * <wsrmp:RMAssertion [wsp:Optional="true"]? ... >
 *   <wsp:Policy>
 *     [ <wsrmp:SequenceSTR/> |
 *       <wsrmp:SequenceTransportSecurity/> ] ?
 *     <wsrmp:DeliveryAssurance>
 *       <wsp:Policy>
 *         [ <wsrmp:ExactlyOnce/> |
 *           <wsrmp:AtLeastOnce/> |
 *           <wsrmp:AtMostOnce/> ]
 *         <wsrmp:InOrder/> ?
 *       </wsp:Policy>
 *     </wsrmp:DeliveryAssurance> ?
 *   </wsp:Policy>
 *   ...
 * </wsrmp:RMAssertion>
 * }</pre>
 */
public final class Rm11Assertion extends ComplexAssertion implements RmConfigurator {
    // TODO: add new assertions for acknowledgement interval and backoff algorithm

    private static final Logger LOGGER = Logger.getLogger(Rm11Assertion.class);
    //
    public static final QName NAME = RmProtocolVersion.WSRM200702.rmAssertionName;
    private static final QName SEQUENCE_STR_QNAME = RmAssertionNamespace.WSRMP_200702.getQName("SequenceSTR");
    private static final QName SEQUENCE_TRANSPORT_SECURITY_QNAME = RmAssertionNamespace.WSRMP_200702.getQName("SequenceTransportSecurity");
    private static AssertionInstantiator instantiator = new AssertionInstantiator() {

        @Override
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
            return new Rm11Assertion(data, assertionParameters, nestedAlternative);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }
    private final SecurityBinding securityBinding;
    private final DeliveryAssurance deliveryAssurance;
    private final boolean isOrderedDelivery;

    private Rm11Assertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
        super(data, assertionParameters, nestedAlternative);

        SecurityBinding _securityBinding = SecurityBinding.NONE;
        DeliveryAssuranceAssertion deliveryAssuranceAssertion = null;

        if (nestedAlternative != null) {
            for (PolicyAssertion nestedAssertion : nestedAlternative) {
                if (SEQUENCE_STR_QNAME.equals(nestedAssertion.getName())) {
                    _securityBinding = evaluateDeliveryAssurance(_securityBinding == SecurityBinding.NONE, SecurityBinding.STR, data);
                } else if (SEQUENCE_TRANSPORT_SECURITY_QNAME.equals(nestedAssertion.getName())) {
                    _securityBinding = evaluateDeliveryAssurance(_securityBinding == SecurityBinding.NONE, SecurityBinding.TRANSPORT, data);
                } else if (DeliveryAssuranceAssertion.NAME.equals(nestedAssertion.getName())) {
                    deliveryAssuranceAssertion = (DeliveryAssuranceAssertion) nestedAssertion;
                }
            }
        }

        if (deliveryAssuranceAssertion == null) {
            deliveryAssurance = DeliveryAssurance.getDefault();
            isOrderedDelivery = false;
        } else {
            deliveryAssurance = deliveryAssuranceAssertion.getDeliveryAssurance();
            isOrderedDelivery = deliveryAssuranceAssertion.isOrderedDelivery();
        }

        securityBinding = _securityBinding;
    }

    public DeliveryAssurance getDeliveryAssurance() {
        return deliveryAssurance;
    }

    public boolean isOrderedDelivery() {
        return isOrderedDelivery;
    }

    public SecurityBinding getSecurityBinding() {
        return securityBinding;
    }

    private SecurityBinding evaluateDeliveryAssurance(boolean successCondition, SecurityBinding bindingOnSuccess, AssertionData data) throws AssertionCreationException {
        if (successCondition) {
            return bindingOnSuccess;
        } else {
            throw LOGGER.logSevereException(new AssertionCreationException(data, LocalizationMessages.WSRM_1005_MULTIPLE_SECURITY_BINDINGS_IN_POLICY()));
        }
    }

    @Override
    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        if (builder.getProtocolVersion() != RmProtocolVersion.WSRM200702) {
            throw new WebServiceException(LocalizationMessages.WSRM_1002_MULTIPLE_WSRM_VERSIONS_IN_POLICY());
        }

        if (isOrderedDelivery) {
            builder = builder.enableOrderedDelivery();
        }

        return builder.deliveryAssurance(deliveryAssurance).securityBinding(securityBinding);
    }

    @Override
    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200702 == version;
    }
}
