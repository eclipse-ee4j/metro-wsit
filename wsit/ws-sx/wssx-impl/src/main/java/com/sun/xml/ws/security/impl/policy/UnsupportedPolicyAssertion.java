/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

/**
 *
 * RuntimeException which is thrown by security policy 
 * assertion implementation when a Invalid PolicyAssertion is found.
 *
 * Note for {@link com.sun.xml.ws.api.pipe.Pipe} implementors using
 * SecurityPolicy Assertions should catch this exception and throw
 * exceptions required by the Pipe.
 *
 * @author K.Venugopal@sun.com
 *
 */

public class UnsupportedPolicyAssertion extends java.lang.RuntimeException{

    private static final long serialVersionUID = -2495837431343306017L;

    /** Creates a new instance of UnsupportedPolicyAssertion */
    public UnsupportedPolicyAssertion() {
    }
    
    public UnsupportedPolicyAssertion(String msg){
        super(msg);
    }
    
    public UnsupportedPolicyAssertion(String msg , Throwable exp){
        super(msg,exp);
    }
    
    public UnsupportedPolicyAssertion(Throwable exp){
        super(exp);
    }
}
