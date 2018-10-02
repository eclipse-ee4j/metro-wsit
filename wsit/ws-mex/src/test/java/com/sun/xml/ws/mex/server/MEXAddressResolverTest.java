/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex.server;

import javax.xml.namespace.QName;
import junit.framework.TestCase;

/**
 *
 * @author Fabian Ritzmann
 */
public class MEXAddressResolverTest extends TestCase {
    
    public MEXAddressResolverTest(String testName) {
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
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForNull() {
        QName serviceName = null;
        QName portName = null;
        String address = null;
        try {
            MEXAddressResolver instance = new MEXAddressResolver(serviceName, portName, address);
            fail("Expected a NullPointerException");
        } catch (NullPointerException e) {
            // This exception is expected
        }
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForNullAddress() {
        QName serviceName = new QName("namespace", "service");
        QName portName = new QName("namespace", "port");
        String address = null;
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, portName, address);
        String result = instance.getAddressFor(serviceName, portName.getLocalPart());
        assertNull(result);
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForAddress() {
        QName serviceName = new QName("namespace", "service");
        QName portName = new QName("namespace", "port");
        String address = "http://myaddress/";
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, portName, address);
        String expResult = address;
        String result = instance.getAddressFor(serviceName, portName.getLocalPart());
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForNullAddressNull() {
        QName serviceName = new QName("namespace", "service");
        QName portName = new QName("namespace", "port");
        String address = null;
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, portName, address);
        String result = instance.getAddressFor(serviceName, portName.getLocalPart(), null);
        assertNull(result);
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForNoUrlAddressNull() {
        QName serviceName = new QName("namespace", "service");
        QName portName = new QName("namespace", "port");
        String address = "myaddress";
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, portName, address);
        String expResult = address;
        String result = instance.getAddressFor(serviceName, portName.getLocalPart(), null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForUrlAddressNull() {
        QName serviceName = new QName("namespace", "service");
        QName portName = new QName("namespace", "port");
        String address = "http://myaddress/";
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, portName, address);
        String expResult = address;
        String result = instance.getAddressFor(serviceName, portName.getLocalPart(), null);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForUrlNameNotEqual() {
        QName serviceName = new QName("namespace", "service");
        QName port1 = new QName("namespace", "port1");
        String port2 = "port2";
        String address1 = "http://myaddress/";
        String address2 = "http://myaddress2/";
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, port1, address1);
        String result = instance.getAddressFor(serviceName, port2, address2);
        assertNull(result);
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForUrlSameAddress() {
        QName serviceName = new QName("namespace", "service");
        QName port = new QName("namespace", "port");
        String address = "http://myaddress/";
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, port, address);
        String expResult = address;
        String result = instance.getAddressFor(serviceName, port.getLocalPart(), address);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForUrlHttpsToHttp() {
        QName serviceName = new QName("namespace", "service");
        QName port = new QName("namespace", "port");
        String address1 = "https://myaddress/";
        String address2 = "http://myaddress/";
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, port, address1);
        String expResult = address2;
        String result = instance.getAddressFor(serviceName, port.getLocalPart(), address2);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAddressFor method, of class MEXAddressResolver.
     */
    public void testGetAddressForUrlFtpToHttp() {
        QName serviceName = new QName("namespace", "service");
        QName port = new QName("namespace", "port");
        String address1 = "ftp://myaddress/";
        String address2 = "http://myaddress/";
        MEXAddressResolver instance = new MEXAddressResolver(serviceName, port, address1);
        String expResult = address1;
        String result = instance.getAddressFor(serviceName, port.getLocalPart(), address2);
        assertEquals(expResult, result);
    }

}
