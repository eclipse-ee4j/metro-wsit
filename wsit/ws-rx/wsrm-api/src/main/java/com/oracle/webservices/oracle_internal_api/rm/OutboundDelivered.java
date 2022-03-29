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
 * {@code OutboundDelivered} is created by a user of client-side (i.e., RMS) RM.
 *
 * <p>It is passed as a
 * {@link com.oracle.webservices.api.message.PropertySet} to
 * {@code com.oracle.webservices.api.disi.DispatcherRequest#request}.
 *
 */
public abstract class OutboundDelivered
    extends BasePropertySet
{
    // ----------------------------------------------------------------------
    /**
     * Key for delivered property
     *
     * @see #getDelivered
     * @see #setDelivered
     */
    public static final String DELIVERED_PROPERTY = "com.oracle.webservices.api.rm.outbound.delivered.delivered";

    /**
     * Default constructor.
     */
    protected OutboundDelivered() {}

    /**
     * @return The value set by {@link #setDelivered} or {@code null}
     * if {@link #setDelivered} has not been called.
     *
     * @see #DELIVERED_PROPERTY
     * @see #setDelivered
     */
    @Property(DELIVERED_PROPERTY)
    public abstract Boolean getDelivered();

    /**
     * <p>When the RMS receives an ACK from the RMD for the request message instance
     * that contains this {@code com.oracle.webserivces.api.message.Property},
     * then the RMS will call {@code #delivered(true)}.</p>
     *
     * <p>If max retries, timeouts or
     * {@code com.oracle.webservices.api.disi.ClientResponseTransport#fail} is called
     * with an non {@code RMRetryException} exception, then the RMS calls
     * {@code #delivered(false)}.
     *
     * @see #DELIVERED_PROPERTY
     * @see #getDelivered
     */
    public abstract void setDelivered(Boolean accept);


    // ----------------------------------------------------------------------
    /**
     * Key for message identity property
     *
     * @see  #getMessageIdentity
     */
    public static final String MESSAGE_IDENTITY_PROPERTY = "com.oracle.webservices.api.rm.outbound.delivered.message.identity";

    /**
     * @return The identity of the message.
     *
     * @throws RuntimeException if String is longer than 256 characters.
     *
     * @see #MESSAGE_IDENTITY_PROPERTY
     */
    @Property(MESSAGE_IDENTITY_PROPERTY)
    public abstract String getMessageIdentity();


    ////////////////////////////////////////////////////
    //
    // PropertySet boilerplate
    //

    private static final PropertyMap model;

    static {
        model = parse(OutboundDelivered.class, MethodHandles.lookup());
    }

    @Override
    protected PropertyMap getPropertyMap() {
        return model;
    }
}

// End of file.
