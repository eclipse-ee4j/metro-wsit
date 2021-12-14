/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.xmlfilter;

import com.sun.xml.ws.api.policy.ModelTranslator;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

/**
 * This class provides utility methods to load resources and unmarshall policy source model.
 *
 * @author Marek Potociar
 * @author Fabian Ritzmann
 */
final class ResourceLoader {

    public static final String POLICY_UNIT_TEST_RESOURCE_ROOT = "xmlfilter/";

    private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    
    private ResourceLoader() {
    }
    
    public static PolicySourceModel unmarshallModel(String resource) throws PolicyException, IOException {
        Reader resourceReader = getResourceReader(resource);
        PolicySourceModel model = ModelUnmarshaller.getUnmarshaller().unmarshalModel(resourceReader);
        resourceReader.close();
        return model;
    }
    
    public static PolicySourceModel unmarshallModel(Reader resourceReader) throws PolicyException, IOException {
        PolicySourceModel model = ModelUnmarshaller.getUnmarshaller().unmarshalModel(resourceReader);
        resourceReader.close();
        return model;
    }
    
    public static InputStream getResourceStream(String resourceName) throws PolicyException {
        String fullName = POLICY_UNIT_TEST_RESOURCE_ROOT + resourceName;
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullName);
        if (input == null) {
            throw new PolicyException("Failed to find resource \"" + fullName + "\"");
        }
        return input;
    }
    
    public static Reader getResourceReader(String resourceName) throws PolicyException {
        return new InputStreamReader(getResourceStream(resourceName));
    }
    
    public static XMLStreamBuffer getResourceXmlBuffer(String resourceName)
        throws PolicyException {
        try {
            return XMLStreamBuffer.createNewBufferFromXMLStreamReader(inputFactory.createXMLStreamReader(getResourceStream(resourceName)));
        } catch (XMLStreamException ex) {
            throw new PolicyException("Failed to create XMLStreamBuffer", ex);
        }
    }
    
    public static URL getResourceUrl(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(POLICY_UNIT_TEST_RESOURCE_ROOT + resourceName);
    }
    
    public static Policy translateModel(PolicySourceModel model) throws PolicyException {
        return ModelTranslator.getTranslator().translate(model);
    }
    
    public static Policy loadPolicy(String resourceName) throws PolicyException, IOException {
        return translateModel(unmarshallModel(resourceName));
    }

   
    // reads policy map from given wsdl document
    public static PolicyMap getPolicyMap(String resourceName)
        throws PolicyException {
        
        WSDLModel model = getWSDLModel(resourceName, true);
        return model.getPolicyMap();
    }
    
    public static PolicyMap getPolicyMap(String resourceName, boolean isClient)
        throws PolicyException {
        
        WSDLModel model = getWSDLModel(resourceName, isClient);
        return model.getPolicyMap();
    }

    public static WSDLModel getWSDLModel(String resourceName) throws PolicyException {
        return getWSDLModel(resourceName, true);        
    }
    
    // reads wsdl model from given wsdl document
    public static WSDLModel getWSDLModel(String resourceName, boolean isClient) throws PolicyException {        
        URL resourceUrl = getResourceUrl(resourceName);
        try {
            return com.sun.xml.ws.policy.parser.PolicyResourceLoader.getWsdlModel(resourceUrl, isClient);
        } catch (XMLStreamException | SAXException | IOException ex) {
            throw new PolicyException("Failed to parse document", ex);
        }
    }
 
}
