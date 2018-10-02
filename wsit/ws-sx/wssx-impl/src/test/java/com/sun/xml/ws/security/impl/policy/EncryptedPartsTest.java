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
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import junit.framework.*;

import java.util.Iterator;

/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class EncryptedPartsTest extends TestCase {
    
    public EncryptedPartsTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(EncryptedPartsTest.class);
        
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
        PolicySourceModel model =  unmarshalPolicyResource(xmlFile);
        Policy mbp = ModelTranslator.getTranslator().translate(model);
        return mbp;
        
    }
    
    public void testEncryptParts2() throws Exception{
        String fileName = "security/EncryptParts2.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()){
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid assertion", "EncryptedParts", assertion.getName().getLocalPart());
                EncryptedParts ep = (EncryptedParts)assertion;
                assertTrue(ep.hasBody());
            }
        }
    }
    
    public void testEncryptPartsCR6421129() throws Exception{
        String fileName = "security/EncryptParts5.xml";
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as){
                assertEquals("Invalid asserton","EncryptedParts", assertion.getName().getLocalPart());
                EncryptedParts ep = (EncryptedParts)assertion;
                Iterator itrTargets = ep.getTargets();
                boolean hasBody = false;
                while(itrTargets.hasNext()){
                    PolicyAssertion assertTargets = (PolicyAssertion)itrTargets.next();
                    SecurityPolicyVersion spVersion = getSPVersion(assertTargets);
                    if ( PolicyUtil.isBody(assertTargets, spVersion)) {
                        if(hasBody==true){
                            assertFalse(true);
                        }else{
                            hasBody = true;
                        }
                    }
                }
            }
        }
    }
    
    private SecurityPolicyVersion getSPVersion(PolicyAssertion pa){
        String nsUri = pa.getName().getNamespaceURI();
        // Default SPVersion
        SecurityPolicyVersion spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
        // If spec version, update
        if(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri.equals(nsUri)){
            spVersion = SecurityPolicyVersion.SECURITYPOLICY12NS;
        }
        return spVersion;
    }
    
}
