/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xwss;


import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.PolicyTypeUtil;
import com.sun.xml.wss.impl.PolicyViolationException;
import com.sun.xml.wss.impl.ProcessingContextImpl;
import com.sun.xml.wss.impl.SecurableSoapMessage;
import com.sun.xml.wss.impl.SecurityAnnotator;
import com.sun.xml.wss.impl.SecurityRecipient;
import com.sun.xml.wss.impl.WssSoapFaultException;
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.impl.config.ApplicationSecurityConfiguration;
import com.sun.xml.wss.impl.config.DeclarativeSecurityConfiguration;
import com.sun.xml.wss.impl.configuration.StaticApplicationContext;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.xwss.SecurityConfiguration;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.soap.MessageFactory;
import org.w3c.dom.Node;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;

/**
 *
 * 
 */
public class XWSSClientTube extends AbstractFilterTubeImpl {

    protected WSDLPort port = null;
    protected WSService service = null;
    protected WSBinding binding = null;
    protected SOAPFactory soapFactory = null;
    protected MessageFactory messageFactory = null;
    protected SOAPVersion soapVersion = null;
    protected boolean isSOAP12 = false;
    private static final String MESSAGE_SECURITY_CONFIGURATION =
            SecurityConfiguration.MESSAGE_SECURITY_CONFIGURATION;
    private static final String CONTEXT_WSDL_OPERATION =
            "com.sun.xml.ws.wsdl.operation";
    protected boolean wasConfigChecked = false;

    protected SecurityConfiguration sConfig;
    
    /** Creates a new instance of XWSSClientPipe */
    //public XWSSClientTube(ClientTubelineAssemblyContext wsitContext, Tube nextTube) {
    public XWSSClientTube(WSDLPort prt, WSService svc, WSBinding bnd, Tube nextTube) {

        super(nextTube);
        port = prt;
        service = svc;
        binding = bnd;

        soapVersion = bnd.getSOAPVersion();
        isSOAP12 = (soapVersion == SOAPVersion.SOAP_12) ? true : false;
        soapFactory = soapVersion.saajSoapFactory;
        messageFactory = soapVersion.saajMessageFactory;

    }

    public XWSSClientTube(XWSSClientTube that, TubeCloner cloner) {
        super(that, cloner);
        this.binding = that.binding;
        this.port = that.port;
        this.service = that.service;
        this.soapFactory = that.soapFactory;
        this.messageFactory = that.messageFactory;
        this.soapVersion = that.soapVersion;
        this.isSOAP12 = that.isSOAP12;
        this.sConfig = that.sConfig;
        this.wasConfigChecked = that.wasConfigChecked;

    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        return new XWSSClientTube(this, cloner);  
    }

    @Override
    public NextAction processRequest(Packet packet){
        try {
            sConfig =
                    (SecurityConfiguration) packet.invocationProperties.get(MESSAGE_SECURITY_CONFIGURATION);
            if (sConfig == null) {
                //No Security case here...
                //now look for "client_security_config.xml file from META-INF/ classpath"
                URL url = null;
                if (!wasConfigChecked) {
                    wasConfigChecked = true;
                    String configUrl = "META-INF/client_security_config.xml";
                    url = SecurityUtil.loadFromClasspath(configUrl);
                }
                if (url != null) {
                    try {
                        sConfig = new SecurityConfiguration(url);
                        packet.invocationProperties.put(MESSAGE_SECURITY_CONFIGURATION, sConfig);
                    } catch (XWSSecurityException e) {
                        throw new XWSSecurityRuntimeException(e);
                    }
                } else {
                    return doInvoke(super.next, packet);
                }
            }

            Packet ret = secureRequest(packet);
            return doInvoke(super.next, ret);
        } catch (Throwable t) {
            if (!(t instanceof WebServiceException)) {
                t = new WebServiceException(t);
            }
            return doThrow(t);
        }        
    }
     
    @Override
    public NextAction processResponse(Packet ret) {
        try{
            if (ret == null || ret.getMessage() == null) {
                return doReturnWith(ret);
            }
            Packet response = validateResponse(ret);
            return doReturnWith(response);
        }catch(Throwable t){
            if (!(t instanceof WebServiceException)) {
                t = new WebServiceException(t);
            }
            return doThrow(t);
        }
         
    }
    

    @Override
    public void preDestroy() {
    }

    // client side incoming request handling code
    public Packet validateResponse(Packet packet) {
        try {
            SecurityConfiguration sConfig = (SecurityConfiguration) packet.invocationProperties.get(MESSAGE_SECURITY_CONFIGURATION);

            if (sConfig == null) {
                return packet;
            }
            SOAPMessage message = null;
            try {
                message = packet.getMessage().readAsSOAPMessage();
            } catch (SOAPException ex) {
                throw new WebServiceException(ex);
            }

            String operation = (String) packet.invocationProperties.get(CONTEXT_WSDL_OPERATION);

            StaticApplicationContext sContext =
                    getPolicyContext(packet, sConfig);
            sContext.setOperationIdentifier(operation);

            ApplicationSecurityConfiguration config =
                    sConfig.getSecurityConfiguration();

            SecurityPolicy policy = config.getSecurityConfiguration(sContext);

            ProcessingContext context = new ProcessingContextImpl(packet.invocationProperties);
            context.setPolicyContext(sContext);
            context.setSOAPMessage(message);

            if (PolicyTypeUtil.declarativeSecurityConfiguration(policy)) {
                context.setSecurityPolicy(
                        ((DeclarativeSecurityConfiguration) policy).receiverSettings());
            } else {
                context.setSecurityPolicy(policy);
            }

            context.setSecurityEnvironment(sConfig.getSecurityEnvironment());
            context.isInboundMessage(true);

            if (config.retainSecurityHeader()) {
                context.retainSecurityHeader(true);
            }
            
            if (config.resetMustUnderstand()) {
                context.resetMustUnderstand(true);
            }

            SecurityRecipient.validateMessage(context);

            /* TODO: not sure if this is needed
            if (messageContext.get("javax.security.auth.Subject") != null) {
            messageContext.setScope("javax.security.auth.Subject", MessageContext.Scope.APPLICATION); 
            }*/
            packet.setMessage(Messages.create(context.getSOAPMessage()));
            return packet;

        } catch (com.sun.xml.wss.impl.WssSoapFaultException soapFaultException) {
            throw getSOAPFaultException(soapFaultException, isSOAP12);
        } catch (com.sun.xml.wss.XWSSecurityException xwse) {
            QName qname = null;

            if (xwse.getCause() instanceof PolicyViolationException) {
                qname = MessageConstants.WSSE_RECEIVER_POLICY_VIOLATION;
            } else {
                qname = MessageConstants.WSSE_FAILED_AUTHENTICATION;
            }
            com.sun.xml.wss.impl.WssSoapFaultException wsfe =
                    SecurableSoapMessage.newSOAPFaultException(
                    qname, xwse.getMessage(), xwse);
            //TODO: MISSING-LOG
            throw getSOAPFaultException(wsfe, isSOAP12);
        }
    }
    // client side request sending hook
    public Packet secureRequest(Packet packet) {

        ProcessingContext context = null;
        SOAPMessage message = null;
        try {
            message = packet.getMessage().readAsSOAPMessage();
        } catch (SOAPException ex) {
            throw new WebServiceException(ex);
        }

        try {
            //TODO: whether property on BindingProvider.RequestContext is available here
            SecurityConfiguration sConfig =
                    (SecurityConfiguration) packet.invocationProperties.get(MESSAGE_SECURITY_CONFIGURATION);

            if (sConfig == null) {
                return packet;
            }

            WSDLBoundOperation op = null;
            if (port != null) {
                op = packet.getMessage().getOperation(port);
            }

            QName operationQName = null;
            if (op != null) {
                operationQName = op.getName();
            }

            String operation = null;
            try {
                if (operationQName == null) {
                    operation = getOperationName(message);
                } else {
                    operation = operationQName.toString();
                }
            } catch (XWSSecurityException e) {
                throw new WebServiceException(e);
            }

            packet.invocationProperties.put(CONTEXT_WSDL_OPERATION, operation);

            StaticApplicationContext sContext =
                    getPolicyContext(packet, sConfig);
            sContext.setOperationIdentifier(operation);

            ApplicationSecurityConfiguration config =
                    sConfig.getSecurityConfiguration();

            SecurityPolicy policy = config.getSecurityConfiguration(sContext);

            context = new ProcessingContextImpl(packet.invocationProperties);

            context.setPolicyContext(sContext);

            if (PolicyTypeUtil.declarativeSecurityConfiguration(policy)) {
                context.setSecurityPolicy(
                        ((DeclarativeSecurityConfiguration) policy).senderSettings());
            } else {
                context.setSecurityPolicy(policy);
            }

            context.setSecurityEnvironment(sConfig.getSecurityEnvironment());
            context.isInboundMessage(false);
            context.setSOAPMessage(message);
            SecurityAnnotator.secureMessage(context);
            packet.setMessage(Messages.create(context.getSOAPMessage()));
            return packet;

        } catch (WssSoapFaultException | XWSSecurityException soapFaultException) {
            throw new WebServiceException(soapFaultException);
        } // log the exception here


    }

    private String getOperationName(SOAPMessage message) throws XWSSecurityException {
        Node node = null;
        String key = null;
        SOAPBody body = null;

        if (message != null) {
            try {
                body = message.getSOAPBody();
            } catch (SOAPException ex) {
                throw new XWSSecurityException(ex);
            }
        } else {
            throw new XWSSecurityException(
                    "SOAPMessage in message context is null");
        }

        if (body != null) {
            node = body.getFirstChild();
        } else {
            throw new XWSSecurityException(
                    "No body element identifying an operation is found");
        }
        StringBuilder tmp = new StringBuilder("");
        String operation = "";

        for (; node != null; node = node.getNextSibling()) {
            tmp.append("{").append(node.getNamespaceURI()).append("}").append(node.getLocalName()).append(":");
        }
        operation = tmp.toString();
        if (operation.length() > 0) {
            return operation.substring(0, operation.length() - 1);
        } else {
            return operation;
        }
    }

    public SOAPFaultException getSOAPFaultException(
            WssSoapFaultException sfe, boolean isSOAP12) {

        SOAPFault fault = null;
        try {
            if (isSOAP12) {
                fault = soapFactory.createFault(sfe.getFaultString(), SOAPConstants.SOAP_SENDER_FAULT);

                fault.appendFaultSubcode(sfe.getFaultCode());
            } else {
                fault = soapFactory.createFault(sfe.getFaultString(), sfe.getFaultCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(this + ": Internal Error while trying to create a SOAPFault");
        }
        return new SOAPFaultException(fault);
    }

    private StaticApplicationContext getPolicyContext(Packet packet, SecurityConfiguration config) {

        // assumed to contain single nested container
        ApplicationSecurityConfiguration appconfig =
                config.getSecurityConfiguration();

        StaticApplicationContext iContext =
                (StaticApplicationContext) appconfig.getAllContexts().next();
        StaticApplicationContext sContext =
                new StaticApplicationContext(iContext);

        QName portQname = null;
        if (port != null) {
            portQname = port.getName();
        }
        String prt = null;

        if (portQname == null) {
            prt = "";
        } else {
            prt = portQname.toString();
        }

        sContext.setPortIdentifier(prt);
        return sContext;
    }
}
