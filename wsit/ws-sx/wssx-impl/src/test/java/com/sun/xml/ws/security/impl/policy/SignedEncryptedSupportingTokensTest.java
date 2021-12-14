/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.security.policy.AlgorithmSuite;
import com.sun.xml.ws.policy.Policy;
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
public class SignedEncryptedSupportingTokensTest extends TestCase {
    
    public SignedEncryptedSupportingTokensTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
    }
    
    @Override
    protected void tearDown() {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SignedEncryptedSupportingTokensTest.class);
        
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
    
    public void testSignedEncryptedSupportingToken() throws Exception {
        String fileName="security/SignedEncryptedSupportingTokenAssertionSP12.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        while(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "SignedEncryptedSupportingTokens",assertion.getName().getLocalPart());
                SignedEncryptedSupportingTokens stk = (SignedEncryptedSupportingTokens)assertion;
                
                AlgorithmSuite aSuite = stk.getAlgorithmSuite();
                assertEquals("Unmatched Algorithm",aSuite.getEncryptionAlgorithm(), AlgorithmSuiteValue.TripleDesRsa15.getEncAlgorithm());
                
                Iterator itrTkn = stk.getTokens();
                if(itrTkn.hasNext()) {
                    assertTrue(((com.sun.xml.ws.security.policy.UserNameToken)itrTkn.next()).getType().equals(com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10));
                }
                
                Iterator itrSparts = stk.getSignedParts();
                if(itrSparts.hasNext()) {
                    assertEquals("Body not found ",true,((SignedParts)itrSparts.next()).hasBody());
                }
            }
        }
    }
    
}
