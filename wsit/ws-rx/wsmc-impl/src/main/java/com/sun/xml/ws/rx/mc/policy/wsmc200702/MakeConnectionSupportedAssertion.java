/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.policy.wsmc200702;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.SimpleAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.mc.policy.McAssertionNamespace;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 * <pre>{@code
 * <wsmc:MCSupported ...>...</wsmc:MCSupported>
 * }</pre>
 *
 * <p>
 * The MakeConnection policy assertion indicates that the MakeConnection protocol
 * (operation and the use of the MakeConnection URI template in EndpointReferences)
 * is required for messages sent from this endpoint.
 * </p>
 * <p>
 * This assertion has Endpoint Policy Subject
 *
 */
public class MakeConnectionSupportedAssertion extends SimpleAssertion {
    public static final QName NAME = McAssertionNamespace.WSMC_200702.getQName("MCSupported");

    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        @Override
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
            return new MakeConnectionSupportedAssertion(data, assertionParameters);
        }
    };

    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    public MakeConnectionSupportedAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);
    }

    public MakeConnectionSupportedAssertion() {
        super(AssertionData.createAssertionData(NAME), null);
    }
}
