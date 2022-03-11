/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx;

/**
 * Represents all exceptions that may possibly be recovered in the client code.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class RxException extends Exception {
    private static final long serialVersionUID = 2877482397322174262L;

    public RxException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxException(String message) {
        super(message);
    }
}
