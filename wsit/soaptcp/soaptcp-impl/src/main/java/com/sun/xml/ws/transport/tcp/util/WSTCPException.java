/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

/**
 * @author Alexey Stashok
 */
public class WSTCPException extends Exception {
    private WSTCPError error;
    
    public WSTCPException(final WSTCPError error) {
        this.error = error;
    }
    
    public String getMessage() {
        return toString();
    }
    
    public WSTCPError getError() {
        return error;
    }
    
    public String toString() {
        if (error != null) {
            return error.toString();
        }
        
        return "";
    }
}
