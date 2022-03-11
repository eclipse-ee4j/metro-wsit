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
public class SymmetricBindingTest extends TestCase {

    public SymmetricBindingTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SymmetricBindingTest.class);

        return suite;
    }

    private PolicySourceModel unmarshalPolicyResource(String resource) throws PolicyException, IOException {
        Reader reader = getResourceReader(resource);
        PolicySourceModel model = ModelUnmarshaller.getUnmarshaller().unmarshalModel(reader);
        reader.close();
        return model;
    }

    private Reader getResourceReader(String resourceName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null) {
            return new InputStreamReader(ClassLoader.getSystemResourceAsStream(resourceName));
        } else {
            return new InputStreamReader(cl.getResourceAsStream(resourceName));
        }


    }

    public Policy unmarshalPolicy(String xmlFile)throws Exception{
        PolicySourceModel model =  unmarshalPolicyResource(
                xmlFile);
        Policy mbp = ModelTranslator.getTranslator().translate(model);
        return mbp;

    }

    public void testSymmerticBinding1() throws Exception {
        String fileName="security/SymmetricBindingAssertion1.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "SymmetricBinding",assertion.getName().getLocalPart());
                SymmetricBinding sb = (SymmetricBinding)assertion;

                //  System.out.println((sb.getProtectionToken().getIncludeToken()));
                assertTrue(sb.getTokenProtection());

                AlgorithmSuite aSuite = sb.getAlgorithmSuite();
                assertEquals("Unmatched Algorithm",aSuite.getEncryptionAlgorithm(), AlgorithmSuiteValue.Basic128.getEncAlgorithm());

                assertTrue(sb.isIncludeTimeStamp());

                assertFalse("Signature is Encrypted", sb.getSignatureProtection());

                assertFalse(sb.isSignContent());
            }
        }
    }


    public void testSymmerticBinding2() throws Exception {
        String fileName="security/SymmetricBindingAssertion2.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "SymmetricBinding",assertion.getName().getLocalPart());
                SymmetricBinding sb = (SymmetricBinding)assertion;

                X509Token tkn1 = (X509Token)sb.getSignatureToken();
                assertTrue(tkn1.getTokenType().equals(com.sun.xml.ws.security.impl.policy.X509Token.WSSX509V1TOKEN10));

                X509Token tkn2 = (X509Token)sb.getEncryptionToken();
                assertTrue(tkn2.getTokenType().equals(com.sun.xml.ws.security.impl.policy.X509Token.WSSX509V3TOKEN10));

                AlgorithmSuite aSuite = sb.getAlgorithmSuite();
                assertEquals("Unmatched Algorithm",aSuite.getEncryptionAlgorithm(), AlgorithmSuiteValue.TripleDesRsa15.getEncAlgorithm());

                assertTrue(sb.isIncludeTimeStamp());

                assertTrue("Signature is not Encypted", sb.getSignatureProtection());

                assertFalse("Tokens are protected", sb.getTokenProtection());
            }
        }
    }


    public void testSymmetricIssuedTokenCR6419493() throws Exception {
        String fileName="security/IssuedTokenCR.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "SymmetricBinding",assertion.getName().getLocalPart());
                SymmetricBinding sb = (SymmetricBinding)assertion;

                IssuedToken tkn1 = (IssuedToken)sb.getProtectionToken();
                assertTrue(tkn1.getIncludeToken().equals(tkn1.getSecurityPolicyVersion().includeTokenAlways));
            }
        }
    }


}
