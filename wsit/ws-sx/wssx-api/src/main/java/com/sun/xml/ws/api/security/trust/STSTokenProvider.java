/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust;

import com.sun.xml.ws.security.IssuedTokenContext;
/**
 *
 * @author Jiandong Guo
 */
public interface STSTokenProvider {
    
    void generateToken(IssuedTokenContext ctx) throws WSTrustException;
    void isValideToken(IssuedTokenContext ctx) throws WSTrustException;
    void renewToken(IssuedTokenContext ctx);
    void invalidateToken(IssuedTokenContext ctx);
    
}
