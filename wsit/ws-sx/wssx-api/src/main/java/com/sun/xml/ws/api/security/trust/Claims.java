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
* $Id: Claims.java,v 1.2 2010-10-21 15:35:33 snajper Exp $
 */

package com.sun.xml.ws.api.security.trust;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 *
 * @author Jiandong Guo
 */
public interface Claims {
    /**
     * Gets the value of the any property for all the claim types.
     */
    List<Object> getAny();

    /**
     * Gets the value of the dialect property.
     * 
     * @return {@link String }
     *     
     */
    String getDialect();

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * @return
     *     always non-null
     */
    Map<QName, String> getOtherAttributes();

    /**
     * Sets the value of the dialect property.
     * 
     * @param value
     *     {@link String }
     *     
     */
    void setDialect(String value);  
    
    List<Object> getSupportingProperties();
}
