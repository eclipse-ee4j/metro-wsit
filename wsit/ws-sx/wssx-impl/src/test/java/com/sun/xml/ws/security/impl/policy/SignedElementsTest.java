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
 * @author Mayank.Mishra@SUN.com
 */
public class SignedElementsTest extends TestCase {
    
    public SignedElementsTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
    }
    
    @Override
    protected void tearDown() {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SignedElementsTest.class);
        
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
    
    public boolean hasXPathTarget(String xpathExpr , Iterator itr){
        while(itr.hasNext()){
            if(xpathExpr.equals(itr.next())){
                return true;
            }
        }
        return false;
    }
    
    public void testSignElements1()throws Exception{
        String fileName = "security/SignElements1.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr  = policy.iterator();
        if(itr.hasNext()){
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid assertion","SignedElements",assertion.getName().getLocalPart());
                SignedElements se = (SignedElements)assertion;
                assertTrue(hasXPathTarget("//soapEnv:Body",se.getTargets()));
                assertTrue(hasXPathTarget("//addr:To",se.getTargets()));
                assertTrue(hasXPathTarget("//addr:From",se.getTargets()));
                assertTrue(hasXPathTarget("//addr:RealtesTo",se.getTargets()));
                
                //   assertFalse("Headers should not be present",se.getHeaders().hasNext());
            }
        }else{
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+"failed!");
        }
    }
    
    
    public void testSignElements2()throws Exception{
        String fileName = "security/SignElements2.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr  = policy.iterator();
        if(itr.hasNext()){
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid assertion","SignedElements",assertion.getName().getLocalPart());
                SignedElements se = (SignedElements)assertion;
                assertFalse(se.getTargets().hasNext());
                
                //   assertFalse("Headers should not be present",se.getHeaders().hasNext());
            }
        }else{
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+"failed!");
        }
    }
    
}
