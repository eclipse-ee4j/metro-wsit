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
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class SignedEndorsingEncryptedSupportingTokensTest extends TestCase {

    public SignedEndorsingEncryptedSupportingTokensTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SignedEndorsingEncryptedSupportingTokensTest.class);
        return suite;
    }

    public boolean hasXPathTarget(String xpathExpr , Iterator itr){
        while(itr.hasNext()){
            if(xpathExpr.equals(itr.next())){
                return true;
            }
        }
        return false;
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

    public void testSignedEndorsingEncryptedSupportingToken() throws Exception {
        String fileName="security/SignedEndorsingEncryptedSupportingTokenSP12.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "SignedEndorsingEncryptedSupportingTokens",assertion.getName().getLocalPart());
                SignedEndorsingEncryptedSupportingTokens sst = (SignedEndorsingEncryptedSupportingTokens)assertion;

                AlgorithmSuite aSuite = (AlgorithmSuite) sst.getAlgorithmSuite();
                assertEquals("Unmatched Algorithm",aSuite.getEncryptionAlgorithm(), AlgorithmSuiteValue.TripleDesRsa15.getEncAlgorithm());

                Iterator itrest = sst.getTokens();
                if(itrest.hasNext()) {
                    assertTrue(X509Token.WSSX509V3TOKEN10.equals(((X509Token)itrest.next()).getTokenType()));
                }

                Iterator itrSparts = sst.getSignedElements();
                if(itrSparts.hasNext()) {
                    SignedElements se = (SignedElements)itrSparts.next();
                    assertTrue(hasXPathTarget("//soapEnv:Body",se.getTargets()));
                    assertTrue(hasXPathTarget("//addr:To",se.getTargets()));
                    assertTrue(hasXPathTarget("//addr:From",se.getTargets()));
                    assertTrue(hasXPathTarget("//addr:RealtesTo",se.getTargets()));
                }
            }
        }
    }
}
