/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

import com.sun.xml.ws.api.server.SDDocumentFilter;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicyModelMarshaller;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import junit.framework.TestCase;

/**
 * Abstract base class for filtering tests
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
abstract class AbstractFilteringTestCase extends TestCase {
    private static final PolicyModelMarshaller marshaller = PolicyModelMarshaller.getXmlMarshaller(true);    
    
    /** Creates a new instance of AbstractFilteringTestCase */
    public AbstractFilteringTestCase(String testName) {super(testName);}
    
    protected final XMLStreamWriter openFilteredWriter(Writer outputStream, InvocationProcessorFactory factory) throws XMLStreamException {
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
        return EnhancedXmlStreamWriterProxy.createProxy(writer, factory);
    }
    
    protected final XMLStreamWriter openFilteredWriter(Writer outputStream, SDDocumentFilter filter) throws XMLStreamException, IOException {
        return filter.filter(null, XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream));
    }
    
    protected final void performResourceBasedTest(String[] resourceNames, String resourcePrefix, String resourceSuffix, InvocationProcessorFactory factory) throws PolicyException, IOException, XMLStreamException {
        for (String testResourceName : resourceNames) {
            PolicySourceModel model = ResourceLoader.unmarshallModel(resourcePrefix + testResourceName + resourceSuffix);
            PolicySourceModel expected = ResourceLoader.unmarshallModel(resourcePrefix + testResourceName + "_expected" + resourceSuffix);
            
            StringWriter buffer = new StringWriter();
            XMLStreamWriter writer = openFilteredWriter(buffer, factory);
            marshaller.marshal(model, writer);
            writer.close();
            
            String marshalledData = buffer.toString();
            
            PolicySourceModel result = ResourceLoader.unmarshallModel(new StringReader(marshalledData));
            assertEquals("Result is not as expected for '" + testResourceName + "' test resource.", expected, result);
        }        
    }

    protected final void performResourceBasedTest(String[] resourceNames, String resourcePrefix, String resourceSuffix, SDDocumentFilter filter) throws PolicyException, IOException, XMLStreamException {
        for (String testResourceName : resourceNames) {
            PolicySourceModel model = ResourceLoader.unmarshallModel(resourcePrefix + testResourceName + resourceSuffix);
            PolicySourceModel expected = ResourceLoader.unmarshallModel(resourcePrefix + testResourceName + "_expected" + resourceSuffix);
            
            StringWriter buffer = new StringWriter();
            XMLStreamWriter writer = openFilteredWriter(buffer, filter);
            marshaller.marshal(model, writer);
            writer.close();
            
            String marshalledData = buffer.toString();
            
            PolicySourceModel result = ResourceLoader.unmarshallModel(new StringReader(marshalledData));
            assertEquals("Result is not as expected for '" + testResourceName + "' test resource.", expected, result);
        }        
    }
    
    protected final PolicyModelMarshaller getPolicyModelMarshaller() {
        return marshaller;
    }
    
}
