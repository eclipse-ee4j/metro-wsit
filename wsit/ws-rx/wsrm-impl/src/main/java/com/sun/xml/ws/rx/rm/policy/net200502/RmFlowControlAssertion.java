/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.net200502;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.ComplexAssertion;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import com.sun.xml.ws.rx.rm.policy.RmConfigurator;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 * <pre>{@code
 * <ms:RmFlowControl>
 *   <ms:MaxReceiveBufferSize>value</ms:MaxReceiveBufferSize>
 * </ms:RmFlowControl>
 * }</pre>
 *
 * Defines maximum server-side buffer size in ordered delivery scenario.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class RmFlowControlAssertion extends ComplexAssertion implements RmConfigurator {

    public static final QName NAME = RmAssertionNamespace.MICROSOFT_200502.getQName("RmFlowControl");
    //    
    private static final Logger LOGGER = Logger.getLogger(RmFlowControlAssertion.class);
    private static final QName BUFFER_SIZE_ASSERTION_QNAME = RmAssertionNamespace.MICROSOFT_200502.getQName("MaxReceiveBufferSize");
    private static final long DEFAULT_DESTINATION_BUFFER_QUOTA = 32;
    //
    private static AssertionInstantiator instantiator = new AssertionInstantiator() {

        @Override
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
            return new RmFlowControlAssertion(data, assertionParameters, nestedAlternative);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }
    private final long maxBufferSize;

    private RmFlowControlAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
        super(data, assertionParameters, nestedAlternative);

        long _maxBufferSize = DEFAULT_DESTINATION_BUFFER_QUOTA; // default
        boolean bufferSizeSet = false;
        if (assertionParameters != null) {
            for (PolicyAssertion assertion : assertionParameters) {
                if (BUFFER_SIZE_ASSERTION_QNAME.equals(assertion.getName())) {
                    if (bufferSizeSet) {
                        throw LOGGER.logSevereException(new AssertionCreationException(data, LocalizationMessages.WSRM_1006_MULTIPLE_BUFFER_SIZES_IN_POLICY()));
                    } else {
                        _maxBufferSize = Long.parseLong(assertion.getValue());
                    }
                }
            }
        }
        maxBufferSize = _maxBufferSize;
    }

    public long getMaximumBufferSize() {
        return maxBufferSize;
    }

    @Override
    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        return builder.destinationBufferQuota(maxBufferSize);
    }

    @Override
    public boolean isCompatibleWith(RmProtocolVersion version) {
        return RmProtocolVersion.WSRM200502 == version || RmProtocolVersion.WSRM200702 == version;
    }
}
