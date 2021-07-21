/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
 */
public class CloseSequenceResponseData {
    public static class Builder {

        private @NotNull final String sequenceId;
        private @Nullable AcknowledgementData acknowledgementData;

        public Builder(@NotNull String sequenceId) {
            this.sequenceId = sequenceId;
        }

        public void acknowledgementData(@Nullable AcknowledgementData acknowledgementData) {
            this.acknowledgementData = acknowledgementData;
        }

        public CloseSequenceResponseData build() {
            return new CloseSequenceResponseData(sequenceId, acknowledgementData);
        }
    }

    public static Builder getBuilder(String sequenceId) {
        return new Builder(sequenceId);
    }

    private @NotNull final String sequenceId;
    private @Nullable final AcknowledgementData acknowledgementData;

    private CloseSequenceResponseData(@NotNull String sequenceId, @Nullable AcknowledgementData acknowledgementData) {
        this.sequenceId = sequenceId;
        this.acknowledgementData = acknowledgementData;
    }

    public @NotNull String getSequenceId() {
        return sequenceId;
    }

    public @Nullable AcknowledgementData getAcknowledgementData() {
        return acknowledgementData;
    }
}
