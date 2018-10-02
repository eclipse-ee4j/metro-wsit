/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx;

import javax.xml.ws.WebServiceException;

/**
 * Represents all generally unrecoverable exceptions that may occur during RX runtime
 * processing
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class RxRuntimeException extends WebServiceException {
    private static final long serialVersionUID = 8154320015679552890L;

    public RxRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxRuntimeException(String message) {
        super(message);
    }    
}
