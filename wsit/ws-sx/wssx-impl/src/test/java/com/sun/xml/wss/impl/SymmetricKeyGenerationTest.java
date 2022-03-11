/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SymmetricKeyGenerationTest.java
 *
 * Created on April 7, 2006, 5:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl;

import com.sun.xml.ws.security.impl.DerivedKeyTokenImpl;
import com.sun.xml.ws.security.DerivedKeyToken;
import com.sun.xml.wss.impl.misc.SecurityUtil;

import javax.crypto.SecretKey;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class SymmetricKeyGenerationTest extends TestCase{

    /** Creates a new instance of SymmetricKeyGenerationTest */
    public SymmetricKeyGenerationTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() {

    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(EndorsingSignatureTest.class);

        return suite;
    }

    public static void testSymmetricKeyGenerationTest() throws Exception {
            String algorithm = MessageConstants.AES_BLOCK_ENCRYPTION_256;
            byte[] secret = "randombytes".getBytes();
            long offset = 0;
            long length = SecurityUtil.getLengthFromAlgorithm(algorithm);
            DerivedKeyToken dkt = new DerivedKeyTokenImpl(offset, length, secret);
            SecretKey sKey = dkt.generateSymmetricKey(algorithm);
            assertEquals(sKey.getEncoded().length, length);
    }

//   public static void main(String[] args) throws Exception{
//       testSymmetricKeyGenerationTest();
//   }
}
