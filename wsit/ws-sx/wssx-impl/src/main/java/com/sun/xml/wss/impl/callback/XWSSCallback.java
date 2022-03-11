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
 * XWSSCallback.java
 *
 * Created on November 14, 2005, 3:51 PM
 *
 */

package com.sun.xml.wss.impl.callback;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Abhijit Das
 */
public abstract class XWSSCallback {

    Map runtimeProperties = null;


    public Map getRuntimeProperties() {
        if ( runtimeProperties == null)
            runtimeProperties = new HashMap();
        return runtimeProperties;
    }

}
