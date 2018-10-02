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

import java.io.Serializable;

/**
 * Protocol-independent abstraction over message/packet
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public interface RxMessage {
    public interface State extends Serializable {
        RxMessage toMessage();
    }

    /**
     * Returns correlation identifier by which this message can be referenced
     *
     * @return correlation identifier by which this message can be referenced
     */
    public String getCorrelationId();

    /**
     * Returns {@code byte[]} representation of the message instance
     *
     * @return {@code byte[]} representation of the message instance
     */
    public byte[] toBytes();

    /**
     * Returns serializable state of the message
     *
     * @return serializable state of the message
     */
    public State getState();
}
