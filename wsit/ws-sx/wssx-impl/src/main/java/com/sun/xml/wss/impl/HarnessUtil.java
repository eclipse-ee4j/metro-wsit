/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: HarnessUtil.java,v 1.2 2010-10-21 15:37:15 snajper Exp $
 */

package com.sun.xml.wss.impl;

import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.SecurityEnvironment;
import com.sun.xml.wss.TokenPolicyMetaData;
import com.sun.xml.wss.XWSSecurityException;
import org.w3c.dom.Node;

import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.wss.logging.LogDomainConstants;

import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.impl.policy.StaticPolicyContext;
import com.sun.xml.wss.impl.policy.mls.WSSPolicy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;

import com.sun.xml.wss.impl.filter.TimestampFilter;
import com.sun.xml.wss.impl.filter.SignatureFilter;
import com.sun.xml.wss.impl.filter.EncryptionFilter;
import com.sun.xml.wss.impl.filter.AuthenticationTokenFilter;
import com.sun.xml.wss.impl.filter.SignatureConfirmationFilter;

import com.sun.xml.ws.api.message.Message;

import com.sun.xml.wss.impl.callback.DynamicPolicyCallback;
import com.sun.xml.wss.logging.LogStringsMessages;
import org.w3c.dom.NodeList;

public final class HarnessUtil {

    private static Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    private HarnessUtil() {}

    /**
     * @param fpContext com.sun.xml.wss.FilterProcessingContext
     *
     */
    @SuppressWarnings("unchecked")
    static void processWSSPolicy(FilterProcessingContext fpContext)
    throws XWSSecurityException {

        WSSPolicy policy = (WSSPolicy) fpContext.getSecurityPolicy();
        if (PolicyTypeUtil.signaturePolicy(policy)) {
            SignatureFilter.process(fpContext);
        } else if (PolicyTypeUtil.encryptionPolicy(policy)) {
            EncryptionFilter.process(fpContext);
        } else if (PolicyTypeUtil.timestampPolicy(policy)) {
            TimestampFilter.process(fpContext);
        } else if(PolicyTypeUtil.signatureConfirmationPolicy(policy)) {
            SignatureConfirmationFilter.process(fpContext);
        }
        else if (PolicyTypeUtil.authenticationTokenPolicy(policy)) {
            fpContext.getExtraneousProperties().put(TokenPolicyMetaData.TOKEN_POLICY, policy);
            WSSPolicy authPolicy = (WSSPolicy)policy.getFeatureBinding();
            if(PolicyTypeUtil.usernameTokenPolicy(authPolicy)) {
                AuthenticationTokenPolicy.UsernameTokenBinding utb=
                        (AuthenticationTokenPolicy.UsernameTokenBinding)authPolicy;
                if (!utb.isEndorsing()) {
                    AuthenticationTokenFilter.processUserNameToken(fpContext);
                }
            } else if (PolicyTypeUtil.samlTokenPolicy(authPolicy)) {
                AuthenticationTokenPolicy.SAMLAssertionBinding samlPolicy =
                    (AuthenticationTokenPolicy.SAMLAssertionBinding)authPolicy;
                try{
                    if (samlPolicy.getAssertionType() ==
                        AuthenticationTokenPolicy.SAMLAssertionBinding.SV_ASSERTION) {
                        AuthenticationTokenFilter.processSamlToken(fpContext);
                    }
                }catch(Exception ex){
                   log.log(Level.WARNING, ex.getMessage());
                }
            }else if (PolicyTypeUtil.x509CertificateBinding(authPolicy)) {
                AuthenticationTokenFilter.processX509Token(fpContext);
            }else if (PolicyTypeUtil.issuedTokenKeyBinding(authPolicy)) {
                AuthenticationTokenFilter.processIssuedToken(fpContext);
            }else if (PolicyTypeUtil.keyValueTokenBinding(authPolicy)) {
                AuthenticationTokenFilter.processRSAToken(fpContext);
            }
        }else if(PolicyTypeUtil.isMandatoryTargetPolicy(policy)) {
        } else {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0801_ILLEGAL_SECURITYPOLICY());
            throw new XWSSecurityException("Invalid WSSPolicy Type");
        }
    }

    /*
      @param fpContext com.sun.xml.wss.FilterProcessingContext
     *
     * @throws com.sun.xml.wss.XWSSecurityException
     */
    /*static void processBooleanComposer(FilterProcessingContext fpContext)
    throws XWSSecurityException {

        BooleanComposer bComposer = (BooleanComposer) fpContext.getSecurityPolicy();

        int type = bComposer.getComposerType();

        SecurityPolicy policyA = (SecurityPolicy) bComposer.getPolicyA();
        SecurityPolicy policyB = (SecurityPolicy) bComposer.getPolicyB();

        if (type == BooleanComposer.OR) {
            fpContext.setSecurityPolicy(policyA);
            processDeep(fpContext);

            if (fpContext.isPrimaryPolicyViolation()) {
                fpContext.setSecurityPolicy(policyB);
                processDeep(fpContext);

                if (fpContext.isPrimaryPolicyViolation()) {
                    String message =
                            "Policy: " + policyA.getClass().getName() + " OR " +
                            "Policy: " + policyB.getClass().getName() +
                            " not satisfied";
                    log.log(Level.SEVERE,
                            "WSS0802.securitypolicy.notsatisfied",
                            new Object[] {message});

                            throw new XWSSecurityException(message, fpContext.getPVE());
                }
            }
        } else
            if (type == BooleanComposer.AND_STRICT) {
            fpContext.setSecurityPolicy(policyA);
            processDeep(fpContext);

            if (fpContext.isPrimaryPolicyViolation()) {
                String message =
                        "Policy: " + policyA.getClass().getName() + " AND_STRICT " +
                        "Policy: " + policyB.getClass().getName() +
                        " not satisfied";
                log.log(Level.SEVERE,
                        "WSS0802.securitypolicy.notsatisfied",
                        new Object[] {message});

                        throw new XWSSecurityException(message, fpContext.getPVE());
            }

            fpContext.setSecurityPolicy(policyB);
            processDeep(fpContext);

            if (fpContext.isPrimaryPolicyViolation()) {
                String message =
                        "Policy: " + policyA.getClass().getName() + " AND_STRICT " +
                        "Policy: " + policyB.getClass().getName() +
                        " not satisfied";
                log.log(Level.SEVERE,
                        "WSS0802.securitypolicy.notsatisfied",
                        new Object[] {message});
                        throw new XWSSecurityException(message, fpContext.getPVE());
            }
            } else
                if (type == BooleanComposer.AND_FLEXIBLE) {
            fpContext.setSecurityPolicy(policyA);
            processDeep(fpContext);

            if (fpContext.isPrimaryPolicyViolation()) {
                fpContext.setSecurityPolicy(policyB);
                processDeep(fpContext);

                if (fpContext.isPrimaryPolicyViolation()) {
                    String message =
                            "Policy: " + policyA.getClass().getName() +
                            " AND_FLEXIBLE " +
                            "Policy: " + policyB.getClass().getName() +
                            " not satisfied";
                    log.log(Level.SEVERE,
                            "WSS0802.securitypolicy.notsatisfied",
                            new Object[] {message});
                            throw new XWSSecurityException(
                                    message, fpContext.getPVE());
                }

                fpContext.setSecurityPolicy(policyA);
                processDeep(fpContext);

                if (fpContext.isPrimaryPolicyViolation()) {
                    String message =
                            "Policy: " + policyA.getClass().getName() +
                            " AND_FLEXIBLE " +
                            "Policy: " + policyB.getClass().getName() +
                            " not satisfied";
                    log.log(Level.SEVERE,
                            "WSS0802.securitypolicy.notsatisfied",
                            new Object[] {message});

                            throw new XWSSecurityException(
                                    message, fpContext.getPVE());
                }

                return;
            }

            fpContext.setSecurityPolicy(policyB);
            processDeep(fpContext);

            if (fpContext.isPrimaryPolicyViolation()) {
                String message =
                        "Policy: " + policyA.getClass().getName() +
                        " AND_FLEXIBLE " +
                        "Policy: " + policyB.getClass().getName() +
                        " not satisfied";
                log.log(Level.SEVERE,
                        "WSS0802.securitypolicy.notsatisfied",
                        new Object[] {message});

                        throw new XWSSecurityException(message, fpContext.getPVE());
            }
                }
    }*/

    /**
     * @param fpContext com.sun.xml.wss.FilterProcessingContext
     *
     */
    static void processDeep(FilterProcessingContext fpContext)
    throws XWSSecurityException {
        /*if (PolicyTypeUtil.booleanComposerPolicy(fpContext.getSecurityPolicy())) {
            processBooleanComposer(fpContext);
        } else*/
        if (fpContext.getSecurityPolicy() instanceof WSSPolicy) {
            processWSSPolicy(fpContext);
        } else {
            log.log(Level.SEVERE,LogStringsMessages.WSS_0801_ILLEGAL_SECURITYPOLICY());
            throw new XWSSecurityException("Invalid SecurityPolicy Type");
        }
    }

    /**
     * Checks for required context parameters
     *
     * @param context ProcessingContext
     *
     */
    static void validateContext(ProcessingContext context)
    throws XWSSecurityException {
        SOAPMessage message = null;
        Message jaxWsMessage = null;
        if(context instanceof JAXBFilterProcessingContext)
            jaxWsMessage = ((JAXBFilterProcessingContext)context).getJAXWSMessage();
        else
            message = context.getSOAPMessage();
        SecurityEnvironment handler = context.getSecurityEnvironment();
        SecurityPolicy policy = context.getSecurityPolicy();
        boolean isInboundMessage = context.isInboundMessage();
        StaticPolicyContext staticContext = context.getPolicyContext();

        if (message == null && jaxWsMessage == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0803_SOAPMESSAGE_NOTSET());
            throw new XWSSecurityException(
                    "jakarta.xml.soap.SOAPMessage parameter not set in the ProcessingContext");
        }

        if (handler == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0804_CALLBACK_HANDLER_NOTSET());
            throw new XWSSecurityException(
                    "SecurityEnvironment/javax.security.auth.callback.CallbackHandler implementation not set in the ProcessingContext");
        }

        if (policy == null && !isInboundMessage) {
            if(log.isLoggable(Level.WARNING)){
            log.log(Level.WARNING, LogStringsMessages.WSS_0805_POLICY_NULL());
            }
        }

        if (staticContext == null) {
            // log.log(Level.WARNING, "WSS0806.static.context.null");
        }
    }

    /**
     * TODO: Handle doc-lit/non-wrap JAXRPC Mode
     * @param message SOAPMessage
     */
    static String resolvePolicyIdentifier(SOAPMessage message)
    throws XWSSecurityException {

        Node firstChild = null;
        SOAPBody body = null;

        try {
            body = message.getSOAPBody();
        } catch (SOAPException se) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0807_NO_BODY_ELEMENT(), se);
            throw new XWSSecurityException(se);
        }

        if (body != null) {
            firstChild = body.getFirstChild();
        } else {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0808_NO_BODY_ELEMENT_OPERATION());
            throw new XWSSecurityException(
                    "No body element identifying an operation is found");
        }

        return firstChild != null ?
            "{"+firstChild.getNamespaceURI()+"}"+firstChild.getLocalName() :
            null;
    }

    /*
     * @param current
     *
     * @return boolean
     */
    static boolean isSecondaryHeaderElement(SOAPElement element) {
        if ( element.getLocalName().equals(MessageConstants.ENCRYPTEDKEY_LNAME)) {
            NodeList nl = element.getElementsByTagNameNS(MessageConstants.XENC_NS, MessageConstants.XENC_REFERENCE_LIST_LNAME);
            if ( nl.getLength() == 0 ) {
                return true;
            }
        }
        return (element.getLocalName().equals(MessageConstants.WSSE_BINARY_SECURITY_TOKEN_LNAME) ||
                element.getLocalName().equals(MessageConstants.USERNAME_TOKEN_LNAME) ||
                element.getLocalName().equals(MessageConstants.SAML_ASSERTION_LNAME) ||
                element.getLocalName().equals(MessageConstants.TIMESTAMP_LNAME) ||
                element.getLocalName().equals(MessageConstants.SIGNATURE_CONFIRMATION_LNAME) ||
                element.getLocalName().equals(MessageConstants.WSSE_SECURITY_TOKEN_REFERENCE_LNAME) ||
                element.getLocalName().equals(MessageConstants.DERIVEDKEY_TOKEN_LNAME) ||
                element.getLocalName().equals(MessageConstants.SECURITY_CONTEXT_TOKEN_LNAME));
    }

    /*
     * @param current SOAPElement
     */
    static SOAPElement getNextElement(SOAPElement current) {
        if(current == null){
            return null;
        }
        Node node = current.getNextSibling();
        while ((null != node) && (node.getNodeType() != Node.ELEMENT_NODE))
            node = node.getNextSibling();
        return (SOAPElement) node;
    }

    /*
     * @param current SOAPElement
     */
    static SOAPElement getPreviousElement(SOAPElement current) {
        Node node = current.getPreviousSibling();
        while ((node !=null) && !(node.getNodeType() == Node.ELEMENT_NODE))
            node = node.getPreviousSibling();
        return (SOAPElement) node;
    }

    /*
     * @param message
     *
     * @throws com.sun.xml.wss.WssSoapFaultException
     */
    static void throwWssSoapFault(String message) throws WssSoapFaultException {
        XWSSecurityException xwsse = new XWSSecurityException(message);
        log.log(Level.SEVERE, LogStringsMessages.WSS_0809_FAULT_WSSSOAP(), xwsse);
        throw SecurableSoapMessage.newSOAPFaultException(
                MessageConstants.WSSE_INVALID_SECURITY,
                message,
                xwsse);
    }

   /*
    * make a DynamicPolicyCallback
    * @param callback the DynamicPolicyCallback object
    */
    public static void makeDynamicPolicyCallback(DynamicPolicyCallback callback,
            CallbackHandler callbackHandler)
            throws XWSSecurityException {

        if (callbackHandler == null)
            return;

        try {
            Callback[] callbacks = new Callback[] { callback };
            callbackHandler.handle(callbacks);
        } catch (UnsupportedCallbackException ex) {
            //ok to ignore this since not all callback-handlers will implement this
        } catch (Exception e) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_0237_FAILED_DYNAMIC_POLICY_CALLBACK(), e);
            throw new XWSSecurityException(e);
        }
    }

}
