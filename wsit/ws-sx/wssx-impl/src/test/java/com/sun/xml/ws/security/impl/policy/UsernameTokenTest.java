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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.*;

/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class UsernameTokenTest extends TestCase {
    
    public UsernameTokenTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
    }
    
    @Override
    protected void tearDown() {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UsernameTokenTest.class);
        
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
    
    public void testUserNameTokenAssertions_8() throws Exception{
        testTokenAssertionsType("security/UserNameTokenAssertions1.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10);
        testTokenAssertionsType("security/UserNameTokenAssertions2.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10);
        testTokenAssertionsType("security/UserNameTokenAssertions3.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10);
        testTokenAssertionsType("security/UserNameTokenAssertions4.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10);
        testTokenAssertionsType("security/UserNameTokenAssertions5.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_11);
        testTokenAssertionsType("security/UserNameTokenAssertions6.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_11);
        testTokenAssertionsType("security/UserNameTokenAssertions7.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_11);
        testTokenAssertionsType("security/UserNameTokenAssertions8.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_11);
    }
    
    public void testTokenAssertionsType(String fileName, String tokenType) throws Exception{
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","UsernameToken",assertion.getName().getLocalPart());
                com.sun.xml.ws.security.policy.UserNameToken ut = (com.sun.xml.ws.security.policy.UserNameToken)assertion;
                assertTrue(tokenType.equals(ut.getType()));
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }

}
