/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.xml.ws.rx.RxException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;

/**
 * Exception used by sequence implementation to reject out of order
 * message number for a given sequence when InOrder QoS is configured.
 *
 * @author Uday Joshi <uday.joshi at oracle.com>
 */
public class OutOfOrderMessageException extends RxException {
    private static final long serialVersionUID = 1L;
    private final String sequenceId;
    private final long messageNumber;

    public OutOfOrderMessageException(String sequenceId, long messageNumber) {
        //TODO i18n
        super("Out of order message received: sequenceId "+sequenceId+", messageNumber "+messageNumber);

        this.sequenceId = sequenceId;
        this.messageNumber = messageNumber;
    }

    public long getMessageNumber() {
        return messageNumber;
    }

    public String getSequenceId() {
        return sequenceId;
    }
}
