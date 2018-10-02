/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.oracle.webservices.oracle_internal_api.rm;

/**
 * {@code InboundAcceptedAcceptFailed} is thrown if the user calls {@code InboundAccepted#accepted(true)} but the RMD is
 *     not able to internally record the message as delivered
 *     (e.g., an atomic transaction fails to commit).
 */
public class InboundAcceptedAcceptFailed extends Exception {
    private static final long serialVersionUID = 1L;

    public InboundAcceptedAcceptFailed() {
        super();
    }

    public InboundAcceptedAcceptFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public InboundAcceptedAcceptFailed(String message) {
        super(message);
    }

    public InboundAcceptedAcceptFailed(Throwable cause) {
        super(cause);
    }
}

// End of file.
