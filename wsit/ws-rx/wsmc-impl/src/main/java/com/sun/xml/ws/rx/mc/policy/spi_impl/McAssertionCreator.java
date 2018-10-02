/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.policy.spi_impl;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.ws.rx.policy.AssertionInstantiator;
import com.sun.xml.ws.rx.mc.policy.McAssertionNamespace;
import com.sun.xml.ws.rx.mc.policy.wsmc200702.MakeConnectionSupportedAssertion;
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
public final class McAssertionCreator implements PolicyAssertionCreator {

    private static final Map<QName, AssertionInstantiator> instantiationMap = new HashMap<QName, AssertionInstantiator>();
    static {
        instantiationMap.put(MakeConnectionSupportedAssertion.NAME, MakeConnectionSupportedAssertion.getInstantiator());
    }    
    
    private static final List<String> SUPPORTED_DOMAINS = Collections.unmodifiableList(McAssertionNamespace.namespacesList());

    public String[] getSupportedDomainNamespaceURIs() {
        return SUPPORTED_DOMAINS.toArray(new String[SUPPORTED_DOMAINS.size()]);
    }

    public PolicyAssertion createAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative, PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
        AssertionInstantiator instantiator = instantiationMap.get(data.getName());
        if (instantiator != null) {
            return instantiator.newInstance(data, assertionParameters, nestedAlternative);
        } else {
            return defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, null);
        }
    }
}
