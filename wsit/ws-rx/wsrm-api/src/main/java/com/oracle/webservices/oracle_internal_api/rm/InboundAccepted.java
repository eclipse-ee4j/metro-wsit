/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.oracle.webservices.oracle_internal_api.rm;

import com.oracle.webservices.api.message.BasePropertySet;

import java.lang.invoke.MethodHandles;

/**
 * {@code InboundAccepted} is created by the RMD.
 *
 * <p>It is passed as a
 * {@link com.oracle.webservices.api.message.PropertySet} to
 * {@code com.oracle.webservices.api.disi.ProviderRequest#request}.
 *
 */
public abstract class InboundAccepted
    extends BasePropertySet
{
    // ----------------------------------------------------------------------
    /**
     * Key for accepted property
     *
     * @see #getAccepted
     * @see #setAccepted
     */
    public static final String ACCEPTED_PROPERTY = "com.oracle.webservices.api.rm.inbound.accepted.accepted";

    /**
     * Default constructor.
     */
    protected InboundAccepted() {}

    /**
     * @return the value set via {@link #setAccepted} or {@code null}
     * if {@link #setAccepted} has not been called or if the call to
     * {@link #setAccepted} resulted in {@link InboundAcceptedAcceptFailed}
     * being thrown.
     *
     * @see #ACCEPTED_PROPERTY
     * @see #setAccepted
     */
    @Property(ACCEPTED_PROPERTY)
    public abstract Boolean getAccepted();

    /**
     * <p>When the user determines that the message has been delivered to them then they call {@code #setAccepted(true)}.</p>
     *
     * <p>The RMD will <em>not</em> acknowledge the message to the RMS until {@code #setAccepted(true)} is called.</p>
     *
     * <p>If the user calls {@code #setAccepted(false)} then the RMD will not
     * acknowledge the delivery of this particular request.  Note: if the
     * RMS sends a retry, that is considered a new request and the
     * delivery/acceptance process starts anew.</p>
     *
     * <p>If the user calls {@code #setAccepted(false)} and an atomic
     * transaction is being used to handle the message, then that
     * transaction will be rolled back.</p>
     *
     * @throws InboundAcceptedAcceptFailed
     *     If the user calls {@code #accepted(true)} but the RMD is
     *     not able to internally record the message as delivered
     *     (e.g., an atomic transaction fails to commit) then this
     *     exception is thrown.
     *
     * @see #ACCEPTED_PROPERTY
     * @see #getAccepted
     */
    public abstract void setAccepted(Boolean accept) throws InboundAcceptedAcceptFailed;


    // ----------------------------------------------------------------------
    /**
     * Key for inbound RM sequence id
     *
     * @see  #getRMSequenceId
     */
    public static final String RM_SEQUENCE_ID_PROPERTY = "com.oracle.webservices.api.rm.inbound.accepted.rm.sequence.id";

    /**
     * @return The RM sequence id associated with the message.
     *     Note: it may be {@code null} if RM is not enabled.
     *
     * @see #RM_SEQUENCE_ID_PROPERTY
     */
    @Property(RM_SEQUENCE_ID_PROPERTY)
    public abstract String getRMSequenceId();


    // ----------------------------------------------------------------------
    /**
     * Key for inbound RM message number
     *
     * @see  #getRMMessageNumber
     */
    public static final String RM_MESSAGE_NUMBER_PROPERTY = "com.oracle.webservices.api.rm.inbound.accepted.rm.message.number";

    /**
     * @return The RM message number associated with the message.
     *     Note: it may be {@code -1} if RM is not enabled.
     *
     * @see #RM_MESSAGE_NUMBER_PROPERTY
     */
    @Property(RM_MESSAGE_NUMBER_PROPERTY)
    public abstract long getRMMessageNumber();


    ////////////////////////////////////////////////////
    //
    // PropertySet boilerplate
    //

    private static final PropertyMap model;

    static {
        model = parse(InboundAccepted.class, MethodHandles.lookup());
    }

    @Override
    protected PropertyMap getPropertyMap() {
        return model;
    }
}

// End of file.
