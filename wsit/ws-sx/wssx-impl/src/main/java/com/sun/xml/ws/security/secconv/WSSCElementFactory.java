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
import com.sun.xml.ws.security.secconv.impl.elements.SecurityContextTokenImpl;
import com.sun.xml.ws.security.trust.impl.WSTrustElementFactoryImpl;

import java.net.URI;

/**
 *
 * @author Jiandong Guo
 */
public class WSSCElementFactory extends WSTrustElementFactoryImpl{

    private static final WSSCElementFactory scElemFactory = new WSSCElementFactory();

    public static WSSCElementFactory newInstance() {
        return scElemFactory;
    }

    @Override
    public SecurityContextToken createSecurityContextToken(final URI identifier, final String instance, final String wsuId){
        return new SecurityContextTokenImpl(identifier, instance, wsuId);
    }
}
