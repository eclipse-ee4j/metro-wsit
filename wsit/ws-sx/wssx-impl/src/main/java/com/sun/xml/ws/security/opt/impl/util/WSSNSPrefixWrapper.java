/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.impl.util;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class WSSNSPrefixWrapper  extends NamespacePrefixMapper{
    private NamespacePrefixMapper npm = null;
    /** Creates a new instance of WSSNSPrefixWrapper */
    public WSSNSPrefixWrapper(NamespacePrefixMapper nsw) {
        npm = nsw;
    }
    
    
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        return npm.getPreferredPrefix(namespaceUri,suggestion,requirePrefix);
    }
    
    public String[] getPreDeclaredNamespaceUris() {
        return npm.getPreDeclaredNamespaceUris();
    }
    
    public String[] getContextualNamespaceDecls() {
        return null;
    }
    
}
