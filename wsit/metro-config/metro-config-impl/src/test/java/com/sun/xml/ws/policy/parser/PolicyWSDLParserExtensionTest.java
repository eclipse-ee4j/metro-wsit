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

import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.testutils.PolicyResourceLoader;

import jakarta.xml.ws.WebServiceException;

import java.net.URL;
import java.util.Collection;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import static com.sun.xml.ws.policy.testutils.PolicyResourceLoader.getPolicyMap;

/**
 *
 * @author Jakub Podlesak (jakub.podlesak at sun.com)
 * @author Fabian Ritzmann
 */
public class PolicyWSDLParserExtensionTest extends TestCase{

    public PolicyWSDLParserExtensionTest(String testName) {
        super(testName);
    }

    public void testClientParsingWithDifferentlyCreatedSDDocumentSource() throws Exception {
        final URL configFileUrl = PolicyResourceLoader.getResourceUrl("parser/wsit-client.xml");
        WSDLModel model = com.sun.xml.ws.policy.parser.PolicyResourceLoader.getWsdlModel(configFileUrl, true);
        assertNotNull(model);
    }

    public void testWsdlParserBasics() throws Exception {
        assertNotNull("PolicyMap can not be null", getPolicyMap("parser/testWsdlParserBasics.wsdl"));
    }

    public void testPolicyReferences() throws Exception {
        PolicyMap map = getPolicyMap("parser/testPolicyReferences.wsdl");
        assertNotNull("PolicyMap can not be null", map);

        map = PolicyConfigParser.parse(PolicyResourceLoader.getResourceUrl("parser/testPolicyReferences.wsdl"), true);
        assertNotNull("PolicyMap can not be null", map);
    }

    public void testWsdlParserImport() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testWsdlImportMain.wsdl");
        Policy policy;
        assertNotNull("PolicyMap can not be null", policyMap);
        assertNotNull(policy = policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
        assertTrue(policy.contains(new QName("http://example.org","dummyAssertion")));

    }

    public void testServiceElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemService.wsdl");
        assertNotNull(policyMap.getServiceEffectivePolicy(PolicyMap.createWsdlServiceScopeKey(
                new QName("http://example.org","DictionaryService"))));
    }

    public void testPortElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemPort.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemPortType-invalid.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    public void testBindingElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemBinding.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    public void testPortTypeOperationElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemPortTypeOperation.wsdl");
        assertNotNull(policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testBindingOperationElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemBindingOperation.wsdl");
        assertNotNull(policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testMessageInElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemMessageIn.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testMessageOutElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemMessageOut.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testMessageFaultElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemMessageFault.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeOpInElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemPortTypeOpIn-invalid.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeOpOutElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemPortTypeOpOut-invalid.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeOpFaultElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemPortTypeOpFault-invalid.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    public void testBindingOpInElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemBindingOpIn.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testBindingOpOutElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemBindingOpOut.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testBindingOpFaultElementAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtElemBindingOpFault.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testServiceAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrService-invalid.wsdl");
        assertNotNull(policyMap.getServiceEffectivePolicy(PolicyMap.createWsdlServiceScopeKey(
                new QName("http://example.org","DictionaryService"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrPort-invalid.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    public void testPortTypeAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrPortType.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testBindingAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrBinding-invalid.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeOperationAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrPortTypeOperation-invalid.wsdl");
        assertNotNull(policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testBindingOperationAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrBindingOperation-invalid.wsdl");
        assertNotNull(policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testMessageInAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrMessageIn-invalid.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testMessageOutAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrMessageOut-invalid.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testMessageFaultAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrMessageFault-invalid.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    public void testPortTypeOpInAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrPortTypeOpIn.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testPortTypeOpOutAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrPortTypeOpOut.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testPortTypeOpFaultAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrPortTypeOpFault.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testBindingOpInAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrBindingOpIn-invalid.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testBindingOpOutAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrBindingOpOut-invalid.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testBindingOpFaultAttrAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtAttrBindingOpFault-invalid.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    public void testServiceHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocService.wsdl");
        assertNotNull(policyMap.getServiceEffectivePolicy(PolicyMap.createWsdlServiceScopeKey(
                new QName("http://example.org","DictionaryService"))));
    }

    public void testPortHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocPort.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocPortType-invalid.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    public void testBindingHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocBinding.wsdl");
        assertNotNull(policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish"))));
    }

    public void testPortTypeOperationHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocPortTypeOperation.wsdl");
        assertNotNull(policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testBindingOperationHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocBindingOperation.wsdl");
        assertNotNull(policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testMessageInHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocMessageIn.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testMessageOutHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocMessageOut.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testMessageFaultHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocMessageFault.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeOpInHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocPortTypeOpIn-invalid.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeOpOutHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocPortTypeOpOut-invalid.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    /**
     * invalid wsdl on input
     */
    public void testPortTypeOpFaultHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocPortTypeOpFault-invalid.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    public void testBindingOpInHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocBindingOpIn.wsdl");
        assertNotNull(policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testBindingOpOutHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocBindingOpOut.wsdl");
        assertNotNull(policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation"))));
    }

    public void testBindingOpFaultHeredocAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtHeredocBindingOpFault.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    public void testBindingOpFaultExternalPolicyAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtExternalBindingOpFault.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    public void testBindingOpFaultExternalFromAnonymousPolicyAttachment() throws Exception {
        PolicyMap policyMap = getPolicyMap("parser/testRuntimeWSExtExternalFromAnonBindingOpFault.wsdl");
        assertNotNull(policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://example.org","DictionaryService")
                ,new QName("http://example.org","CzechToEnglish")
                ,new QName("http://example.org","TranslateOperation")
                ,new QName("http://example.org","Fault"))));
    }

    public void testInvalidAssertionShouldCauseException() throws Exception {
        try {
            PolicyMap policyMap = getPolicyMap("parser/testInvalidAssertionError.wsdl", false);
            fail("WSDL validation should fail");
        } catch (WebServiceException e) {
            // ok - exception thrown as expected
        }
    }

    public void testCircularReference() throws Exception {
        try {
            getPolicyMap("parser/testPolicyCircularReferences.wsdl", false);
            fail("Parsing WSDL containing circular policy references should fail");
        } catch (WebServiceException e) {
            // ok - exception thrown as expected
        }
    }

    public void testComprehensive() throws PolicyException {
        PolicyMap policyMap = getPolicyMap("parser/testComprehensive.wsdl");

        // Test service scope

        Collection<PolicyMapKey> keys = policyMap.getAllServiceScopeKeys();
        assertEquals(1, keys.size());

        Policy policy = policyMap.getServiceEffectivePolicy(PolicyMap.createWsdlServiceScopeKey(
                new QName("http://wsit.test/","FaultServiceService")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        AssertionSet assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "ServiceMarker")));

        // Test endpoint scope

        keys = policyMap.getAllEndpointScopeKeys();
        assertEquals(1, keys.size());

        policy = policyMap.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingMarker")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "PortMarker")));

        // Test operation scope

        keys = policyMap.getAllOperationScopeKeys();
        assertEquals(3, keys.size());

        policy = policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","echo")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingOperationEcho")));

        policy = policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","hello")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingOperationHello")));

        policy = policyMap.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","ping")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingOperationPing")));

        // Test input message scope

        keys = policyMap.getAllInputMessageScopeKeys();
        assertEquals(3, keys.size());

        policy = policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","echo")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "MessageEcho")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingEchoInput")));

        policy = policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","hello")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "MessageHello")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingHelloInput")));

        policy = policyMap.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","ping")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "MessagePing")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingPingInput")));

        // Test output message scope

        keys = policyMap.getAllOutputMessageScopeKeys();
        assertEquals(3, keys.size());

        policy = policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","echo")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "MessageEchoResponse")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingEchoOutput")));

        policy = policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","hello")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "MessageHelloResponse")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingHelloOutput")));

        policy = policyMap.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
                new QName("http://wsit.test/","FaultServiceService"),
                new QName("http://wsit.test/","FaultServicePort"),
                new QName("http://wsit.test/","ping")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingPingOutput")));

        // Test fault message scope

        keys = policyMap.getAllFaultMessageScopeKeys();
        assertEquals(6, keys.size());

        policy = policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://wsit.test/", "FaultServiceService"),
                new QName("http://wsit.test/", "FaultServicePort"),
                new QName("http://wsit.test/", "echo"),
                new QName("http://wsit.test/", "EchoException")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingEchoException")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "EchoException")));

        policy = policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://wsit.test/", "FaultServiceService"),
                new QName("http://wsit.test/", "FaultServicePort"),
                new QName("http://wsit.test/", "echo"),
                new QName("http://wsit.test/", "Echo2Exception")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingEcho2Exception")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "Echo2Exception")));

        policy = policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://wsit.test/", "FaultServiceService"),
                new QName("http://wsit.test/", "FaultServicePort"),
                new QName("http://wsit.test/", "hello"),
                new QName("http://wsit.test/", "HelloException")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingHelloException")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "HelloException")));

        policy = policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://wsit.test/", "FaultServiceService"),
                new QName("http://wsit.test/", "FaultServicePort"),
                new QName("http://wsit.test/", "hello"),
                new QName("http://wsit.test/", "Hello2Exception")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingHello2Exception")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "Hello2Exception")));

        policy = policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://wsit.test/", "FaultServiceService"),
                new QName("http://wsit.test/", "FaultServicePort"),
                new QName("http://wsit.test/", "ping"),
                new QName("http://wsit.test/", "EchoException")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingPingException")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingEchoException")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingEcho2Exception")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "EchoException")));
        assertFalse(assertionSet.contains(new QName("http://wsit.test/", "Echo2Exception")));

        policy = policyMap.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
                new QName("http://wsit.test/", "FaultServiceService"),
                new QName("http://wsit.test/", "FaultServicePort"),
                new QName("http://wsit.test/", "ping"),
                new QName("http://wsit.test/", "Echo2Exception")));
        assertEquals(1, policy.getNumberOfAssertionSets());
        assertionSet = policy.iterator().next();
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "BindingPing2Exception")));
        assertFalse(assertionSet.contains(new QName("http://wsit.test/", "EchoException")));
        assertTrue(assertionSet.contains(new QName("http://wsit.test/", "Echo2Exception")));

    }

      // FIXME: For some reason doesn't pass validation:
      // WSP1015: Server side assertion validation failed for "{http://www.w3.org/2006/05/addressing/wsdl}UsingAddressing" assertion. Assertion was evaluated as "UNKNOWN".
//    public void testNamespaceImport() throws PolicyException {
//        PolicyMap map = getPolicyMap("parser/testNamespaceImport.wsdl", false);
//
//        Policy policy = map.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
//                new QName("STSUserAuth_svc_app", "casaService1"),
//                new QName("STSUserAuth_svc_app", "SecuredEchoPort")));
//        assertEquals("casaBinding1Policy", policy.getId());
//
//        policy = map.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
//                new QName("STSUserAuth_svc_app", "casaService1"),
//                new QName("STSUserAuth_svc_app", "SecuredEchoPort"),
//                new QName("STSUserAuth_svc_app", "EchoServiceOperation")));
//        assertEquals("casaBinding1_operation_Policy", policy.getId());
//
//        policy = map.getInputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
//                new QName("STSUserAuth_svc_app", "casaService1"),
//                new QName("STSUserAuth_svc_app", "SecuredEchoPort"),
//                new QName("STSUserAuth_svc_app", "EchoServiceOperation")));
//        assertEquals("casaBinding1_input1_Policy", policy.getId());
//
//        policy = map.getOutputMessageEffectivePolicy(PolicyMap.createWsdlMessageScopeKey(
//                new QName("STSUserAuth_svc_app", "casaService1"),
//                new QName("STSUserAuth_svc_app", "SecuredEchoPort"),
//                new QName("STSUserAuth_svc_app", "EchoServiceOperation")));
//        assertEquals("casaBinding1_output1_Policy", policy.getId());
//
//        policy = map.getFaultMessageEffectivePolicy(PolicyMap.createWsdlFaultMessageScopeKey(
//                new QName("STSUserAuth_svc_app", "casaService1"),
//                new QName("STSUserAuth_svc_app", "SecuredEchoPort"),
//                new QName("STSUserAuth_svc_app", "EchoServiceOperation"),
//                new QName("STSUserAuth_svc_app", "fault1")));
//        assertEquals("casaBinding1_fault1_Policy", policy.getId());
//    }

    public void testPolicyMapToString() throws Exception {
        PolicyMap policyMap = getPolicyMap("bug_reproduction/simple.wsdl");
        String result = policyMap.toString();
        assertNotNull(result);
    }

    public void testDuplicateId() throws PolicyException {
        try {
            getPolicyMap("duplicateid/duplicate.wsdl", true);
            fail("Read WSDL with two policies that have the same ID. This should have triggered a PolicyException.");
        } catch (WebServiceException e) {
            // This test is supposed to trigger an exception
        }
    }

    public void testDuplicateImport() throws PolicyException {
        final PolicyMap map = getPolicyMap("duplicateid/importer.wsdl", true);

        final Policy policy1 = map.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org", "DictionaryService"),
                new QName("http://example.org", "CzechToEnglish")));
        assertEquals("Policy1", policy1.getId());
        assertTrue(policy1.contains(new QName("http://example.org", "Assertion1")));
        assertFalse(policy1.contains(new QName("http://example.org", "Assertion2")));

        final Policy policy2 = map.getOperationEffectivePolicy(PolicyMap.createWsdlOperationScopeKey(
                new QName("http://example.org", "DictionaryService"),
                new QName("http://example.org", "CzechToEnglish"),
                new QName("http://example.org", "TranslateOperation")));
        assertEquals("Policy1", policy2.getId());
        assertTrue(policy2.contains(new QName("http://example.org", "Assertion2")));
        assertFalse(policy2.contains(new QName("http://example.org", "Assertion1")));
    }

    public void testDuplicateImport2() throws PolicyException {
        final PolicyMap map = getPolicyMap("duplicateid/importer2.wsdl", true);

        final Policy policy = map.getEndpointEffectivePolicy(PolicyMap.createWsdlEndpointScopeKey(
                new QName("http://example.org", "DictionaryService"),
                new QName("http://example.org", "CzechToEnglish")));
        assertTrue(policy.contains(new QName("http://example.org", "Assertion1")));
        assertTrue(policy.contains(new QName("http://example.org", "Assertion2")));
    }

}
