/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv;

import com.sun.xml.ws.security.SecurityContextToken;
import com.sun.xml.ws.security.secconv.impl.wssx.elements.SecurityContextTokenImpl;
import com.sun.xml.ws.security.trust.impl.wssx.WSTrustElementFactoryImpl;

import java.net.URI;

/**
 *
 * @author Shyam Rao
 */
public class WSSCElementFactory13 extends WSTrustElementFactoryImpl{
    
    private static final WSSCElementFactory13 scElemFactory13 = new WSSCElementFactory13();
    
    public static WSSCElementFactory13 newInstance() {
        return scElemFactory13;
    }
    
    @Override
    public SecurityContextToken createSecurityContextToken(final URI identifier, final String instance, final String wsuId){
        return new SecurityContextTokenImpl(identifier, instance, wsuId);
    }
}

