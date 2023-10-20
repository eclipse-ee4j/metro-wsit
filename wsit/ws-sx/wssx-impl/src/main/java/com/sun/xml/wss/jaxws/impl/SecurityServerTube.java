/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.jaxws.impl;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.security.impl.policy.Constants;

import com.sun.xml.ws.api.message.stream.InputStreamMessage;
import com.sun.xml.ws.api.message.stream.XMLStreamReaderMessage;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.api.security.CallbackHandlerFeature;
import com.sun.xml.ws.api.security.secconv.WSSecureConversationRuntimeException;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.runtime.dev.Session;
import com.sun.xml.ws.runtime.dev.SessionManager;
import com.sun.xml.ws.security.impl.policyconv.SecurityPolicyHolder;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.ws.security.policy.SecureConversationToken;
import com.sun.xml.ws.security.trust.WSTrustConstants;

import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.ProcessingContextImpl;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.WssSoapFaultException;

import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.SecurityContextToken;
import com.sun.xml.ws.security.impl.IssuedTokenContextImpl;
import com.sun.xml.ws.security.opt.impl.util.SOAPUtil;
import com.sun.xml.ws.security.secconv.WSSecureConversationException;
import com.sun.xml.wss.impl.misc.DefaultSecurityEnvironmentImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import java.util.Properties;
import java.util.Iterator;
import java.util.Set;

import java.net.URI;
import com.sun.xml.ws.security.policy.Token;

import com.sun.xml.ws.security.secconv.WSSCContract;
import com.sun.xml.ws.security.secconv.WSSCFactory;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.elements.BaseSTSRequest;
import com.sun.xml.ws.security.trust.elements.BaseSTSResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityToken;
import com.sun.xml.wss.NonceManager;
import com.sun.xml.wss.SubjectAccessor;
import com.sun.xml.wss.RealmAuthenticationAdapter;
import com.sun.xml.wss.impl.NewSecurityRecipient;
import com.sun.xml.wss.impl.PolicyResolver;
import com.sun.xml.wss.impl.misc.DefaultCallbackHandler;

import static com.sun.xml.wss.jaxws.impl.Constants.SC_ASSERTION;
import java.security.AccessController;
import java.security.PrivilegedAction;


import java.util.logging.Level;
import com.sun.xml.wss.jaxws.impl.logging.LogStringsMessages;
import com.sun.xml.wss.provider.wsit.PipeConstants;
import com.sun.xml.wss.provider.wsit.PolicyAlternativeHolder;
import com.sun.xml.wss.provider.wsit.PolicyResolverFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * @author shyam.rao@sun.com
 * @author Vbkumar.Jayanti@Sun.COM
 */
public class SecurityServerTube extends SecurityTubeBase {

    private static final String WSCONTEXT_DELEGATE = "META-INF/services/com.sun.xml.ws.api.server.WebServiceContextDelegate";
    private Class contextDelegate = null;
    private SessionManager sessionManager = null;
    //private WSDLBoundOperation cachedOperation = null;
    private Set trustConfig = null;
    private Set wsscConfig = null;
    private CallbackHandler handler = null;
    private Packet tmpPacket;
    private boolean isTrustMessage;
    private boolean isSCIssueMessage;
    private boolean isSCCancelMessage;
    private String reqAction = null;
    private WSEndpoint wsEndpoint = null;

    // Creates a new instance of SecurityServerTube
    @SuppressWarnings("unchecked")
    public SecurityServerTube(ServerTubelineAssemblyContext context, Tube nextTube) {
        super(new ServerTubeConfiguration(context.getPolicyMap(), context.getWsdlPort(), context.getEndpoint()), nextTube);
        this.wsEndpoint = context.getEndpoint();
        try {
            //need to merge config assertions from all alternatives
            //because we do not know which alternative the req uses
            //and so if username comes we need to have usersupplied validator
            //and if SAML comes we need the userspecified SAML validator
            Set configAssertions = null;
            for (PolicyAlternativeHolder p : policyAlternatives) {               
                Iterator it = p.getInMessagePolicyMap().values().iterator();
                while (it.hasNext()) {
                    SecurityPolicyHolder holder = (SecurityPolicyHolder) it.next();
                    if (configAssertions != null) {
                        configAssertions.addAll(holder.getConfigAssertions(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS));
                    } else {
                        configAssertions = holder.getConfigAssertions(Constants.SUN_WSS_SECURITY_SERVER_POLICY_NS);
                    }
                    if (trustConfig != null) {
                        trustConfig.addAll(holder.getConfigAssertions(Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS));
                    } else {
                        trustConfig = holder.getConfigAssertions(Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS);
                    }
                    if (wsscConfig != null) {
                        wsscConfig.addAll(holder.getConfigAssertions(Constants.SUN_SECURE_SERVER_CONVERSATION_POLICY_NS));
                    } else {
                        wsscConfig = holder.getConfigAssertions(Constants.SUN_SECURE_SERVER_CONVERSATION_POLICY_NS);
                    }
                }
            }

            Properties props = new Properties();
            handler = configureServerHandler(configAssertions, props);
            secEnv = new DefaultSecurityEnvironmentImpl(handler, props);
            String cntxtClass = getMetaINFServiceClass(WSCONTEXT_DELEGATE);
            if (cntxtClass != null) {
                contextDelegate = this.loadClass(cntxtClass);
            }
            boolean isSC = false;
            if (!this.getSecureConversationPolicies(null, null).isEmpty()){
                isSC = true;
            }  
            sessionManager = SessionManager.getSessionManager(((ServerTubeConfiguration) tubeConfig).getEndpoint(), isSC, props);
            props.put(PipeConstants.ENDPOINT, context.getEndpoint());
            props.put(PipeConstants.POLICY, context.getPolicyMap());
            props.put(PipeConstants.WSDL_MODEL, context.getWsdlPort());
        //Registers IdentityComponent if either cs is not null


        } catch (Exception e) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WSSTUBE_0028_ERROR_CREATING_NEW_INSTANCE_SEC_SERVER_TUBE(), e);
            throw new RuntimeException(
                    LogStringsMessages.WSSTUBE_0028_ERROR_CREATING_NEW_INSTANCE_SEC_SERVER_TUBE(), e);
        }
    }

    // copy constructor
    protected SecurityServerTube(SecurityServerTube that, TubeCloner cloner) {
        super(that, cloner);
        sessionManager = that.sessionManager;
        trustConfig = that.trustConfig;
        wsscConfig = that.wsscConfig;
        handler = that.handler;
        contextDelegate = that.contextDelegate;
    }

    public AbstractTubeImpl copy(TubeCloner cloner) {
        return new SecurityServerTube(this, cloner);
    }

    //Note: There is an Assumption that the STS is distinct from the WebService in case of
    // WS-Trust and the STS and WebService are the same entity for SecureConversation
    @Override
    @SuppressWarnings("unchecked")
    public NextAction processRequest(Packet packet) {

// Not required, Commenting
//        if (!optimized) {
//            cacheMessage(packet);
//        }

        try {
            HaContext.initFrom(packet);
            
            Message msg = packet.getInternalMessage();
            isSCIssueMessage = false;
            isSCCancelMessage = false;
            isTrustMessage = false;
            tmpPacket = null;
            //String reqAction= null;

            boolean thereWasAFault = false;


            if (this.contextDelegate != null) {
                try {
                    WebServiceContextDelegate current = packet.webServiceContextDelegate;
                    Constructor ctor = contextDelegate.getConstructor(new Class[]{WebServiceContextDelegate.class});
                    packet.webServiceContextDelegate = (WebServiceContextDelegate) ctor.newInstance(new Object[]{current});
                } catch (InstantiationException ex) {
                    log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0036_ERROR_INSTATIATE_WEBSERVICE_CONTEXT_DELEGATE(), ex);
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0036_ERROR_INSTATIATE_WEBSERVICE_CONTEXT_DELEGATE(), ex);
                    throw new RuntimeException(ex);
                } catch (IllegalArgumentException ex) {
                    log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0036_ERROR_INSTATIATE_WEBSERVICE_CONTEXT_DELEGATE(), ex);
                    throw new RuntimeException(ex);
                } catch (InvocationTargetException ex) {
                    log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0036_ERROR_INSTATIATE_WEBSERVICE_CONTEXT_DELEGATE(), ex);
                    throw new RuntimeException(ex);
                } catch (NoSuchMethodException ex) {
                    log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0036_ERROR_INSTATIATE_WEBSERVICE_CONTEXT_DELEGATE(), ex);
                    throw new RuntimeException(ex);
                } catch (SecurityException ex) {
                    log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0036_ERROR_INSTATIATE_WEBSERVICE_CONTEXT_DELEGATE(), ex);
                    throw new RuntimeException(ex);
                }
            }

            //Do Security Processing for Incoming Message
            //---------------INBOUND SECURITY VERIFICATION----------
            ProcessingContext ctx = initializeInboundProcessingContext(packet/*, isSCIssueMessage, isTrustMessage*/);

            PolicyResolver pr = PolicyResolverFactory.createPolicyResolver(policyAlternatives,
                    cachedOperation, tubeConfig, addVer, false, rmVer, mcVer);
            ctx.setExtraneousProperty(ProcessingContext.OPERATION_RESOLVER, pr);
            ctx.setExtraneousProperty("SessionManager", sessionManager);
            try {
                if (!optimized) {
                    SOAPMessage soapMessage = msg.readAsSOAPMessage();
                    soapMessage = verifyInboundMessage(soapMessage, ctx);
                    msg = Messages.create(soapMessage);
                } else {
                    msg = verifyInboundMessage(msg, ctx);
                }
            } catch (WssSoapFaultException ex) {
                thereWasAFault = true;
                log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0025_ERROR_VERIFY_INBOUND_MSG(), ex);
                SOAPFaultException sfe = SOAPUtil.getSOAPFaultException(ex, soapFactory, soapVersion);
                if (sfe.getCause() == null) {
                    sfe.initCause(ex);
                }
                msg = Messages.create(sfe, soapVersion);
            } catch (XWSSecurityException xwse) {
                thereWasAFault = true;
                log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0025_ERROR_VERIFY_INBOUND_MSG(), xwse);
                SOAPFaultException sfe = SOAPUtil.getSOAPFaultException(xwse, soapFactory, soapVersion);
                if (sfe.getCause() == null) {
                    sfe.initCause(xwse);
                }
                msg = Messages.create(sfe, soapVersion);

            } catch (XWSSecurityRuntimeException xwse) {
                thereWasAFault = true;
                log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0025_ERROR_VERIFY_INBOUND_MSG(), xwse);
                SOAPFaultException sfe = SOAPUtil.getSOAPFaultException(xwse, soapFactory, soapVersion);
                if (sfe.getCause() == null) {
                    sfe.initCause(xwse);
                }
                msg = Messages.create(sfe, soapVersion);

            } catch (WebServiceException xwse) {
                thereWasAFault = true;
                log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0025_ERROR_VERIFY_INBOUND_MSG(), xwse);
                SOAPFaultException sfe = SOAPUtil.getSOAPFaultException(xwse, soapFactory, soapVersion);
                if (sfe.getCause() == null) {
                    sfe.initCause(xwse);
                }
                msg = Messages.create(sfe, soapVersion);

            } catch (WSSecureConversationRuntimeException wsre) {
                thereWasAFault = true;
                log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0025_ERROR_VERIFY_INBOUND_MSG(), wsre);
                QName faultCode = wsre.getFaultCode();
                if (faultCode != null) {
                    faultCode = new QName(wsscVer.getNamespaceURI(), faultCode.getLocalPart());
                }
                SOAPFaultException sfe = SOAPUtil.getSOAPFaultException(faultCode, wsre, soapFactory, soapVersion);
                msg = Messages.create(sfe, soapVersion);
            } catch (SOAPException se) {
                // internal error
                // Log here because this catch is an internal error not logged by the callee
                log.log(Level.SEVERE,
                        LogStringsMessages.WSSTUBE_0025_ERROR_VERIFY_INBOUND_MSG(), se);
                thereWasAFault = true;
                SOAPFaultException sfe = SOAPUtil.getSOAPFaultException(se, soapFactory, soapVersion);
                if (sfe.getCause() == null) {
                    sfe.initCause(se);
                }
                msg = Messages.create(sfe, soapVersion);
            }

            Packet retPacket = null;
            if (thereWasAFault) {
                //retPacket = packet;
                if (this.isAddressingEnabled()) {
                    if (optimized) {
                        packet.setMessage(((JAXBFilterProcessingContext) ctx).getPVMessage());
                    }
                    retPacket = packet.createServerResponse(
                            msg, this.addVer, this.soapVersion, this.addVer.getDefaultFaultAction());
                } else {
                    packet.setMessage(msg);
                    retPacket = packet;
                }
            }

            packet.setMessage(msg);

            if (isAddressingEnabled()) {
                reqAction = getAction(packet);
                if (wsscVer.getSCTRequestAction().equals(reqAction) || wsscVer.getSCTRenewRequestAction().equals(reqAction)) {
                    isSCIssueMessage = true;
                    if (wsscConfig != null) {
                        packet.invocationProperties.put(Constants.SUN_SECURE_SERVER_CONVERSATION_POLICY_NS, wsscConfig.iterator());
                    }
                } else if (wsscVer.getSCTCancelRequestAction().equals(reqAction)) {
                    isSCCancelMessage = true;
                } else if (wsTrustVer.getIssueRequestAction().equals(reqAction) ||
                        wsTrustVer.getValidateRequestAction().equals(reqAction)) {
                    isTrustMessage = true;
                    //packet.getMessage().getHeaders().getTo(addVer, tubeConfig.getBinding().getSOAPVersion());

                    if (trustConfig != null) {
                        packet.invocationProperties.put(Constants.SUN_TRUST_SERVER_SECURITY_POLICY_NS, trustConfig.iterator());
                    }

                    //set the callbackhandler
                    packet.invocationProperties.put(WSTrustConstants.SECURITY_ENVIRONMENT, secEnv);
                    packet.invocationProperties.put(WSTrustConstants.WST_VERSION, this.wsTrustVer);
                    IssuedTokenContext ictx = ((ProcessingContextImpl) ctx).getTrustContext();
                    if (ictx != null && ictx.getAuthnContextClass() != null) {
                        packet.invocationProperties.put(WSTrustConstants.AUTHN_CONTEXT_CLASS, ictx.getAuthnContextClass());
                    }
                }

                if (isSCIssueMessage) {
                    List<PolicyAssertion> policies = getInBoundSCP(packet.getMessage());
                    if (!policies.isEmpty()) {
                        packet.invocationProperties.put(SC_ASSERTION, policies.get(0));
                    }
                }
            }

            if (!isSCIssueMessage) {
                cachedOperation = msg.getOperation(tubeConfig.getWSDLPort());
                if (cachedOperation == null) {
                    if (addVer != null) {
                        if(thereWasAFault) {
                            cachedOperation = getWSDLOpFromAction(packet, true, true);
                        } else {
                            cachedOperation = getWSDLOpFromAction(packet, true);
                        }
                    }
                }
            }



            if (!thereWasAFault) {

                if (isSCIssueMessage || isSCCancelMessage) {
                    //-------put application message on hold and invoke SC contract--------

                    retPacket = invokeSecureConversationContract(
                            packet, ctx, isSCIssueMessage, reqAction);
                    tmpPacket = packet;
                    return processResponse(retPacket);

                } else {
                    //--------INVOKE NEXT TUBE------------
                    // Put the addressing headers as unread
                    // packet.invocationProperties.put(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND, null);
                    updateSCBootstrapCredentials(packet, ctx);
                    tmpPacket = packet;
                    return doInvoke(next, packet);
                }
            } else {
                return processResponse(retPacket);
            }

        } finally {
            HaContext.clear();
        }
    }

    @Override
    public NextAction processResponse(Packet retPacket) {

        // Add addrsssing headers to trust message
        //if (isTrustMessage){
        //  retPacket = addAddressingHeaders(tmpPacket, retPacket.getMessage(), wsTrustVer.getFinalResponseAction(reqAction));
        // }
        
        if (retPacket.getMessage() == null) {
            return doReturnWith(retPacket);
        }

        /* TODO:this piece of code present since payload should be read once*/
        if (!optimized) {
            try {
                SOAPMessage sm = retPacket.getMessage().readAsSOAPMessage();
                Message newMsg = Messages.create(sm);
                retPacket.setMessage(newMsg);
            } catch (SOAPException ex) {
                log.log(Level.SEVERE,
                        LogStringsMessages.WSSTUBE_0005_PROBLEM_PROC_SOAP_MESSAGE(), ex);
                return doThrow(new WebServiceException(LogStringsMessages.WSSTUBE_0005_PROBLEM_PROC_SOAP_MESSAGE(), ex));
            }
        }

        //---------------OUTBOUND SECURITY PROCESSING----------
        ProcessingContext ctx = initializeOutgoingProcessingContext(retPacket, isSCIssueMessage, isTrustMessage /*, thereWasAFault*/);
        ctx.setExtraneousProperty("SessionManager", sessionManager);
        Message msg = null;
        try {
            msg = retPacket.getMessage();
            if (ctx.getSecurityPolicy() != null && ((MessagePolicy) ctx.getSecurityPolicy()).size() > 0) {
                if (!optimized) {
                    SOAPMessage soapMessage = msg.readAsSOAPMessage();
                    soapMessage = secureOutboundMessage(soapMessage, ctx);
                    msg = Messages.create(soapMessage);
                } else {
                    msg = secureOutboundMessage(msg, ctx);
                }
            }
        } catch (WssSoapFaultException ex) {
            msg = Messages.create(getSOAPFault(ex));
        } catch (SOAPException se) {
            // internal error
            log.log(Level.SEVERE,
                    LogStringsMessages.WSSTUBE_0024_ERROR_SECURING_OUTBOUND_MSG(), se);
            return doThrow(new WebServiceException(LogStringsMessages.WSSTUBE_0024_ERROR_SECURING_OUTBOUND_MSG(), se));
        } finally {
            if (isSCCancel(retPacket)) {
                removeContext(tmpPacket);
            }
            tmpPacket = null;
        }
        resetCachedOperation();
        retPacket.setMessage(msg);
        return doReturnWith(retPacket);        
    }

    @Override
    public NextAction processException(Throwable t) {
        if (!(t instanceof WebServiceException)) {
            t = new WebServiceException(t);
        }
        return doThrow(t);
    }

    private void removeContext(final Packet packet) {
        SecurityContextToken sct = (SecurityContextToken) packet.invocationProperties.get(MessageConstants.INCOMING_SCT);
        if (sct != null) {
            String strId = sct.getIdentifier().toString();
            if (strId != null) {
                issuedTokenContextMap.remove(strId);
                sessionManager.terminateSession(strId);
            }
        }
    }

    @Override
    public void preDestroy() {
        super.preDestroy();
        issuedTokenContextMap.clear();
        SessionManager.removeSessionManager(((ServerTubeConfiguration) tubeConfig).getEndpoint());
        NonceManager.deleteInstance(wsEndpoint);
    }

    public Packet processMessage(XMLStreamReaderMessage msg) {
        //TODO:Optimized security
        throw new UnsupportedOperationException();
    }

    public InputStreamMessage processInputStream(XMLStreamReaderMessage msg) {
        //TODO:Optimized security
        throw new UnsupportedOperationException();
    }

    public InputStreamMessage processInputStream(Message msg) {
        //TODO:Optimized security
        throw new UnsupportedOperationException();
    }

    protected ProcessingContext initializeOutgoingProcessingContext(
            Packet packet, boolean isSCMessage, boolean isTrustMessage /*, boolean thereWasAFault*/) {
        ProcessingContext ctx = initializeOutgoingProcessingContext(packet, isSCMessage/*, thereWasAFault*/);
        return ctx;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ProcessingContext initializeOutgoingProcessingContext(
            Packet packet, boolean isSCMessage /*, boolean thereWasAFault*/) {

        ProcessingContextImpl ctx;
        if (optimized) {
            ctx = new JAXBFilterProcessingContext(packet.invocationProperties);
            ((JAXBFilterProcessingContext) ctx).setAddressingVersion(addVer);
            ((JAXBFilterProcessingContext) ctx).setSOAPVersion(soapVersion);
            ((JAXBFilterProcessingContext) ctx).setBSP(bsp10);
        } else {
            ctx = new ProcessingContextImpl(packet.invocationProperties);
        }
        if (addVer != null) {
            ctx.setAction(getAction(packet));
        }
        ctx.setSecurityPolicyVersion(spVersion.namespaceUri);
        try {
            MessagePolicy policy = null;
            PolicyAlternativeHolder applicableAlternative =
                    resolveAlternative(packet, isSCMessage);

            if (packet.getMessage().isFault()) {
                policy = getOutgoingFaultPolicy(packet);
            } else if (isRMMessage(packet) || isMakeConnectionMessage(packet)) {
                SecurityPolicyHolder holder = applicableAlternative.getOutProtocolPM().get("RM");
                policy = holder.getMessagePolicy();
            } else if (isSCCancel(packet)) {
                SecurityPolicyHolder holder = applicableAlternative.getOutProtocolPM().get("SC-CANCEL");
                policy = holder.getMessagePolicy();
            } else {
                policy = getOutgoingXWSSecurityPolicy(packet, isSCMessage);
            }

            if (debug && policy != null) {
                policy.dumpMessages(true);
            }
            //this might mislead if there is a bug in code above
            //but we are doing this check for cases such as no-fault-security-policy
            if (policy != null) {
                ctx.setSecurityPolicy(policy);
            }
            if (isTrustMessage(packet)) {
                ctx.isTrustMessage(true);
            }
            // set the policy, issued-token-map, and extraneous properties
            //ctx.setIssuedTokenContextMap(issuedTokenContextMap);
            if (isSCMessage || policy.getAlgorithmSuite() != null) {
                //override the binding level suite
                ctx.setAlgorithmSuite(policy.getAlgorithmSuite());
            } else {
                ctx.setAlgorithmSuite(getAlgoSuite(getBindingAlgorithmSuite(packet)));
            }
            ctx.setSecurityEnvironment(secEnv);
            ctx.isInboundMessage(false);
            ctx.getExtraneousProperties().put(WSDLPORT, tubeConfig.getWSDLPort());
        } catch (XWSSecurityException e) {
            log.log(
                    Level.SEVERE, LogStringsMessages.WSSTUBE_0006_PROBLEM_INIT_OUT_PROC_CONTEXT(), e);
            throw new RuntimeException(
                    LogStringsMessages.WSSTUBE_0006_PROBLEM_INIT_OUT_PROC_CONTEXT(), e);
        }
        return ctx;
    }

    @Override
    protected MessagePolicy getOutgoingXWSSecurityPolicy(
            Packet packet, boolean isSCMessage) {
        if (isSCMessage) {
            Token scToken = (Token) packet.invocationProperties.get(SC_ASSERTION);
            return getOutgoingXWSBootstrapPolicy(scToken);
        }

        MessagePolicy mp = null;
        PolicyAlternativeHolder applicableAlternative =
                resolveAlternative(packet, isSCMessage);
        WSDLBoundOperation wsdlOperation = cachedOperation;
        //if(operation == null){
        //Body could be encrypted. Security will have to infer the
        //policy from the message till the Body is decrypted.
        //    mp = emptyMessagePolicy;
        //}
        if (applicableAlternative.getOutMessagePolicyMap() == null) {
            //empty message policy
            return new MessagePolicy();
        }

        if (isTrustMessage(packet) || cachedOperation == null) {
            cachedOperation = getWSDLOpFromAction(packet, false);
        }

        SecurityPolicyHolder sph = applicableAlternative.getOutMessagePolicyMap().get(cachedOperation);
        if (sph == null) {
            return new MessagePolicy();
        }
        mp = sph.getMessagePolicy();
        return mp;
    }

    protected MessagePolicy getOutgoingFaultPolicy(Packet packet) {
        PolicyAlternativeHolder applicableAlternative =
                resolveAlternative(packet, false);
        if (cachedOperation != null) {
            WSDLOperation operation = cachedOperation.getOperation();
            QName faultDetail = packet.getMessage().getFirstDetailEntryName();
            WSDLFault fault = null;
            if (faultDetail != null) {
                fault = operation.getFault(faultDetail);
            }
            SecurityPolicyHolder sph = applicableAlternative.getOutMessagePolicyMap().get(cachedOperation);
            if (fault == null) {
                MessagePolicy faultPolicy1 = (sph != null) ? (sph.getMessagePolicy()) : new MessagePolicy();
                return faultPolicy1;
            }
            SecurityPolicyHolder faultPolicyHolder = sph.getFaultPolicy(fault);
            MessagePolicy faultPolicy = (faultPolicyHolder == null) ? new MessagePolicy() : faultPolicyHolder.getMessagePolicy();
            return faultPolicy;
        }
        return null;

    }

    @Override
    protected SOAPMessage verifyInboundMessage(SOAPMessage message, ProcessingContext ctx)
            throws WssSoapFaultException, XWSSecurityException {
        ctx.setSOAPMessage(message);
        NewSecurityRecipient.validateMessage(ctx);
        return ctx.getSOAPMessage();
    }

    // The packet has the Message with RST/SCT inside it
    // TODO: Need to inspect if it is really a Issue or a Cancel
    @SuppressWarnings("unchecked")
    private Packet invokeSecureConversationContract(
            Packet packet, ProcessingContext ctx, boolean isSCTIssue, String action) {

        IssuedTokenContext ictx = new IssuedTokenContextImpl();
        ictx.getOtherProperties().put("SessionManager", sessionManager);

        Message msg = packet.getMessage();
        Message retMsg;
        String retAction;

        try {
            // Set the requestor authenticated Subject in the IssuedTokenContext
            Subject subject = SubjectAccessor.getRequesterSubject(ctx);

            ictx.setRequestorSubject(subject);

            WSTrustElementFactory wsscEleFac = WSTrustElementFactory.newInstance(wsscVer);

            JAXBElement rstEle = msg.readPayloadAsJAXB(WSTrustElementFactory.getContext(wsTrustVer).createUnmarshaller());
            BaseSTSRequest rst;

            rst = wsscEleFac.createRSTFrom(rstEle);
            URI requestType = ((RequestSecurityToken) rst).getRequestType();
            BaseSTSResponse rstr;
            WSSCContract scContract = WSSCFactory.newWSSCContract(wsscVer);
            scContract.setWSSCServerConfig((Iterator) packet.invocationProperties.get(
                    Constants.SUN_SECURE_SERVER_CONVERSATION_POLICY_NS));
            if (requestType.toString().equals(wsTrustVer.getIssueRequestTypeURI())) {
                List<PolicyAssertion> policies = getOutBoundSCP(packet.getMessage());
                rstr = scContract.issue(rst, ictx, (SecureConversationToken) policies.get(0));
                retAction = wsscVer.getSCTResponseAction();
                SecurityContextToken sct = (SecurityContextToken) ictx.getSecurityToken();
                String sctId = sct.getIdentifier().toString();

                Session session = sessionManager.getSession(sctId);
                if (session == null) {
                    log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0029_ERROR_SESSION_CREATION());
                    throw new WSSecureConversationException(
                            LogStringsMessages.WSSTUBE_0029_ERROR_SESSION_CREATION());
                }

                // Put it here for RM to pick up
                packet.invocationProperties.put(
                        Session.SESSION_ID_KEY, sctId);

                packet.invocationProperties.put(
                        Session.SESSION_KEY, session.getUserData());

            //((ProcessingContextImpl)ctx).getIssuedTokenContextMap().put(sctId, ictx);

            } else if (requestType.toString().equals(wsTrustVer.getRenewRequestTypeURI())) {
                List<PolicyAssertion> policies = getOutBoundSCP(packet.getMessage());
                retAction = wsscVer.getSCTRenewResponseAction();
                rstr = scContract.renew(rst, ictx, (SecureConversationToken) policies.get(0));
            } else if (requestType.toString().equals(wsTrustVer.getCancelRequestTypeURI())) {
                retAction = wsscVer.getSCTCancelResponseAction();
                rstr = scContract.cancel(rst, ictx);
            } else {
                log.log(Level.SEVERE,
                        LogStringsMessages.WSSTUBE_0030_UNSUPPORTED_OPERATION_EXCEPTION(requestType));
                throw new UnsupportedOperationException(
                        LogStringsMessages.WSSTUBE_0030_UNSUPPORTED_OPERATION_EXCEPTION(requestType));
            }

            // construct the complete message here containing the RSTR and the
            // correct Action headers if any and return the message.
            retMsg = Messages.create(WSTrustElementFactory.getContext(wsTrustVer).createMarshaller(), wsscEleFac.toJAXBElement(rstr), soapVersion);
        } catch (com.sun.xml.wss.XWSSecurityException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0031_ERROR_INVOKE_SC_CONTRACT(), ex);
            throw new RuntimeException(LogStringsMessages.WSSTUBE_0031_ERROR_INVOKE_SC_CONTRACT(), ex);
        } catch (javax.xml.bind.JAXBException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0001_PROBLEM_MAR_UNMAR(), ex);
            throw new RuntimeException(LogStringsMessages.WSSTUBE_0001_PROBLEM_MAR_UNMAR(), ex);
        } catch (WSSecureConversationException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSSTUBE_0031_ERROR_INVOKE_SC_CONTRACT(), ex);
            throw new RuntimeException(LogStringsMessages.WSSTUBE_0031_ERROR_INVOKE_SC_CONTRACT(), ex);
        }


        Packet retPacket = addAddressingHeaders(packet, retMsg, retAction);
        if (isSCTIssue) {
            List<PolicyAssertion> policies = getOutBoundSCP(packet.getMessage());

            if (!policies.isEmpty()) {
                retPacket.invocationProperties.put(SC_ASSERTION, policies.get(0));
            }
        }

        return retPacket;
    }

    public InputStreamMessage processInputStream(Packet packet) {
        //TODO:Optimized security
        throw new UnsupportedOperationException("Will be supported for optimized path");
    }

    /** private Packet addAddressingHeaders(Packet packet, String relatesTo, String action){
     * AddressingBuilder builder = AddressingBuilder.newInstance();
     * AddressingProperties ap = builder.newAddressingProperties();
     *
     * try{
     * // Action
     * ap.setAction(builder.newURI(new URI(action)));
     *
     * // RelatesTo
     * Relationship[] rs = new Relationship[]{builder.newRelationship(new URI(relatesTo))};
     * ap.setRelatesTo(rs);
     *
     * // To
     * ap.setTo(builder.newURI(new URI(builder.newAddressingConstants().getAnonymousURI())));
     *
     * } catch (URISyntaxException e) {
     * throw new RuntimeException("Exception when adding Addressing Headers");
     * }
     *
     * WsaRuntimeFactory fac = WsaRuntimeFactory.newInstance(ap.getNamespaceURI(), pipeConfig.getWSDLModel(), pipeConfig.getBinding());
     * fac.writeHeaders(packet, ap);
     * packet.invocationProperties
     * .put(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_OUTBOUND, ap);
     *
     * return packet;
     * }*/
    //TODO:Encapsulate, change this direct member access in all the 4 methods
    protected SecurityPolicyHolder addOutgoingMP(WSDLBoundOperation operation, Policy policy, PolicyAlternativeHolder ph) throws PolicyException {
        SecurityPolicyHolder sph = constructPolicyHolder(policy, true, true);
        ph.getInMessagePolicyMap().put(operation, sph);
        return sph;
    }

    protected SecurityPolicyHolder addIncomingMP(WSDLBoundOperation operation, Policy policy, PolicyAlternativeHolder ph) throws PolicyException {
        SecurityPolicyHolder sph = constructPolicyHolder(policy, true, false);
        ph.getOutMessagePolicyMap().put(operation, sph);
        return sph;
    }

    protected void addIncomingProtocolPolicy(Policy effectivePolicy, String protocol, PolicyAlternativeHolder ph) throws PolicyException {
        ph.getOutProtocolPM().put(protocol, constructPolicyHolder(effectivePolicy, true, false, true));
    }

    protected void addOutgoingProtocolPolicy(Policy effectivePolicy, String protocol, PolicyAlternativeHolder ph) throws PolicyException {
        ph.getInProtocolPM().put(protocol, constructPolicyHolder(effectivePolicy, true, true, false));
    }

    protected void addIncomingFaultPolicy(Policy effectivePolicy, SecurityPolicyHolder sph, WSDLFault fault) throws PolicyException {
        SecurityPolicyHolder faultPH = constructPolicyHolder(effectivePolicy, true, false);
        sph.addFaultPolicy(fault, faultPH);
    }

    protected void addOutgoingFaultPolicy(Policy effectivePolicy, SecurityPolicyHolder sph, WSDLFault fault) throws PolicyException {
        SecurityPolicyHolder faultPH = constructPolicyHolder(effectivePolicy, true, true);
        sph.addFaultPolicy(fault, faultPH);
    }

    protected String getAction(WSDLOperation operation, boolean inComming) {
        if (inComming) {
            return operation.getInput().getAction();
        } else {
            return operation.getOutput().getAction();
        }
    }

    private Packet addAddressingHeaders(Packet packet, Message retMsg, String action) {
        Packet retPacket = packet.createServerResponse(retMsg, addVer, soapVersion, action);

        retPacket.proxy = packet.proxy;
        retPacket.invocationProperties.putAll(packet.invocationProperties);

        return retPacket;
    }

    private CallbackHandler configureServerHandler(Set<PolicyAssertion> configAssertions, Properties props) {
        //Properties props = new Properties();
        CallbackHandlerFeature cbFeature =
                tubeConfig.getBinding().getFeature(CallbackHandlerFeature.class);
        if (cbFeature != null) {
            return cbFeature.getHandler();
        }
        String ret = populateConfigProperties(configAssertions, props);
        try {
            if (ret != null) {
                Object obj = loadClass(ret).newInstance();
                if (!(obj instanceof CallbackHandler)) {
                    log.log(Level.SEVERE,
                            LogStringsMessages.WSSTUBE_0033_INVALID_CALLBACK_HANDLER_CLASS(ret));
                    throw new RuntimeException(
                            LogStringsMessages.WSSTUBE_0033_INVALID_CALLBACK_HANDLER_CLASS(ret));
                }
                return (CallbackHandler) obj;
            }
            // ServletContext context =
            //         ((ServerPipeConfiguration)pipeConfig).getEndpoint().getContainer().getSPI(ServletContext.class);
            RealmAuthenticationAdapter adapter = getRealmAuthenticationAdapter(((ServerTubeConfiguration) tubeConfig).getEndpoint());
            return new DefaultCallbackHandler("server", props, adapter);
        //return new DefaultCallbackHandler("server", props);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WSSTUBE_0032_ERROR_CONFIGURE_SERVER_HANDLER(), e);
            throw new RuntimeException(LogStringsMessages.WSSTUBE_0032_ERROR_CONFIGURE_SERVER_HANDLER(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private RealmAuthenticationAdapter getRealmAuthenticationAdapter(WSEndpoint wSEndpoint) {
        String className = "javax.servlet.ServletContext";
        Class ret = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                ret = loader.loadClass(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        if (ret == null) {
            // if context classloader didnt work, try this
            loader = this.getClass().getClassLoader();
            try {
                ret = loader.loadClass(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        if (ret != null) {
            Object obj = wSEndpoint.getContainer().getSPI(ret);
            if (obj != null) {
                return RealmAuthenticationAdapter.newInstance(obj);
            }
        }
        return null;
    }

    //doing this here becuase doing inside keyselector of optimized security would
    //mean doing it twice (if SCT was used for sign and encrypt) which can impact performance
    @SuppressWarnings("unchecked")
    private void updateSCBootstrapCredentials(Packet packet, ProcessingContext ctx) {
        SecurityContextToken sct =
                (SecurityContextToken) packet.invocationProperties.get(MessageConstants.INCOMING_SCT);
        if (sct != null) {
            //Session session = this.sessionManager.getSession(sct.getIdentifier().toString());
            //IssuedTokenContext ctx = session.getSecurityInfo().getIssuedTokenContext();
            //IssuedTokenContext itctx = (IssuedTokenContext)((ProcessingContextImpl)ctx).getIssuedTokenContextMap().get(sct.getIdentifier().toString());

            // get the secure session id 
            String sessionId = sct.getIdentifier().toString();

            // put the secure session id the the message context
            packet.invocationProperties.put(Session.SESSION_ID_KEY, sessionId);
            packet.invocationProperties.put(Session.SESSION_KEY, sessionManager.getSession(sessionId).getUserData());

            // update the Sbject
            IssuedTokenContext itctx = sessionManager.getSecurityContext(sessionId, true);
            if (itctx != null) {
                Subject from = itctx.getRequestorSubject();
                Subject to = DefaultSecurityEnvironmentImpl.getSubject(packet.invocationProperties);
                copySubject(from, to);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void copySubject(final Subject from, final Subject to) {
        if (from == null || to == null) {
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                to.getPrincipals().addAll(from.getPrincipals());
                to.getPublicCredentials().addAll(from.getPublicCredentials());
                to.getPrivateCredentials().addAll(from.getPrivateCredentials());
                return null; // nothing to return
            }
        });
    }

    private static String getMetaINFServiceClass(String metaInfService) {
        URL url = loadFromClasspath(metaInfService);
        if (url != null) {
            InputStream is = null;
            try {
                is = url.openStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int val = is.read();
                while (val != -1) {
                    os.write(val);
                    val = is.read();
                }
                String classname = os.toString();
                return classname;

            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
                throw new WebServiceException(ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    log.log(Level.WARNING, null, ex);
                }
            }
        }
        return null;
    }

    public static URL loadFromClasspath(final String configFileName) {

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            return ClassLoader.getSystemResource(configFileName);
        } else {
            return cl.getResource(configFileName);
        }
    }
}
