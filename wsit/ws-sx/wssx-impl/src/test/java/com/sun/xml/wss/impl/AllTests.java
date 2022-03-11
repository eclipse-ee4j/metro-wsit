/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl;

import junit.framework.*;

public class AllTests extends TestCase {
    public AllTests(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
       TestSuite suite = new TestSuite("Security unit tests suite");
       suite.addTest(com.sun.xml.wss.impl.EndorsingSignatureTest.suite());
       suite.addTest(com.sun.xml.wss.impl.AsymmetricBindingTest.suite());

      //  suite.addTestSuite(SCTDKTTest.class);
      //  suite.addTestSuite(SecurityContextTokenTest.class);

        suite.addTest(com.sun.xml.wss.impl.SignAllHeadersTest.suite());
        suite.addTest(com.sun.xml.wss.impl.SignatureConfirmationTest.suite());
        suite.addTest(com.sun.xml.wss.impl.SignSOAPHeadersOnlyTest.suite());
        suite.addTest(com.sun.xml.wss.impl.SymmetricBindingTest.suite());
        suite.addTest(com.sun.xml.wss.impl.SymmetricDktTest.suite());
        suite.addTest(com.sun.xml.wss.impl.SymmetricKeyGenerationTest.suite());
        suite.addTest(com.sun.xml.wss.impl.TimestampTest.suite());
        suite.addTest(com.sun.xml.wss.impl.TrustTest.suite());
        suite.addTest(com.sun.xml.wss.impl.TrustDKTTest.suite());

        return suite;
    }

}

