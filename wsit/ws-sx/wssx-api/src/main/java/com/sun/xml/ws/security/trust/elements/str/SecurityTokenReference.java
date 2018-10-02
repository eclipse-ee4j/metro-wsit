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
 * SecurityTokenReference.java
 *
 * Created on October 19, 2005, 12:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.trust.elements.str;

import com.sun.xml.ws.security.Token;
import javax.xml.namespace.QName;

public interface SecurityTokenReference extends Token {
    
    public static final String KEYIDENTIFIER = "KeyIdentifier";
    public static final String REFERENCE = "Reference";
    public static final QName TOKEN_TYPE = new QName("http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd","TokenType");
    
    void setReference(Reference ref);
    
    Reference getReference();

    void setTokenType(String tokenType);

    String getTokenType();   
}
