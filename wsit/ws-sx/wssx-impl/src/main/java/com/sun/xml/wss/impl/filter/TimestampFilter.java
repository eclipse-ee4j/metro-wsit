/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.filter;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.soap.Name;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import com.sun.xml.wss.core.Timestamp;
import com.sun.xml.wss.core.SecurityHeader;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.impl.FilterProcessingContext;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.impl.policy.mls.TimestampPolicy;
import com.sun.xml.wss.impl.callback.DynamicPolicyCallback;
import com.sun.xml.wss.impl.configuration.DynamicApplicationContext;
import com.sun.xml.wss.impl.misc.SecurityHeaderBlockImpl;
import com.sun.xml.wss.impl.HarnessUtil;
import com.sun.xml.wss.logging.impl.filter.LogStringsMessages;

/**
 * Processes export and import of wsu:Timestamp
 *
 * Message ANNOTATION is performed as follows:
 *
 *   if (policy resolution should happen)
 *       // make-DPC flag turned on
 *       resolve TimestampPolicy
 *   write wsu:Timestamp to header
 *
 * Message VALIDATION is performed as follows:
 *
 *   if (ADHOC processing mode)
 *       if (policy resolution should happen)
 *           // make-DPC flag turned on
 *           resolve TimestampPolicy
 *       locate wsu:Timestamp element in the header
 *       throw Exception if more than one found
 *       validate wsu:Timestamp (delegate to SecurityEnvironment Implementation)
 *   else
 *       import wsu:Timestamp element
 *       if (POSTHOC processing mode)
 *           construct Timestamp policy and set it on FPC
 *       else
 *       if (DEFAULT processing mode)
 *           validate wsu:Timestamp
 */
public class TimestampFilter {

    protected static final Logger log = Logger.getLogger(
        LogDomainConstants.FILTER_DOMAIN,
        LogDomainConstants.FILTER_DOMAIN_BUNDLE);

    /**
     * processes the time stamps and verifies whether the message is conform to time stamp policies or not
     * @param context FilterProcessingContext     *
     */
    public static void process (FilterProcessingContext context) throws XWSSecurityException {

         if (!context.isInboundMessage ()) {

             //hack to prevent multiple timestamp exports
             //TODO: revisit
             if (context.timestampExported())
                 return;

             TimestampPolicy policy = (TimestampPolicy) context.getSecurityPolicy();
             long timeout = policy.getTimeout();
             String created = policy.getCreationTime();
             String id = policy.getUUID();
             if (context.getTimestampTimeout() > 0) {
                 timeout = context.getTimestampTimeout();
             }
             if (context.makeDynamicPolicyCallback()) {
                TimestampPolicy policyClone = (TimestampPolicy) policy.clone();
        try {
                      DynamicApplicationContext dynamicContext =
                          new DynamicApplicationContext (context.getPolicyContext ());

                      dynamicContext.setMessageIdentifier (context.getMessageIdentifier ());
                      dynamicContext.inBoundMessage (false);
                      // TODO: copy runtime properties into callback context
                      DynamicPolicyCallback callback =
                          new DynamicPolicyCallback (policyClone, dynamicContext);
                      ProcessingContext.copy (dynamicContext.getRuntimeProperties(), context.getExtraneousProperties());
                      HarnessUtil.makeDynamicPolicyCallback(callback,
                          context.getSecurityEnvironment().getCallbackHandler());

                 } catch (Exception e) {
                    log.log(Level.SEVERE, "Message does not conform to time stamp policy", e);
                    throw new XWSSecurityException (e);
                 }

                timeout   = policyClone.getTimeout();
                created = policyClone.getCreationTime();
             }

             setTimestamp(context, timeout, created, id);

             //hack to prevent multiple timestamp exports
             //TODO: revisit
             context.timestampExported(true);

         } else {

             // Processing inbound messages
             Timestamp timestamp = null;

             if (context.getMode() == FilterProcessingContext.ADHOC) {

                 if (context.makeDynamicPolicyCallback()) {
                     TimestampPolicy policyClone = (TimestampPolicy)
                         ((TimestampPolicy)context.getSecurityPolicy()).clone();
             try {
                         DynamicApplicationContext dynamicContext =
                             new DynamicApplicationContext (context.getPolicyContext ());

                         dynamicContext.setMessageIdentifier (context.getMessageIdentifier ());
                         dynamicContext.inBoundMessage (true);
                         DynamicPolicyCallback callback =
                             new DynamicPolicyCallback (policyClone, dynamicContext);
                         ProcessingContext.copy (dynamicContext.getRuntimeProperties(), context.getExtraneousProperties());
                         HarnessUtil.makeDynamicPolicyCallback(callback,
                             context.getSecurityEnvironment().getCallbackHandler());

                     } catch (Exception e) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_1436_MESSAGE_DOESNOT_CONFORM_TIMESTAMP_POLICY(), e);
                         throw new XWSSecurityException (e);
                     }
                     context.setSecurityPolicy(policyClone);
                 }

                 TimestampPolicy policy = (TimestampPolicy) context.getSecurityPolicy();
                 long maxClockSkew = policy.getMaxClockSkew ();
                 long timeStampFreshness = policy.getTimestampFreshness ();

                 SecurityHeader secHeader = context.getSecurableSoapMessage().findSecurityHeader();
                 if (secHeader == null) {
                 log.log(Level.SEVERE, com.sun.xml.wss.logging.LogStringsMessages.WSS_0276_INVALID_POLICY_NO_TIMESTAMP_SEC_HEADER());
                         throw new XWSSecurityException(
                        "Message does not conform to Timestamp policy: " +
                    "wsu:Timestamp element not found in header");
                 }

                 SOAPElement ts = null;

                 try {
             SOAPFactory factory = SOAPFactory.newInstance();
             Name name = factory.createName(
                         MessageConstants.TIMESTAMP_LNAME,
                 MessageConstants.WSU_PREFIX,
             MessageConstants.WSU_NS);
             Iterator i = secHeader.getChildElements (name);

             if (i.hasNext()) {
                 ts = (SOAPElement) i.next();
             if (i.hasNext()) {
                             log.log(Level.SEVERE, com.sun.xml.wss.logging.LogStringsMessages.BSP_3227_SINGLE_TIMESTAMP());
                 throw new XWSSecurityException("More than one wsu:Timestamp element in the header");
             }
             } else {
              log.log(Level.SEVERE, com.sun.xml.wss.logging.LogStringsMessages.WSS_0276_INVALID_POLICY_NO_TIMESTAMP_SEC_HEADER());
             throw new XWSSecurityException(
                             "Message does not conform to Timestamp policy: " +
                     "wsu:Timestamp element not found in header");
             }
         } catch (SOAPException se) {
             // log
             throw new XWSSecurityRuntimeException (se);
         }

         try {
             timestamp = new Timestamp (ts);
                 } catch (XWSSecurityException xwsse) {
                     log.log(Level.SEVERE, LogStringsMessages.WSS_1429_ERROR_TIMESTAMP_INTERNALIZATION(), xwsse);
                    throw SecurableSoapMessage.newSOAPFaultException(
                         MessageConstants.WSSE_INVALID_SECURITY,
                         "Failure in Timestamp internalization.\n" +
                         "Message is: " + xwsse.getMessage(),
                         xwsse);
                 }

                 try {
                     context.getSecurityEnvironment().validateTimestamp(
                         context.getExtraneousProperties(), timestamp, maxClockSkew, timeStampFreshness);
                 } catch (XWSSecurityException xwsse) {
                     log.log(Level.SEVERE, LogStringsMessages.WSS_1430_ERROR_TIMESTAMP_VALIDATION(), xwsse);
                    throw SecurableSoapMessage.newSOAPFaultException(
                         MessageConstants.WSSE_INVALID_SECURITY,
                         "Failure in Timestamp validation.\n" +
                         "Message is: " + xwsse.getMessage(),
                         xwsse);
                 }

                 if (MessageConstants.debug) {
                     log.log(Level.FINEST, "Validated TIMESTAMP.....");
                 }
             } else {

                 if (context.getMode() == FilterProcessingContext.POSTHOC) {
                     throw new XWSSecurityException("Internal Error: Called TimestampFilter in POSTHOC Mode");
                 }

                 if (context.getMode() == FilterProcessingContext.WSDL_POLICY) {
                     TimestampPolicy ts =  new TimestampPolicy();
                     context.getInferredSecurityPolicy().append(ts);
                 }

                 SecurityHeader secHeader = context.getSecurableSoapMessage().findSecurityHeader();
                 try {
                     timestamp = (Timestamp)
                         SecurityHeaderBlockImpl.fromSoapElement(
                             secHeader.getCurrentHeaderElement(),Timestamp.class);
                 } catch (XWSSecurityException xwsse) {
                     log.log(Level.SEVERE, LogStringsMessages.WSS_1429_ERROR_TIMESTAMP_INTERNALIZATION(), xwsse);
                     throw SecurableSoapMessage.newSOAPFaultException(
                           MessageConstants.WSSE_INVALID_SECURITY,
                           "Failure in Timestamp internalization.\n" +
                           "Message is: " + xwsse.getMessage(),
                           xwsse);
                 }
             // FilterProcessingContext.DEFAULT
                 try {
                     context.getSecurityEnvironment().validateTimestamp (
                          context.getExtraneousProperties(),
                          timestamp,
                          Timestamp.MAX_CLOCK_SKEW,
                          Timestamp.TIMESTAMP_FRESHNESS_LIMIT);
                 } catch (XWSSecurityException xwsse) {
                     log.log(Level.SEVERE, LogStringsMessages.WSS_1430_ERROR_TIMESTAMP_VALIDATION(), xwsse);
                    throw SecurableSoapMessage.newSOAPFaultException(
                         MessageConstants.WSSE_INVALID_SECURITY,
                         "Failure in Timestamp validation.\n" +
                         "Message is: " + xwsse.getMessage(),
                         xwsse);
                 }

                 if (MessageConstants.debug) {
                     log.log(Level.FINEST, "Validated TIMESTAMP.....");
                 }
            }
         }
     }
    /**
     * sets the timestamp in the security header
     * sets the creation time and expiration times in the time stamp
     * @param context FilterProcessingContext
     * @param timeout Long
     * @param created String
     * @param id String
     */
    private static void setTimestamp(FilterProcessingContext context,
            Long timeout, String created, String id) throws XWSSecurityException{
        if(context instanceof JAXBFilterProcessingContext){
            JAXBFilterProcessingContext optContext = (JAXBFilterProcessingContext)context;
            com.sun.xml.ws.security.opt.impl.outgoing.SecurityHeader secHeader =
                    optContext.getSecurityHeader();
            com.sun.xml.ws.security.opt.impl.tokens.Timestamp wsuTimestamp =
                    new com.sun.xml.ws.security.opt.impl.tokens.Timestamp(optContext.getSOAPVersion());

            wsuTimestamp.setTimeout(timeout);
             if (id != null) {
                 wsuTimestamp.setId(id);
             }

            //sets the creation and expiration time
            wsuTimestamp.createDateTime();

            secHeader.add(wsuTimestamp);

        } else{
             SecurityHeader secHeader = context.getSecurableSoapMessage().findOrCreateSecurityHeader();

             Timestamp wsuTimestamp = new Timestamp();
             if ("".equals(created)) {
                 wsuTimestamp.setCreated(null);
             }else {
                 wsuTimestamp.setCreated(created);
             }

             wsuTimestamp.setTimeout(timeout);
             if (id != null) {
                 wsuTimestamp.setId(id);
             }
             secHeader.insertHeaderBlock(wsuTimestamp);
        }
    }
}
