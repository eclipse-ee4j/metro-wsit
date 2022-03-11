/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.mls;

import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/**
 *
 * @author K.venugopal@sun.com
 */
public class Parameter implements TransformParameterSpec {
    private String paramName = null;
    private String paramValue = null;
    /** Creates a new instance of Parameter */
    public Parameter(){
        
    }
    public Parameter(String name,String value) {
        this.paramName = name;
        this.paramValue = value;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
    
    
}
