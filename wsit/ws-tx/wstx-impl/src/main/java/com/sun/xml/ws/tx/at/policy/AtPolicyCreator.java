/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

import static com.sun.xml.ws.api.tx.at.Transactional.TransactionFlowType.*;
import static com.sun.xml.ws.api.tx.at.WsatNamespace.*;

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
        SUPPORTED_COMBINATIONS = new EnumMap<WsatNamespace, Map<TransactionFlowType, Map<EjbTransactionType, Collection<WsatAssertionBase>>>>(WsatNamespace.class);
        for (WsatNamespace ns : WsatNamespace.values()) {
            Map<TransactionFlowType, Map<EjbTransactionType, Collection<WsatAssertionBase>>> nsMap = new EnumMap<TransactionFlowType, Map<EjbTransactionType, Collection<WsatAssertionBase>>>(TransactionFlowType.class);
            for (TransactionFlowType flowType : TransactionFlowType.values()) {
                nsMap.put(flowType, new EnumMap<EjbTransactionType, Collection<WsatAssertionBase>>(EjbTransactionType.class));
            }
            SUPPORTED_COMBINATIONS.put(ns, nsMap);
        }

        // WSAT200410
        registerCombination(WSAT200410, MANDATORY, EjbTransactionType.NOT_DEFINED, new AtAssertion(WSAT200410, false));
        registerCombination(WSAT200410, MANDATORY, EjbTransactionType.MANDATORY, new AtAssertion(WSAT200410, false));
        registerCombination(WSAT200410, MANDATORY, EjbTransactionType.REQUIRED, new AtAssertion(WSAT200410, false));

        registerCombination(WSAT200410, SUPPORTS, EjbTransactionType.NOT_DEFINED, new AtAssertion(WSAT200410, true));
        registerCombination(WSAT200410, SUPPORTS, EjbTransactionType.SUPPORTS, new AtAssertion(WSAT200410, true));
        registerCombination(WSAT200410, SUPPORTS, EjbTransactionType.REQUIRED, new AtAssertion(WSAT200410, true), new AtAlwaysCapability(false));

        registerCombination(WSAT200410, NEVER, EjbTransactionType.NOT_DEFINED); // no assertions
        registerCombination(WSAT200410, NEVER, EjbTransactionType.NEVER); // no assertions
        registerCombination(WSAT200410, NEVER, EjbTransactionType.REQUIRES_NEW, new AtAlwaysCapability(false));
        registerCombination(WSAT200410, NEVER, EjbTransactionType.REQUIRED, new AtAlwaysCapability(false));

        // WSAT200606
        registerCombination(WSAT200606, MANDATORY, EjbTransactionType.NOT_DEFINED, new AtAssertion(WSAT200606, false));
        registerCombination(WSAT200606, MANDATORY, EjbTransactionType.MANDATORY, new AtAssertion(WSAT200606, false));
        registerCombination(WSAT200606, MANDATORY, EjbTransactionType.REQUIRED, new AtAssertion(WSAT200606, false));

        registerCombination(WSAT200606, SUPPORTS, EjbTransactionType.NOT_DEFINED, new AtAssertion(WSAT200606, true));
        registerCombination(WSAT200606, SUPPORTS, EjbTransactionType.SUPPORTS, new AtAssertion(WSAT200606, true));
        registerCombination(WSAT200606, SUPPORTS, EjbTransactionType.REQUIRED, new AtAssertion(WSAT200606, true));

        registerCombination(WSAT200606, NEVER, EjbTransactionType.NOT_DEFINED); // no assertions
        registerCombination(WSAT200606, NEVER, EjbTransactionType.NEVER); // no assertions
        registerCombination(WSAT200606, NEVER, EjbTransactionType.REQUIRES_NEW); // no assertions
        registerCombination(WSAT200606, NEVER, EjbTransactionType.NOT_SUPPORTED); // no assertions
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

        final List<AssertionSet> assertionSets = new ArrayList<AssertionSet>(1);
        assertionSets.add(AssertionSet.createAssertionSet(assertions));

        return Policy.createPolicy("", policyId, assertionSets);
    }
}
