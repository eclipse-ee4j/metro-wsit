/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: KeyIdentifierImpl.java,v 1.2 2010-10-21 15:36:57 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements.str;

import com.sun.xml.ws.security.secext10.KeyIdentifierType;
import com.sun.xml.ws.security.trust.elements.str.KeyIdentifier;

import java.net.URI;

/**
 * KeyIdentifier implementation
 */
public class KeyIdentifierImpl extends KeyIdentifierType implements KeyIdentifier {
    
    
    public KeyIdentifierImpl() {
        // default c'tor
    }

    public KeyIdentifierImpl(final String valueType, final String encodingType) {
        setValueType(valueType);
        setEncodingType(encodingType);
    }
    
    public KeyIdentifierImpl(final KeyIdentifierType kidType){
        this(kidType.getValueType(), kidType.getEncodingType());
        setValue(kidType.getValue());
    }
    
    public URI getValueTypeURI(){
        return URI.create(super.getValueType());
    }
    
    public URI getEncodingTypeURI (){
        return URI.create(super.getEncodingType());
    }
    
    public String getType(){
        return "KeyIdentifier";
    }
}
