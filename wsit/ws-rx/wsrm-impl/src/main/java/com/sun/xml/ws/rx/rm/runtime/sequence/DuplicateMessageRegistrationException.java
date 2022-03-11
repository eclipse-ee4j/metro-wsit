/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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
 * Exception used by sequence implementation to notify RM runtime infrastructure of
 * a detected attempt to register a duplicate message number on a given sequence.
 *
 */
public class DuplicateMessageRegistrationException extends RxException {
    private static final long serialVersionUID = 8605938716798458482L;

    private final String sequenceId;
    private final long messageNumber;

    public DuplicateMessageRegistrationException(String sequenceId, long messageNumber) {
        super(LocalizationMessages.WSRM_1148_DUPLICATE_MSG_NUMBER_REGISTRATION_ATTEMPTED(
                messageNumber,
                sequenceId));

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
