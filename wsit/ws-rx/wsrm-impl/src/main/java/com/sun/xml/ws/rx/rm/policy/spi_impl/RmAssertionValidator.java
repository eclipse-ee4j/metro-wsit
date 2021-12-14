/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.spi_impl;

import com.sun.xml.ws.rx.rm.policy.wsrm200502.Rm10Assertion;
import com.sun.xml.ws.rx.rm.policy.wsrm200702.Rm11Assertion;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator.Fitness;

import com.sun.xml.ws.rx.rm.policy.metro200603.AckRequestIntervalClientAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.AllowDuplicatesAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.CloseTimeoutClientAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.OrderedDeliveryAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200603.ResendIntervalClientAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.AckRequestIntervalAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.CloseSequenceTimeoutAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.MaintenanceTaskPeriodAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.MaxConcurrentSessionsAssertion;
import com.sun.xml.ws.rx.rm.policy.net200502.RmFlowControlAssertion;
import com.sun.xml.ws.rx.rm.policy.net200702.AcknowledgementIntervalAssertion;
import com.sun.xml.ws.rx.rm.policy.net200702.InactivityTimeoutAssertion;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;

import com.sun.xml.ws.rx.rm.policy.metro200702.PersistentAssertion;
import com.sun.xml.ws.rx.rm.policy.metro200702.RetransmissionConfigAssertion;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RmAssertionValidator implements PolicyAssertionValidator {

    private static final ArrayList<QName> SERVER_SIDE_ASSERTIONS = new ArrayList<>(13);
    private static final ArrayList<QName> CLIENT_SIDE_ASSERTIONS = new ArrayList<>(16);

    private static final List<String> SUPPORTED_DOMAINS = Collections.unmodifiableList(RmAssertionNamespace.namespacesList());

    static {
        //WSRM200502
        SERVER_SIDE_ASSERTIONS.add(Rm10Assertion.NAME);
        //WSRM200702
        SERVER_SIDE_ASSERTIONS.add(Rm11Assertion.NAME);
        //METRORM200603
        SERVER_SIDE_ASSERTIONS.add(AllowDuplicatesAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(OrderedDeliveryAssertion.NAME);
        //METRORM200702
        SERVER_SIDE_ASSERTIONS.add(AckRequestIntervalAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(CloseSequenceTimeoutAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(MaintenanceTaskPeriodAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(MaxConcurrentSessionsAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(PersistentAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(RetransmissionConfigAssertion.NAME);
        //NETRM200502
        SERVER_SIDE_ASSERTIONS.add(RmFlowControlAssertion.NAME);
        //NETRM200702
        SERVER_SIDE_ASSERTIONS.add(AcknowledgementIntervalAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(InactivityTimeoutAssertion.NAME);

        //METRORMC200603
        CLIENT_SIDE_ASSERTIONS.add(AckRequestIntervalClientAssertion.NAME);
        CLIENT_SIDE_ASSERTIONS.add(CloseTimeoutClientAssertion.NAME);
        CLIENT_SIDE_ASSERTIONS.add(ResendIntervalClientAssertion.NAME);
        //
        CLIENT_SIDE_ASSERTIONS.addAll(SERVER_SIDE_ASSERTIONS);
    }

    public RmAssertionValidator() {
    }

    @Override
    public Fitness validateClientSide(PolicyAssertion assertion) {
        return CLIENT_SIDE_ASSERTIONS.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }

    @Override
    public Fitness validateServerSide(PolicyAssertion assertion) {
        QName assertionName = assertion.getName();
        if (SERVER_SIDE_ASSERTIONS.contains(assertionName)) {
            return Fitness.SUPPORTED;
        } else if (CLIENT_SIDE_ASSERTIONS.contains(assertionName)) {
            return Fitness.UNSUPPORTED;
        } else {
            return Fitness.UNKNOWN;
        }
    }

    @Override
    public String[] declareSupportedDomains() {
        return SUPPORTED_DOMAINS.toArray(new String[0]);
    }
}
