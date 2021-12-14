/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessageBase;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.AckRange;
import java.util.List;

class DummyAppMessage extends ApplicationMessageBase {


    public DummyAppMessage(String sequenceId, long messageNumber, String ackSequenceId, List<AckRange> ackRanges, boolean ackReqestedFlag, String correlationId) {
        super(correlationId, sequenceId, messageNumber, null);

        AcknowledgementData.Builder ackDataBuilder = AcknowledgementData.getBuilder();
        if (ackReqestedFlag) {
            ackDataBuilder.ackReqestedSequenceId(sequenceId);
        }

        if (ackSequenceId != null) {
            ackDataBuilder.acknowledgements(ackSequenceId, ackRanges, false);
        }

        setAcknowledgementData(ackDataBuilder.build());
    }

    public DummyAppMessage(String correlationId, String sequenceId, long messageNumber, AcknowledgementData acknowledgementData) {
        super(correlationId, sequenceId, messageNumber, acknowledgementData);
    }

    public DummyAppMessage(String correlationId) {
        super(correlationId);
    }

    @Override
    public State getState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
