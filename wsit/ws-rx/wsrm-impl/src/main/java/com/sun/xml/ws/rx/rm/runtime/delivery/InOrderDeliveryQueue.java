/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.delivery;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.JaxwsApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.delivery.Postman.Callback;
import com.sun.xml.ws.rx.rm.runtime.sequence.OutOfOrderMessageException;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence.AckRange;
import com.sun.xml.ws.rx.util.SuspendedFiberStorage;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 */
class InOrderDeliveryQueue implements DeliveryQueue {

    private static final class MessageIdComparator implements Comparator<ApplicationMessage> {

        @Override
        public int compare(ApplicationMessage o1, ApplicationMessage o2) {
            return (o1.getMessageNumber() < o2.getMessageNumber()) ? -1 : (o1.getMessageNumber() > o2.getMessageNumber()) ? 1 : 0;
        }
    }
    private static final Logger LOGGER = Logger.getLogger(InOrderDeliveryQueue.class);
    private static final MessageIdComparator MSG_ID_COMPARATOR = new MessageIdComparator();
    //
    private final @NotNull Postman postman;
    private final @NotNull Postman.Callback deliveryCallback;
    private final @NotNull Sequence sequence;
    //
    private final long maxMessageBufferSize;
    private final @NotNull BlockingQueue<ApplicationMessage> postponedMessageQueue;
    //
    private volatile boolean isClosed;
    //
    private boolean rejectOutOfOrderMessages;

    public InOrderDeliveryQueue(@NotNull Postman postman, @NotNull Callback deliveryCallback, @NotNull Sequence sequence, long maxMessageBufferSize, boolean rejectOutOfOrderMessages) {
        assert postman != null;
        assert deliveryCallback != null;
        assert sequence != null;
        assert maxMessageBufferSize >= DeliveryQueue.UNLIMITED_BUFFER_SIZE;

        this.postman = postman;
        this.deliveryCallback = deliveryCallback;
        this.sequence = sequence;

        this.maxMessageBufferSize = maxMessageBufferSize;
        this.postponedMessageQueue = new PriorityBlockingQueue<>(32, MSG_ID_COMPARATOR);

        this.isClosed = false;
        
        this.rejectOutOfOrderMessages = rejectOutOfOrderMessages;
    }

    @Override
    public void put(ApplicationMessage message) {
//        LOGGER.info(Thread.currentThread().getName() + " put: mesageNumber = " + message.getMessageNumber());
        assert message.getSequenceId().equals(sequence.getId());

        if (rejectOutOfOrderMessages && !isDeliverable(message)) {
            JaxwsApplicationMessage jam = null;
                    
            if (message instanceof JaxwsApplicationMessage) {
                jam = (JaxwsApplicationMessage) message;
            } else {
                throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1141_UNEXPECTED_MESSAGE_CLASS(
                        message.getClass().getName(),
                        JaxwsApplicationMessage.class.getName())));
            }
            
            String correlationId = jam.getCorrelationId();
            SuspendedFiberStorage sfs = deliveryCallback.getRuntimeContext().suspendedFiberStorage;
            OutOfOrderMessageException e = new OutOfOrderMessageException(sequence.getId(), message.getMessageNumber());
            sfs.resumeFiber(correlationId, e);
        } else {
            try {
                postponedMessageQueue.put(message);
            } catch (InterruptedException ex) {
                throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSRM_1147_ADDING_MSG_TO_QUEUE_INTERRUPTED(), ex));
            }

            tryDelivery();
        }
    }
    
    @Override
    public void onSequenceAcknowledgement() {
//        LOGGER.info(Thread.currentThread().getName() + " onSequenceAcknowledgement");
        if (!isClosed) {
            tryDelivery();
        }
    }
    
    private void tryDelivery() {
//        LOGGER.info(Thread.currentThread().getName() + " postponedMessageQueue.size() = " + postponedMessageQueue.size());
        if (isClosed) {
            throw new RxRuntimeException(LocalizationMessages.WSRM_1160_DELIVERY_QUEUE_CLOSED());
        }

        if (!postponedMessageQueue.isEmpty()) {
            for (;;) {
                ApplicationMessage deliverableMessage = null;

                synchronized (postponedMessageQueue) {
                    ApplicationMessage queueHead = postponedMessageQueue.peek();

//                  LOGGER.info(Thread.currentThread().getName() + " postponedMessageQueue head message number = " + ((queueHead != null) ? queueHead.getMessageNumber() + " is deliverable: " + isDeliverable(queueHead) : "n/a"));

                    if(queueHead != null && isDeliverable(queueHead)) {
                        deliverableMessage = postponedMessageQueue.poll();
                        assert isDeliverable(deliverableMessage);
                    }
                }

                if (deliverableMessage != null) {
//                    LOGGER.info(Thread.currentThread().getName() + " delivering message number = " + deliverableMessage.getMessageNumber());
                    postman.deliver(deliverableMessage, deliveryCallback);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public long getRemainingMessageBufferSize() {
        return (maxMessageBufferSize == DeliveryQueue.UNLIMITED_BUFFER_SIZE) ? maxMessageBufferSize : maxMessageBufferSize - postponedMessageQueue.size();
    }

    @Override
    public void close() {
//        LOGGER.info(Thread.currentThread().getName() + " close");
        isClosed = true;
    }

    private boolean isDeliverable(ApplicationMessage message) {
        List<Sequence.AckRange> ackedIds = sequence.getAcknowledgedMessageNumbers();
        if (ackedIds.isEmpty()) {
            return message.getMessageNumber() == 1L; // this is a first message
        } else {
            AckRange firstRange = ackedIds.get(0);
            return (firstRange.lower != 1L) ? message.getMessageNumber() == 1L : message.getMessageNumber() == firstRange.upper + 1;
        }
    }
}
