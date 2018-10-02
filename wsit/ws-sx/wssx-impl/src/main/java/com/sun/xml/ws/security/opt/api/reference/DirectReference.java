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
 * DirectReference.java
 *
 * Created on August 2, 2006, 4:42 PM
 */

package com.sun.xml.ws.security.opt.api.reference;

/**
 * 
 * Interface for DirectReference reference type inside a STR
 * @author Ashutosh.Shahi@Sun.com
 */
public interface DirectReference extends Reference{
    
    /**
     * 
     * @return the valueType attribute of direct reference
     */
    String getValueType();
    
    /**
     * 
     * @param valueType sets the valueType attribute
     */
    void setValueType(final String valueType);
    
    /**
     * 
     * @return the URI attribute
     */
    String getURI();
    
    /**
     * 
     * @param uri sets the uri attribute
     */
    void setURI(final String uri);
}
