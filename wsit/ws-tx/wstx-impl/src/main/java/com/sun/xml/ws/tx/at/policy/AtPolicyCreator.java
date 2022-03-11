/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.policy;

import java.util.Arrays;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.api.tx.at.Transactional.TransactionFlowType;
import com.sun.xml.ws.api.tx.at.WsatNamespace;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class AtPolicyCreator {
    private static final Map<WsatNamespace, Map<TransactionFlowType, Map<EjbTransactionType, Collection<WsatAssertionBase>>>> SUPPORTED_COMBINATIONS;

    private static void registerCombination(WsatNamespace version, TransactionFlowType flowType, EjbTransactionType ejbTat, WsatAssertionBase... assertions) {
        if (assertions == null) {
            assertions = new WsatAssertionBase[0];
        }
        SUPPORTED_COMBINATIONS.get(version).get(flowType).put(ejbTat, Arrays.asList(assertions));
    }

    static {
        SUPPORTED_COMBINATIONS = new EnumMap<>(WsatNamespace.class);
        for (WsatNamespace ns : WsatNamespace.values()) {
            Map<TransactionFlowType, Map<EjbTransactionType, Collection<WsatAssertionBase>>> nsMap = new EnumMap<>(TransactionFlowType.class);
            for (TransactionFlowType flowType : TransactionFlowType.values()) {
                nsMap.put(flowType, new EnumMap<>(EjbTransactionType.class));
            }
            SUPPORTED_COMBINATIONS.put(ns, nsMap);
        }

        // WSAT200410
        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.MANDATORY, EjbTransactionType.NOT_DEFINED, new AtAssertion(WsatNamespace.WSAT200410, false));
        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.MANDATORY, EjbTransactionType.MANDATORY, new AtAssertion(WsatNamespace.WSAT200410, false));
        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.MANDATORY, EjbTransactionType.REQUIRED, new AtAssertion(WsatNamespace.WSAT200410, false));

        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.SUPPORTS, EjbTransactionType.NOT_DEFINED, new AtAssertion(WsatNamespace.WSAT200410, true));
        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.SUPPORTS, EjbTransactionType.SUPPORTS, new AtAssertion(WsatNamespace.WSAT200410, true));
        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.SUPPORTS, EjbTransactionType.REQUIRED, new AtAssertion(WsatNamespace.WSAT200410, true), new AtAlwaysCapability(false));

        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.NEVER, EjbTransactionType.NOT_DEFINED); // no assertions
        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.NEVER, EjbTransactionType.NEVER); // no assertions
        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.NEVER, EjbTransactionType.REQUIRES_NEW, new AtAlwaysCapability(false));
        registerCombination(WsatNamespace.WSAT200410, TransactionFlowType.NEVER, EjbTransactionType.REQUIRED, new AtAlwaysCapability(false));

        // WSAT200606
        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.MANDATORY, EjbTransactionType.NOT_DEFINED, new AtAssertion(WsatNamespace.WSAT200606, false));
        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.MANDATORY, EjbTransactionType.MANDATORY, new AtAssertion(WsatNamespace.WSAT200606, false));
        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.MANDATORY, EjbTransactionType.REQUIRED, new AtAssertion(WsatNamespace.WSAT200606, false));

        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.SUPPORTS, EjbTransactionType.NOT_DEFINED, new AtAssertion(WsatNamespace.WSAT200606, true));
        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.SUPPORTS, EjbTransactionType.SUPPORTS, new AtAssertion(WsatNamespace.WSAT200606, true));
        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.SUPPORTS, EjbTransactionType.REQUIRED, new AtAssertion(WsatNamespace.WSAT200606, true));

        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.NEVER, EjbTransactionType.NOT_DEFINED); // no assertions
        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.NEVER, EjbTransactionType.NEVER); // no assertions
        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.NEVER, EjbTransactionType.REQUIRES_NEW); // no assertions
        registerCombination(WsatNamespace.WSAT200606, TransactionFlowType.NEVER, EjbTransactionType.NOT_SUPPORTED); // no assertions
    }

    public static Policy createPolicy(String policyId, WsatNamespace version, Transactional.TransactionFlowType wsatFlowType, EjbTransactionType ejbTat) {
        if (wsatFlowType == null || ejbTat == null) {
            return null;
        }

        final Collection<WsatAssertionBase> assertions = AtPolicyCreator.SUPPORTED_COMBINATIONS.get(version).get(wsatFlowType).get(ejbTat);
        if (assertions == null) {
            throw new IllegalArgumentException(String.format("Unsupported combinantion: WS-AT namespace: [ %s ], WS-AT flow type: [ %s ], EJB transaction attribute: [ %s ]", version, wsatFlowType, ejbTat));
        }
        if (assertions.isEmpty()) {
            return null;
        }

        final List<AssertionSet> assertionSets = new ArrayList<>(1);
        assertionSets.add(AssertionSet.createAssertionSet(assertions));

        return Policy.createPolicy("", policyId, assertionSets);
    }
}
