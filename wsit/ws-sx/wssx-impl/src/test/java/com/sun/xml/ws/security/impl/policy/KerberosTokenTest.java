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
import java.util.Set;
import java.util.Iterator;

import junit.framework.*;

/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class KerberosTokenTest extends TestCase {
    
    public KerberosTokenTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
    }
    
    @Override
    protected void tearDown() {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(KerberosTokenTest.class);
        
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
    
    
    public void testKerberosTokenAssertions() throws Exception{
        testKerberosTokenType("security/KerberosTokenAssertions1.xml", com.sun.xml.ws.security.impl.policy.KerberosToken.WSSKERBEROS_V5_AP_REQ_TOKEN11);
        testKerberosTokenType("security/KerberosTokenAssertions2.xml", com.sun.xml.ws.security.impl.policy.KerberosToken.WSSKERBEROS_GSS_V5_AP_REQ_TOKEN11);
        testKerberosTokenKeys();
        testKerberosTokenReference();
    }
    
    public void testKerberosTokenType(String fileName, String TokenType) throws Exception{
        
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","KerberosToken",assertion.getName().getLocalPart());
                KerberosToken kt = (KerberosToken)assertion;
                assertTrue(TokenType.equals(kt.getTokenType()));
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }
    
    public void testKerberosTokenKeys() throws Exception{
        String fileName = "security/KerberosTokenAssertions3.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","KerberosToken",assertion.getName().getLocalPart());
                KerberosToken kt = (KerberosToken)assertion;
                assertTrue(kt.isRequireDerivedKeys());
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }
    
    
    public void testKerberosTokenReference() throws Exception{
        String fileName = "security/KerberosTokenAssertions4.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","KerberosToken",assertion.getName().getLocalPart());
                KerberosToken kt = (KerberosToken)assertion;
                Set tokenRefType = kt.getTokenRefernceType();
                Iterator itrkt = tokenRefType.iterator();
                if(itrkt.hasNext()) {
                    assertTrue(itrkt.next().equals(com.sun.xml.ws.security.impl.policy.KerberosToken.REQUIRE_KEY_IDENTIFIER_REFERENCE));
                }
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }
    

}
