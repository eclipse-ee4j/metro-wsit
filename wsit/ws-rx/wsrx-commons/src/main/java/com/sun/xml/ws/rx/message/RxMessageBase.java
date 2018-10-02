/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.message;

import com.sun.istack.NotNull;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public abstract class RxMessageBase implements RxMessage {

    private
    @NotNull
    final String correlationId;

    public RxMessageBase(@NotNull String correlationId) {
        if (correlationId == null) {
            throw new NullPointerException("correlationId initialization parameter must not be 'null'");
        }
        this.correlationId = correlationId;
    }

    @NotNull
    public String getCorrelationId() {
        return this.correlationId;
    }

    public byte[] toBytes() {
        return new byte[0];
    }
}
