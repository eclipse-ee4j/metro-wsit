/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.State;

/**
 *
 */
public interface SequenceDataLoader {
    public void setUp();

    public void tearDown();

    public SequenceData newInstance(boolean isInbound, String sequenceId, String securityContextTokenId, long expirationTime, State state, boolean ackRequestedFlag, long lastMessageId, long lastActivityTime, long lastAcknowledgementRequestTime);
}
