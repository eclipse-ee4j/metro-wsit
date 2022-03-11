/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: PropertyCallback.java,v 1.2 2010-10-21 15:37:24 snajper Exp $
 */

package com.sun.xml.wss.impl.callback;

import javax.security.auth.callback.Callback;

/**
 * This callback is an optional callback that can be handled by an
 * implementation of CallbackHandler to specify the values of properties
 * configurable with XWS-Security runtime. The properties are:
 *
 * <ul><li>MAX_CLOCK_SKEW  : The assumed maximum skew (milliseconds) between the local times of any two systems </li>
 * <li>TIMESTAMP_FRESHNESS_LIMIT : The period (milliseconds) for which a Timestamp is considered fresh </li>
 * <li>MAX_NONCE_AGE   : The length of time (milliseconds) a previously received Nonce value will be stored </li></ul>
 * @deprecated This callback is no longer supported by the XWS-Security runtime, use the XWS-Security configuration
 * file to set the above property values instead.
 */
public class PropertyCallback extends XWSSCallback implements Callback {

    public static final long MAX_NONCE_AGE = 900000 ;
    public static final long MAX_CLOCK_SKEW = 60000;
    public static final long TIMESTAMP_FRESHNESS_LIMIT = 300000;

    long maxSkew = MAX_CLOCK_SKEW;
    long freshnessLimit = TIMESTAMP_FRESHNESS_LIMIT;
    long maxNonceAge = MAX_NONCE_AGE;

    /**
     *@param skew the assumed maximum skew (milliseconds) between the local times of any two systems
     */
    public void setMaxClockSkew(long skew) {
        this.maxSkew = skew;
    }

    /**
     *@return the maximum clock skew
     */
    public long getMaxClockSkew() {
        return maxSkew;
    }

    /**
     *@param freshnessLimit the period (milliseconds) for which a Timestamp is considered fresh
     */
    public void setTimestampFreshnessLimit(long freshnessLimit) {
        this.freshnessLimit = freshnessLimit;
    }

    /**
     *@return the Timestamp Freshness Limit
     */
    public long getTimestampFreshnessLimit() {
        return freshnessLimit;
    }

    /**
     *@param maxNonceAge The length of time (milliseconds) a previously received Nonce value
     *will be stored
     * Implementation Note: The actual time for which any Nonce will be stored can be greater
     * than maxNonceAge. In some cases when the implementation is unable to determine a receiver
     * side policy ahead of processing the Message, the maxNonceAge value used will be a default
     * value of 30 mins.
     */
    public void setMaxNonceAge(long maxNonceAge) {
        this.maxNonceAge = maxNonceAge;
    }

    /**
     *@return the Maximum Nonce Age value
     */
    public long getMaxNonceAge() {
        return this.maxNonceAge;
    }

}
