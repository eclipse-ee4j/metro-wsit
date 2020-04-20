/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
import static com.sun.xml.ws.policy.testutils.PolicyResourceLoader.getResourceUrl;
import static com.sun.xml.ws.policy.testutils.PolicyResourceLoader.loadPolicy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceException;
import junit.framework.TestCase;

/**
 * @author Fabian Ritzmann
 */
public class PolicyConfigParserTest extends TestCase {
    private static final String TEST_FILE_PATH = "test/unit/data/policy/config/wsit.xml";
    private static final String CONFIG_FILE_PATH = "test/unit/data/META-INF";
    private static final String CLASSPATH_CONFIG_FILE_PATH = "test/unit/data";
    private static final String CONFIG_FILE_NAME = "wsit-test.xml";
    private static final String CLIENT_CONFIG_FILE_NAME = "wsit-client.xml";
    
    public PolicyConfigParserTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
    }
    
    @Override
    protected void tearDown() throws Exception {
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
    
    public void testParseContainerNullWithConfig() throws Exception {        
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, CONFIG_FILE_PATH, CONFIG_FILE_NAME, "test", null);
        testLoadedMap(map);
    }

    public void testParseContainerWithoutContext() throws Exception {
        Container container = new MockContainer(null);
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, CONFIG_FILE_PATH, CONFIG_FILE_NAME, "test", container);
        testLoadedMap(map);
    }
    
    public void testParseContainerWithContext() throws Exception {
        // TODO Need MockServletContext
    }
    
    public void testWsitXmlNotLoadedContainerNullWithConfig() throws Exception {        
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, CONFIG_FILE_PATH, "wsit.xml", "test", null);
        assertNull(map);
    }

    public void testWsitXmlNotLoadedContainerWithoutContext() throws Exception {
        Container container = new MockContainer(null);
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, CONFIG_FILE_PATH, "wsit.xml", "test", container);
        assertNull(map);
    }
    
    public void testWsitXmlNotLoadedContainerWithContext() throws Exception {
        // TODO Need MockServletContext
    }
    
    public void testParseClientWithoutContextWithoutConfig() throws Exception {
        PolicyMap result = PolicyConfigParser.parse(PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, null);
        assertNull(result);
    }
    
    public void testParseClientMetainfContainerNullWithConfig() throws Exception {        
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, CONFIG_FILE_PATH, CLIENT_CONFIG_FILE_NAME, PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, null);
        testLoadedMap(map);
    }
    
    public void testParseClientMetainfWithoutContext() throws Exception {
        Container container = new MockContainer(null);
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, CONFIG_FILE_PATH, CLIENT_CONFIG_FILE_NAME, PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, container);
        testLoadedMap(map);
    }
    
    public void testParseClientClasspathContainerNullWithConfig() throws Exception {        
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, CLASSPATH_CONFIG_FILE_PATH, CLIENT_CONFIG_FILE_NAME, PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, null);
        testLoadedMap(map);
    }
    
    public void testParseClientClasspathWithoutContext() throws Exception {
        Container container = new MockContainer(null);
        PolicyMap map = prepareTestFileAndLoadPolicyMap(TEST_FILE_PATH, CLASSPATH_CONFIG_FILE_PATH, CLIENT_CONFIG_FILE_NAME, PolicyConstants.CLIENT_CONFIGURATION_IDENTIFIER, container);
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

    /**
     * Copy a file
     *
     * @param sourceName source file name
     * @param destPath destination path
     * @param destName destination file name
     * @throws IOException Thrown if copy failed
     */
    private static void copyFile(String sourceName, String destPath, String destName) throws IOException {
        FileChannel source = null;
        FileChannel dest = null;
        try {
            File destDir = new File(destPath);
            destDir.mkdir();
            
            // Create channel on the source
            source = new FileInputStream(sourceName).getChannel();
            
            // Create channel on the destination
            dest = new FileOutputStream(destPath + File.separatorChar + destName).getChannel();
            
            // Copy file contents from source to destination
            dest.transferFrom(source, 0, source.size());
            
        } finally {
            // Close the channels
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                }
            }
            if (dest != null) {
                dest.close();
            }
        }
    }

    private PolicyMap prepareTestFileAndLoadPolicyMap(String sourceName, String destPath, String destName, String cfgFileId, Container container) throws PolicyException, IOException {
        PolicyMap result;
        try {
            copyFile(sourceName, destPath, destName);
            result = PolicyConfigParser.parse(cfgFileId, container);
            return result;
        } finally {
            File wsitxml = new File(destPath + File.separatorChar + destName);
            wsitxml.delete();
        }
    }
    
    private void testLoadedMap(PolicyMap map) throws PolicyException {
        PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey(new QName("http://example.org/", "AddNumbersService"), new QName("http://example.org/", "AddNumbersPort"));
        Policy policy = map.getEndpointEffectivePolicy(key);
        assertNotNull(policy);
        assertEquals("MutualCertificate10Sign_IPingService_policy", policy.getId());        
    }
    
    class MockContainer extends Container {
        private final Object spi;
        
        public MockContainer(Object spi) {
            this.spi = spi;
        }
        
        public <T> T getSPI(Class<T> spiType) {
            if (spiType.isInstance(this.spi)) {
                return spiType.cast(this.spi);
            } else {
                return null;
            }
        }
        
    }
}
