/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss;

import java.util.Map;

/**
 *
 * Used with a Keystore Assertion to select an Alias to be used for locating the Private Key 
 * at runtime.
 * @author kumar.jayanti
 */
public interface AliasSelector {
    
    /**
     * 
     * @param runtimeProps a map of runtime properties which for a WebService Client includes those set on BindingProvider.RequestContext
     * @return the selected alias or null.
     */
    public String select(Map runtimeProps);
}
