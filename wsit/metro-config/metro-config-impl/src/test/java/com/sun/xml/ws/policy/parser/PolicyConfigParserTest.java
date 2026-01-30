/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.policy.parser;

import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyConstants;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.policy.testutils.PolicyResourceLoader;

import jakarta.xml.ws.WebServiceException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import static com.sun.xml.ws.policy.testutils.PolicyResourceLoader.getResourceUrl;
import static com.sun.xml.ws.policy.testutils.PolicyResourceLoader.loadPolicy;

/**
 * @author Fabian Ritzmann
 */
public class PolicyConfigParserTest extends TestCase {
    private static final Path SRC_DIR = new File(System.getProperty("srcDir")).toPath();
    private static final Path MANIFEST_DIR = new File(System.getProperty("manifestDir")).toPath();

    private static final Path TEST_FILE_PATH = SRC_DIR.resolve(Path.of("policy", "config", "wsit.xml"));
    private static final String CLIENT_CONFIG_FILE_NAME = "wsit-client.xml";

    public PolicyConfigParserTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public void testParseContainerNullWithoutConfig() {
        try {
            PolicyMap result = PolicyConfigParser.parse(null, null);
            fail("Expected PolicyException, got result = " + result);
        } catch (PolicyException e) {
            // Expected exception
        }
    }

    public void testParseContainerWithoutContextWithoutConfig() {
        try {
            Container container = new MockContainer(null);
            PolicyMap result = PolicyConfigParser.parse(null, container);
            fail("Expected PolicyException, got result = " + result);
        } catch (PolicyException e) {
            // Expected exception
        }
    }

      // Throws exception, "{http://schemas.sun.com/2006/03/wss/server}KeyStore" is not available
//    public void testParseContainerNullWithConfig() throws Exception {
//        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, MANIFEST_DIR.resolve(CONFIG_FILE_NAME), "test", null);
//        testLoadedMap(map);
//    }
//
//    public void testParseContainerWithoutContext() throws Exception {
//        Container container = new MockContainer(null);
//        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, MANIFEST_DIR.resolve(CONFIG_FILE_NAME), "test", container);
//        testLoadedMap(map);
//    }

    public void testParseContainerWithContext() {
        // TODO Need MockServletContext
    }

    public void testWsitXmlNotLoadedContainerNullWithConfig() throws Exception {
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, MANIFEST_DIR.resolve("wsit.xml"), "test", null);
        assertNull(map);
    }

    public void testWsitXmlNotLoadedContainerWithoutContext() throws Exception {
        Container container = new MockContainer(null);
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, MANIFEST_DIR.resolve("wsit.xml"), "test", container);
        assertNull(map);
    }

    public void testWsitXmlNotLoadedContainerWithContext() {
        // TODO Need MockServletContext
    }

    // Throws exception, tries to load file which we did not prepare.
//    public void testParseClientWithoutContextWithoutConfig() throws Exception {
//        PolicyMap result = PolicyConfigParser.parse(PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, null);
//        assertNull(result);
//    }

    public void testParseClientMetainfContainerNullWithConfig() throws Exception {
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, MANIFEST_DIR.resolve(CLIENT_CONFIG_FILE_NAME), PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, null);
        testLoadedMap(map);
    }

    public void testParseClientMetainfWithoutContext() throws Exception {
        Container container = new MockContainer(null);
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, MANIFEST_DIR.resolve(CLIENT_CONFIG_FILE_NAME), PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, container);
        testLoadedMap(map);
    }

    public void testParseClientClasspathContainerNullWithConfig() throws Exception {
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, SRC_DIR.resolve(CLIENT_CONFIG_FILE_NAME), PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, null);
        testLoadedMap(map);
    }

    public void testParseClientClasspathWithoutContext() throws Exception {
        Container container = new MockContainer(null);
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, SRC_DIR.resolve(CLIENT_CONFIG_FILE_NAME), PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, container);
        testLoadedMap(map);
    }

    public void testParseURLNull() throws Exception {
        PolicyMap result = null;

        try {
            result = PolicyConfigParser.parse(null, false);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        assertNull(result);
    }

    public void testParseBufferMex() throws Exception {
        PolicyMap map = parseConfigFile("mex/mex.xml");
        PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(new QName("http://schemas.xmlsoap.org/ws/2004/09/mex", "MetadataExchangeService"), new QName("http://schemas.xmlsoap.org/ws/2004/09/mex", "MetadataExchangePort"));
        Policy policy = map.getEndpointEffectivePolicy(key);
        assertNotNull(policy);
        assertEquals("MEXPolicy", policy.getId());
    }


    public void testParseBufferSimple() throws Exception {
        PolicyMap map = parseConfigFile("config/simple.wsdl");
        PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.org/", "AddNumbersService"), new QName("http://example.org/", "AddNumbersPort"));
        Policy policy = map.getEndpointEffectivePolicy(key);
        assertNotNull(policy);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy.getId());
    }

    public void testParseBufferSingleImport() throws Exception {
        PolicyMap map = parseConfigFile("config/single-import.wsdl");
        assertNotNull(map);

        PolicyMapKey key1 = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.org/", "AddNumbersService"),
                new QName("http://example.org/", "AddNumbersPort"));
        Policy policy1 = map.getEndpointEffectivePolicy(key1);
        assertNotNull(policy1);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy1.getId());

        PolicyMapKey key2 = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.net/", "AddNumbersService"),
                new QName("http://example.net/", "AddNumbersPort"));
        Policy policy2 = map.getEndpointEffectivePolicy(key2);
        assertNotNull(policy2);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy2.getId());
    }

    public void testParseBufferMultiImport() throws Exception {
        PolicyMap map = parseConfigFile("config/import.wsdl");

        assertNotNull(map);

        PolicyMapKey key1 = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.org/", "AddNumbersService"),
                new QName("http://example.org/", "AddNumbersPort"));
        Policy policy1 = map.getEndpointEffectivePolicy(key1);
        assertNotNull(policy1);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy1.getId());

        PolicyMapKey key2 = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.net/", "AddNumbersService"),
                new QName("http://example.net/", "AddNumbersPort"));
        Policy policy2 = map.getEndpointEffectivePolicy(key2);
        assertNotNull(policy2);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy2.getId());

        PolicyMapKey key3 = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.com/", "AddNumbersService"),
                new QName("http://example.com/", "AddNumbersPort"));
        Policy policy3 = map.getEndpointEffectivePolicy(key3);
        assertNotNull(policy3);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy3.getId());

        PolicyMapKey key4 = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.com/import3/", "AddNumbersService"),
                new QName("http://example.com/import3/", "AddNumbersPort"));
        Policy policy4 = map.getEndpointEffectivePolicy(key4);
        assertNotNull(policy4);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy4.getId());
    }

    public void testParseBufferCyclicImport() throws Exception {
        PolicyMap map = parseConfigFile("config/cyclic.wsdl");
        PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.org/", "AddNumbersService"), new QName("http://example.org/", "AddNumbersPort"));
        Policy policy = map.getEndpointEffectivePolicy(key);
        assertNotNull(policy);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy.getId());
    }

    public void testParseBufferExternalReference() throws Exception {
        try {
            parseConfigFile("config/service.wsdl");
            fail("Expected a WebServiceException");
        } catch (WebServiceException wse) {
        }
    }

    public void testParseBufferExternalReferenceName() throws Exception {
        PolicyMap map = parseConfigFile("config/service-name.wsdl");
        PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.org/AddNumbers/service", "AddNumbersService"), new QName("http://example.org/AddNumbers/service", "AddNumbersPort"));
        Policy policy = map.getEndpointEffectivePolicy(key);
        assertNotNull(policy);
        assertEquals("http://example.org/AddNumbers/porttype#AddNumbersServicePolicy", policy.getName());
    }

    public void testGetOperationEffectivePolicy() throws Exception {
        PolicyMap policyMap = PolicyConfigParser.parse(getResourceUrl("effective/all.wsdl"), true);
        Policy expectedPolicy1 = loadPolicy("effective/resultOperation.xml");
        Policy expectedPolicy2 = loadPolicy("effective/result2Operation.xml");
        PolicyMapKey policyMapKey = PolicyMap.createWsdlOperationScopeKey(new QName("http://example.org/","Service"),new QName("http://example.org/","Port"),new QName("http://example.org/","Operation"));
        Policy policy = policyMap.getOperationEffectivePolicy(policyMapKey);
        boolean policyEquals1 = expectedPolicy1.equals(policy);
        boolean policyEquals2 = expectedPolicy2.equals(policy);
        // One of the policy alternatives is randomly selected. That means one of the
        // expected policies must match but not both of them may match.
        if (policyEquals1 && policyEquals2) {
            fail("Both policies are matching. Expected only one to match. Computed policy = "
                 + policy + "\nexpected policy 1 = " + expectedPolicy1 + "\n expected policy 2 = " + expectedPolicy2);
        }
        if (!policyEquals1 && !policyEquals2) {
            fail("None of the expected policies matched. Computed policy = " + policy
                 + "\nexpected policy 1 = " + expectedPolicy1 + "\n expected policy 2 = " + expectedPolicy2);
        }
    }

    public void testGetInputMessageEffectivePolicy() throws Exception {
        PolicyMap policyMap = PolicyConfigParser.parse(getResourceUrl("effective/all.wsdl"), true);
        Policy expectedPolicy1 = loadPolicy("effective/resultInput.xml");
        Policy expectedPolicy2 = loadPolicy("effective/result2Input.xml");
        PolicyMapKey policyMapKey = PolicyMap.createWsdlMessageScopeKey(new QName("http://example.org/","Service"),new QName("http://example.org/","Port"),new QName("http://example.org/","Operation"));
        Policy policy = policyMap.getInputMessageEffectivePolicy(policyMapKey);
        boolean policyEquals1 = expectedPolicy1.equals(policy);
        boolean policyEquals2 = expectedPolicy2.equals(policy);
        // One of the policy alternatives is randomly selected. That means one of the
        // expected policies must match but not both of them may match.
        if (policyEquals1 && policyEquals2) {
            fail("Both policies are matching. Expected only one to match. Computed policy = "
                 + policy + "\nexpected policy 1 = " + expectedPolicy1 + "\n expected policy 2 = " + expectedPolicy2);
        }
        if (!policyEquals1 && !policyEquals2) {
            fail("None of the expected policies matched. Computed policy = " + policy
                 + "\nexpected policy 1 = " + expectedPolicy1 + "\n expected policy 2 = " + expectedPolicy2);
        }
    }

    public void testGetFaultMessageEffectivePolicy() throws Exception {
        PolicyMap policyMap = PolicyConfigParser.parse(getResourceUrl("effective/all.wsdl"), true);
        Policy expectedPolicy1 = loadPolicy("effective/resultFault.xml");
        Policy expectedPolicy2 = loadPolicy("effective/result2Fault.xml");
        PolicyMapKey policyMapKey = PolicyMap.createWsdlFaultMessageScopeKey(new QName("http://example.org/","Service"),new QName("http://example.org/","Port"),new QName("http://example.org/","Operation"),new QName("http://example.org/","Fault"));
        Policy policy = policyMap.getFaultMessageEffectivePolicy(policyMapKey);
        boolean policyEquals1 = expectedPolicy1.equals(policy);
        boolean policyEquals2 = expectedPolicy2.equals(policy);
        // One of the policy alternatives is randomly selected. That means one of the
        // expected policies must match but not both of them may match.
        if (policyEquals1 && policyEquals2) {
            fail("Both policies are matching. Expected only one to match. Computed policy = "
                 + policy + "\nexpected policy 1 = " + expectedPolicy1 + "\n expected policy 2 = " + expectedPolicy2);
        }
        if (!policyEquals1 && !policyEquals2) {
            fail("None of the expected policies matched. Computed policy = " + policy
                 + "\nexpected policy 1 = " + expectedPolicy1 + "\n expected policy 2 = " + expectedPolicy2);
        }
    }

    public void testGetFaultMessageWithTwoServicesEffectivePolicy() throws Exception {
        PolicyMap policyMap = PolicyConfigParser.parse(getResourceUrl("effective/twoservices.wsdl"), true);
        Policy expectedPolicy1 = loadPolicy("effective/resultFault.xml");
        Policy expectedPolicy2 = loadPolicy("effective/result2Fault.xml");
        PolicyMapKey policyMapKey = PolicyMap.createWsdlFaultMessageScopeKey(new QName("http://example.org/","Service"),new QName("http://example.org/","Port"),new QName("http://example.org/","Operation"),new QName("http://example.org/","Fault"));
        Policy policy = policyMap.getFaultMessageEffectivePolicy(policyMapKey);
        boolean policyEquals1 = expectedPolicy1.equals(policy);
        boolean policyEquals2 = expectedPolicy2.equals(policy);
        // One of the policy alternatives is randomly selected. That means one of the
        // expected policies must match but not both of them may match.
        if (policyEquals1 && policyEquals2) {
            fail("Both policies are matching. Expected only one to match. Computed policy = "
                 + policy + "\nexpected policy 1 = " + expectedPolicy1 + "\n expected policy 2 = " + expectedPolicy2);
        }
        if (!policyEquals1 && !policyEquals2) {
            fail("None of the expected policies matched. Computed policy = " + policy
                 + "\nexpected policy 1 = " + expectedPolicy1 + "\n expected policy 2 = " + expectedPolicy2);
        }
    }

    private PolicyMap parseConfigFile(String configFile) throws Exception {
        URL url = PolicyUtils.ConfigFile.loadFromClasspath(PolicyResourceLoader.POLICY_UNIT_TEST_RESOURCE_ROOT + configFile);
        return PolicyConfigParser.parse(url, true);
    }

    private PolicyMap prepareTestFileAndLoadPolicyMap(Path srcFile, Path target, String cfgFileId, Container container) throws PolicyException, IOException {
        try {
            if (!Files.isDirectory(target.getParent())) {
                Files.createDirectories(target.getParent());
            }
            Files.copy(srcFile, target);
            return PolicyConfigParser.parse(cfgFileId, container);
        } finally {
            Files.deleteIfExists(target);
        }
    }

    private void testLoadedMap(PolicyMap map) throws PolicyException {
        PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.org/", "AddNumbersService"), new QName("http://example.org/", "AddNumbersPort"));
        Policy policy = map.getEndpointEffectivePolicy(key);
        assertNotNull(policy);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy.getId());
    }

    static class MockContainer extends Container {
        private final Object spi;

        public MockContainer(Object spi) {
            this.spi = spi;
        }

        @Override
        public <T> T getSPI(Class<T> spiType) {
            if (spiType.isInstance(this.spi)) {
                return spiType.cast(this.spi);
            } else {
                return null;
            }
        }

    }
}
