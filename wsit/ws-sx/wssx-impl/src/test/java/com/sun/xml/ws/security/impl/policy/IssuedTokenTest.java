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
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import com.sun.xml.ws.security.policy.Token;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class IssuedTokenTest extends TestCase {

    public IssuedTokenTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IssuedTokenTest.class);

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

    public void testIssuedTokenAssertions1() throws Exception{
        String fileName = "security/IssuedTokenAssertions1.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","IssuedToken",assertion.getName().getLocalPart());
                IssuedToken it = (IssuedToken)assertion;
                assertEquals("Invalid Dervied Keys", "RequireDerivedKeys", Token.REQUIRE_DERIVED_KEYS);
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }


    public void testIssuedTokenAssertions2() throws Exception{
        String fileName = "security/IssuedTokenAssertions2.xml";
        Policy policy = unmarshalPolicy(fileName);
        String rType = com.sun.xml.ws.security.policy.IssuedToken.REQUIRE_EXTERNAL_REFERENCE;
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","IssuedToken",assertion.getName().getLocalPart());
                IssuedToken it = (IssuedToken)assertion;
                System.out.println(it.getIncludeToken());
                Iterator itrIt = it.getTokenRefernceType();
                if(itrIt.hasNext()) {
                    assertTrue(itrIt.next().equals(rType));
                }
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }


    public void testIssuedTokenAssertions3() throws Exception{
        String fileName = "security/IssuedTokenAssertions3.xml";
        String rType = com.sun.xml.ws.security.policy.IssuedToken.REQUIRE_INTERNAL_REFERENCE;
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","IssuedToken",assertion.getName().getLocalPart());
                IssuedToken it = (IssuedToken)assertion;
                System.out.println(it.getIncludeToken());
                Iterator itrIt = it.getTokenRefernceType();
                if(itrIt.hasNext()) {
                    assertTrue(itrIt.next().equals(rType));
                }
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }


     public void testIssuedTokenAssertions4() throws Exception{
         // test for bug
         //https://wsit.dev.java.net/issues/show_bug.cgi?id=314
        String fileName = "security/IssuedTokenAssertions_issuer.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","IssuedToken",assertion.getName().getLocalPart());
                IssuedToken it = (IssuedToken)assertion;
               assertNotNull(it.getIssuer().getAddress().getURI());
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }


}
