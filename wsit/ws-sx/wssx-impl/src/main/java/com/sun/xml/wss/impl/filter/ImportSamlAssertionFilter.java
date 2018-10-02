/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.filter;

import com.sun.xml.wss.impl.misc.DefaultSecurityEnvironmentImpl;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.AssertionUtil;

import java.util.Iterator;
import java.util.HashMap;
import java.util.logging.Level;
import javax.xml.soap.SOAPElement;
import org.w3c.dom.Element;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.core.SecurityHeader;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;

import java.util.logging.Logger;
import org.w3c.dom.NodeList;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.logging.impl.filter.LogStringsMessages;
import org.w3c.dom.Text;

/**
 * @author Kumar Jayanti
 */
public class ImportSamlAssertionFilter{

    protected static final Logger log =  Logger.getLogger( LogDomainConstants.FILTER_DOMAIN,LogDomainConstants.FILTER_DOMAIN_BUNDLE);

    /**
     * reads the saml element from the soap message and creates a SAML assertion
     * object from this saml element
     * @param context FilterProcessingContext
     * @throws com.sun.xml.wss.XWSSecurityException
     */
    @SuppressWarnings("unchecked")
    public static void process(FilterProcessingContext context)	throws XWSSecurityException {
  
        SecurableSoapMessage secureMessage = context.getSecurableSoapMessage();
        SecurityHeader wsseSecurity = secureMessage.findSecurityHeader();
        Assertion samlAssertion = null;
        SOAPElement samlElement = null;

        if( context.getMode() == FilterProcessingContext.ADHOC || 
            context.getMode() == FilterProcessingContext.DEFAULT || 
            context.getMode() == FilterProcessingContext.WSDL_POLICY) {
            
            NodeList nl = null;            
            Element elem = null;
            
            for (Iterator iter = wsseSecurity.getChildElements(); iter.hasNext();) {
                Object obj = iter.next();
                /*if(obj instanceof Text){
                continue;
                }*/
                if(obj instanceof Text){
                continue;
                }
                if (obj instanceof Element) {
                    elem = (Element) obj;
                    if (elem.getAttributeNode("ID") != null) {
                        nl = wsseSecurity.getElementsByTagNameNS(
                                MessageConstants.SAML_v2_0_NS, MessageConstants.SAML_ASSERTION_LNAME);
                        break;
                    } else if (elem.getAttributeNode("AssertionID") != null) {
                        nl = wsseSecurity.getElementsByTagNameNS(
                                MessageConstants.SAML_v1_0_NS, MessageConstants.SAML_ASSERTION_LNAME);
                        break;
                    }
                }
            }                   
//            if (wsseSecurity.getChildElements()Attributes().equals("AssertionID")){
//                nl = wsseSecurity.getElementsByTagNameNS(
//                        MessageConstants.SAML_v1_0_NS, MessageConstants.SAML_ASSERTION_LNAME);
//            }else{
//                nl = wsseSecurity.getElementsByTagNameNS(
//                        MessageConstants.SAML_v2_0_NS, MessageConstants.SAML_ASSERTION_LNAME);
//            }
            
            if (nl == null){
                throw new XWSSecurityException("SAMLAssertion is null");
            }
            int nodeListLength = nl.getLength();              
            int countSamlInsideAdviceElement = 0;
            for(int i =0; i<nodeListLength; i++){
                if(nl.item(i).getParentNode().getLocalName().equals("Advice")){                                                            
                    countSamlInsideAdviceElement++;
                }               
            }                        
            
            //for now we dont allow multiple saml assertions
            if (nodeListLength == 0) {
               log.log(Level.SEVERE, LogStringsMessages.WSS_1431_NO_SAML_FOUND());
                throw new XWSSecurityException(
                "No SAML Assertion found, Reciever requirement not met");
            //}else if ((nodeListLength - countSamlInsideAdviceElement) > 1) {
            //    throw new XWSSecurityException(
            //        "More than one SAML Assertion found, Reciever requirement not met");
            }else{
                samlElement = (SOAPElement)nl.item(0);
                try {
                    samlAssertion = AssertionUtil.fromElement(samlElement);
                } catch(Exception e) {
                    log.log(Level.SEVERE,LogStringsMessages.WSS_1432_SAML_IMPORT_EXCEPTION(),e);
                    throw SecurableSoapMessage.newSOAPFaultException(
                            MessageConstants.WSSE_INVALID_SECURITY,
                            "Exception while importing SAML Token",
                            e);
                }
            }

            if (context.getMode() == FilterProcessingContext.ADHOC) {

                //try to validate against the policy
                AuthenticationTokenPolicy policy = (AuthenticationTokenPolicy)context.getSecurityPolicy();
                AuthenticationTokenPolicy.SAMLAssertionBinding samlPolicy =
                    (AuthenticationTokenPolicy.SAMLAssertionBinding)policy.getFeatureBinding();

                //ensure the authorityId if specified matches
                if (!"".equals(samlPolicy.getAuthorityIdentifier())) {
                    if (!samlPolicy.getAuthorityIdentifier().equals(samlAssertion.getSamlIssuer())) {
                        //log here
                        XWSSecurityException xwse = new XWSSecurityException("Invalid Assertion Issuer, expected "  + 
                            samlPolicy.getAuthorityIdentifier() + ", found " + (samlAssertion.getSamlIssuer()));
                        log.log(Level.SEVERE, LogStringsMessages.WSS_1434_SAML_ISSUER_VALIDATION_FAILED(), xwse);
                        throw SecurableSoapMessage.newSOAPFaultException(
                            MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                            "Received SAML Assertion has invalid Issuer",
                                xwse);
                    
                    }
                }
            }

        }else {
             if (context.getMode() == FilterProcessingContext.POSTHOC) {
                 throw new XWSSecurityException(
                     "Internal Error: Called ImportSAMLAssertionFilter in POSTHOC Mode");
             }

             if (context.getMode() == FilterProcessingContext.WSDL_POLICY) {
                 AuthenticationTokenPolicy.SAMLAssertionBinding bind =
                     new AuthenticationTokenPolicy.SAMLAssertionBinding();
                 ((MessagePolicy)context.getInferredSecurityPolicy()).append(bind);
             }
                                                                                                  
            try{
                samlAssertion = AssertionUtil.fromElement(wsseSecurity.getCurrentHeaderElement());
            } catch(Exception ex) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_1432_SAML_IMPORT_EXCEPTION(), ex);
                throw SecurableSoapMessage.newSOAPFaultException(
                MessageConstants.WSSE_INVALID_SECURITY_TOKEN,
                "Exception while importing SAML Assertion",
                ex);
            }
        }

        HashMap tokenCache = context.getTokenCache();
        //assuming unique IDs
        tokenCache.put(samlAssertion.getAssertionID(), samlAssertion);

        //if (!samlAssertion.isTimeValid()) {
        //    log.log(Level.SEVERE, "WSS0417.saml.timestamp.invalid");
        //    throw SecurableSoapMessage.newSOAPFaultException(
        //        MessageConstants.WSSE_FAILED_AUTHENTICATION,
        //        "SAML Condition (notBefore, notOnOrAfter) Validation failed",
        //            new Exception(
        //                "SAML Condition (notBefore, notOnOrAfter) Validation failed"));
        //}

        //ensure it is an SV assertion
        /*String confirmationMethod = AssertionUtil.getConfirmationMethod(samlElement);
        if (!MessageConstants.SAML_SENDER_VOUCHES.equals(confirmationMethod)) {
            XWSSecurityException xwse = new XWSSecurityException("Invalid ConfirmationMethod "  + confirmationMethod);
            throw SecurableSoapMessage.newSOAPFaultException(
                        MessageConstants.WSSE_INVALID_SECURITY,
                        "Invalid ConfirmationMethod",
                        xwse);
        }*/
        
        context.getSecurityEnvironment().validateSAMLAssertion(context.getExtraneousProperties(), samlElement);
        
        context.getSecurityEnvironment().updateOtherPartySubject(
                DefaultSecurityEnvironmentImpl.getSubject(context), samlAssertion);

        AuthenticationTokenPolicy.SAMLAssertionBinding samlPolicy = new AuthenticationTokenPolicy.SAMLAssertionBinding();
        samlPolicy.setUUID(samlAssertion.getAssertionID());
        context.getInferredSecurityPolicy().append(samlPolicy); 
    }

}
