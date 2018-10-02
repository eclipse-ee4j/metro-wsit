/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.metro200603;

import com.sun.xml.ws.rx.testutil.ResourceLoader;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class OrderedDeliveryAssertionTest extends TestCase {
    
    public OrderedDeliveryAssertionTest(String testName) {
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
     * Test of getInstantiator method, of class OrderedDeliveryAssertion.
     */
    public void testGetInstantiator() {
        assertNotNull(OrderedDeliveryAssertion.getInstantiator());
    }

    /**
     * Test presence of OrderedDelivery assertion in the policy.
     */
    public void testAssertionPresenceInPolicy() {
        assertNull(ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_DEFAULT_POLICY_RESOURCE_NAME, OrderedDeliveryAssertion.class));
        assertNotNull(ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_CUSTOM_POLICY_RESOURCE_NAME, OrderedDeliveryAssertion.class));
    }
}
