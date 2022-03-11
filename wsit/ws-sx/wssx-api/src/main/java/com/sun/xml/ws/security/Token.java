/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security;

/**
 * Base Interface for all Tokens
 * Question: Can we adapt all tokens to implement this interface
 */
public interface Token {

    /**
     * The type of the Token
     */
    String getType();

    /**
     * The token Value
     */
    Object getTokenValue();

}
