/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * WSSCElementFactory.java
 *
 * Created on February 16, 2006, 12:11 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
    
    public SecurityContextToken createSecurityContextToken(final URI identifier, final String instance, final String wsuId){
        return new SecurityContextTokenImpl(identifier, instance, wsuId);
    }
}
