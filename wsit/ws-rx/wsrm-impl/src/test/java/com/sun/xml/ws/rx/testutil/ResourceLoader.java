/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.testutil;

import com.sun.xml.ws.api.policy.ModelTranslator;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class ResourceLoader {
    public static final String RM_1_0_DEFAULT_POLICY_RESOURCE_NAME = "rm/policy/rm-1_0-policy-default.xml";
    public static final String RM_1_0_CUSTOM_POLICY_RESOURCE_NAME = "rm/policy/rm-1_0-policy-custom.xml";

    public static final String RM_1_1_DEFAULT_POLICY_RESOURCE_NAME = "rm/policy/rm-1_1-policy-default.xml";
    public static final String RM_1_1_CUSTOM_1_POLICY_RESOURCE_NAME = "rm/policy/rm-1_1-policy-custom_1.xml";
    public static final String RM_1_1_CUSTOM_2_POLICY_RESOURCE_NAME = "rm/policy/rm-1_1-policy-custom_2.xml";
    public static final String RM_1_1_CUSTOM_3_POLICY_RESOURCE_NAME = "rm/policy/rm-1_1-policy-custom_3.xml";

    private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    private ResourceLoader() {
    }

    public static URL getResourceUrl(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(resourceName);
    }

    public static InputStream getResourceAsStream(String resourceName) throws PolicyException {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        if (input == null) {
            throw new PolicyException("Failed to find resource \"" + resourceName + "\"");
        }
        return input;
    }

    public static Reader getResourceAsReader(String resourceName) throws PolicyException {
        return new InputStreamReader(getResourceAsStream(resourceName));
    }

    public static XMLStreamBuffer getResourceAsXmlBuffer(String resourceName)
            throws PolicyException {
        try {
            return XMLStreamBuffer.createNewBufferFromXMLStreamReader(inputFactory.createXMLStreamReader(getResourceAsStream(resourceName)));
        } catch (XMLStreamException ex) {
            throw new PolicyException("Failed to create XMLStreamBuffer", ex);
        }
    }

    public static Policy loadPolicy(String resourceName) throws PolicyException, IOException {
        return translateModel(unmarshallModel(resourceName));
    }

    private static PolicySourceModel unmarshallModel(String resource) throws PolicyException, IOException {
        Reader resourceReader = getResourceAsReader(resource);
        PolicySourceModel model = ModelUnmarshaller.getUnmarshaller().unmarshalModel(resourceReader);
        resourceReader.close();
        return model;
    }

    private static Policy translateModel(PolicySourceModel model) throws PolicyException {
        return ModelTranslator.getTranslator().translate(model);
    }

    public static <T extends PolicyAssertion> T getAssertionFromPolicy(String resourceName, Class<T> assertionClass) {
        try {
            Policy policy = loadPolicy(resourceName);
            if (policy.getNumberOfAssertionSets() != 1) {
                throw new IllegalStateException(String.format("Policy in '%s' does not contain a single alternative. Number of alternatives is %d", resourceName, policy.getNumberOfAssertionSets()));
            }

            QName assertionName = (QName) assertionClass.getField("NAME").get(null);
            if (!policy.contains(assertionName)) {
                return null;
            }
            
            return assertionClass.cast(policy.iterator().next().get(assertionName).iterator().next());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (PolicyException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
