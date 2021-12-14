/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.addressing.policy;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.policy.spi.PrefixMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * This supplies the prefixes for the namespaces under Addressing domain that are not covered by the default
 * Addressing Policy provider in JAX-WS.
 *
 * This class exists in WSIT to provide functionality for backwards compatibility with previously generated
 * wsaw:UsingAddressing assertion.
 *
 * @author Rama Pulavarthi
 */
public class WsawAddressingPrefixMapper implements PrefixMapper {

    private static final Map<String, String> prefixMap = new HashMap<>();

    static {
        prefixMap.put(AddressingVersion.W3C.policyNsUri, "wsapw3c");
        prefixMap.put(AddressingVersion.W3C.nsUri, "wsaw3c");
    }

    @Override
    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }

}
