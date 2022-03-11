/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * Reference.java
 *
 * Created on August 2, 2006, 4:06 PM
 */

package com.sun.xml.ws.security.opt.api.reference;

/**
 *
 * Parent interface for different reference mechanisms inside a STR
 * @author Ashutosh.Shahi@sun.com
 */
public interface Reference {

    /**
     *
     * @return the reference type used
     */
    String getType();
}
