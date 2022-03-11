/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.xml.ws.rx.RxRuntimeException;

/**
 * Indicates the sequence specified is in state of CLOSING, CLOSED, or TERMINTAING,
 * not appropriate to be used for sending additional application messages.
 */
public class InvalidSequenceException extends RxRuntimeException {

    private static final long serialVersionUID = -929471072307639315L;

    private final String sequenceId;

    public InvalidSequenceException(String msg, String sequenceId) {
        super(msg);
        this.sequenceId = sequenceId;
    }

    public String getSequenceId() {
        return sequenceId;
    }
}
