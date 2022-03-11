/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.filter;

import com.sun.xml.ws.security.opt.impl.tokens.SignatureConfirmation;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.wss.logging.LogDomainConstants;

import com.sun.xml.wss.XWSSecurityException;

import com.sun.xml.wss.core.SecurityHeader;
import com.sun.xml.wss.core.SignatureConfirmationHeaderBlock;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.ws.security.opt.impl.util.NamespaceContextEx;

import com.sun.xml.wss.logging.impl.filter.LogStringsMessages;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPException;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Process SignatureConfirmation: Add SignatureConfirmation or verify
 * received SignatureConfirmation
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class SignatureConfirmationFilter {
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.FILTER_DOMAIN,
            LogDomainConstants.FILTER_DOMAIN_BUNDLE);

    /**
    * If the message is incoming,gets the all SignatureConfirmation security headers
    * and check if each of the values is present in the SignatureConfirmation property of context.Extraneous properties
    * also make sure that all the values in SignatureConfirmation are exhausted.
    *
    * If the message is outgoing checks for the property receivedSignValues in context's extraneous properties and
    * if it is not null, add a SignatureConfirmation Header for each of the values in the property
    * @param context FilterProcessingContext
     */
    public static void process(FilterProcessingContext context) throws XWSSecurityException{
        
        if(!context.isInboundMessage()){
            //The message is outgoing message
            //Check for the property receivedSignValues in context.Extraneous properties
            //If it is not null, add a SignatureConfirmation Header for each of the values in the property
            
            List scList = (ArrayList)context.getExtraneousProperty("receivedSignValues");
            //SignatureConfirmationPolicy policy = (SignatureConfirmationPolicy)context.getSecurityPolicy();
            
            setSignConfirmValues(context, scList);
            
        } else {
            // The message is incoming message
            // Take out all the SignatureConfirmation security headers, and check if each of the values is present
            // in the SignatureConfirmation property of context.Extraneous properties
            // Also make sure that all the values in SignatureConfirmation are exhausted

            //SignatureConfirmationPolicy policy = (SignatureConfirmationPolicy)context.getSecurityPolicy();
            
            SecurityHeader secHeader = context.getSecurableSoapMessage().findSecurityHeader();
            if(secHeader == null){
               log.log(Level.SEVERE, LogStringsMessages.WSS_1428_SIGNATURE_CONFIRMATION_ERROR());
                throw new XWSSecurityException(
                        "Message does not confirm to SignatureConfirmation Policy:" + 
                        "wsse11:SignatureConfirmation element not found in Header");
            }
            
            Object temp = context.getExtraneousProperty("SignatureConfirmation");
            List scList = null;
            if(temp != null && temp instanceof ArrayList)
                scList = (ArrayList)temp;
            if(scList != null){
            
                SignatureConfirmationHeaderBlock signConfirm = null;
                SOAPElement sc = null;
                try{
                    SOAPFactory factory = SOAPFactory.newInstance();
                    Name name = factory.createName(
                            MessageConstants.SIGNATURE_CONFIRMATION_LNAME,
                            MessageConstants.WSSE11_PREFIX,
                            MessageConstants.WSSE11_NS);
                    Iterator i = secHeader.getChildElements(name);
                    if(!i.hasNext()){
                        log.log(Level.SEVERE, LogStringsMessages.WSS_1428_SIGNATURE_CONFIRMATION_ERROR());
                        throw new XWSSecurityException("Message does not confirm to Security Policy:" + 
                                "wss11:SignatureConfirmation Element not found");
                    }
                    while(i.hasNext()){
                        sc = (SOAPElement)i.next();
                        try{
                            signConfirm = new SignatureConfirmationHeaderBlock(sc);
                        } catch( XWSSecurityException xwsse){
                            log.log(Level.SEVERE, LogStringsMessages.WSS_1435_SIGNATURE_CONFIRMATION_VALIDATION_FAILURE(), xwsse);
                            throw SecurableSoapMessage.newSOAPFaultException(
                                MessageConstants.WSSE_INVALID_SECURITY,
                                "Failure in SignatureConfirmation validation\n" + 
                                "Message is: " + xwsse.getMessage(),
                                xwsse );
                        }
                        String signValue = signConfirm.getSignatureValue();

                        //Case when there was no Signature in sent message, the received message should have one
                        //SignatureConfirmation with no Value attribute
                        if(signValue == null){
                            if(i.hasNext() || !scList.isEmpty()){                            
                                log.log(Level.SEVERE, LogStringsMessages.WSS_1435_SIGNATURE_CONFIRMATION_VALIDATION_FAILURE());
                                throw new XWSSecurityException("Failure in SignatureConfirmation Validation");
                            }
                        } else if(scList.contains(signValue)){ // match the Value in received message
                            //with the stored value
                            scList.remove(signValue);
                        }else{
                            log.log(Level.SEVERE, LogStringsMessages.WSS_1435_SIGNATURE_CONFIRMATION_VALIDATION_FAILURE());
                            throw new XWSSecurityException("Mismatch in SignatureConfirmation Element");
                        }
                    }
                
                } catch(SOAPException se){
                    throw new XWSSecurityException(se);
                }
                if(!scList.isEmpty()){
                    log.log(Level.SEVERE, LogStringsMessages.WSS_1435_SIGNATURE_CONFIRMATION_VALIDATION_FAILURE());
                    throw new XWSSecurityException("Failure in SignatureConfirmation");
                }
                context.setExtraneousProperty("SignatureConfirmation", MessageConstants._EMPTY);
                /*if (context.getMode() == FilterProcessingContext.WSDL_POLICY) {
                    SignatureConfirmationPolicy policy = new SignatureConfirmationPolicy();
                    context.getInferredSecurityPolicy().append(policy);
                }*/
            }        
        }
    }
    /**
     * this adds signature confirmation element into the security header
     * @param context FilterProcessingContext
     * @param scList List
     */
    @SuppressWarnings("unchecked")
    private static void setSignConfirmValues(com.sun.xml.wss.impl.FilterProcessingContext context, List scList) 
            throws XWSSecurityException{
        if(scList != null){
            Iterator it = scList.iterator();
            if(context instanceof JAXBFilterProcessingContext){
                JAXBFilterProcessingContext optContext = (JAXBFilterProcessingContext)context;
                com.sun.xml.ws.security.opt.impl.outgoing.SecurityHeader secHeader = 
                        optContext.getSecurityHeader();
                ((NamespaceContextEx)optContext.getNamespaceContext()).addWSS11NS();
                if(!it.hasNext()){
                    // Insert a SignatureConfirmation element with no Value attribute
                    String id = optContext.generateID();
                    SignatureConfirmation scHeader = new SignatureConfirmation(id, optContext.getSOAPVersion());
                    secHeader.add(scHeader);
                    optContext.getSignatureConfirmationIds().add(id);
                }
                
                while(it.hasNext()){
                    byte[] signValue = (byte[])it.next();
                    String id = optContext.generateID();
                    SignatureConfirmation scHeader = new SignatureConfirmation(id, optContext.getSOAPVersion());
                    scHeader.setValue(signValue);
                    secHeader.add(scHeader);
                    optContext.getSignatureConfirmationIds().add(id);
                }
            } else{
                SecurableSoapMessage secureMessage = context.getSecurableSoapMessage();
                SecurityHeader secHeader = secureMessage.findOrCreateSecurityHeader();

                if(!it.hasNext()){
                    // Insert a SignatureConfirmation element with no Value attribute
                    String id = secureMessage.generateId();
                    SignatureConfirmationHeaderBlock signConfirm = new SignatureConfirmationHeaderBlock(id);
                    secHeader.insertHeaderBlock(signConfirm);
                    context.getSignatureConfirmationIds().add(id);
                }

                while(it.hasNext()){

                    String signValue = (String)it.next();     
                    String id = secureMessage.generateId();
                    SignatureConfirmationHeaderBlock signConfirm = new SignatureConfirmationHeaderBlock(id);
                    signConfirm.setSignatureValue(signValue);
                    secHeader.insertHeaderBlock(signConfirm);
                    context.getSignatureConfirmationIds().add(id);
                }         
            }
        }
    }
    
}
