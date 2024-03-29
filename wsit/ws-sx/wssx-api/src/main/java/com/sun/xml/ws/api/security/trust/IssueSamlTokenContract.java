/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.Token;


public interface IssueSamlTokenContract<K, V> extends WSTrustContract<K, V> {

    Token createSAMLAssertion(String appliesTo, String tokenType, String keyType, String assertionId, String issuer, Map<QName, List<String>> claimedAttrs, IssuedTokenContext context) throws WSTrustException;
}
