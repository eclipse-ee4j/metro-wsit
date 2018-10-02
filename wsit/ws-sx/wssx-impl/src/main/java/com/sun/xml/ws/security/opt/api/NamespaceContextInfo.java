/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.api;

import java.util.HashMap;

/**
 * Maintains map of all inscope namespaces
 * @author K.Venugopal@sun.com
 */
public interface NamespaceContextInfo {
    /**
     * map of all inscope namespace declarations.
     * @return {@link java.util.HashMap} of all inscope namespaces.
     */
    HashMap<String,String> getInscopeNSContext();
}
