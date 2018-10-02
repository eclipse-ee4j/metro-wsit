/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.util;

import com.sun.xml.ws.rx.RxRuntimeException;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class ResumeFiberException extends RxRuntimeException {

    public ResumeFiberException(String message) {
        super(message);
    }

    public ResumeFiberException(String message, Throwable cause) {
        super(message, cause);
    }
}
