/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.config.metro.parser;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import junit.framework.TestCase;

/**
 *
 * @author Fabian Ritzmann
 */
public class WsParserTest extends TestCase {
    
    public WsParserTest(String testName) {
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
     * Test of parse method, of class WsParser.
     */
    public void testParse() throws XMLStreamException {
        final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = inputFactory.createXMLStreamReader(getClass().getClassLoader().getResourceAsStream("config/webservices.xml"));
        final WsParser instance = new WsParser();
        final Object expResult = null;
        final Object result = instance.parse(reader);
//        assertEquals(expResult, result);
    }

}
