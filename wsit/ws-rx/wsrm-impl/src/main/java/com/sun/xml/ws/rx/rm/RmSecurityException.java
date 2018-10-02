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
 * RMSecurityException.java
 *
 * @author Mike Grogan
 * Created on February 14, 2006, 10:31 AM
 *
 */
package com.sun.xml.ws.rx.rm;

import com.sun.xml.ws.rx.RxRuntimeException;

/**
 * Subclass of RMException thrown when an incorrect STR is
 * used to secure an inbound message.
 */
public class RmSecurityException extends RxRuntimeException {
    private static final long serialVersionUID = -8808571889805267275L;

    public RmSecurityException(String message) {
        super(message);
    }
}
