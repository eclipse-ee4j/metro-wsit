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
public class AtAssertion extends WsatAssertionBase {


    private final WsatNamespace namespace;

    public static QName nameForNamespace(WsatNamespace ns) {
        return ns.createFqn("ATAssertion");
    }

    public AtAssertion(WsatNamespace ns, boolean isOptional) {
        super(nameForNamespace(ns), isOptional);

        this.namespace = ns;
    }

    public AtAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);

        this.namespace = WsatNamespace.forNamespaceUri(data.getName().getNamespaceURI());
    }
}
