/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.wsit;

import com.sun.xml.ws.policy.spi.PrefixMapper;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabian Ritzmann
 */
public class TCPTransportPrefixMapper implements PrefixMapper {

    private static final Map<String, String> prefixMap = new HashMap<String, String>();

    static {
        prefixMap.put(TCPConstants.TCPTRANSPORT_POLICY_NAMESPACE_URI, "soaptcpsvc");
        prefixMap.put(TCPConstants.CLIENT_TRANSPORT_NS, "transport");
        prefixMap.put(TCPConstants.TCPTRANSPORT_CONNECTION_MANAGEMENT_NAMESPACE_URI, "soaptcp");
    }
        
    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }
    
}
