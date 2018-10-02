/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.wsrm200702;

import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.DeliveryAssurance;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.SecurityBinding;
import com.sun.xml.ws.rx.testutil.ResourceLoader;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class Rm11AssertionTest extends TestCase {

    public Rm11AssertionTest(String testName) {
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
     * Test of getInstantiator method, of class Rm11Assertion.
     */
    public void testGetInstantiator() {
        assertNotNull(Rm11Assertion.getInstantiator());
    }

    /**
     * Test of getDeliveryAssurance method, of class Rm11Assertion.
     */
    public void testGetDeliveryAssurance() {
        assertEquals(DeliveryAssurance.getDefault(), ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_DEFAULT_POLICY_RESOURCE_NAME, Rm11Assertion.class).getDeliveryAssurance());
        assertEquals(DeliveryAssurance.AT_LEAST_ONCE, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_1_POLICY_RESOURCE_NAME, Rm11Assertion.class).getDeliveryAssurance());
        assertEquals(DeliveryAssurance.AT_MOST_ONCE, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_2_POLICY_RESOURCE_NAME, Rm11Assertion.class).getDeliveryAssurance());
        assertEquals(DeliveryAssurance.EXACTLY_ONCE, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_3_POLICY_RESOURCE_NAME, Rm11Assertion.class).getDeliveryAssurance());
    }

    /**
     * Test of isOrderedDelivery method, of class Rm11Assertion.
     */
    public void testIsOrderedDelivery() {
        assertEquals(false, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_DEFAULT_POLICY_RESOURCE_NAME, Rm11Assertion.class).isOrderedDelivery());
        assertEquals(true, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_1_POLICY_RESOURCE_NAME, Rm11Assertion.class).isOrderedDelivery());
        assertEquals(false, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_2_POLICY_RESOURCE_NAME, Rm11Assertion.class).isOrderedDelivery());
    }

    /**
     * Test of getSecurityBinding method, of class Rm11Assertion.
     */
    public void testGetSecurityBinding() {
        assertEquals(SecurityBinding.NONE, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_DEFAULT_POLICY_RESOURCE_NAME, Rm11Assertion.class).getSecurityBinding());
        assertEquals(SecurityBinding.TRANSPORT, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_1_POLICY_RESOURCE_NAME, Rm11Assertion.class).getSecurityBinding());
        assertEquals(SecurityBinding.STR, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_2_POLICY_RESOURCE_NAME, Rm11Assertion.class).getSecurityBinding());
    }
}
