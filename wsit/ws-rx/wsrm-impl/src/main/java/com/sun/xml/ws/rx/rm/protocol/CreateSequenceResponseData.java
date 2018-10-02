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
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.IncompleteSequenceBehavior;
import javax.xml.ws.EndpointReference;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class CreateSequenceResponseData {
    public static class Builder {
        private final @NotNull String sequenceId;
        private long duration;
        private @Nullable EndpointReference acceptedSequenceAcksTo;
        private Sequence.IncompleteSequenceBehavior incompleteSequenceBehavior;

        private Builder(String sequenceId) {
            this.sequenceId = sequenceId;
            this.duration = Sequence.NO_EXPIRY;
            this.incompleteSequenceBehavior = Sequence.IncompleteSequenceBehavior.getDefault();
        }

        public Builder acceptedSequenceAcksTo(EndpointReference acceptedSequenceAcksTo) {
            this.acceptedSequenceAcksTo = acceptedSequenceAcksTo;
            return this;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder incompleteSequenceBehavior(Sequence.IncompleteSequenceBehavior value) {
            this.incompleteSequenceBehavior = value;
            return this;
        }

        public CreateSequenceResponseData build() {
            return new CreateSequenceResponseData(sequenceId, duration, acceptedSequenceAcksTo, incompleteSequenceBehavior);
        }
    }

    public static Builder getBuilder(String sequenceId) {
        return new Builder(sequenceId);
    }

    private final @NotNull String sequenceId;
    private final long duration;
    private final @Nullable EndpointReference acceptedSequenceAcksTo;
    private final Sequence.IncompleteSequenceBehavior incompleteSequenceBehavior;
    // TODO add incompleteSequenceBehavior handling

    private CreateSequenceResponseData(@NotNull String sequenceId, long expirationTime, @Nullable EndpointReference acceptedSequenceAcksTo, Sequence.IncompleteSequenceBehavior incompleteSequenceBehavior) {
        this.sequenceId = sequenceId;
        this.duration = expirationTime;
        this.acceptedSequenceAcksTo = acceptedSequenceAcksTo;
        this.incompleteSequenceBehavior = incompleteSequenceBehavior;
    }

    public @Nullable EndpointReference getAcceptedSequenceAcksTo() {
        return acceptedSequenceAcksTo;
    }

    public long getDuration() {
        return duration;
    }

    public boolean doesNotExpire() {
        return duration == Sequence.NO_EXPIRY;
    }

    public @NotNull String getSequenceId() {
        return sequenceId;
    }

    public IncompleteSequenceBehavior getIncompleteSequenceBehavior() {
        return incompleteSequenceBehavior;
    }
}
