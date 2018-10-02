/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.security.policy.Header;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import javax.xml.namespace.QName;

import junit.framework.*;


/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class SignedPartsTest extends TestCase {
    
    public SignedPartsTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(SignedPartsTest.class);
        
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
    
    public boolean isHeaderPresent(QName expected , Iterator headers){
        while(headers.hasNext()){
            Header header = (Header) headers.next();
            if(expected.getLocalPart().equals(header.getLocalName())){
                if(expected.getNamespaceURI().equals(header.getURI())){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void testSignParts1()throws Exception {
        String fileName = "security/SignParts1.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr  = policy.iterator();
        if(itr.hasNext()){
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid assertion","SignedParts",assertion.getName().getLocalPart());
                SignedParts sp = (SignedParts)assertion;
                assertEquals("Body not found ",true,sp.hasBody());
                
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","To"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","From"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","FaultTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","ReplyTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","MessageID"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","RelatesTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","Action"),sp.getHeaders()));
            }
        }else{
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+"failed!");
        }
    }
    
    
    public void testSignParts2()throws Exception {
        String fileName = "security/SignParts2.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr  = policy.iterator();
        if(itr.hasNext()){
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid assertion","SignedParts",assertion.getName().getLocalPart());
                SignedParts sp = (SignedParts)assertion;
                assertEquals("Body not found ",true,sp.hasBody());
                // assertTrue("Header elements should not be present",sp.getHeaders().hasNext());
            }
        }else{
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+"failed!");
        }
    }
    
    public void testSignParts3()throws Exception {
        String fileName = "security/SignParts3.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr  = policy.iterator();
        if(itr.hasNext()){
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid assertion","SignedParts",assertion.getName().getLocalPart());
                SignedParts sp = (SignedParts)assertion;
                assertEquals("Body should not be present ",false,sp.hasBody());
                
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","To"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","From"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","FaultTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","ReplyTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","MessageID"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","RelatesTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","Action"),sp.getHeaders()));
            }
        }else{
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+"failed!");
        }
    }
    
    public void testSignParts4()throws Exception {
        String fileName = "security/SignParts4.xml";
        try{
            Policy policy = unmarshalPolicy(fileName);
        }catch(PolicyException ex){
//java.lang.reflect.InvocationTargetException for Namespace attribute is required under Header element
            if(!"java.lang.reflect.InvocationTargetException".equals(ex.getMessage())){
                System.out.println("Exception is different "+ex.getMessage());
                throw ex;
            }
        }
    }
    
    
    public void testSignParts5()throws Exception {
        String fileName = "security/SignParts5.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr  = policy.iterator();
        if(itr.hasNext()){
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid assertion","SignedParts",assertion.getName().getLocalPart());
                SignedParts sp = (SignedParts)assertion;
                assertEquals("Body should not be present ",false,sp.hasBody());
                
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing",""),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","FaultTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","ReplyTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","MessageID"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","RelatesTo"),sp.getHeaders()));
                assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","Action"),sp.getHeaders()));
            }
        }else{
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+"failed!");
        }
    }
    
    public void testSignParts6()throws Exception {
        String fileName = "security/SignParts6.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr  = policy.iterator();
        if(itr.hasNext()){
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid assertion","SignedParts",assertion.getName().getLocalPart());
                SignedParts sp = (SignedParts)assertion;
                assertEquals("Body should not be present ",false,sp.hasBody());
                assertFalse("Headers should not be present",sp.getHeaders().hasNext());
            }
        }else{
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+"failed!");
        }
    }

}
