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

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.api.tx.at.WsatNamespace;
import java.util.Collection;
import javax.xml.namespace.QName;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class AtAlwaysCapability extends WsatAssertionBase {
    public static final QName NAME = WsatNamespace.WSAT200410.createFqn("ATAlwaysCapability");

    public AtAlwaysCapability(boolean isOptional) {
        super(NAME, isOptional);
    }

    public AtAlwaysCapability(AssertionData data, Collection<PolicyAssertion> assertionParameters) {
        super (data, assertionParameters);
    }
}
