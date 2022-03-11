/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.config.metro.parser;

import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

/**
 *
 * @author Fabian Ritzmann
 */
public class MetroWsParserTest extends TestCase {

    public MetroWsParserTest(String testName) {
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

    public void testNew() {
        final MetroWsParser result = new MetroWsParser();
        assertNotNull(result);
    }

    /**
     * Test of unmarshal method, of class MetroWsParser.
     */
    public void testUnmarshal_Reader() throws Exception {
        final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        final XMLStreamReader streamReader = inputFactory.createXMLStreamReader(getClass().getClassLoader().getResourceAsStream("config/metro-webservices.xml"));
        final MetroWsParser instance = new MetroWsParser();
        final List<ParsedElement> result = instance.unmarshal(streamReader);
        assertNotNull(result);
        assertEquals(6, result.size());
    }

}
