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
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.IncompleteSequenceBehavior;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import javax.xml.ws.EndpointReference;

/**
 *
 */
public class CreateSequenceData {
    public static class Builder {
        private @NotNull final EndpointReference acksToEpr;
        private long duration;
        private @Nullable SecurityTokenReferenceType strType;
        private @Nullable String offeredSequenceId;
        private long offeredSequenceExpiry;
        private IncompleteSequenceBehavior offeredSequenceIncompleteBehavior;

        private Builder(EndpointReference acksToEpr) {
            this.acksToEpr = acksToEpr;
            this.duration = Sequence.NO_EXPIRY;
            this.offeredSequenceExpiry = Sequence.NO_EXPIRY;
            this.offeredSequenceIncompleteBehavior = IncompleteSequenceBehavior.getDefault();
        }

        public void duration(long expiry) {
            this.duration = expiry;
        }

        public Builder strType(SecurityTokenReferenceType value) {
            this.strType = value;

            return this;
        }

        public void offeredSequenceExpiry(long offeredSequenceExpiry) {
            this.offeredSequenceExpiry = offeredSequenceExpiry;
        }


        public Builder offeredInboundSequenceId(String value) {
            this.offeredSequenceId = value;

            return this;
        }

        public void offeredSequenceIncompleteBehavior(IncompleteSequenceBehavior value) {
            this.offeredSequenceIncompleteBehavior = value;
        }

        public CreateSequenceData build() {
            return new CreateSequenceData(acksToEpr, duration, strType, offeredSequenceId, offeredSequenceExpiry, offeredSequenceIncompleteBehavior);
        }
    }

    public static Builder getBuilder(EndpointReference acksToEpr) {
        return new Builder(acksToEpr);
    }

    private @NotNull final EndpointReference acksToEpr;
    private final long duration;
    private @Nullable final String offeredSequenceId;
    private final long offeredSequenceExpiry;
    private @Nullable final SecurityTokenReferenceType strType;
    private @NotNull final IncompleteSequenceBehavior offeredSequenceIncompleteBehavior;

    private CreateSequenceData(
            @NotNull EndpointReference acksToEpr,
            @Nullable long exipry,
            @Nullable SecurityTokenReferenceType strType,
            @Nullable String offeredSequenceId,
            @Nullable long offeredSequenceExpiry,
            @NotNull IncompleteSequenceBehavior offeredSequenceIncompleteBehavior) {
        this.acksToEpr = acksToEpr;
        this.duration = exipry;
        this.offeredSequenceId = offeredSequenceId;
        this.offeredSequenceExpiry = offeredSequenceExpiry;
        this.strType = strType;
        this.offeredSequenceIncompleteBehavior = offeredSequenceIncompleteBehavior;
    }

    public @NotNull EndpointReference getAcksToEpr() {
        return acksToEpr;
    }

    public long getDuration() {
        return duration;
    }

    public boolean doesNotExpire() {
        return duration == Sequence.NO_EXPIRY;
    }

    public @Nullable SecurityTokenReferenceType getStrType() {
        return strType;
    }

    public @Nullable String getOfferedSequenceId() {
        return offeredSequenceId;
    }

    public long getOfferedSequenceExpiry() {
        return offeredSequenceExpiry;
    }

    public boolean offeredSequenceDoesNotExpire() {
        return offeredSequenceExpiry == Sequence.NO_EXPIRY;
    }

    public IncompleteSequenceBehavior getOfferedSequenceIncompleteBehavior() {
        return offeredSequenceIncompleteBehavior;
    }
}
