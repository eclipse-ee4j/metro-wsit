/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SecurityAnnotator.java,v 1.2 2010-10-21 15:37:15 snajper Exp $
 */

package com.sun.xml.wss.impl;

import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.StaticPolicyContext;
import com.sun.xml.wss.impl.callback.DynamicPolicyCallback;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.impl.configuration.DynamicApplicationContext;
import com.sun.xml.wss.impl.filter.DumpFilter;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.*;
import com.sun.xml.wss.logging.LogStringsMessages;

/**
 * This class exports a static Security Service for Securing an Outbound SOAPMessage.
 * The policy to be applied for Securing the Message and the SOAPMessage itself are 
 * supplied in an instance of a com.sun.xml.wss.ProcessingContext
 * @see ProcessingContext
 */
public class SecurityAnnotator {

	private static Logger log = Logger.getLogger(
	    LogDomainConstants.WSS_API_DOMAIN,
	    LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
     * Secure an Outbound SOAP Message. 
     * <P>
     * Calling code should create a com.sun.xml.wss.ProcessingContext object with
     * runtime properties. Specifically, it should set SecurityPolicy, application
     * CallbackHandler Or a SecurityEnvironment and static security policy context. 
     * The SecurityPolicy instance can be of the following types:
     * <UL>
     *  <LI> A concrete WSSPolicy
     *  <LI> A MessagePolicy
     *  <LI> A DynamicSecurityPolicy
     * </UL>
     *
     * A DynamicSecurityPolicy can inturn resolve to the following:
     * <UL>
     *  <LI> A concrete WSSPolicy 
     *  <LI> A MessagePolicy
     * </UL>
     *
     * @param context an instance of com.sun.xml.wss.ProcessingContext
     * @throws com.sun.xml.wss.XWSSecurityException if there was an error in securing the Outbound SOAPMessage
     */
    public static void secureMessage(ProcessingContext context)
    throws XWSSecurityException {

        HarnessUtil.validateContext (context);

        SecurityPolicy policy = context.getSecurityPolicy ();
        SecurityEnvironment handler = context.getSecurityEnvironment ();
        StaticPolicyContext staticContext = context.getPolicyContext ();

        FilterProcessingContext fpContext = setFilterProcessingContext(context);
        
        fpContext.isInboundMessage (false);
         if (fpContext.resetMustUnderstand()) {
            fpContext.getSecurableSoapMessage().setDoNotSetMU(true);
        }
   
        if (PolicyTypeUtil.messagePolicy(policy) &&
                (((MessagePolicy)policy).enableDynamicPolicy() && 
                ((MessagePolicy)policy).size() == 0)) {
            policy = new com.sun.xml.wss.impl.policy.mls.DynamicSecurityPolicy();
        }
        
        if (PolicyTypeUtil.dynamicSecurityPolicy(policy)) {

            // create dynamic callback context
            DynamicApplicationContext dynamicContext = new DynamicApplicationContext (staticContext);
            dynamicContext.setMessageIdentifier (context.getMessageIdentifier ());
            dynamicContext.inBoundMessage (false);
            ProcessingContext.copy (dynamicContext.getRuntimeProperties(), context.getExtraneousProperties());

            // make dynamic policy callback
            DynamicPolicyCallback dpCallback = new DynamicPolicyCallback (policy, dynamicContext);
            try {
               HarnessUtil.makeDynamicPolicyCallback(dpCallback,
                          handler.getCallbackHandler());

            } catch (Exception e) {
               log.log(Level.SEVERE, LogStringsMessages.WSS_0237_FAILED_DYNAMIC_POLICY_CALLBACK(), e);
               throw new XWSSecurityException (e);
            }

            SecurityPolicy result = dpCallback.getSecurityPolicy ();
            fpContext.setSecurityPolicy (result);

            if (PolicyTypeUtil.messagePolicy(result)) {
                processMessagePolicy (fpContext);
            } else
            if (result instanceof WSSPolicy) {
                HarnessUtil.processWSSPolicy (fpContext);
            } else if ( result != null ) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_0260_INVALID_DSP());
                throw new XWSSecurityException ("Invalid dynamic security policy returned by callback handler");
            }

        } else if (PolicyTypeUtil.messagePolicy(policy)) {
            fpContext.enableDynamicPolicyCallback(((MessagePolicy)policy).enableDynamicPolicy());
            processMessagePolicy(fpContext);
        } else if (policy instanceof WSSPolicy) {
            HarnessUtil.processWSSPolicy (fpContext);
        } else {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0251_INVALID_SECURITY_POLICY_INSTANCE());
            throw new XWSSecurityException ("SecurityPolicy instance should be of type: " +
                                            "WSSPolicy OR MessagePolicy OR DynamicSecurityPolicy");
        }
    }

    /*
     * @param fpContext com.sun.xml.wss.FilterProcessingContext
     *
     * @throws com.sun.xml.wss.XWSSecurityException
     */
    private static void processMessagePolicy (FilterProcessingContext fpContext)
    throws XWSSecurityException {

        MessagePolicy policy = (MessagePolicy) fpContext.getSecurityPolicy ();

        if(policy.enableWSS11Policy()){
            // set a property in context to determine if its WSS11
            fpContext.setExtraneousProperty("EnableWSS11PolicySender","true");
        }

        // DO it always as policy not available in optimized path
        //if (policy.enableSignatureConfirmation()) {
            //For SignatureConfirmation
            //Set a list in extraneous property which will store all the outgoing SignatureValues
            //If there was no Signature in outgoing message this list will be empty
            List scList = new ArrayList();
            fpContext.setExtraneousProperty("SignatureConfirmation", scList);
        //}
        
        Iterator i = policy.iterator ();

        while (i.hasNext ()) {
            SecurityPolicy sPolicy = (SecurityPolicy) i.next();
            fpContext.setSecurityPolicy (sPolicy);
            HarnessUtil.processDeep (fpContext);
        }

        if(!(fpContext instanceof JAXBFilterProcessingContext)){
            if (policy.dumpMessages())
                DumpFilter.process(fpContext);
        }
    }

    /*
     * @param context com.sun.xml.wss.Processing Context
     */
    public static void handleFault (ProcessingContext context) {
        /*
          TODO:
         */
    }
    
    public static FilterProcessingContext setFilterProcessingContext(ProcessingContext context) 
            throws XWSSecurityException{
        if(context instanceof JAXBFilterProcessingContext)
            return (JAXBFilterProcessingContext)context;
        return new FilterProcessingContext (context);
    }
}
