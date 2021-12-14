/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SignatureConfirmationTest.java
 *
 * Created on April 4, 2006, 6:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sun.xml.wss.callback.PolicyCallbackHandler1;
import com.sun.xml.ws.security.impl.policy.*;
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;
import com.sun.xml.wss.impl.policy.mls.*;
import com.sun.xml.wss.impl.*;
import com.sun.xml.wss.*;
import com.sun.xml.wss.impl.misc.DefaultSecurityEnvironmentImpl;
import com.sun.xml.wss.impl.util.PolicyResourceLoader;
import com.sun.xml.wss.impl.util.TestUtil;

import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.AssertionSet;

import javax.security.auth.callback.CallbackHandler;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.namespace.QName;
import jakarta.xml.soap.*;

import java.util.*;
import java.io.*;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class SignatureConfirmationTest extends TestCase {
 
    private static Hashtable client = new Hashtable();
    private static Hashtable server = new Hashtable();
    private static AlgorithmSuite alg = null;
    
    /** Creates a new instance of SignatureConfirmationTest */
    public SignatureConfirmationTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() {
	    
    }
                                                                                                                                                             
    @Override
    protected void tearDown() {
    }
                                                                                                                                                             
    public static Test suite() {
        TestSuite suite = new TestSuite(SymmetricDktTest.class);                                                                                                                                                            
        return suite;
    }
    
    public static void testSignatureConfirmationTest() throws Exception {
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

    	    SymmetricKeyBinding sigKb = 
        	(SymmetricKeyBinding)signaturePolicy.newSymmetricKeyBinding();
	    AuthenticationTokenPolicy.X509CertificateBinding x509bind = 
    	            (AuthenticationTokenPolicy.X509CertificateBinding)sigKb.newX509CertificateKeyBinding();
            x509bind.setReferenceType(MessageConstants.THUMB_PRINT_TYPE);
	    //x509bind.setPolicyToken(tok);
    	    x509bind.setUUID(new String("1018"));
            
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
	    //wssAssertion.addRequiredProperty("RequireSignatureConfirmation");
            wssAssertion = getWssAssertion(wssAssertionws);
     	    MessagePolicy pol = new MessagePolicy();
	    pol.append(signaturePolicy);
            pol.setWSSAssertion(wssAssertion);
        
	    context.setAlgorithmSuite(alg);
        
    	    context.setSecurityPolicy(pol);
            CallbackHandler handler = new PolicyCallbackHandler1("client");
	    SecurityEnvironment env = new DefaultSecurityEnvironmentImpl(handler);
    	    context.setSecurityEnvironment(env);

            SecurityAnnotator.secureMessage(context);

	    SOAPMessage secMsg = context.getSOAPMessage();
    	    //DumpFilter.process(context);
            
            // now persist the message and read-back
            FileOutputStream sentFile = new FileOutputStream("golden.msg");
            secMsg.saveChanges();
            TestUtil.saveMimeHeaders(secMsg, "golden.mh");
            secMsg.writeTo(sentFile);
            sentFile.close();
            
            // now create the message
            SOAPMessage recMsg = TestUtil.constructMessage("golden.mh", "golden.msg");
            // verify
            ProcessingContextImpl context1 = verify(recMsg, null, null);
            
            //Send the response
            context1.setAlgorithmSuite(alg);
            context1.setSecurityPolicy(pol);
            SecurityAnnotator.secureMessage(context1);
            secMsg = context1.getSOAPMessage();
            //DumpFilter.process(context1);
            
            // now persist the message and read-back
            FileOutputStream recvdFile = new FileOutputStream("recvd.msg");
            secMsg.saveChanges();
            TestUtil.saveMimeHeaders(secMsg, "recvd.mh");
            secMsg.writeTo(recvdFile);
            recvdFile.close();
            
            // now create the message
            SOAPMessage clientRecMsg = TestUtil.constructMessage("recvd.mh", "recvd.msg");
            verify(clientRecMsg, null, client);
            
    }
    
   protected static WSSAssertion getWssAssertion(com.sun.xml.ws.security.policy.WSSAssertion asser) {
        com.sun.xml.wss.impl.WSSAssertion assertion = new com.sun.xml.wss.impl.WSSAssertion(
                asser.getRequiredProperties(),
                asser.getType());
        return assertion;
    }

   public static ProcessingContextImpl verify(SOAPMessage msg, byte[] proofKey, Map map) throws Exception {
       //Create processing context and set the soap
       //message to be processed.
       ProcessingContextImpl context = new ProcessingContextImpl(map);
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
       //wssAssertion.addRequiredProperty("RequireSignatureConfirmation");
        wssAssertion = getWssAssertion(wssAssertionws);
        MessagePolicy pol = new MessagePolicy();
        context.setAlgorithmSuite(alg);
        pol.setWSSAssertion(wssAssertion);
                                                                                                           
        context.setSecurityPolicy(pol);
        CallbackHandler handler = new PolicyCallbackHandler1("server");
        SecurityEnvironment env = new DefaultSecurityEnvironmentImpl(handler);
        context.setSecurityEnvironment(env);

        SecurityRecipient.validateMessage(context);

        //System.out.println("Verfied Message");
        //DumpFilter.process(context);

        return context;
   }
   
//   public static void main(String[] args) throws Exception{
//       testSignatureConfirmationTest();
//   }
    
}
