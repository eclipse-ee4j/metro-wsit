/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

/**
 * 
 * @author ashutosh.shahi@sun.com
 */
public interface KeyValueToken extends Token{
    
    /**
     * returns the type of the token.
     * @return RsaKeyValue if RSA cryptographic algoroithm should be used
     */
    public String getTokenType();
    
}
