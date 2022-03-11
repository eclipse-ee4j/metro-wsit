/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence.invm;

import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.State;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class SequenceDataPojoTest extends TestCase {

    public SequenceDataPojoTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSerialization() throws Exception {
        SequenceDataPojo original = new SequenceDataPojo("sequenceId", "boundTokenId", 1111, true, null);
        original.setAckRequestedFlag(true);
        original.setLastAcknowledgementRequestTime(2222);
        original.setLastActivityTime(3333);
        original.setLastMessageNumber(4444);
        original.setState(State.CLOSED);
        original.getReceivedUnackedMessageNumbers().add(Long.valueOf(1));
        original.getReceivedUnackedMessageNumbers().add(Long.valueOf(2));
        original.getReceivedUnackedMessageNumbers().add(Long.valueOf(3));
        original.getUnackedNumberToCorrelationIdMap().put(Long.valueOf(1), "1");
        original.getUnackedNumberToCorrelationIdMap().put(Long.valueOf(2), "2");
        original.getUnackedNumberToCorrelationIdMap().put(Long.valueOf(3), "3");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);

        Object _replica = ois.readObject();
        ois.close();

        assertTrue("Unexpected replica class: " + _replica.getClass(), _replica instanceof SequenceDataPojo);
        SequenceDataPojo replica = (SequenceDataPojo) _replica;

        assertEquals("Original and replica are expected to be equal", original, replica);
        assertEquals(original.getAckRequestedFlag(), replica.getAckRequestedFlag());
        assertEquals(original.getAllUnackedMessageNumbers(), replica.getAllUnackedMessageNumbers());
        assertEquals(original.getBoundSecurityTokenReferenceId(), replica.getBoundSecurityTokenReferenceId());
        assertEquals(original.getExpirationTime(), replica.getExpirationTime());
        assertEquals(original.getLastAcknowledgementRequestTime(), replica.getLastAcknowledgementRequestTime());
        assertEquals(original.getLastActivityTime(), replica.getLastActivityTime());
        assertEquals(original.getLastMessageNumber(), replica.getLastMessageNumber());
        assertEquals(original.getReceivedUnackedMessageNumbers(), replica.getReceivedUnackedMessageNumbers());
        assertEquals(original.getSequenceId(), replica.getSequenceId());
        assertEquals(original.getState(), replica.getState());
        assertEquals(original.getUnackedNumberToCorrelationIdMap(), replica.getUnackedNumberToCorrelationIdMap());
    }

}
