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
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author ashutosh
 */
public class KeyValueTokenTest extends TestCase {
    
    /** Creates a new instance of KeyValueTokenTest */
    public KeyValueTokenTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
    }
    
    @Override
    protected void tearDown() {
    }
    
     public static Test suite() {
        TestSuite suite = new TestSuite(KeyValueTokenTest.class);
        
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
    
    public void testKeyValueTokenAssertions() throws Exception{
        testKeyValueTokenType("security/KeyValueTokenAssertions1.xml", com.sun.xml.ws.security.impl.policy.KeyValueToken.RSA_KEYVALUE_TOKEN);
        testRsaToken("security/RsaTokenAssertions1.xml");
    }
    
    public void testKeyValueTokenType(String fileName, String tokenType) throws Exception{
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","KeyValueToken",assertion.getName().getLocalPart());
                KeyValueToken kt = (KeyValueToken)assertion;
                assertTrue(tokenType.equals(kt.getTokenType()));
                assertEquals("Token Inclusion incorrect", kt.getIncludeToken(), SecurityPolicyVersion.SECURITYPOLICY12NS.includeTokenNever);
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }
    
    public void testRsaToken(String fileName) throws Exception{
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","RsaToken",assertion.getName().getLocalPart());
                RsaToken rsaToken = (RsaToken)assertion;
                assertEquals("Token Inclusion incorrect", rsaToken.getIncludeToken(), SecurityPolicyVersion.SECURITYPOLICY200507.includeTokenNever);
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }
    
}
