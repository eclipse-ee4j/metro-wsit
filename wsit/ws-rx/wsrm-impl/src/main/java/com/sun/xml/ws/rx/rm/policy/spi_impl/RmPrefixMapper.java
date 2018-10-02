/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.spi_impl;

import com.sun.xml.ws.policy.spi.PrefixMapper;
import com.sun.xml.ws.rx.rm.api.RmAssertionNamespace;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fabian Ritzmann
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class RmPrefixMapper implements PrefixMapper {

    private static final Map<String, String> prefixMap;


    static {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        for (RmAssertionNamespace ns : RmAssertionNamespace.values()) {
            tempMap.put(ns.toString(), ns.defaultPrefix());
        }
        prefixMap = Collections.unmodifiableMap(tempMap);
    }
        
    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }
    
}
