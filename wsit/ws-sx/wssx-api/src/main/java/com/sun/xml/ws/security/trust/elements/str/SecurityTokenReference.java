/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.elements.str;

import com.sun.xml.ws.security.Token;
import javax.xml.namespace.QName;

public interface SecurityTokenReference extends Token {
    
    String KEYIDENTIFIER = "KeyIdentifier";
    String REFERENCE = "Reference";
    QName TOKEN_TYPE = new QName("http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd","TokenType");
    
    void setReference(Reference ref);
    
    Reference getReference();

    void setTokenType(String tokenType);

    String getTokenType();   
}
