/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.api;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Class contains constants for policy namespaces used by this RM implementation.
 *
 */
public enum RmAssertionNamespace {

    WSRMP_200502("http://schemas.xmlsoap.org/ws/2005/02/rm/policy", "wsrmp10"),
    WSRMP_200702("http://docs.oasis-open.org/ws-rx/wsrmp/200702", "wsrmp"),
    MICROSOFT_200502("http://schemas.microsoft.com/net/2005/02/rm/policy", "net30rmp"),
    MICROSOFT_200702("http://schemas.microsoft.com/ws-rx/wsrmp/200702", "net35rmp"),
    METRO_200603("http://sun.com/2006/03/rm", "sunrmp"),
    METRO_CLIENT_200603("http://sun.com/2006/03/rm/client", "sunrmcp"),
    METRO_200702("http://java.sun.com/xml/ns/metro/ws-rx/wsrmp/200702", "metrormp");

    public static List<String> namespacesList() {
        List<String> retVal = new ArrayList<String>(RmAssertionNamespace.values().length);
        for (RmAssertionNamespace pns : RmAssertionNamespace.values()) {
            retVal.add(pns.toString());
        }
        return retVal;
    }

    private final String namespace;
    private final String prefix;

    private RmAssertionNamespace(String namespace, String prefix) {
        this.namespace = namespace;
        this.prefix = prefix;
    }

    public String defaultPrefix() {
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
