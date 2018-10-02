/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.wsrm200502;

import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import com.sun.xml.ws.rx.testutil.ResourceLoader;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class Rm10AssertionTest extends TestCase {

    public Rm10AssertionTest(String testName) {
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

    /**
     * Test of getInstantiator method, of class Rm10Assertion.
     */
    public void testGetInstantiator() {
        assertNotNull(Rm10Assertion.getInstantiator());
    }

    /**
     * Test of getInactivityTimeout method, of class Rm10Assertion.
     */
    public void testGetInactivityTimeout() {
        final Rm10Assertion assertionFromPolicy = ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_DEFAULT_POLICY_RESOURCE_NAME, Rm10Assertion.class);
        assertEquals(ReliableMessagingFeature.DEFAULT_SEQUENCE_INACTIVITY_TIMEOUT, assertionFromPolicy.getInactivityTimeout());
        assertEquals(1000, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_CUSTOM_POLICY_RESOURCE_NAME, Rm10Assertion.class).getInactivityTimeout());
    }

    /**
     * Test of getBaseRetransmittionInterval method, of class Rm10Assertion.
     */
    public void testGetBaseRetransmittionInterval() {
        assertEquals(ReliableMessagingFeature.DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_DEFAULT_POLICY_RESOURCE_NAME, Rm10Assertion.class).getBaseRetransmittionInterval());
        assertEquals(1000, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_CUSTOM_POLICY_RESOURCE_NAME, Rm10Assertion.class).getBaseRetransmittionInterval());
    }

    /**
     * Test of useExponentialBackoffAlgorithm method, of class Rm10Assertion.
     */
    public void testUseExponentialBackoffAlgorithm() {
        assertEquals(false, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_DEFAULT_POLICY_RESOURCE_NAME, Rm10Assertion.class).useExponentialBackoffAlgorithm());
        assertEquals(true, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_CUSTOM_POLICY_RESOURCE_NAME, Rm10Assertion.class).useExponentialBackoffAlgorithm());
    }
}
