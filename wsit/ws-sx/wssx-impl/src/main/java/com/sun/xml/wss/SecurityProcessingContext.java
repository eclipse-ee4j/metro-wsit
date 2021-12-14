/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss;

import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.StaticPolicyContext;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import jakarta.xml.soap.SOAPMessage;

/**
 * This interface represents a Context that is used by the XWS-Security 2.0 Runtime to
 * apply/verify Security Policies on an Outgoing/Incoming SOAP Message.
 * The context contains among other things
 * <UL>
 *   <LI>The SOAP Message to be operated upon
 *   <LI>The Message direction (incoming or outgoing)
 *   <LI>The security policy to be applied by XWS-Security on the message
 *   <LI>A randomly generated Message-Identifier that can be used for request-response correlation,
 *    by a CallbackHandler, the handles <code>DynamicPolicyCallback</code>
 *   <LI>A list of properties associated with the calling Application Runtime, that can be used to
 *    make Dynamic Policy decisions.
 *   <LI>A concrete implementation of the SecurityEnvironment interface OR a CallbackHandler
 * </UL>
 */
public interface SecurityProcessingContext {
    /**
     * copy operator
     * 
     * @param ctx1 the ProcessingContext to which to copy
     * @param ctx2 the ProcessingContext from which to copy
     */
    void copy(SecurityProcessingContext ctx1, SecurityProcessingContext ctx2);

    /**
     * This method is used for internal purposes
     */
    int getConfigType();

    /**
     * Properties extraneously defined by XWSS runtime - can contain
     * application's runtime context (like JAXRPCContext etc)
     * 
     * 
     * @return Map of extraneous properties
     */
    Map getExtraneousProperties();

    /**
     * 
     * 
     * @return the value for the named extraneous property.
     */
    Object getExtraneousProperty(String name);

    /**
     * 
     * 
     * @return the CallbackHandler set for the context
     */
    CallbackHandler getHandler();

    /**
     * 
     * 
     * @return message identifier for the Message in the context
     */
    String getMessageIdentifier();

    /**
     * 
     * 
     * @return StaticPolicyContext associated with this ProcessingContext, null otherwise
     */
    StaticPolicyContext getPolicyContext();

    /**
     * 
     * 
     * @return the SOAPMessage from the context
     */
    SOAPMessage getSOAPMessage();

    /**
     * 
     * 
     * @return The SecurityEnvironment Handler set for the context
     */
    SecurityEnvironment getSecurityEnvironment();

    /**
     * 
     * 
     * @return SecurityPolicy for this context
     */
    SecurityPolicy getSecurityPolicy();

    /**
     * 
     * 
     * @return message flow direction, true if incoming, false otherwise
     */
    boolean isInboundMessage();

    /**
     * set the message flow direction (to true if inbound, false if outbound)
     * 
     * @param inBound message flow direction
     */
    void isInboundMessage(boolean inBound);

    /**
     * remove the named extraneous property if present
     * 
     * @param name the Extraneous Property to be removed
     */
    void removeExtraneousProperty(String name);

    /**
     * This method is used for internal purposes
     */
    void reset();

    /**
     * This method is used for internal purposes
     */
    void setConfigType(int type);

    /**
     * set the extraneous property into the context
     * Extraneous Properties are properties extraneously defined by XWSS runtime
     * and can contain application's runtime context (like JAXRPCContext etc)
     * 
     * @param name the property name
     * @param value the property value
     */
    void setExtraneousProperty(String name, Object value);

    /**
     * set the CallbackHandler for the context
     * 
     * @param handler The CallbackHandler
     */
    void setHandler(CallbackHandler handler);

    /**
     * Allow for message identifier to be generated externally
     * 
     * @param identifier the Message Identifier value
     */
    void setMessageIdentifier(String identifier);

    /**
     * set the StaticPolicyContext for this ProcessingContext.
     * 
     * @param context StaticPolicyContext for this context
     */
    void setPolicyContext(StaticPolicyContext context);

    /**
     * set the SOAP Message into the ProcessingContext.
     * 
     * @param message SOAPMessage
     * @throws XWSSecurityException if there was an error in setting the SOAPMessage
     */
    void setSOAPMessage(SOAPMessage message) throws XWSSecurityException;

    /**
     * set the SecurityEnvironment Handler for the context
     * 
     * @param handler The SecurityEnvironment Handler
     */
    void setSecurityEnvironment(SecurityEnvironment handler);

    /**
     * set the SecurityPolicy for the context
     * 
     * @param securityPolicy SecurityPolicy
     * @throws XWSSecurityException if the securityPolicy is of invalid type
     */
    void setSecurityPolicy(SecurityPolicy securityPolicy) throws XWSSecurityException;
    
    
}
