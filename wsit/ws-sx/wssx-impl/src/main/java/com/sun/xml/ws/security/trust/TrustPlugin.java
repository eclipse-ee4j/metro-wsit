/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: TrustPlugin.java,v 1.2 2010-10-21 15:36:48 snajper Exp $
 */

package com.sun.xml.ws.security.trust;

import com.sun.xml.ws.security.IssuedTokenContext;

import com.sun.xml.ws.api.security.trust.WSTrustException;


public interface TrustPlugin {
    public void process(IssuedTokenContext ctx) throws WSTrustException;
    public void processValidate(IssuedTokenContext ctx) throws WSTrustException;
}
