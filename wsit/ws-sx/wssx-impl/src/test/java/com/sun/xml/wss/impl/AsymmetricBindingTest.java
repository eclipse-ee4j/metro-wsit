/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * AsymmetricBindingTest.java
 *
 * Created on April 7, 2006, 6:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl;

import java.util.*;
import java.io.*;

import com.sun.xml.wss.callback.PolicyCallbackHandler1;
import com.sun.xml.wss.*;

import jakarta.xml.soap.*;
import com.sun.xml.wss.impl.policy.mls.*;
import com.sun.xml.wss.impl.filter.*;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.ws.security.impl.*;
import com.sun.xml.wss.core.*;
import com.sun.xml.wss.impl.ProcessingContextImpl;
//import com.sun.xml.ws.security.policy.*;
import com.sun.xml.ws.security.impl.policy.*;
import javax.xml.namespace.QName;
import java.security.SecureRandom;
import com.sun.xml.wss.impl.misc.*;
import javax.security.auth.callback.CallbackHandler;
import com.sun.xml.wss.impl.*;
import javax.xml.crypto.dsig.DigestMethod;
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;
import com.sun.xml.wss.impl.WSSAssertion;
import com.sun.xml.wss.impl.util.PolicyResourceLoader;
import com.sun.xml.wss.impl.util.TestUtil;
import com.sun.xml.wss.impl.AlgorithmSuite;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.AssertionSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class AsymmetricBindingTest extends TestCase{
    
    private static HashMap client = new HashMap();
    private static HashMap server = new HashMap();
    private static AlgorithmSuite alg = null;
    /** Creates a new instance of AsymmetricBindingTest */
    public AsymmetricBindingTest(String testName) throws Exception {
        super(testName);
    }
    
                                                                                                                                                             
    protected void setUp() throws Exception {
    	
    }
                                                                                                                                                             
    protected void tearDown() throws Exception {
    }
                                                                                                                                                             
    public static Test suite() {
        TestSuite suite = new TestSuite(AsymmetricBindingTest.class);                                                                                                 return suite;
    }
    
    public static void testSymmetricBindingTest() throws Exception {
       
	    //algws.setType(AlgorithmSuiteValue.Basic128);
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
            x509bind.setReferenceType(MessageConstants.THUMB_PRINT_TYPE);
            
	    //x509bind.setPolicyToken(tok);
    	    x509bind.setUUID(new String("1000"));

            EncryptionPolicy encryptPolicy = new EncryptionPolicy();
	    EncryptionTarget et = new EncryptionTarget();
    	    et.setType("qname");
            ((EncryptionPolicy.FeatureBinding)encryptPolicy.getFeatureBinding()).
            	    addTargetBinding(st);

	    x509bind = (AuthenticationTokenPolicy.X509CertificateBinding)encryptPolicy.newX509CertificateKeyBinding();
	    x509bind.setReferenceType(MessageConstants.THUMB_PRINT_TYPE);
    	    //x509bind.setPolicyToken(tok);
            x509bind.setUUID(new String("1001"));
        
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
            Policy wssPolicy = new PolicyResourceLoader().loadPolicy("security/policy-binding2.xml");
            Iterator<AssertionSet> i = wssPolicy.iterator();
            if(i.hasNext())
                as = i.next();
            
            for(PolicyAssertion assertion:as){
                if(assertion instanceof com.sun.xml.ws.security.policy.WSSAssertion){
                    wssAssertionws = (com.sun.xml.ws.security.policy.WSSAssertion)assertion;
                }                      
            }
	    //wssAssertion.addRequiredProperty("RequireSignatureConfirmation");
            wssAssertion = new WSSAssertion(wssAssertionws.getRequiredProperties(), "1.0");
                    MessagePolicy pol = new MessagePolicy();
	    pol.append(signaturePolicy);
    	    pol.append(encryptPolicy);
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
            verify(recMsg, null, null);
        
    }

   public static ProcessingContextImpl verify(SOAPMessage msg, byte[] proofKey, Map map) throws Exception {
       //Create processing context and set the soap
       //message to be processed.
       ProcessingContextImpl context = new ProcessingContextImpl(map);
       context.setSOAPMessage(msg);
       
       com.sun.xml.ws.security.policy.WSSAssertion wssAssertionws = null;
       WSSAssertion wssAssertion = null;
       AssertionSet as = null;
       Policy wssPolicy = new PolicyResourceLoader().loadPolicy("security/policy-binding2.xml");
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
//       testSymmetricBindingTest();
//   }
}
