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

import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class InboundSequenceTest extends TestCase {
    private final SequenceManager sequenceManager = SequenceManagerFactory.INSTANCE.createSequenceManager(
            false,
            "1234567890",
            SequenceTestUtils.getDeliveryQueueBuilder(),
            SequenceTestUtils.getDeliveryQueueBuilder(),
            SequenceTestUtils.getConfiguration(),
            Container.NONE,
            null);
    private Sequence sequence;
    
    public InboundSequenceTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        sequence = sequenceManager.createInboundSequence(
                sequenceManager.generateSequenceUID(),
                null,
                Sequence.NO_EXPIRY);
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        sequenceManager.terminateSequence(sequence.getId());
        super.tearDown();
    }

    public void testRegisterMessage() throws Exception {
        for (int i = 1; i <= 5; i++) {
            DummyAppMessage message = new DummyAppMessage(sequence.getId(), i, null, null, false, "" + i);
            sequence.registerMessage(message, true);
            assertEquals(sequence.getId(), message.getSequenceId());
            assertEquals(i, message.getMessageNumber());

        }
    }

    public void testGetLastMessageId() throws Exception {
        for (int i = 1; i <= 5; i++) {
            sequence.registerMessage(new DummyAppMessage(sequence.getId(), i, null, null, false, "" + i), true);
        }
        assertEquals(5, sequence.getLastMessageNumber());

        DummyAppMessage message = new DummyAppMessage(sequence.getId(), 10, null, null, false, "" + 10);
        sequence.registerMessage(message, true);
        assertEquals(10, sequence.getLastMessageNumber());

    }

    public void testPendingAcknowedgements() throws Exception {
        assertFalse(sequence.hasUnacknowledgedMessages());


        sequence.registerMessage(new DummyAppMessage(sequence.getId(), 1, null, null, false, "A"), true);
        assertTrue(sequence.hasUnacknowledgedMessages());

        sequence.acknowledgeMessageNumber(1);
        assertFalse(sequence.hasUnacknowledgedMessages());

        List<Sequence.AckRange> ackedRages;
        ackedRages = sequence.getAcknowledgedMessageNumbers();
        assertEquals(1, ackedRages.size());
        assertEquals(1, ackedRages.get(0).lower);
        assertEquals(1, ackedRages.get(0).upper);

        for (int i = 2; i <= 5; i++) {
            sequence.registerMessage(new DummyAppMessage(sequence.getId(), i, null, null, false, "" + i), true);
        }
        sequence.acknowledgeMessageNumber(2);
        sequence.acknowledgeMessageNumber(4);
        sequence.acknowledgeMessageNumber(5);
        assertTrue(sequence.hasUnacknowledgedMessages());

        ackedRages = sequence.getAcknowledgedMessageNumbers();
        assertEquals(2, ackedRages.size());
        assertEquals(1, ackedRages.get(0).lower);
        assertEquals(2, ackedRages.get(0).upper);
        assertEquals(4, ackedRages.get(1).lower);
        assertEquals(5, ackedRages.get(1).upper);

        sequence.acknowledgeMessageNumber(3);
        assertFalse(sequence.hasUnacknowledgedMessages());
        ackedRages = sequence.getAcknowledgedMessageNumbers();
        assertEquals(1, ackedRages.size());
        assertEquals(1, ackedRages.get(0).lower);
        assertEquals(5, ackedRages.get(0).upper);

//        boolean passed = false;
//        try {
//            sequence.acknowledgeMessageNumber(4); // duplicate message acknowledgement
//        } catch (IllegalMessageIdentifierException e) {
//            passed = true;
//        }
//        assertTrue("IllegalMessageIdentifierException expected", passed);

        try {
            // duplicate message acknowledgement
            sequence.acknowledgeMessageNumbers(Arrays.asList(new Sequence.AckRange(2, 2),
                    new Sequence.AckRange(4, 5)));
        } catch (UnsupportedOperationException e) {
            return;
        }
        fail("UnsupportedOperationException expected");
    }

    public void testBehaviorAfterCloseOperation() throws Exception {
        sequence.registerMessage(new DummyAppMessage(sequence.getId(), 1, null, null, false, "A"), true);
        sequence.registerMessage(new DummyAppMessage(sequence.getId(), 2, null, null, false, "B"), true);
        sequence.registerMessage(new DummyAppMessage(sequence.getId(), 4, null, null, false, "D"), true);

        sequence.close();

        // sequence acknowledgement behavior
        boolean passed = false;
        try {
            sequence.registerMessage(new DummyAppMessage(sequence.getId(), 3, null, null, false, "C"), true); // error
        } catch (SequenceClosedException e) {
            passed = true;
        }
        assertTrue("Expected exception was not thrown", passed);

        passed = false;
        try {
            sequence.acknowledgeMessageNumber(1); // error
        } catch (SequenceClosedException e) {
            passed = true;
        }
        assertTrue("Expected exception was not thrown", passed);
    }

    public void testSequenceState() {
        Sequence inbound = sequenceManager.createInboundSequence(
                sequenceManager.generateSequenceUID(),
                null,
                Sequence.NO_EXPIRY);
        assertEquals(Sequence.State.CREATED, inbound.getState());

        inbound.close();
        assertEquals(Sequence.State.CLOSED, inbound.getState());

        sequenceManager.terminateSequence(inbound.getId());
        assertEquals(Sequence.State.TERMINATING, inbound.getState());
    }

    public void testStoreAndRetrieveMessage() throws Exception {
        Map<String, ApplicationMessage> correlatedMessageMap = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            ApplicationMessage message = new DummyAppMessage(sequence.getId(), i, null, null, false, "" + i);
            sequence.registerMessage(message, true);
            correlatedMessageMap.put(message.getCorrelationId(), message);
        }

        System.gc();

        for (Map.Entry<String, ApplicationMessage> entry : correlatedMessageMap.entrySet()) {
            Object actual = sequence.retrieveMessage(entry.getKey());
            assertEquals("Retrieved message is not the same as stored message", entry.getValue(), actual);
            sequence.acknowledgeMessageNumber(entry.getValue().getMessageNumber());
        }
    }

    public void testSequenceExpiry() throws Exception {
        Sequence inbound = sequenceManager.createInboundSequence(
        sequenceManager.generateSequenceUID(),
        null,
        System.currentTimeMillis() + 1000);
        
        assertFalse(inbound.isExpired());

        Thread.sleep(1100);
        assertTrue(inbound.isExpired());
    }
}
