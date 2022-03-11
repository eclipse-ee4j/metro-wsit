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

import junit.framework.*;

/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class SamlTokenTest extends TestCase {

    public SamlTokenTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SamlTokenTest.class);

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

     public void testSamlToken_Types_5() throws Exception{
        testSamlToken_Type("security/SamlTokenAssertions1.xml", com.sun.xml.ws.security.impl.policy.SamlToken.WSS_SAML_V10_TOKEN10);
        testSamlToken_Type("security/SamlTokenAssertions2.xml", com.sun.xml.ws.security.impl.policy.SamlToken.WSS_SAML_V11_TOKEN10);
        testSamlToken_Type("security/SamlTokenAssertions3.xml", com.sun.xml.ws.security.impl.policy.SamlToken.WSS_SAML_V10_TOKEN11);
        testSamlToken_Type("security/SamlTokenAssertions4.xml", com.sun.xml.ws.security.impl.policy.SamlToken.WSS_SAML_V11_TOKEN11);
        testSamlToken_Type("security/SamlTokenAssertions5.xml", com.sun.xml.ws.security.impl.policy.SamlToken.WSS_SAML_V20_TOKEN11);
    }

    public void testSamlToken_Keys() throws Exception {
        String fileName = "security/SamlTokenAssertions1.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","SamlToken",assertion.getName().getLocalPart());
                SamlToken samlt = (SamlToken)assertion;
                assertTrue(samlt.isRequireDerivedKeys());
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }


    public void testSamlToken_Reference() throws Exception {
        String fileName = "security/SamlTokenAssertions2.xml";
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","SamlToken",assertion.getName().getLocalPart());
                SamlToken samlt = (SamlToken)assertion;
                Iterator itrst = samlt.getTokenRefernceType();
                if(itrst.hasNext()) {
                    assertTrue(itrst.next().equals(com.sun.xml.ws.security.impl.policy.SamlToken.REQUIRE_KEY_IDENTIFIER_REFERENCE));
                }
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }

    public void testSamlToken_Type(String fileName, String tokenType) throws Exception {
        Policy policy = unmarshalPolicy(fileName);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","SamlToken",assertion.getName().getLocalPart());
                SamlToken samlt = (SamlToken)assertion;
                assertTrue(samlt.getTokenType().equals(tokenType));
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }

}
