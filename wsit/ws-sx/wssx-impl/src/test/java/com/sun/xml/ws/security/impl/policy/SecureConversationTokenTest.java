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
public class SecureConversationTokenTest extends TestCase {

    public SecureConversationTokenTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SecureConversationTokenTest.class);

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

    public void testSecureConversationToken1() throws Exception {
        String fileName = "security/SecureConversationTokenAssertions1.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","SecureConversationToken",assertion.getName().getLocalPart());
                SecureConversationToken sct = (SecureConversationToken)assertion;
                assertTrue(sct.isRequireDerivedKeys());
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }

    public void testSecureConversationToken2() throws Exception {
        String fileName = "security/SecureConversationTokenAssertions2.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","SecureConversationToken",assertion.getName().getLocalPart());
                SecureConversationToken sct = (SecureConversationToken)assertion;
                assertTrue(sct.getTokenRefernceTypes().contains(com.sun.xml.ws.security.impl.policy.SecureConversationToken.REQUIRE_EXTERNAL_URI_REFERENCE));
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }

    public void testSecureConversationToken3() throws Exception {
        String fileName = "security/SecureConversationTokenAssertions3.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","SecureConversationToken",assertion.getName().getLocalPart());
                SecureConversationToken sct = (SecureConversationToken)assertion;
                assertTrue(sct.getTokenType().equals(com.sun.xml.ws.security.impl.policy.SecureConversationToken.SC10_SECURITYCONTEXT_TOKEN));
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }

}
