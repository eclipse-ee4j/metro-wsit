/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.tx.at;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Enumeration of all supported WS-AT namespaces
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public enum WsatNamespace {

    WSAT200410("wsat200410", "http://schemas.xmlsoap.org/ws/2004/10/wsat"),
    WSAT200606("wsat200410", "http://docs.oasis-open.org/ws-tx/wsat/2006/06");
    //
    public static List<String> namespacesList() {
        List<String> retVal = new ArrayList<>(WsatNamespace.values().length);
        for (WsatNamespace wsatNamespaceEnum : WsatNamespace.values()) {
            retVal.add(wsatNamespaceEnum.namespace);
        }
        return retVal;
    }
    //
    public final String defaultPrefix;
    public final String namespace;

    WsatNamespace(String defaultPrefix, String namespace) {
        this.defaultPrefix = defaultPrefix;
        this.namespace = namespace;
    }

    public QName createFqn(final String name) {
        return new QName(namespace, name, defaultPrefix);
    }

    public QName createFqn(final String prefix, final String name) {
        return new QName(namespace, name, prefix);
    }

    public static WsatNamespace forNamespaceUri(String uri) {
        for (WsatNamespace ns : WsatNamespace.values()) {
            if (ns.namespace.equals(uri)) {
                return ns;
            }
        }

        return null;
    }
}
