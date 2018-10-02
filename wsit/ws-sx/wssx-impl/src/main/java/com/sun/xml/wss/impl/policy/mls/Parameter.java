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
 * Parameter.java
 *
 * Created on August 1, 2005, 3:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
