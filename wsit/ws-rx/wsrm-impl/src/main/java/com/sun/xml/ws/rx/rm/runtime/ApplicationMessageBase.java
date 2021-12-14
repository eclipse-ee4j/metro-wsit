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

import com.sun.istack.NotNull;
import com.sun.xml.ws.rx.message.RxMessageBase;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public abstract class ApplicationMessageBase extends RxMessageBase implements ApplicationMessage {
    
    private String sequenceId;
    private long messageNumber;
    private AcknowledgementData acknowledgementData;
    private final AtomicInteger resendCount;

    protected ApplicationMessageBase(@NotNull String correlationId) {
        this(1, correlationId, null, 0L, null);
    }

    protected ApplicationMessageBase(@NotNull String correlationId, String sequenceId, long messageNumber, AcknowledgementData acknowledgementData) {
        this(1, correlationId, sequenceId, messageNumber, acknowledgementData);
    }

    protected ApplicationMessageBase(int initialResendCounterValue, @NotNull String correlationId, String sequenceId, long messageNumber, AcknowledgementData acknowledgementData) {
        super(correlationId);

        this.resendCount = new AtomicInteger(initialResendCounterValue);

        this.sequenceId = sequenceId;
        this.messageNumber = messageNumber;
        this.acknowledgementData = acknowledgementData;
    }

    @Override
    public AcknowledgementData getAcknowledgementData() {
        return acknowledgementData;
    }

    @Override
    public long getMessageNumber() {
        return messageNumber;
    }

    @Override
    public String getSequenceId() {
        return sequenceId;
    }

    @Override
    public void setAcknowledgementData(AcknowledgementData data) {
        this.acknowledgementData = data;
    }

    @Override
    public void setSequenceData(String sequenceId, long messageNumber) {
        assert sequenceId != null;
        this.sequenceId = sequenceId;
        this.messageNumber = messageNumber;
    }

    @Override
    public int getNextResendCount() {
        return resendCount.getAndIncrement();
    }
}
