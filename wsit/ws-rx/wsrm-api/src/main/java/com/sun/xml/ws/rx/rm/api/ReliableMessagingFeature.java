/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.api;

import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.api.ha.StickyFeature;
import jakarta.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * Reliable messaging feature is an implementation of {@link WebServiceFeature}
 * that configures Metro Reliable Messaging runtime functionality.
 * <p>
 * Since there is a lot to configure in Reliable Messaging domain, to build create
 * an instance of {@code ReliableMessagingFeature}, use a convenience
 * {@link ReliableMessagingFeatureBuilder} builder class.
 *
 */
@ManagedData
public class ReliableMessagingFeature extends WebServiceFeature implements StickyFeature {

    public static final String ID = "http://docs.oasis-open.org/ws-rx/wsrm/";
    /**
     * A constant specifying the default value of sequence inactivity timeout.
     * Currently the default value is set to 600000.
     */
    public static final long DEFAULT_SEQUENCE_INACTIVITY_TIMEOUT = 600000;
    /**
     * A constant specifying the default value of destination flow control buffer quota.
     * Currently the default value is set to 32.
     */
    public static final long DEFAULT_DESTINATION_BUFFER_QUOTA = 32;
    /**
     * A constant specifying the default value of base message retransmission interval.
     * Currently the default value is set to 2000.
     */
    public static final long DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL = 2000;
    /**
     * A constant specifies the default value of maximum number of message redelivery
     * attempts.
     *
     * Currently, the default value is set to infinity (-1).
     */
    public static final long DEFAULT_MAX_MESSAGE_RETRANSMISSION_COUNT = -1;
    /**
     * A constant specifies the default value of maximum number of attempts to send
     * a reliable messaging session control messgae such as CreateSequence, CloseSequence
     * and TerminateSequence
     *
     * Currently, the default value is set to 3.
     */
    public static final long DEFAULT_MAX_RM_SESSION_CONTROL_MESSAGE_RESEND_ATTEMPTS = 3;
    /**
     * Specifies the duration in milliseconds after which the RM Destination will
     * transmit an acknowledgement.
     * Currently the default value is set to -1 {@literal =>} unspecified.
     */
    public static final long DEFAULT_ACKNOWLEDGEMENT_TRANSMISSION_INTERVAL = -1;
    /**
     * A constant specifying the default value of interval between sending subsequent
     * acknowledgement request messages. Currently the default value is set to 2000.
     */
    public static final long DEFAULT_ACK_REQUEST_TRANSMISSION_INTERVAL = 2000;
    /**
     * A constant specifying the default value of close sequence operation timeout.
     * Currently the default value is set to 3000.
     */
    public static final long DEFAULT_CLOSE_SEQUENCE_OPERATION_TIMEOUT = 3000;
    /**
     * Default period (in milliseconds) of a sequence manager maintenance task execution.
     */
    public static final long DEFAULT_SEQUENCE_MANAGER_MAINTENANCE_PERIOD = 60000;
    /**
     * A constant specifying the default value for how many concurrently active
     * RM sessions (measured based on inbound RM sequences) the sequence manager
     * dedicated to the WS Endpoint accepts before starting to refuse new requests
     * for sequence creation.
     *
     * Currently, the default value is set to infinity (-1).
     */
    public static final long DEFAULT_MAX_CONCURRENT_SESSIONS = -1;

    /**
     * A constant specifying the default value for disabling the generation of the {@code Offer}
     * element as part of the {@code CreateSequence} message.
     */
    public static final boolean DEFAULT_OFFER_ELEMENT_GENERATION_DISABLED = false;

    /**
     * A constant specifying the default value for enabling "reject out-of-order messages".
     */
    public static final boolean DEFAULT_REJECT_OUT_OF_ORDER_MESSAGES = false;

    /**
     * A constant specifying the default value for turning off state update when
     * received AckRequested is processed at RMD.
     */
    public static final boolean DEFAULT_STATE_UPDATE_ON_RECEIVED_ACKREQUESTED_DISABLED = false;

    /**
     * A constant specifying the default value for enabling XA TX at server side RMD.
     */
    public static final boolean DEFAULT_XA_TX_FOR_SERVER_RMD = false;

    /**
     * A constant specifying the default value for XA TX timeout that is used at
     * server side RMD (in seconds).
     */
    public static final int DEFAULT_XA_TX_FOR_SERVER_RMD_TIMEOUT_SECONDS = 60;

    /**
     * Defines the enumeration of possible security binding mechanism options that
     * can be applied to a created sequence.
     *
     * @see SecurityBinding#NONE
     * @see SecurityBinding#STR
     * @see SecurityBinding#TRANSPORT
     */
    public static enum SecurityBinding {

        /**
         * An RM Sequence MUST be bound to an explicit token that is referenced
         * from a wsse:SecurityTokenReference in the CreateSequence message.
         *
         * @see SecurityBinding
         */
        STR,
        /**
         * <p>
         * An RM Sequence MUST be bound to the session(s) of the underlying transport-level
         * security protocol (e.g. SSL/TLS) used to carry the {@code CreateSequence}
         * and {@code CreateSequenceResponse} messages.
         * </p>
         * <p>
         * The assertion specifying this requirement MUST be used in conjunction
         * with the sp:TransportBinding assertion that requires the use of some
         * transport-level security mechanism (e.g. sp:HttpsToken)."
         * </p>
         *
         * @see SecurityBinding
         */
        TRANSPORT,
        /**
         * There are no security binding requirements specified for the message.
         *
         * @see SecurityBinding
         */
        NONE;

        /**
         * Provides a default security binding value.
         *
         * @return a default security binding value. Currently returns {@link #NONE}.
         *
         * @see SecurityBinding
         */
        public static SecurityBinding getDefault() {
            return NONE; // if changed, update also in ReliableMesaging annotation
        }
    }

    /**
     * Defines the enumeration of Delivery Assurance options, which
     * can be supported by RM Sources and RM Destinations.
     *
     * @see DeliveryAssurance#EXACTLY_ONCE
     * @see DeliveryAssurance#AT_LEAST_ONCE
     * @see DeliveryAssurance#AT_MOST_ONCE
     */
    public static enum DeliveryAssurance {

        /**
         * Each message is to be delivered exactly once; if a message cannot be
         * delivered then an error will be raised by the RM Source and/or RM Destination.
         * The requirement on an RM Source is that it should retry transmission of
         * every message sent by the Application Source until it receives an acknowledgement
         * from the RM Destination. The requirement on the RM Destination is that it
         * should retry the transfer to the Application Destination of any message
         * that it accepts from the RM Source until that message has been successfully
         * delivered, and that it must not deliver a duplicate of a message that
         * has already been delivered.
         *
         * @see DeliveryAssurance
         */
        EXACTLY_ONCE,
        /**
         * Each message is to be delivered at least once, or else an error will
         * be raised by the RM Source and/or RM Destination. The requirement on
         * an RM Source is that it should retry transmission of every message sent
         * by the Application Source until it receives an acknowledgement from the
         * RM Destination. The requirement on the RM Destination is that it should
         * retry the transfer to the Application Destination of any message that it
         * accepts from the RM Source, until that message has been successfully delivered.
         * There is no requirement for the RM Destination to apply duplicate message
         * filtering.
         *
         * @see DeliveryAssurance
         */
        AT_LEAST_ONCE,
        /**
         * Each message is to be delivered at most once. The RM Source may retry
         * transmission of unacknowledged messages, but is not required to do so.
         * The requirement on the RM Destination is that it must filter out duplicate
         * messages, i.e. that it must not deliver a duplicate of a message that
         * has already been delivered.
         *
         * @see DeliveryAssurance
         */
        AT_MOST_ONCE;

        /**
         * Provides a default delivery assurance value.
         *
         * @return a default delivery assurance value. Currently returns {@link #EXACTLY_ONCE}.
         *
         * @see DeliveryAssurance
         */
        public static DeliveryAssurance getDefault() {
            return DeliveryAssurance.EXACTLY_ONCE; // if changed, update also in ReliableMesaging annotation
        }
    }

    /**
     * Defines the enumeration of all possible backoff algortihms that can be applied
     * for to message retransmission.
     *
     * @see BackoffAlgorithm#CONSTANT
     * @see BackoffAlgorithm#EXPONENTIAL
     */
    public static enum BackoffAlgorithm {

        /**
         * This algorithm ensures that a message retransmission rate remains constant
         * at all times.
         *
         * @see BackoffAlgorithm
         */
        CONSTANT("Constant") {

            public long getDelayInMillis(int resendAttemptNumber, long baseRate) {
                return baseRate;
            }
        },
        /**
         * This algorithm ensures that a message retransmission rate is multiplicatively
         * decreased with each resend of the particular message.
         *
         * @see BackoffAlgorithm
         */
        EXPONENTIAL("Exponential") {

            public long getDelayInMillis(int resendAttemptNumber, long baseRate) {
                return resendAttemptNumber * baseRate;
            }
        };

        private final String name;

        private BackoffAlgorithm(String name) {
            this.name = name;
        }

        public static BackoffAlgorithm parse(String name) {
            for (BackoffAlgorithm value : values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }

            return null;
        }

        /**
         * Provides a default back-off algorithm value.
         *
         * @return a default back-off algorithm value. Currently returns {@link #CONSTANT}.
         *
         * @see BackoffAlgorithm
         */
        public static BackoffAlgorithm getDefault() {
            return BackoffAlgorithm.CONSTANT;
        }



        /**
         * Calculates the delay before the next possible scheduled resume time based on the resend
         * attempt number.
         *
         * @param resendAttemptNumber number of the resend attempt for a message
         * @param baseRate base resend interval
         *
         * @return next scheduled resume time
         */
        public abstract long getDelayInMillis(int resendAttemptNumber, long baseRate);
    }
    //
    private final RmProtocolVersion version;
    private final long sequenceInactivityTimeout;
    private final long destinationBufferQuota;
    private final boolean orderedDelivery;
    private final DeliveryAssurance deliveryAssurance;
    private final SecurityBinding securityBinding;
    //
    private final long messageRetransmissionInterval;
    private final BackoffAlgorithm retransmissionBackoffAlgorithm;
    private final long maxMessageRetransmissionCount;
    //
    private final long maxRmSessionControlMessageResendAttempts;
    //
    private final long acknowledgementTransmissionInterval;
    private final long ackRequestTransmissionInterval;
    //
    private final long closeSequenceOperationTimeout;
    //
    private final boolean persistenceEnabled;
    //
    private final long sequenceManagerMaintenancePeriod;
    //
    private final long maxConcurrentSessions;
    //
    private final boolean offerElementGenerationDisabled;
    //Do not retain out-of-order messages waiting for 'gap'
    //message(s) to arrive
    private final boolean rejectOutOfOrderMessagesEnabled;

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    public ReliableMessagingFeature() {
        this(true);
    }

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    public ReliableMessagingFeature(boolean enabled) {
        this(
                enabled, // this.enabled
                RmProtocolVersion.getDefault(), // this.rmVersion
                DEFAULT_SEQUENCE_INACTIVITY_TIMEOUT, // this.inactivityTimeout
                DEFAULT_DESTINATION_BUFFER_QUOTA, // this.bufferQuota
                false, // this.orderedDelivery
                DeliveryAssurance.getDefault(), // this.deliveryAssurance
                SecurityBinding.getDefault(), // this.securityBinding
                DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL, // this.baseRetransmissionInterval
                BackoffAlgorithm.getDefault(), // this.retransmissionBackoffAlgorithm
                DEFAULT_MAX_MESSAGE_RETRANSMISSION_COUNT,
                DEFAULT_MAX_RM_SESSION_CONTROL_MESSAGE_RESEND_ATTEMPTS, // this.maxRmSessionControlMessageResendAttempts
                DEFAULT_ACKNOWLEDGEMENT_TRANSMISSION_INTERVAL, // this.acknowledgementTransmissionInterval
                DEFAULT_ACK_REQUEST_TRANSMISSION_INTERVAL, // this.ackRequestTransmissionInterval
                DEFAULT_CLOSE_SEQUENCE_OPERATION_TIMEOUT, // this.closeSequenceOperationTimeout
                false, // this.persistenceEnabled
                DEFAULT_SEQUENCE_MANAGER_MAINTENANCE_PERIOD,
                DEFAULT_MAX_CONCURRENT_SESSIONS,
                DEFAULT_OFFER_ELEMENT_GENERATION_DISABLED,
                DEFAULT_REJECT_OUT_OF_ORDER_MESSAGES);
    }

    @FeatureConstructor({
        "enabled",
        "version",
        "sequenceInactivityTimeout",
        "destinationBufferQuota",
        "orderedDeliveryEnabled",
        "deliveryAssurance",
        "securityBinding",
        "persistenceEnabled",
        "sequenceManagerMaintenancePeriod",
        "maxConcurrentSessions"
    })
    public ReliableMessagingFeature(
            boolean enabled,
            RmProtocolVersion version,
            long inactivityTimeout,
            long bufferQuota,
            boolean orderedDelivery,
            DeliveryAssurance deliveryAssurance,
            SecurityBinding securityBinding,
            boolean persistenceEnabled,
            long sequenceManagerMaintenancePeriod,
            long maxConcurrentSessions) {

        this(
                enabled, // this.enabled
                version, // this.rmVersion
                inactivityTimeout, // this.inactivityTimeout
                bufferQuota, // this.bufferQuota
                orderedDelivery, // this.orderedDelivery
                deliveryAssurance, // this.deliveryAssurance
                securityBinding, // this.securityBinding

                DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL, // this.baseRetransmissionInterval
                BackoffAlgorithm.getDefault(), // this.retransmissionBackoffAlgorithm
                DEFAULT_MAX_MESSAGE_RETRANSMISSION_COUNT,

                DEFAULT_MAX_RM_SESSION_CONTROL_MESSAGE_RESEND_ATTEMPTS, // this.maxRmSessionControlMessageResendAttempts

                DEFAULT_ACKNOWLEDGEMENT_TRANSMISSION_INTERVAL, // this.acknowledgementTransmissionInterval
                DEFAULT_ACK_REQUEST_TRANSMISSION_INTERVAL, // this.ackRequestTransmissionInterval
                DEFAULT_CLOSE_SEQUENCE_OPERATION_TIMEOUT, // this.closeSequenceOperationTimeout
                persistenceEnabled, // this.persistenceEnabled
                sequenceManagerMaintenancePeriod,
                maxConcurrentSessions,
                DEFAULT_OFFER_ELEMENT_GENERATION_DISABLED,
                DEFAULT_REJECT_OUT_OF_ORDER_MESSAGES);
    }

    ReliableMessagingFeature(
            boolean enabled,
            RmProtocolVersion version,
            long inactivityTimeout,
            long bufferQuota,
            boolean orderedDelivery,
            DeliveryAssurance deliveryAssurance,
            SecurityBinding securityBinding,
            long messageRetransmissionInterval,
            BackoffAlgorithm retransmissionBackoffAlgorithm,
            long maxMessageRetransmissionCount,
            long maxRmSessionControlMessageResendAttempts,
            long acknowledgementTransmissionInterval,
            long ackRequestTransmissionInterval,
            long closeSequenceOperationTimeout,
            boolean persistenceEnabled,
            long sequenceManagerMaintenancePeriod,
            long maxConcurrentRmSessions,
            boolean offerElementGenerationDisabled,
            boolean rejectOutOfOrderMessagesEnabled) {

        super.enabled = enabled;
        this.version = version;
        this.sequenceInactivityTimeout = inactivityTimeout;
        this.destinationBufferQuota = bufferQuota;
        this.orderedDelivery = orderedDelivery;
        this.deliveryAssurance = deliveryAssurance;
        this.securityBinding = securityBinding;
        this.messageRetransmissionInterval = messageRetransmissionInterval;
        this.retransmissionBackoffAlgorithm = retransmissionBackoffAlgorithm;
        this.maxMessageRetransmissionCount = maxMessageRetransmissionCount;
        this.maxRmSessionControlMessageResendAttempts = maxRmSessionControlMessageResendAttempts;
        this.acknowledgementTransmissionInterval = acknowledgementTransmissionInterval;
        this.ackRequestTransmissionInterval = ackRequestTransmissionInterval;
        this.closeSequenceOperationTimeout = closeSequenceOperationTimeout;
        this.persistenceEnabled = persistenceEnabled;
        this.sequenceManagerMaintenancePeriod = sequenceManagerMaintenancePeriod;
        this.maxConcurrentSessions = maxConcurrentRmSessions;
        this.offerElementGenerationDisabled = offerElementGenerationDisabled;
        this.rejectOutOfOrderMessagesEnabled = rejectOutOfOrderMessagesEnabled;
    }

    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    /**
     * Specifies which WS-RM version SOAP messages and SOAP message headers should
     * be used for communication between RM source and RM destination
     *
     * @return version currently configured for the feature. If not set explicitly,
     *         the default value is specified by a call to {@link RmProtocolVersion#getDefault()}.
     */
    @ManagedAttribute
    public RmProtocolVersion getProtocolVersion() {
        return version;
    }

    /**
     * Specifies a period of inactivity for a Sequence in ms.
     *
     * @return currently configured sequence inactivity timeout. If not set explicitly,
     *         the default value is specified by {@link #DEFAULT_SEQUENCE_INACTIVITY_TIMEOUT}
     *         constant.
     */
    @ManagedAttribute
    public long getSequenceInactivityTimeout() {
        return sequenceInactivityTimeout;
    }

    /**
     * Specifies whether each created RM sequence must be bound to a specific
     * underlying security token or secured transport.
     *
     * @return configured security binding requirement. If not set explicitly, the
     *         default value is specified by a call to {@link SecurityBinding#getDefault()}.
     *
     * @see SecurityBinding
     */
    @ManagedAttribute
    public SecurityBinding getSecurityBinding() {
        return securityBinding;
    }

    /**
     * Specifies the message delivery quality of service between the RM and
     * application layer. It expresses the delivery assurance in effect between
     * the RM Destination and its corresponding application destination, and it
     * also indicates requirements on any RM Source that transmits messages to
     * this RM destination. Conversely when used by an RM Source it expresses
     * the delivery assurance in effect between the RM Source and its corresponding
     * application source, as well as indicating requirements on any RM Destination
     * that receives messages from this RM Source. In either case the delivery
     * assurance does not affect the messages transmitted on the wire.
     *
     * @return currently configured delivery assurance mode. If not set explicitly,
     *         the default value is specified by a call to {@link DeliveryAssurance#getDefault()}.
     *
     * @see DeliveryAssurance
     */
    @ManagedAttribute
    public DeliveryAssurance getDeliveryAssurance() {
        return deliveryAssurance;
    }

    /**
     * <p>
     * Specifies a requirement that messages from each individual Sequence are
     * to be delivered in the same order they have been sent by the Application
     * Source. The RM Source will ensure that the ordinal position of
     * each message in the Sequence (as indicated by a message Sequence number)
     * is consistent with the order in which the messages have been sent from
     * the Application Source. The RM Destination will deliver received messages
     * for each Sequence in the order indicated by the message numbering.
     * </p>
     * <p>
     * In-order delivery can be used in combination with any of the {@link DeliveryAssurance} values,
     * and the requirements of those values will also be met. In particular if the
     * {@link DeliveryAssurance#AT_LEAST_ONCE} or {@link DeliveryAssurance#EXACTLY_ONCE}
     * value is applied and the RM Destination detects  a gap in the Sequence then
     * the RM Destination will not deliver any subsequent messages from that Sequence
     * until the missing messages are received or until the Sequence is closed.
     * </p>
     *
     * @return {@code true} if the ordered delivery si required, {@code false} otherwise.
     *         If not set explicitly, the default value is {@code false}.
     */
    @ManagedAttribute
    public boolean isOrderedDeliveryEnabled() {
        return orderedDelivery;
    }

    /**
     * This attribute may be used together with ordered delivery requirement.
     * It specifies the maximum number of out-of-order unprocessed request messages
     * that may be stored in the unprocessed request message buffer within the RM
     * destination before the RM destination starts rejecting new request messages.
     *
     * @return currently configured flow control buffer on the destination. If not
     *         set explicitly, the default value is specified by {@link #DEFAULT_DESTINATION_BUFFER_QUOTA}
     *         constant.
     */
    @ManagedAttribute
    public long getDestinationBufferQuota() {
        return destinationBufferQuota;
    }

    /**
     * Specifies how long the RM Source will wait after transmitting a message
     * before retransmitting the message if no acknowledgement arrives.
     *
     * @return currently configured base retransmission interval. If not set explicitly,
     *         the default value is specified by {@link #DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL}
     *         constant.
     */
    @ManagedAttribute
    public long getMessageRetransmissionInterval() {
        return messageRetransmissionInterval;
    }

    /**
     * Specifies that the retransmission interval will be adjusted using a specific
     * backoff algorithm.
     *
     * @return currently configured retransmission back-off algorithm that should be
     *         used. If not set explicitly, the default value is specified by a
     *         call to {@link BackoffAlgorithm#getDefault()}.
     *
     * @see BackoffAlgorithm
     */
    @ManagedAttribute
    public BackoffAlgorithm getRetransmissionBackoffAlgorithm() {
        return retransmissionBackoffAlgorithm;
    }

    /**
     * A message is considered to be transferred if its delivery at the recipient
     * has been acknowledged by the recipient.
     *
     * If an acknowledgment has not been received within a certain amount of time
     * for a message that has been transmitted, the infrastructure automatically
     * retransmits the message. By default, the number of retransmissions is not
     * limited. This can be checked by a call to {@code #isMessageRetransmissionLimited()}
     * method. In case the {@code isMessageRetransmissionLimited()} returns {@code true},
     * the infrastructure tries to send the message for at most a {@code getMaxMessageRetransmissionCount()}
     * number of times. Not receiving an acknowledgment before this limit is reached
     * is considered to be a fatal communication failure, and causes the RM session to fail.
     *
     * The infrastructure uses a back-off algorithm retrieved via
     * {@link #getRetransmissionBackoffAlgorithm()} to determine when to retransmit,
     * based on the configured base retransmission time retrieved via a call to
     * {@link #getMessageRetransmissionInterval()}.
     *
     * @return maximum number of message transmission retries
     */
    @ManagedAttribute
    public long getMaxMessageRetransmissionCount() {
        return maxMessageRetransmissionCount;
    }

    /**
     * Returns {@code true} if the next attempt to retransmit a message indicated by
     * {@code nextRetransmissionCount} input parameter is allowed as per current
     * feature configuration.
     *
     * @param nextRetransmissionCount indicates the ordinal number of the next (potential)
     * attempt to retransmit a single message
     *
     * @return {@code true} if it is possible to retransmit a message, {@code false} otherwise.
     *
     * @see #getMaxMessageRetransmissionCount()
     */
    public boolean canRetransmitMessage(long nextRetransmissionCount) {
        if (maxMessageRetransmissionCount < 0) {
            return true;
        }

        return nextRetransmissionCount <= maxMessageRetransmissionCount;
    }

    /**
     * The infrastructure tries to send each RM session protocol control message
     * such as CreateSequence, CloseSequence, TerminateSequence at most a
     * {@link #getMaxRmSessionControlMessageResendAttempts()} number of times. Not receiving
     * an acknowledgment before this limit is reached is considered a fatal communication
     * failure, and causes the RM session to fail.
     *
     * @return maximum number of reliable messaging session handshake message transmission attempts
     */
    @ManagedAttribute
    public long getMaxRmSessionControlMessageResendAttempts() {
        return maxRmSessionControlMessageResendAttempts;
    }

    /**
     * Specifies the duration after which the RM Destination will transmit an acknowledgement.
     * Specified in milliseconds.
     *
     * @return currently configured acknowledgement transmission interval. If not set explicitly,
     *         the default value is specified by the {@link #DEFAULT_ACKNOWLEDGEMENT_TRANSMISSION_INTERVAL}
     *         constant.
     */
    @ManagedAttribute
    public long getAcknowledgementTransmissionInterval() {
        return acknowledgementTransmissionInterval;
    }

    /**
     * Specifies interval between sending subsequent acknowledgement request messages
     * by an RM Source in case of any unacknowledged messages on the sequence.
     *
     * @return currently configured acknowledgement request transmission interval.
     *         If not set explicitly, the default value is specified by the
     *         {@link #DEFAULT_ACK_REQUEST_TRANSMISSION_INTERVAL} constant.
     */
    @ManagedAttribute
    public long getAckRequestTransmissionInterval() {
        return ackRequestTransmissionInterval;
    }

    /**
     * Specifies the timeout for a {@code CloseSequenceRequest} message. If no response
     * is returned from RM destination before the timout expires, the sequence is
     * automatically closed by the RM source and all associated resources are released.
     *
     * @return currently configured close sequence operation timeout. If not set explicitly,
     *         the default value is specified by the {@link #DEFAULT_CLOSE_SEQUENCE_OPERATION_TIMEOUT}
     *         constant.
     */
    @ManagedAttribute
    public long getCloseSequenceOperationTimeout() {
        return closeSequenceOperationTimeout;
    }

    /**
     * Specifies whether the runtime should use persistent message storage or not.
     *
     * @return {@code true} if the runtime should use persistent message storage, {@code false} otherwise
     */
    @ManagedAttribute
    public boolean isPersistenceEnabled() {
        return persistenceEnabled;
    }

    /**
     * Specifies the period (in milliseconds) of a sequence manager maintenance task execution.
     *
     * @return the period (in milliseconds) of a sequence manager maintenance task execution.
     */
    @ManagedAttribute
    public long getSequenceManagerMaintenancePeriod() {
        return sequenceManagerMaintenancePeriod;
    }

    /**
     * Specifies how many concurrently active RM sessions (measured based on
     * inbound RM sequences) the sequence manager dedicated to the WS Endpoint
     * accepts before starting to refuse new requests for sequence creation.
     *
     * @return maximum number of concurrent RM sessions
     */
    @ManagedAttribute
    public long getMaxConcurrentSessions() {
        return maxConcurrentSessions;
    }

    /**
     * Specifies whether the generation of the {@code Offer} element as part of
     * the {@code CreateSequence} message is forced-disabled or not.
     *
     * @return {@code true} if the generation of the {@code Offer} element as part of
     *         the {@code CreateSequence} message is forced-disabled, {@code false} otherwise.
     */
    public boolean isOfferElementGenerationDisabled() {
        return offerElementGenerationDisabled;
    }

    /**
     * Specifies whether RMD should reject out-of-order messages that it receives.
     *
     * @return {@code true} if RMD should reject out-of-order messages. Default is false.
     */
    public boolean isRejectOutOfOrderMessagesEnabled() {
        return rejectOutOfOrderMessagesEnabled;
    }

    @Override
    public String toString() {
        return "ReliableMessagingFeature" +
                "{\n\tversion=" + version +
                ",\n\tdeliveryAssurance=" + deliveryAssurance +
                ",\n\torderedDelivery=" + orderedDelivery +
                ",\n\tsecurityBinding=" + securityBinding +
                ",\n\tdestinationBufferQuota=" + destinationBufferQuota +
                ",\n\tpersistenceEnabled=" + persistenceEnabled +
                ",\n\tmessageRetransmissionInterval=" + messageRetransmissionInterval +
                ",\n\tretransmissionBackoffAlgorithm=" + retransmissionBackoffAlgorithm +
                ",\n\tmaxMessageRetransmissionCount=" + maxMessageRetransmissionCount +
                ",\n\tmaxRmSessionControlMessageResendAttempts=" + maxRmSessionControlMessageResendAttempts +
                ",\n\tacknowledgementTransmissionInterval=" + acknowledgementTransmissionInterval +
                ",\n\tackRequestTransmissionInterval=" + ackRequestTransmissionInterval +
                ",\n\tsequenceInactivityTimeout=" + sequenceInactivityTimeout +
                ",\n\tcloseSequenceOperationTimeout=" + closeSequenceOperationTimeout +
                ",\n\tsequenceManagerMaintenancePeriod=" + sequenceManagerMaintenancePeriod +
                ",\n\tmaxConcurrentSessions=" + maxConcurrentSessions +
                ",\n\tofferElementGenerationDisabled=" + offerElementGenerationDisabled +
                ",\n\trejectOutOfOrderMessagesEnabled=" + rejectOutOfOrderMessagesEnabled +
                "\n}";
    }
}
