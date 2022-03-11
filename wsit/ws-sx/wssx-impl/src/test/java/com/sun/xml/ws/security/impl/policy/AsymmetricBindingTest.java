/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.api.policy.ModelTranslator;
import com.sun.xml.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.*;

/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class AsymmetricBindingTest extends TestCase {

    public AsymmetricBindingTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(AsymmetricBindingTest.class);

        return suite;
    }

    private PolicySourceModel unmarshalPolicyResource(String resource) throws PolicyException, IOException {
        Reader reader = getResourceReader(resource);
        PolicySourceModel model = ModelUnmarshaller.getUnmarshaller().unmarshalModel(reader);
        reader.close();
        return model;
    }

    private Reader getResourceReader(String resourceName) {
        return new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
    }

    public Policy unmarshalPolicy(String xmlFile)throws Exception{
        PolicySourceModel model =  unmarshalPolicyResource(
                xmlFile);
        Policy mbp = ModelTranslator.getTranslator().translate(model);
        return mbp;

    }

    public void testAsymmerticBinding1() throws Exception {
        String fileName="security/AsymmetricBindingAssertion1.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "AsymmetricBinding",assertion.getName().getLocalPart());
                AsymmetricBinding asb = (AsymmetricBinding)assertion;

                X509Token tkn1 = (X509Token)asb.getInitiatorToken();
                assertTrue(tkn1.getTokenType().equals(com.sun.xml.ws.security.impl.policy.X509Token.WSSX509V1TOKEN10));

                X509Token tkn2 = (X509Token)asb.getRecipientToken();
                assertTrue(tkn2.getTokenType().equals(com.sun.xml.ws.security.impl.policy.X509Token.WSSX509V3TOKEN11));

                assertFalse("Tokens are Protected", asb.getTokenProtection());

                AlgorithmSuite aSuite = asb.getAlgorithmSuite();
                assertEquals("Unmatched Algorithm",aSuite.getEncryptionAlgorithm(), AlgorithmSuiteValue.TripleDesRsa15.getEncAlgorithm());

                assertTrue(asb.isIncludeTimeStamp());

                assertFalse("Signature is Encrypted", asb.getSignatureProtection());

                assertFalse(asb.isSignContent());
            }
        }
    }


    public void testAsymmerticBinding2() throws Exception {
        String fileName="security/AsymmetricBindingAssertion2.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "AsymmetricBinding",assertion.getName().getLocalPart());
                AsymmetricBinding asb = (AsymmetricBinding)assertion;

                X509Token tkn1 = (X509Token)asb.getInitiatorToken();
                assertTrue(tkn1.getTokenType().equals(com.sun.xml.ws.security.impl.policy.X509Token.WSSX509V1TOKEN10));

                X509Token tkn2 = (X509Token)asb.getRecipientToken();
                assertTrue(tkn2.getTokenType().equals(com.sun.xml.ws.security.impl.policy.X509Token.WSSX509V3TOKEN11));

                AlgorithmSuite aSuite = asb.getAlgorithmSuite();
                assertEquals("Unmatched Algorithm",aSuite.getEncryptionAlgorithm(), AlgorithmSuiteValue.TripleDesRsa15.getEncAlgorithm());

                assertTrue(asb.isIncludeTimeStamp());

                assertTrue("Signature is not Encrypted", asb.getSignatureProtection());

                assertTrue("Tokens are not protected", asb.getTokenProtection());

                assertEquals("SIGN_ENCRYPT is the protection order",com.sun.xml.ws.security.impl.policy.AsymmetricBinding.ENCRYPT_SIGN,asb.getProtectionOrder());
            }
        }
    }


    public void testAsymmerticCR6420594() throws Exception {
        String fileName="security/AsymmetricCR.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "AsymmetricBinding",assertion.getName().getLocalPart());
                AsymmetricBinding asb = (AsymmetricBinding)assertion;

                X509Token tkn1 = (X509Token)asb.getInitiatorToken();
                assertTrue(tkn1.getIncludeToken().equals(tkn1.getSecurityPolicyVersion().includeTokenAlways));
                assertTrue(tkn1.getTokenType().equals(com.sun.xml.ws.security.impl.policy.X509Token.WSSX509V3TOKEN10));

                X509Token tkn2 = (X509Token)asb.getRecipientToken();
                assertTrue(tkn2.getIncludeToken().equals(tkn2.getSecurityPolicyVersion().includeTokenAlways));
                assertTrue(tkn2.getTokenType().equals(com.sun.xml.ws.security.impl.policy.X509Token.WSSX509V3TOKEN10));
            }
        }
    }

}
