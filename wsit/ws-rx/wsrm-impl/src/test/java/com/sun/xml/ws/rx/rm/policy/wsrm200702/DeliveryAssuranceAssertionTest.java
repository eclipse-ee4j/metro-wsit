/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.policy.wsrm200702;

import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class DeliveryAssuranceAssertionTest extends TestCase {

    public DeliveryAssuranceAssertionTest(String testName) {
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
     * Test of getInstantiator method, of class DeliveryAssuranceAssertion.
     */
    public void testGetInstantiator() {
        assertNotNull(DeliveryAssuranceAssertion.getInstantiator());
    }

    /*
      Other methods are tested as part of Rm11Assertion unit tests
     */
}
