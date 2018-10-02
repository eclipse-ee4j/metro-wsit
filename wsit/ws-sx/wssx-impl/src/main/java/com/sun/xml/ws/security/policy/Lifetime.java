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
 * LifeTime.java
 *
 * Created on February 23, 2006, 12:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.ws.security.policy;


/**
 *
 * @author Abhijit Das
 */
public interface Lifetime {
    
    /**
     * Get creation time
     *
     * @return String representing created time
     */
    String getCreated();
    
    
    /**
     *
     * Get Expires time
     *
     * @return String representing expires time.
     */
    String getExpires();
}
