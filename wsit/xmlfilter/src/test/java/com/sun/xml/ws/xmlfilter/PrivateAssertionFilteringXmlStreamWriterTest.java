/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * FilteringXmlStreamWriterProxyTest.java
 * JUnit based test
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */

package com.sun.xml.ws.xmlfilter;

import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class PrivateAssertionFilteringXmlStreamWriterTest extends AbstractFilteringTestCase {
    private static final String[] testResources = new String[] {
        "policy_0_visible",
    };

    private static final InvocationProcessorFactory factory = new InvocationProcessorFactory() {
        @Override
        public InvocationProcessor createInvocationProcessor(XMLStreamWriter writer) throws XMLStreamException {
            return new FilteringInvocationProcessor(writer, new PrivateAttributeFilteringStateMachine());
        }
    };
    
    public PrivateAssertionFilteringXmlStreamWriterTest(String testName) {
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
        performResourceBasedTest(testResources, "visibility/", ".xml", factory);
    }
}
