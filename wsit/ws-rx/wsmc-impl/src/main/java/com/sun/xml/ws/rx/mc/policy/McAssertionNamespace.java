/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.sun.xml.ws.rx.mc.policy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Class contains constants for policy namespaces used by this RM
 * implementation.
 *
 */
public enum McAssertionNamespace {

    WSMC_200702("http://docs.oasis-open.org/ws-rx/wsmc/200702", "wsmc");

    public static List<String> namespacesList() {
        List<String> retVal = new ArrayList<String>(McAssertionNamespace.values().length);
        for (McAssertionNamespace pns : McAssertionNamespace.values()) {
            retVal.add(pns.toString());
        }
        return retVal;
    }

    private final String namespace;
    private final String prefix;

    private McAssertionNamespace(String namespace, String prefix) {
        this.namespace = namespace;
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return namespace;
    }

    public QName getQName(String name) {
        return new QName(namespace, name);
    }
}
