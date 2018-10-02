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
 * KeyIdentifier.java
 *
 * Created on August 7, 2006, 12:23 PM
 */

package com.sun.xml.ws.security.opt.api.reference;

/**
 * Interface for KeyIdentifier reference type inside a STR
 * @author Ashutosh.Shahi@sun.com
 */
public interface KeyIdentifier extends Reference{
    
    /**
     * 
     * @return the valueType attribute for KeyIdentifier
     */
    String getValueType();
    
    /**
     * 
     * @param valueType the valueType attribute for KeyIdentifier
     */
    void setValueType(final String valueType);
    
    /**
     * 
     * @return the encodingType attribute
     */
    String getEncodingType();
    
    /**
     * 
     * @param value the encodingType attribute
     */
    void setEncodingType(final String value);
    
    /**
     * 
     * @return the referenced value by this key identifier
     */
    String getReferenceValue();
    
    /**
     * 
     * @param referenceValue the referenced value by this keyIdentifier
     */
    void setReferenceValue(final String referenceValue);
    
}
