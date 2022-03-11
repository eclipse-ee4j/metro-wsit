/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.rx.rm.faults.AbstractSoapFaultException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.delivery.DeliveryQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
@ManagedData
@Description("Reliable Messaging Sequence")
public interface Sequence {

    long UNSPECIFIED_MESSAGE_ID = 0; // this MUST be 0 in order for AbstractSequence.createAckRanges() method to work properly
    long MIN_MESSAGE_ID = 1;
    long MAX_MESSAGE_ID = 9223372036854775807L;
    long NO_EXPIRY = -1;

    enum State {
        // CREATING(10) not needed

        CREATED(15) {

            @Override
            void verifyAcceptingAcknowledgement(String sequenceId, AbstractSoapFaultException.Code code) throws AbstractSoapFaultException {
                // do nothing
            }

            @Override
            void verifyAcceptingMessageRegistration(String sequenceId, AbstractSoapFaultException.Code code) throws AbstractSoapFaultException {
                // do nothing
            }
        },
        CLOSING(20) {

            @Override
            void verifyAcceptingAcknowledgement(String sequenceId, AbstractSoapFaultException.Code code) throws AbstractSoapFaultException {
                throw new SequenceClosedException(sequenceId, LocalizationMessages.WSRM_1135_WRONG_SEQUENCE_STATE_ACKNOWLEDGEMENT_REJECTED(sequenceId, this));
            }

            @Override
            void verifyAcceptingMessageRegistration(String sequenceId, AbstractSoapFaultException.Code code) throws AbstractSoapFaultException {
                throw new SequenceClosedException(sequenceId, LocalizationMessages.WSRM_1136_WRONG_SEQUENCE_STATE_MESSAGE_REGISTRATION(sequenceId, this));
            }
        },
        CLOSED(25) {

            @Override
            void verifyAcceptingAcknowledgement(String sequenceId, AbstractSoapFaultException.Code code) throws AbstractSoapFaultException {
                throw new SequenceClosedException(sequenceId, LocalizationMessages.WSRM_1135_WRONG_SEQUENCE_STATE_ACKNOWLEDGEMENT_REJECTED(sequenceId, this));
            }

            @Override
            void verifyAcceptingMessageRegistration(String sequenceId, AbstractSoapFaultException.Code code) throws AbstractSoapFaultException {
                throw new SequenceClosedException(sequenceId, LocalizationMessages.WSRM_1136_WRONG_SEQUENCE_STATE_MESSAGE_REGISTRATION(sequenceId, this));
            }
        },
        TERMINATING(30) {

            @Override
            void verifyAcceptingAcknowledgement(String sequenceId, AbstractSoapFaultException.Code code) throws AbstractSoapFaultException {
                throw new SequenceTerminatedException(sequenceId, LocalizationMessages.WSRM_1135_WRONG_SEQUENCE_STATE_ACKNOWLEDGEMENT_REJECTED(sequenceId, this), code);
            }

            @Override
            void verifyAcceptingMessageRegistration(String sequenceId, AbstractSoapFaultException.Code code) throws AbstractSoapFaultException {
                throw new SequenceTerminatedException(sequenceId, LocalizationMessages.WSRM_1136_WRONG_SEQUENCE_STATE_MESSAGE_REGISTRATION(sequenceId, this), code);
            }
        };
        private final int value;

        State(int value) {
            this.value = value;
        }

        public int asInt() {
            return value;
        }

        public static State asState(int value) {
            for (State status : State.values()) {
                if (status.value == value) {
                    return status;
                }
            }

            return null;
        }

        abstract void verifyAcceptingMessageRegistration(String sequenceId, AbstractSoapFaultException.Code code);

        abstract void verifyAcceptingAcknowledgement(String sequenceId, AbstractSoapFaultException.Code code);
    }

    enum IncompleteSequenceBehavior {

        /**
         * The default value which indicates that no acknowledged messages in the Sequence
         * will be discarded.
         */
        NO_DISCARD,
        /**
         * Value indicates that the entire Sequence MUST be discarded if the
         * Sequence is closed, or terminated,  when there are one or more gaps
         * in the final SequenceAcknowledgement.
         */
        DISCARD_ENTIRE_SEQUENCE,
        /**
         * Value indicates that messages in the Sequence beyond the first gap
         * MUST be discarded when there are one or more gaps in the final SequenceAcknowledgement.
         */
        DISCARD_FOLLOWING_FIRST_GAP;

        public static IncompleteSequenceBehavior getDefault() {
            return NO_DISCARD;
        }
    }

    class AckRange {

        private static final Comparator<AckRange> COMPARATOR = new Comparator<>() {

            @Override
            public int compare(AckRange range1, AckRange range2) {
                if (range1.lower <= range2.lower) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };

        public static void sort(@NotNull List<AckRange> ranges) {
            if (ranges.size() > 1) {
                Collections.sort(ranges, COMPARATOR);
            }
        }

        public List<Long> rangeValues() {
            List<Long> values = new ArrayList<>();
            for(long value = lower; value <= upper; value++) {
                values.add(value);
            }
            return values;
        }

        //
        public final long lower;
        public final long upper;
        //

        public AckRange(long lower, long upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    /**
     * Returns unique identifier of the sequence
     *
     * @return unique sequence identifier
     */
    @ManagedAttribute
    @Description("Unique sequence identifier")
    String getId();

    /**
     * Provides information on the message number of the last message registered on this sequence
     *
     * @return message number of the last message registered on this sequence
     */
    @ManagedAttribute
    @Description("Last message identifier register on this sequence")
    long getLastMessageNumber();

    /**
     * Registers given message with the sequence
     *
     * @param message application message to be registered
     * @param storeMessageFlag boolean flag indicating whether message should be stored until acknowledged or not
     *
     * @throws DuplicateMessageRegistrationException in case a message with such message number
     * has already been registered with the sequence
     *
     * @exception AbstractSoapFaultException in a case the sequence is closed or terminated
     */
    void registerMessage(@NotNull ApplicationMessage message, boolean storeMessageFlag) throws DuplicateMessageRegistrationException, AbstractSoapFaultException;

    /**
     * Retrieves a message stored within the sequence under the provided {@code correlationId}
     * if avalable. May return {@code null} if no stored message under given {@code correlationId}
     * is available.
     * <p>
     * Availability of the message depends on the message identifier acknowledgement.
     * Message, if stored (see {@link #registerMessage(com.sun.xml.ws.rx.rm.runtime.ApplicationMessage, boolean)}
     * remains available for retrieval until it is acknowledged. Once the message identifier
     * associated with the stored message has been acknowledged, availability of the
     * stored message is no longer guaranteed and stored message becomes eligible for
     * garbage collection (if stored in memory) or removal.
     * <p>
     * Note however, that message MAY still be available even after it has been acknowledged.
     * Thus it is NOT safe to use this method as a test of a message acknowledgement.
     *
     * @param correlationId correlation identifier of the stored {@link ApplicationMessage}
     *
     * @return the message that is stored in the sequence if available, {@code null} otherwise.
     */
    @Nullable
    ApplicationMessage retrieveMessage(@NotNull String correlationId);

    /**
     * Updates a delivery queue for this sequence with any unacknowledged messages that
     * should be sent and returns the delivery queue instance. Messages in the queue are
     * the ones currently waiting for a delivery.
     *
     * @return delivery queue with a messages waiting for a delivery on this particular sequence
     */
    DeliveryQueue getDeliveryQueue();

    /**
     * Marks given message numbers with the sequence as aknowledged
     *
     * @param ranges message number ranges to be acknowledged
     *
     * @exception InvalidAcknowledgementException is generated when acked ranges contain
     * a SequenceAcknowledgement covering messages that have not been sent.
     *
     * @exception AbstractSoapFaultException in case the sequence is terminated
     */
    void acknowledgeMessageNumbers(List<AckRange> ranges) throws AbstractSoapFaultException;

    /**
     * Marks given message number with the sequence as aknowledged
     *
     * @param messageNumber message number to be acknowledged
     *
     * @exception AbstractSoapFaultException in case the sequence is terminated
     */
    void acknowledgeMessageNumber(long messageNumber) throws AbstractSoapFaultException;

    /**
     * Determines whether a given message number is registered as
     * received, unacknowledged and failed over.
     *
     * @param messageNumber message number to be tested
     *
     * @return {@code true} if the message number is registered as received, unacknowledged
     *         and failed over, {@code false} otherwise
     */
    boolean isFailedOver(long messageNumber);

    /**
     * Provides a collection of ranges of message numbers acknowledged with the sequence
     *
     * @return collection of ranges of message numbers registered with the sequence
     */
    List<AckRange> getAcknowledgedMessageNumbers();

    /**
     * Is this message number acknowledged with the sequence?
     * @param messageNumber in a sequence
     * @return true if acknowledged, otherwise false
     */
    boolean isAcknowledged(long messageNumber);

    /**
     * The method may be called to determine whether the sequence has some unacknowledged messages or not
     *
     * @return {@code true} if the sequence has any unacknowledged message identifiers, {@code false} otherwise
     */
    @ManagedAttribute
    @Description("True if the sequence has unacknowledged message identifiers")
    boolean hasUnacknowledgedMessages();

    /**
     * Provides information on the state of the message sequence
     *
     * @return current state of the message sequence
     */
    @ManagedAttribute
    @Description("Runtime state of the sequence")
    State getState();

    /**
     * This method should be called to set the AckRequested flag, which indicates
     * a pending request for acknowledgement of all message identifiers registered
     * with this sequence.
     */
    void setAckRequestedFlag();

    /**
     * This method should be called to clear the AckRequested flag, which indicates
     * that any pending requests for acknowledgement of all message identifiers registered
     * with this sequence were satisfied.
     */
    void clearAckRequestedFlag();

    /**
     * Provides information on the actual AckRequested flag status
     *
     * @return {@code true} if the AckRequested flag is set, {@code false} otherwise
     */
    @ManagedAttribute
    @Description("True if AckRequested flag set")
    boolean isAckRequested();

    /**
     * Updates information on when was the last acknowledgement request for this sequence
     * sent to current time.
     */
    void updateLastAcknowledgementRequestTime();

    /**
     * Determines whether a standalone acnowledgement request can be scheduled or not
     * based on the {@link #hasUnacknowledgedMessages()} value, last acknowledgement request time
     * (see {@link #updateLastAcknowledgementRequestTime()}) and {@code delayPeriod}
     * parameter.
     *
     * Returns {@code true} if the sequence has any pending acknowledgements is set and last
     * acknowledgement request time is older than delay period substracted from the current time.
     * Returns {@code false} otherwise.
     *
     * @param delayPeriod delay period that should pass since the last acknowledgement request
     * before an autonomous acnowledgement request is sent.
     *
     * @return {@code true} or {@code false} depending on whether
     */
    boolean isStandaloneAcknowledgementRequestSchedulable(long delayPeriod);

    /**
     * Provides information on a security session to which this sequence is bound to.
     *
     * @return security token reference identifier to which this sequence is bound to.
     */
    @ManagedAttribute
    @Description("The security token reference identifier to which this sequence is bound")
    String getBoundSecurityTokenReferenceId();

    /**
     * Closes the sequence. Subsequent calls to this method have no effect.
     * <p>
     * Once this method is called, any subsequent calls to the {@code #getNextMessageId()} method will
     * result in a {@link IllegalStateException} being raised. It is however still possible to accept message identifier
     * acknowledgements, as well as retrieve any other information on the sequence.
     */
    void close();

    /**
     * Provides information on the sequence closed status.
     *
     * @return {@code true} if the sequence has been closed, {@code false} otherwise
     */
    @ManagedAttribute
    @Description("True if the sequence has been closed")
    boolean isClosed();

    /**
     * Provides information on the sequence expiration status.
     *
     * @return {@code true} if the sequence has already expired, {@code false} otherwise
     */
    @ManagedAttribute
    @Description("True if the sequence has expired")
    boolean isExpired();

    /**
     * Provides information on the last activity time of this sequence. Following is the
     * list of operations invocation of which causes an update of last activity time:
     * <ul>
     *   <li>{@link #acknowledgeMessageNumber(long)  }</li>
     *   <li>{@link #acknowledgeMessageNumbers(java.util.List)  }</li>
     *   <li>{@link #clearAckRequestedFlag() }</li>
     *   <li>{@link #close() }</li>
     *   <li>{@link #registerMessage(ApplicationMessage, boolean) }</li>
     *   <li>{@link #retrieveMessage(java.lang.String)  }</li>
     *   <li>{@link #setAckRequestedFlag() }</li>
     *   <li>{@link #updateLastAcknowledgementRequestTime() }</li>
     * </ul>
     *
     * @return last activity time on the sequence in milliseconds
     */
    @ManagedAttribute
    @Description("Last activity time on the sequence in milliseconds")
    long getLastActivityTime();

    /**
     * The method is called during the sequence termination to allow sequence object to release its allocated resources
     */
    void preDestroy();
}
