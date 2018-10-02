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
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;

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
public class EndorsingSupportingTokensTest extends TestCase {
    
    public EndorsingSupportingTokensTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(EndorsingSupportingTokensTest.class);
        
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
    
        public void testEndorsingSupportingToken() throws Exception {
        String fileName="security/EndorsingSupportingToken.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion", "EndorsingSupportingTokens",assertion.getName().getLocalPart());
                EndorsingSupportingTokens est = (EndorsingSupportingTokens)assertion;
                
                AlgorithmSuite aSuite = (AlgorithmSuite) est.getAlgorithmSuite();
                assertEquals("Unmatched Algorithm",aSuite.getEncryptionAlgorithm(), AlgorithmSuiteValue.TripleDesRsa15.getEncAlgorithm());
                
                Iterator itrest = est.getTokens();
                if(itrest.hasNext()) {
                    assertTrue(X509Token.WSSX509V3TOKEN10.equals(((X509Token)itrest.next()).getTokenType()));
                }
                
                Iterator itrTkn = est.getSignedParts();
                if(itrTkn.hasNext()) {
                    SignedParts sp = ((SignedParts)itrTkn.next());
                    
                    assertEquals("Body should be present",true,sp.hasBody());
                    assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","To"),sp.getHeaders()));
                    assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","From"),sp.getHeaders()));
                    assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","FaultTo"),sp.getHeaders()));
                    assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","ReplyTo"),sp.getHeaders()));
                    assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","MessageID"),sp.getHeaders()));
                    assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","RelatesTo"),sp.getHeaders()));
                    assertTrue(isHeaderPresent(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing","Action"),sp.getHeaders()));
                    
                }
            }
        }
    }
}
