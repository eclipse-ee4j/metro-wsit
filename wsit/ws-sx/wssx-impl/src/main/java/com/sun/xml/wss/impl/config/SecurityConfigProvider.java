/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */


package com.sun.xml.wss.impl.config;

import com.sun.xml.wss.impl.MessageConstants;

/**
 * @author suresh
 * This class sets the maxNonceAge property required for metro HA Backing store impl
 * Also see the SecurityTubeFactory static block for use of this maxNonceAge
 */
public enum SecurityConfigProvider {
    INSTANCE;

    public static final long DEFAULT_MAX_NONCE_AGE = MessageConstants.MAX_NONCE_AGE;

    private long maxNonceAge = DEFAULT_MAX_NONCE_AGE;

    public void init(final long maxNonceAge) { this.maxNonceAge = maxNonceAge; }

    public long getMaxNonceAge() { return this.maxNonceAge; }
} 
