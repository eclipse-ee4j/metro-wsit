/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm;

import com.sun.xml.ws.rx.RxException;

/**
 * Subclass of <code>RMException</code> thrown from errors resulting
 *  because the endpoint has encountered an unrecoverable condition or
 * detected a violation of the protocol and as a result has chosen to
 * terminate the sequence
 * @author Bhakti Mehta
 *
 */
public class TerminateSequenceException extends RxException {
    private static final long serialVersionUID = -6941741820196934499L;

    private String sequenceId;

    public TerminateSequenceException(String message) {
        super(message);
    }

    public TerminateSequenceException(String message, String id) {
        super(message);
        this.sequenceId = id;
    }

    public TerminateSequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getSequenceId() {
        return sequenceId;
    }
}
