/*
 * Copyright (c) 2013, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * SignC14Test.java
 *
 * Created on August 28, 2013, 4:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.xml.crypto.dsig.DigestMethod;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPHeaderElement;
import jakarta.xml.soap.SOAPMessage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.security.policy.AlgorithmSuiteValue;
import com.sun.xml.wss.SecurityEnvironment;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.callback.PolicyCallbackHandler1;
import com.sun.xml.wss.impl.misc.DefaultSecurityEnvironmentImpl;
import com.sun.xml.wss.impl.policy.PolicyGenerationException;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.SignatureTarget;
import com.sun.xml.wss.impl.policy.mls.SymmetricKeyBinding;
import com.sun.xml.wss.impl.util.PolicyResourceLoader;
import com.sun.xml.wss.impl.util.TestUtil;

/**
 * 
 * @author shihua.guo@oracle.com
 */
public class SignC14Test extends TestCase {

	private static HashMap client = new HashMap();
	private static AlgorithmSuite alg = null;
	private static final String c14Value = "http://www.w3.org/2000/09/xmldsig#base64";

	/**
	 * Creates a new instance of SignSOAPHeadersOnlyTest
	 */
	public SignC14Test(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SignC14Test.class);
		return suite;
	}

	public static void testNoTransforms() throws Exception {
		SOAPMessage secMsg = constructSOAPMsg(false);
		verify(secMsg, null, null);
	}

	public static void testInvalidC14() throws Exception {
		SOAPMessage secMsg = constructSOAPMsg(true);

		SOAPHeader soapHeader = secMsg.getSOAPHeader();
		Node c14Node = findNode("CanonicalizationMethod", soapHeader);
		c14Node.removeChild(c14Node.getFirstChild());
		Node algorithmNode = c14Node.getAttributes().getNamedItem("Algorithm");
		algorithmNode.setNodeValue(c14Value);
		secMsg.saveChanges();

		SOAPMessage recMsg = reConstructSOAPMsg(secMsg);
		// verify
		verify(recMsg, null, null);
	}

	private static SOAPMessage reConstructSOAPMsg(SOAPMessage secMsg)
			throws FileNotFoundException, IOException, SOAPException, Exception {
		// now persist the message and read-back
		FileOutputStream sentFile = new FileOutputStream("golden.msg");
		TestUtil.saveMimeHeaders(secMsg, "golden.mh");
		secMsg.writeTo(sentFile);
		sentFile.close();

		// now create the message
		SOAPMessage recMsg = TestUtil.constructMessage("golden.mh",
				"golden.msg");
		return recMsg;
	}

	private static SOAPMessage constructSOAPMsg(boolean withTransforms)
			throws SOAPException, XWSSecurityException, PolicyException,
			IOException, PolicyGenerationException, Exception {
		// alg.setType(AlgorithmSuiteValue.Basic128);
		alg = new AlgorithmSuite(
				AlgorithmSuiteValue.Basic128.getDigAlgorithm(),
				AlgorithmSuiteValue.Basic128.getEncAlgorithm(),
				AlgorithmSuiteValue.Basic128.getSymKWAlgorithm(),
				AlgorithmSuiteValue.Basic128.getAsymKWAlgorithm());
		SignaturePolicy signaturePolicy = new SignaturePolicy();
		SignatureTarget st = new SignatureTarget();
		st.setType("qname");
		st.isSOAPHeadersOnly(true);
		st.setValue("{http://stockhome.com/quote}");
		st.setDigestAlgorithm(DigestMethod.SHA1);

		if (withTransforms) {
			SignatureTarget.Transform trans = new SignatureTarget.Transform();
			trans.setTransform(MessageConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
			st.addTransform(trans);
		}

		((SignaturePolicy.FeatureBinding) signaturePolicy.getFeatureBinding())
				.addTargetBinding(st);
		((SignaturePolicy.FeatureBinding) signaturePolicy.getFeatureBinding())
				.setCanonicalizationAlgorithm(MessageConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

		SymmetricKeyBinding sigKb = (SymmetricKeyBinding) signaturePolicy
				.newSymmetricKeyBinding();
		AuthenticationTokenPolicy.X509CertificateBinding x509bind = (AuthenticationTokenPolicy.X509CertificateBinding) sigKb
				.newX509CertificateKeyBinding();
		x509bind.setReferenceType(MessageConstants.DIRECT_REFERENCE_TYPE);
		// x509bind.setPolicyToken(tok);
		x509bind.setUUID(new String("1019"));

		// create SOAPMessage
		SOAPMessage msg = MessageFactory.newInstance().createMessage();
		SOAPHeader header = msg.getSOAPHeader();
		SOAPHeaderElement she1 = header.addHeaderElement(SOAPFactory
				.newInstance().createName("StockHeader", "stkheader",
						"http://stockhome.com/quote"));
		she1.addTextNode("Head Text Node1");
		SOAPHeaderElement she2 = header.addHeaderElement(SOAPFactory
				.newInstance().createName("Quote", "quote",
						"http://stockhome.com/quote"));
		she2.addTextNode("Head Text Node2");
		SOAPBody body = msg.getSOAPBody();
		SOAPBodyElement sbe = body
				.addBodyElement(SOAPFactory.newInstance().createName(
						"StockSymbol", "tru", "http://stockhome.com/quote"));
		sbe.addTextNode("QQQ");

		// Create processing context and set the soap message to be processed.
		ProcessingContextImpl context = new ProcessingContextImpl(client);
		context.setSOAPMessage(msg);

		com.sun.xml.ws.security.policy.WSSAssertion wssAssertionws = null;
		WSSAssertion wssAssertion = null;
		AssertionSet as = null;
		Policy wssPolicy = new PolicyResourceLoader()
				.loadPolicy("security/policy-binding2.xml");
		Iterator<AssertionSet> i = wssPolicy.iterator();
		if (i.hasNext())
			as = i.next();

		for (PolicyAssertion assertion : as) {
			if (assertion instanceof com.sun.xml.ws.security.policy.WSSAssertion) {
				wssAssertionws = (com.sun.xml.ws.security.policy.WSSAssertion) assertion;
			}
		}
		wssAssertion = new WSSAssertion(wssAssertionws.getRequiredProperties(),
				"1.0");
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
		return secMsg;
	}

	private static Node findNode(String name, Node node) {
		String nodeName = node.getLocalName();
		if (name.equals(nodeName)) {
			return node;
		} else {
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				Node n = findNode(name, item);
				if (n != null)
					return n;
			}
			return null;
		}
	}

	public static ProcessingContextImpl verify(SOAPMessage msg,
			byte[] proofKey, Map map) throws Exception {
		// Create processing context and set the soap
		// message to be processed.
		ProcessingContextImpl context = new ProcessingContextImpl(map);
		context.setSOAPMessage(msg);

		com.sun.xml.ws.security.policy.WSSAssertion wssAssertionws = null;
		WSSAssertion wssAssertion = null;
		AssertionSet as = null;
		Policy wssPolicy = new PolicyResourceLoader()
				.loadPolicy("security/policy-binding2.xml");
		Iterator<AssertionSet> i = wssPolicy.iterator();
		if (i.hasNext())
			as = i.next();

		for (PolicyAssertion assertion : as) {
			if (assertion instanceof com.sun.xml.ws.security.policy.WSSAssertion) {
				wssAssertionws = (com.sun.xml.ws.security.policy.WSSAssertion) assertion;
			}
		}
//		 wssAssertion.addRequiredProperty("RequireSignatureConfirmation");
		wssAssertion = new WSSAssertion(wssAssertionws.getRequiredProperties(),
				"1.0");
		MessagePolicy pol = new MessagePolicy();
		context.setAlgorithmSuite(alg);
		pol.setWSSAssertion(wssAssertion);

		context.setSecurityPolicy(pol);
		CallbackHandler handler = new PolicyCallbackHandler1("server");
		SecurityEnvironment env = new DefaultSecurityEnvironmentImpl(handler);
		context.setSecurityEnvironment(env);
		try {
			SecurityRecipient.validateMessage(context);
		} catch (Exception ex) {
			String errMsg = ex.getMessage();
			if (!(errMsg.endsWith("Unexpected ds:CanonicalizationMethod, ["
					+ c14Value + "]") || errMsg.endsWith("Illegal CanonicalizationMethod"))) {
				throw ex;
			}

		}
		return context;
	}

	// public static void main(String[] args) throws Exception{
	// testSignSOAPHeadersOnlyTest();
	// }

}
