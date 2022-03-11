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
 * PrivateElementFilteringInvocationProcessorTest.java
 * JUnit based test
 *
 * Created on November 10, 2006, 2:51 PM
 */

package com.sun.xml.ws.xmlfilter;

import java.io.StringWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class PrivateElementFilteringInvocationProcessorTest  extends AbstractFilteringTestCase {
    private static final String[] testResources = new String[] {
        "element_01",
        "element_02"
    };

    private static final InvocationProcessorFactory factory = new InvocationProcessorFactory() {
        @Override
        public InvocationProcessor createInvocationProcessor(XMLStreamWriter writer) throws XMLStreamException {
            return new FilteringInvocationProcessor(writer, new PrivateElementFilteringStateMachine(
                    new QName("http://schemas.sun.com/2006/03/wss/server", "KeyStore"),
                    new QName("http://schemas.sun.com/2006/03/wss/server", "TrustStore"),
                    new QName("http://schemas.sun.com/2006/03/wss/server", "CallbackHandlerConfiguration"),
                    new QName("http://schemas.sun.com/2006/03/wss/server", "ValidatorConfiguration"),
                    new QName("http://schemas.sun.com/2006/03/wss/server", "DisablePayloadBuffering"),

                    new QName("http://schemas.sun.com/2006/03/wss/client", "KeyStore"),
                    new QName("http://schemas.sun.com/2006/03/wss/client", "TrustStore"),
                    new QName("http://schemas.sun.com/2006/03/wss/client", "CallbackHandlerConfiguration"),
                    new QName("http://schemas.sun.com/2006/03/wss/client", "ValidatorConfiguration"),
                    new QName("http://schemas.sun.com/2006/03/wss/client", "DisablePayloadBuffering"),

                    new QName("http://schemas.sun.com/ws/2006/05/sc/server", "SCConfiguration"),

                    new QName("http://schemas.sun.com/ws/2006/05/sc/client", "SCClientConfiguration"),

                    new QName("http://schemas.sun.com/ws/2006/05/trust/server", "STSConfiguration"),

                    new QName("http://schemas.sun.com/ws/2006/05/trust/client", "PreconfiguredSTS")));
        }
    };

    public PrivateElementFilteringInvocationProcessorTest(String testName) {
        super(testName);
    }

    /**
     * Test of createProxy method, of class com.sun.xml.ws.policy.jaxws.documentfilter.FilteringXmlStreamWriterProxy.
     */
    public void testCreateProxy() throws Exception {
        XMLStreamWriter result = openFilteredWriter(new StringWriter(), factory);

        assertNotNull(result);
    }

    public void testFilterPrivateAssertionsFromPolicyExpression() throws Exception {
        performResourceBasedTest(testResources, "element_filtering/", ".xml", factory);
    }
}
