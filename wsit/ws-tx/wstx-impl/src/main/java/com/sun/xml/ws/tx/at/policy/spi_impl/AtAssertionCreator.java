/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.policy.spi_impl;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.ws.api.tx.at.WsatNamespace;
import com.sun.xml.ws.tx.at.policy.AtAlwaysCapability;
import com.sun.xml.ws.tx.at.policy.AtAssertion;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class AtAssertionCreator implements PolicyAssertionCreator {
    private interface AssertionInstantiator {
        PolicyAssertion instantiate(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative);
    }

    private static final Map<QName, AssertionInstantiator> instantiationMap = new HashMap<>();
    static {
        final AssertionInstantiator atAssertionInstantiator = new AssertionInstantiator() {

            @Override
            public PolicyAssertion instantiate(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
                return new AtAssertion(data, assertionParameters);
            }
        };

        for (WsatNamespace ns : WsatNamespace.values()) {
            instantiationMap.put(AtAssertion.nameForNamespace(ns), atAssertionInstantiator);
        }
        instantiationMap.put(AtAlwaysCapability.NAME, new AssertionInstantiator() {

            @Override
            public PolicyAssertion instantiate(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
                return new AtAlwaysCapability(data, assertionParameters);
            }
        });
    }

    private static final List<String> SUPPORTED_DOMAINS = Collections.unmodifiableList(WsatNamespace.namespacesList());

    @Override
    public String[] getSupportedDomainNamespaceURIs() {
        return SUPPORTED_DOMAINS.toArray(new String[0]);
    }

    @Override
    public PolicyAssertion createAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative, PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
        AssertionInstantiator instantiator = instantiationMap.get(data.getName());
        if (instantiator != null) {
            return instantiator.instantiate(data, assertionParameters, nestedAlternative);
        } else {
            return defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, null);
        }
    }
}
