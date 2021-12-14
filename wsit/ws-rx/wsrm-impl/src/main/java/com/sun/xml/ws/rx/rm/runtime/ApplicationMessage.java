/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import com.sun.xml.ws.rx.message.RxMessage;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;

/**
 * A protocol independent abstraction of an application message that is used as part of RM processing.
 *
 */
public interface ApplicationMessage extends RxMessage {

    /**
     * Returns identifier of a sequence this message is associated with
     *
     * @return associated sequence identifier
     */
    String getSequenceId();

    /**
     * Returns message number within a given sequence of this message
     *
     * @return sequence message number of this message
     */
    long getMessageNumber();

    /**
     * Sets reliable messaging sequence data for this message.
     *
     * @param sequenceId identifier of a sequence this message is associated with
     * @param messageNumber message number within a given RM sequence
     */
    void setSequenceData(String sequenceId, long messageNumber);

    /**
     * Returns acknowledgement data attached to the message
     *
     * @return acknowledgement data attached to the message
     */
    AcknowledgementData getAcknowledgementData();

    /**
     * Sets acknowledgement data attached to the message
     *
     * @param data acknowledgement data attached to the message
     */
    void setAcknowledgementData(AcknowledgementData data);

    /**
     * Retrieves number of the next resend attempt
     *
     * @return number of the next resend attempt
     */
    int getNextResendCount();
}
