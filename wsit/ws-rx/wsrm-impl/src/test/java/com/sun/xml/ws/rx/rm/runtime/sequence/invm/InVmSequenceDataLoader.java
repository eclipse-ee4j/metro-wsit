/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.invm;

import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.State;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceData;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceDataLoader;
import com.sun.xml.ws.rx.util.TimeSynchronizer;
import java.util.HashMap;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class InVmSequenceDataLoader implements SequenceDataLoader {

    public void setUp() {
    }

    public void tearDown() {
    }

    public SequenceData newInstance(boolean isInbound, String sequenceId, String securityContextTokenId, long expirationTime, State state, boolean ackRequestedFlag, long lastMessageId, long lastActivityTime, long lastAcknowledgementRequestTime) {
        SequenceDataPojo sdPojo = new SequenceDataPojo(sequenceId, securityContextTokenId, expirationTime, isInbound, null);
        sdPojo.setState(state);
        sdPojo.setAckRequestedFlag(ackRequestedFlag);
        sdPojo.setLastMessageNumber(lastMessageId);
        sdPojo.setLastActivityTime(lastActivityTime);
        sdPojo.setLastAcknowledgementRequestTime(lastAcknowledgementRequestTime);

        return InVmSequenceData.newInstace(
                sdPojo,
                new TimeSynchronizer() {

                    public long currentTimeInMillis() {
                        return System.currentTimeMillis();
                    }
                },
                new HashMap<String, ApplicationMessage>());
    }
}
