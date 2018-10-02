/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.protocol;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class TerminateSequenceResponseData {
    public static class Builder {

        private @NotNull final String sequenceId;
        private @Nullable String boundSequenceId;
        private long boundSequenceLastMessageId;
        private @Nullable AcknowledgementData acknowledgementData;

        public Builder(@NotNull String sequenceId) {
            this.sequenceId = sequenceId;
        }

        public Builder acknowledgementData(@Nullable AcknowledgementData acknowledgementData) {
            this.acknowledgementData = acknowledgementData;

            return this;
        }

        public Builder boundSequenceData(String sequenceId, long lastMessageId) {
            this.boundSequenceId = sequenceId;
            this.boundSequenceLastMessageId = lastMessageId;

            return this;
        }

        public TerminateSequenceResponseData build() {
            return new TerminateSequenceResponseData(sequenceId, boundSequenceId, boundSequenceLastMessageId, acknowledgementData);
        }
    }

    public static Builder getBuilder(String sequenceId) {
        return new Builder(sequenceId);
    }

    private @NotNull final String sequenceId;
    private @Nullable final String boundSequenceId;
    private final long boundSequenceLastMessageId;
    private @Nullable final AcknowledgementData acknowledgementData;

    private TerminateSequenceResponseData(
            @NotNull String sequenceId,
            @Nullable String boundSequenceId,
            long boundSequenceLastMessageId,
            @Nullable AcknowledgementData acknowledgementData) {
        this.sequenceId = sequenceId;
        this.boundSequenceId = boundSequenceId;
        this.boundSequenceLastMessageId = boundSequenceLastMessageId;
        this.acknowledgementData = acknowledgementData;
    }

    public @NotNull String getSequenceId() {
        return sequenceId;
    }

    public @Nullable AcknowledgementData getAcknowledgementData() {
        return acknowledgementData;
    }

    public @Nullable String getBoundSequenceId() {
        return boundSequenceId;
    }

    public long getBoundSequenceLastMessageId() {
        return boundSequenceLastMessageId;
    }
}
