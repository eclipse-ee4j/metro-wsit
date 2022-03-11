/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * TimestampTest.java
 *
 * Created on April 6, 2006, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl;

import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.*;

import com.sun.xml.wss.callback.PolicyCallbackHandler1;
import com.sun.xml.wss.*;

import jakarta.xml.soap.*;
import com.sun.xml.wss.impl.policy.mls.*;
import com.sun.xml.wss.core.*;
import com.sun.xml.ws.security.impl.policy.*;
import javax.xml.namespace.QName;

import com.sun.xml.wss.impl.misc.*;
import javax.security.auth.callback.CallbackHandler;
import com.sun.xml.wss.impl.*;
import javax.xml.crypto.dsig.DigestMethod;
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;
import com.sun.xml.wss.impl.util.PolicyResourceLoader;

import com.sun.xml.wss.impl.AlgorithmSuite;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.AssertionSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author admin
 */
public class TimestampTest extends TestCase {
    private static HashMap client = new HashMap();
    private static  AlgorithmSuite alg = null;

    /** Creates a new instance of TimestampTest */
    public TimestampTest(String testName) {
        super(testName);
    }


    @Override
    protected void setUp() {

    }

    @Override
    protected void tearDown() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TimestampTest.class);                                                                                                 return suite;
    }

    public static void testTimestampOnTop() throws Exception {
            //alg.setType(AlgorithmSuiteValue.Basic128);
            alg = new AlgorithmSuite(AlgorithmSuiteValue.Basic128.getDigAlgorithm(), AlgorithmSuiteValue.Basic128.getEncAlgorithm(), AlgorithmSuiteValue.Basic128.getSymKWAlgorithm(), AlgorithmSuiteValue.Basic128.getAsymKWAlgorithm());
            SignaturePolicy signaturePolicy = new SignaturePolicy();
            SignatureTarget st = new SignatureTarget();
            st.setType("qname");
            st.setDigestAlgorithm(DigestMethod.SHA1);
            ((SignaturePolicy.FeatureBinding)signaturePolicy.getFeatureBinding()).
                    addTargetBinding(st);
            ((SignaturePolicy.FeatureBinding)signaturePolicy.getFeatureBinding()).
                    setCanonicalizationAlgorithm(MessageConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

            QName name = new QName("X509Certificate");
            Token tok = new Token(name);

            AuthenticationTokenPolicy.X509CertificateBinding x509bind =
                    (AuthenticationTokenPolicy.X509CertificateBinding)signaturePolicy.newX509CertificateKeyBinding();
            x509bind.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
            x509bind.setIncludeToken(SecurityPolicyVersion.SECURITYPOLICY200507.includeTokenAlways);
        //x509bind.setPolicyToken(tok);
            x509bind.setUUID(new String("1008"));

            TimestampPolicy tsPolicy = new TimestampPolicy();

            SOAPMessage msg = MessageFactory.newInstance().createMessage();
            SOAPBody body = msg.getSOAPBody();
            SOAPBodyElement sbe = body.addBodyElement(
                    SOAPFactory.newInstance().createName(
                    "StockSymbol",
                        "tru",
                    "http://fabrikam123.com/payloads"));
            sbe.addTextNode("QQQ");

            //Create processing context and set the soap
            //message to be processed.
            ProcessingContextImpl context = new ProcessingContextImpl(client);
        context.setSOAPMessage(msg);

            com.sun.xml.ws.security.policy.WSSAssertion wssAssertionws = null;
            WSSAssertion wssAssertion = null;
            AssertionSet as = null;
            Policy wssPolicy = PolicyResourceLoader.loadPolicy("security/policy-binding2.xml");
            Iterator<AssertionSet> i = wssPolicy.iterator();
            if(i.hasNext())
                as = i.next();

            for(PolicyAssertion assertion:as){
                if(assertion instanceof com.sun.xml.ws.security.policy.WSSAssertion){
                    wssAssertionws = (com.sun.xml.ws.security.policy.WSSAssertion)assertion;
                }
            }
            wssAssertion = new WSSAssertion(wssAssertionws.getRequiredProperties(), "1.0");
            MessagePolicy pol = new MessagePolicy();
            pol.append(tsPolicy);
        pol.append(signaturePolicy);
            pol.setWSSAssertion(wssAssertion);

            context.setAlgorithmSuite(alg);

            context.setSecurityPolicy(pol);
            CallbackHandler handler = new PolicyCallbackHandler1("client");
        SecurityEnvironment env = new DefaultSecurityEnvironmentImpl(handler);
            context.setSecurityEnvironment(env);

            SecurityAnnotator.secureMessage(context);

        SecurableSoapMessage secMsg = context.getSecurableSoapMessage();
            //DumpFilter.process(context);

            SecurityHeader securityHeader = secMsg.findSecurityHeader();
            org.w3c.dom.Node timestamp = securityHeader.getFirstChild();
            assertEquals("Timestamp", timestamp.getLocalName());


    }

//   public static void main(String[] args) throws Exception{
//       testTimestampOnTop();
//   }

}
