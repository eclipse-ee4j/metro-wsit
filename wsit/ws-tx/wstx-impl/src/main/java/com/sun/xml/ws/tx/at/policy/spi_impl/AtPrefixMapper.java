/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.policy.spi_impl;

import com.sun.xml.ws.policy.spi.PrefixMapper;
import com.sun.xml.ws.api.tx.at.WsatNamespace;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabian Ritzmann
 */
public class AtPrefixMapper implements PrefixMapper {

    private static final Map<String, String> prefixMap;

    static {
        Map<String, String> tmpMap = new HashMap<String, String>();

        for (WsatNamespace ns : WsatNamespace.values()) {
            tmpMap.put(ns.namespace, ns.defaultPrefix);
        }

        prefixMap = Collections.unmodifiableMap(tmpMap);
    }
        
    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }
}
