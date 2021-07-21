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
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.AckRange;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class AcknowledgementData {
    public static final class Builder {
        private String ackedSequenceId;
        private List<AckRange> ackedRanges;
        private String ackRequestedSequenceId;
        private boolean isFinalAcknowledgement;

        private Builder() {
        }

        private Builder(AcknowledgementData data) {
            this.ackRequestedSequenceId = data.ackRequestedSequenceId;
            this.ackedRanges = data.ackedRanges;
            this.ackedSequenceId = data.ackedSequenceId;
            this.isFinalAcknowledgement = data.isFinalAcknowledgement;
        }

        /**
         * Sets acknowledgements
         *
         * @param ackedSequenceId idnetifier of a sequence to which acknowledged message number ranges (if any) belong
         * @param acknowledgedMessageIds acknowledged ranges for the sequence identified by {@code ackSequenceId}
         * @param isFinal sets the final flag on the acknowledgement data which means that this is a final acknowledgement.
         */
        public Builder acknowledgements(@NotNull String ackedSequenceId, List<AckRange> acknowledgedMessageIds, boolean isFinal) {
            assert ackedSequenceId != null;

            this.ackedSequenceId = ackedSequenceId;
            this.ackedRanges = acknowledgedMessageIds;
            this.isFinalAcknowledgement = isFinal;

            return this;
        }

        /**
         * Sets value of AckRequested flag for the sequence associated with this message
         *
         * @param ackRequestedSequenceId value of sequence identifier for which acknowledgement is requested
         */
        public Builder ackReqestedSequenceId(@NotNull String ackRequestedSequenceId) {
            assert ackRequestedSequenceId != null;

            this.ackRequestedSequenceId = ackRequestedSequenceId;

            return this;
        }

        public AcknowledgementData build() {
            return new AcknowledgementData(ackedSequenceId, ackedRanges, ackRequestedSequenceId, isFinalAcknowledgement);
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static Builder getBuilder(AcknowledgementData data) {
        return new Builder(data);
    }

    private final String ackedSequenceId;
    private final List<AckRange> ackedRanges;
    private final String ackRequestedSequenceId;
    private final boolean isFinalAcknowledgement;

    private AcknowledgementData(String ackedSequenceId, List<AckRange> ackedRanges, String ackRequestedSequenceId, boolean isFinal) {
        this.ackedSequenceId = ackedSequenceId;
        this.ackedRanges = ackedRanges;
        this.ackRequestedSequenceId = ackRequestedSequenceId;
        this.isFinalAcknowledgement = isFinal;
    }
    /**
     * Returns idnetifier of a sequence to which acknowledged message number ranges (if any) belong
     *
     * @return idnetifier of a sequence to which acknowledged message number ranges (if any) belong
     */
    public String getAcknowledgedSequenceId() {
        return this.ackedSequenceId;
    }

    /**
     * Returns acknowledged ranges for the sequence identified by acknowledged sequence identifier
     *
     * @return acknowledged ranges for the sequence identified by acknowledged sequence identifier
     */
    public @NotNull List<AckRange> getAcknowledgedRanges() {
        if (this.ackedRanges != null) {
            return this.ackedRanges;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns sequence identifier for which acknowledgement is requested
     *
     * @return value of sequence identifier for which acknowledgement is requested
     */
    public String getAckReqestedSequenceId() {
        return this.ackRequestedSequenceId;
    }

    /**
     * Returns value of the final flag which determines whether this is a final acknowledgement or not
     *
     * @return value of the final flag which determines whether this is a final acknowledgement or not
     */
    public boolean isFinalAcknowledgement() {
        return isFinalAcknowledgement;
    }

    /**
     * Returns {@code true} if the instance contains any acknowledgement data that could be sent
     * to an RM source. Otherwise returns {@code false}.
     *
     * @return {@code true} if the instance contains any acknowledgement data that could be sent
     * to an RM source. Otherwise returns {@code false}.
     */
    public boolean containsSequenceAcknowledgementData() {
        return this.ackedSequenceId != null && this.ackedRanges != null && !this.ackedRanges.isEmpty();
    }
}
