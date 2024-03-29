/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.impl;

import com.sun.xml.ws.api.security.trust.STSAuthorizationProvider;

import javax.security.auth.Subject;

/**
 *
 * @author Jiandong Guo
 */
public class DefaultSTSAuthorizationProvider implements STSAuthorizationProvider{

    public DefaultSTSAuthorizationProvider() {}

     @Override
     public boolean isAuthorized(final Subject subject, final String appliesTo, final String tokenType, final String keyType){
        return true;
   }
}
