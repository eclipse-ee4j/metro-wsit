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

import com.sun.xml.ws.api.security.trust.config.STSConfiguration;
import com.sun.xml.ws.security.IssuedTokenContext;

import java.util.Map;


/**
 *    The Contract (SPI) to be used by an STS to handle an Incoming WS-Trust request and 
 *    send the corresponding response.
 */
public interface WSTrustContract<K, V> {

    public void init(STSConfiguration config);
    
    /** Issue a Token */
    public V issue(K rst, IssuedTokenContext context) throws WSTrustException;

    /** Renew a Token */
    public V renew(K rst, IssuedTokenContext context)
            throws WSTrustException;

    /** Cancel a Token */
    public V cancel(K rst, IssuedTokenContext context, Map map)
            throws WSTrustException;

    /** Validate a Token */
    public V validate(K request, IssuedTokenContext context)
            throws WSTrustException;

    /** 
     * handle an unsolicited RSTR like in the case of 
     * Client Initiated Secure Conversation.
     */
    public void handleUnsolicited(V rstr, IssuedTokenContext context)
           throws WSTrustException;
}
