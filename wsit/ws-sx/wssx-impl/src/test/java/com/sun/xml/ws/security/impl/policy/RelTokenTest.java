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

import com.sun.xml.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.ws.api.policy.ModelTranslator;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.*;

/**
 *
 * @author Mayank.Mishra@sun.com
 */
public class RelTokenTest extends TestCase {
    
    public RelTokenTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
    }
    
    @Override
    protected void tearDown() {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(RelTokenTest.class);
        
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
    
    public void testRelToken1()throws Exception{
        testRelToken_Keys_Reference("security/RelToken1.xml", "");
        testRelToken_Keys_Reference("security/RelToken2.xml", "RequireKeyIdentifierReference");
    }
    
    public void testRelToken2() throws Exception{
        testRelTokenType("security/RelToken1.xml", com.sun.xml.ws.security.policy.RelToken.WSS_REL_V10_TOKEN10);
        testRelTokenType("security/RelToken2.xml", com.sun.xml.ws.security.policy.RelToken.WSS_REL_V20_TOKEN10);
        testRelTokenType("security/RelToken3.xml", com.sun.xml.ws.security.policy.RelToken.WSS_REL_V10_TOKEN11);
        testRelTokenType("security/RelToken4.xml", com.sun.xml.ws.security.policy.RelToken.WSS_REL_V20_TOKEN11);
    }
    
    public void testRelToken_Keys_Reference(String fileName, String param) throws Exception {
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","RelToken",assertion.getName().getLocalPart());
                com.sun.xml.ws.security.impl.policy.RelToken rt=(com.sun.xml.ws.security.impl.policy.RelToken)assertion;
                if(param.equals(""))
                    assertTrue(rt.isRequireDerivedKeys());
                else {
                    Iterator itrRt = rt.getTokenRefernceType();
                    if(itrRt.hasNext()) {
                        assertTrue(itrRt.next().equals(com.sun.xml.ws.security.policy.RelToken.REQUIRE_KEY_IDENTIFIER_REFERENCE));
                    }
                }
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }
    
    public void testRelTokenType(String fileName, String tokenType) throws Exception {
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","RelToken",assertion.getName().getLocalPart());
                assertion = assertion;
                com.sun.xml.ws.security.impl.policy.RelToken rt = (com.sun.xml.ws.security.impl.policy.RelToken)assertion;
                assertTrue(rt.getTokenType().equals(tokenType));
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }
    

}
