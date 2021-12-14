/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.misc;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSProcessor;
import java.io.InputStream;

import jakarta.xml.soap.SOAPMessage;
import javax.security.auth.callback.CallbackHandler;

import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.config.DeclarativeSecurityConfiguration;
import com.sun.xml.wss.impl.config.SecurityConfigurationXmlReader;

import com.sun.xml.wss.impl.SecurityRecipient;
import com.sun.xml.wss.impl.SecurityAnnotator;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.SecurityEnvironment;


public class XWSSProcessor2_0Impl implements XWSSProcessor {

    private DeclarativeSecurityConfiguration declSecConfig = null;
    private CallbackHandler handler = null;
    private SecurityEnvironment secEnv = null;
    
    protected XWSSProcessor2_0Impl(
        InputStream securityConfig, CallbackHandler handler) 
        throws XWSSecurityException {
        try {
            declSecConfig = 
                SecurityConfigurationXmlReader.createDeclarativeConfiguration(securityConfig);
            this.handler = handler;
            secEnv = new DefaultSecurityEnvironmentImpl(this.handler);
        }catch (Exception e) {
            // log
            throw new XWSSecurityException(e);
        }
    }


    protected XWSSProcessor2_0Impl(
        InputStream securityConfig) {
        throw new UnsupportedOperationException("Operation Not Supported");
    }

    @Override
    public SOAPMessage secureOutboundMessage(
        ProcessingContext context) 
        throws XWSSecurityException {

        //resolve the policy first
        MessagePolicy resolvedPolicy = null;

        if (declSecConfig != null) {
            resolvedPolicy = declSecConfig.senderSettings();
        } else {
            //log
            throw new XWSSecurityException("Security Policy Unknown");
        }
                                                                                                      
        if (resolvedPolicy == null) {
            // log that no outbound security specified ?
            return context.getSOAPMessage();
        }
        
        if (context.getHandler() == null  && context.getSecurityEnvironment() == null) {
            context.setSecurityEnvironment(secEnv);
        }

        context.setSecurityPolicy(resolvedPolicy);
 
        try {
            SecurityAnnotator.secureMessage(context);
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }

        try {
            SOAPMessage msg = context.getSOAPMessage();
            //System.out.println("\n Secure Message Start .........\n\n");
            //msg.writeTo(System.out);
            //System.out.println("\n Secure Message End .........\n\n");
            return msg;
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }

    }

    @Override
    public SOAPMessage verifyInboundMessage(
        ProcessingContext context) 
        throws XWSSecurityException {

        MessagePolicy resolvedPolicy = null;

        if (declSecConfig != null) {
            resolvedPolicy = declSecConfig.receiverSettings();
        } else {
            //log
            throw new XWSSecurityException("Security Policy Unknown");
        }

        if (context.getHandler() == null  && context.getSecurityEnvironment() == null) {
            context.setSecurityEnvironment(secEnv);
        }
        
        if (declSecConfig.retainSecurityHeader()) {
            context.retainSecurityHeader(true);
        }
        
        if (declSecConfig.resetMustUnderstand()) {
            context.resetMustUnderstand(true);
        }

        context.setSecurityPolicy(resolvedPolicy);
        try {
            SecurityRecipient.validateMessage(context);
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }

        try {
            SOAPMessage msg = context.getSOAPMessage();
            //System.out.println("\n Verified Message Start .........\n\n");
            //msg.writeTo(System.out);
            //System.out.println("\n Verified Message End .........\n\n");
            return msg;
        } catch (Exception e) {
            throw new XWSSecurityException(e);
        }

    }

    @Override
    public ProcessingContext createProcessingContext(SOAPMessage msg) throws XWSSecurityException {
        ProcessingContext cntxt = new ProcessingContext();
        cntxt.setSOAPMessage(msg);
        return cntxt;
    }
}
