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
public class TerminateSequenceData {
    public static class Builder {
        private @NotNull final String sequenceId;
        private final long lastMessageId;
        private @Nullable AcknowledgementData acknowledgementData;

        private Builder(String sequenceId, long lastMessageId) {
            this.sequenceId = sequenceId;
            this.lastMessageId = lastMessageId;
        }

        public Builder acknowledgementData(@Nullable AcknowledgementData acknowledgementData) {
            this.acknowledgementData = acknowledgementData;

            return this;
        }

        public TerminateSequenceData build() {
            // TODO construct parent class object
            return new TerminateSequenceData(sequenceId, lastMessageId, acknowledgementData);
        }
    }

    public static Builder getBuilder(@NotNull String sequenceId, long lastMessageId) {
        // TODO construct builder
        return new Builder(sequenceId, lastMessageId);
    }

    private @NotNull final String sequenceId;
    private final long lastMessageId;
    private @Nullable final AcknowledgementData acknowledgementData;

    private TerminateSequenceData(@NotNull String sequenceId, long lastMessageId, @Nullable AcknowledgementData acknowledgementData) {
        assert sequenceId != null;

        this.sequenceId = sequenceId;
        this.lastMessageId = lastMessageId;
        this.acknowledgementData = acknowledgementData;
    }

    public @NotNull String getSequenceId() {
        return sequenceId;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public @Nullable AcknowledgementData getAcknowledgementData() {
        return acknowledgementData;
    }
}
