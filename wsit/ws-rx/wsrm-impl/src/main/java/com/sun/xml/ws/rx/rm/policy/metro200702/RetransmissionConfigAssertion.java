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

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.ComplexAssertion;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import com.sun.xml.ws.rx.rm.policy.RmConfigurator;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.BackoffAlgorithm;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 *
 */
public class RetransmissionConfigAssertion extends ComplexAssertion implements RmConfigurator {

    public static final QName NAME = RmAssertionNamespace.METRO_200702.getQName("RetransmissionConfig");
    //
    private static final Logger LOGGER = Logger.getLogger(RetransmissionConfigAssertion.class);
    //
    private static final QName INTERVAL_PARAMETER_QNAME = RmAssertionNamespace.METRO_200702.getQName("Interval");
    private static final QName ALGORITHM_PARAMETER_QNAME = RmAssertionNamespace.METRO_200702.getQName("Algorithm");
    private static final QName MAX_RETRIES_PARAMETER_QNAME = RmAssertionNamespace.METRO_200702.getQName("MaxRetries");
    //
    private static final QName MILLISECONDS_ATTRIBUTE_QNAME = new QName("", "Milliseconds");
    //
    private static AssertionInstantiator instantiator = new AssertionInstantiator() {

        @Override
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
            return new RetransmissionConfigAssertion(data, assertionParameters, nestedAlternative);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }
    private final long interval;
    private final long maxRetries;
    private final ReliableMessagingFeature.BackoffAlgorithm algorithm;

    private RetransmissionConfigAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
        super(data, assertionParameters, nestedAlternative);

        if (assertionParameters == null || assertionParameters.isEmpty()) {
            // TODO P1
            throw new AssertionCreationException(data, "No assertion parameters found.");
        }
        PolicyAssertion _interval = getParameter(INTERVAL_PARAMETER_QNAME, data, assertionParameters);
        interval = (_interval == null) ? ReliableMessagingFeature.DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL : Long.parseLong(_interval.getAttributeValue(MILLISECONDS_ATTRIBUTE_QNAME));

        PolicyAssertion _maxRetries = getParameter(MAX_RETRIES_PARAMETER_QNAME, data, assertionParameters);
        maxRetries = (_maxRetries == null) ? ReliableMessagingFeature.DEFAULT_MAX_MESSAGE_RETRANSMISSION_COUNT : Long.parseLong(_maxRetries.getValue());

        final PolicyAssertion algorithmParameter = getParameter(ALGORITHM_PARAMETER_QNAME, data, assertionParameters);
        BackoffAlgorithm _algorithm = (algorithmParameter == null) ? null : ReliableMessagingFeature.BackoffAlgorithm.parse(algorithmParameter.getValue());
        algorithm = (_algorithm == null) ? ReliableMessagingFeature.BackoffAlgorithm.getDefault() : _algorithm;
    }

    private static PolicyAssertion getParameter(@NotNull QName parameterName, AssertionData data, @NotNull Collection<? extends PolicyAssertion> assertionParameters) throws AssertionCreationException {
        assert parameterName != null;
        assert assertionParameters != null;

        PolicyAssertion parameter = null;
        boolean parameterSet = false;

        for (PolicyAssertion assertion : assertionParameters) {
            if (parameterName.equals(assertion.getName())) {
                if (parameterSet) {
                    throw LOGGER.logSevereException(new AssertionCreationException(
                            data,
                            LocalizationMessages.WSRM_1007_MULTIPLE_OCCURENCES_OF_ASSERTION_PARAMETER(parameterName, NAME)));
                } else {
                    parameter = assertion;
                }
            }
        }

        return parameter;
    }

    public BackoffAlgorithm getAlgorithm() {
        return algorithm;
    }

    public long getInterval() {
        return interval;
    }

    public long getMaxRetries() {
        return maxRetries;
    }

    @Override
    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        return builder.messageRetransmissionInterval(interval).retransmissionBackoffAlgorithm(algorithm).maxMessageRetransmissionCount(maxRetries);
    }

    @Override
    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200702 == version;
    }
}
