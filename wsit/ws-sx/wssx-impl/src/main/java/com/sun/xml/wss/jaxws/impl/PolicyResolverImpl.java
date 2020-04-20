/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.jaxws.impl;

import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.security.impl.policyconv.SCTokenWrapper;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.impl.PolicyResolver;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.security.impl.policyconv.SecurityPolicyHolder;
import com.sun.xml.wss.ProcessingContext;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConstants;
import org.w3c.dom.Node;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPException;
import java.util.HashMap;
import org.w3c.dom.NodeList;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.ws.api.addressing.*;
import com.sun.xml.ws.rx.mc.api.McProtocolVersion;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.secconv.WSSCVersion;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.wss.impl.ProcessingContextImpl;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.jaxws.impl.logging.LogDomainConstants;
import java.util.logging.Logger;

/**
 *
 * @author Ashutosh.Shahi@sun.com
 */
public class PolicyResolverImpl implements PolicyResolver {
    
    protected static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSS_JAXWS_IMPL_DOMAIN,
            LogDomainConstants.WSS_JAXWS_IMPL_DOMAIN_BUNDLE);

    private WSDLBoundOperation cachedOperation = null;
    private HashMap<WSDLBoundOperation, SecurityPolicyHolder> inMessagePolicyMap = null;
    private HashMap<String, SecurityPolicyHolder> inProtocolPM = null;
    //private PolicyAttributes pa = null;
    private AddressingVersion addVer = null;
    private RmProtocolVersion rmVer = null;
    private McProtocolVersion mcVer = null;
    private TubeConfiguration tubeConfig = null;
    private boolean isClient = false;
    private boolean isSCMessage = false;
    //private boolean isTrustOrSCMessage = false;
    private String action = "";
    private WSTrustVersion wstVer = WSTrustVersion.WS_TRUST_10;
    private WSSCVersion wsscVer = WSSCVersion.WSSC_10;

    /**
     * Creates a new instance of OperationResolverImpl
     */
    public PolicyResolverImpl(HashMap<WSDLBoundOperation, SecurityPolicyHolder> inMessagePolicyMap, HashMap<String, SecurityPolicyHolder> ip, WSDLBoundOperation cachedOperation, TubeConfiguration tubeConfig, AddressingVersion addVer, boolean isClient, RmProtocolVersion rmVer, McProtocolVersion mcVer) {
        this.inMessagePolicyMap = inMessagePolicyMap;
        this.inProtocolPM = ip;
        this.cachedOperation = cachedOperation;
        this.tubeConfig = tubeConfig;
        this.addVer = addVer;
        this.isClient = isClient;
        this.rmVer = rmVer;
        this.mcVer = mcVer;
    }

    public SecurityPolicy resolvePolicy(ProcessingContext ctx) {
        Message msg = null;
        SOAPMessage soapMsg = null;
        if (ctx instanceof JAXBFilterProcessingContext) {
            msg = ((JAXBFilterProcessingContext) ctx).getJAXWSMessage();
        } else {
            soapMsg = ctx.getSOAPMessage();
            msg = Messages.create(soapMsg);
        }
        if (((ProcessingContextImpl) ctx).getSecurityPolicyVersion().equals(
                SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            wstVer = WSTrustVersion.WS_TRUST_13;
            wsscVer = WSSCVersion.WSSC_13;
        }

        SecurityPolicy mp = null;

        action = getAction(msg);
        if (isRMMessage() || isMCMessage()) {
            SecurityPolicyHolder holder = inProtocolPM.get("RM");
            return holder.getMessagePolicy();
        }

        if (isSCCancel()) {
            SecurityPolicyHolder holder = inProtocolPM.get("SC-CANCEL");
            /*SecurityPolicyHolder holder = inProtocolPM.get("SC");
            if (WSSCVersion.WSSC_13.getNamespaceURI().equals(wsscVer.getNamespaceURI())){
            holder = inProtocolPM.get("RM");
            }*/
            return holder.getMessagePolicy();
        }
        isSCMessage = isSCMessage();
        if (isSCMessage) {
            Token scToken = (Token) getInBoundSCP();
            return getInboundXWSBootstrapPolicy(scToken);
        }

        if (msg.isFault()) {
            if (soapMsg == null) {
                try {
                    soapMsg = msg.readAsSOAPMessage();
                } catch (SOAPException ex) {
                    //ex.printStackTrace();
                }
            }
            mp = getInboundFaultPolicy(soapMsg);
        } else {
            mp = getInboundXWSSecurityPolicy(msg);
        }

        if (mp == null) {
            return new MessagePolicy();
        }
        return mp;
    }

    protected PolicyAssertion getInBoundSCP() {

        SecurityPolicyHolder sph = null;
        Collection coll = inMessagePolicyMap.values();
        Iterator itr = coll.iterator();

        while (itr.hasNext()) {
            SecurityPolicyHolder ph = (SecurityPolicyHolder) itr.next();
            if (ph != null) {
                sph = ph;
                break;
            }
        }
        if (sph == null) {
            return null;
        }
        List<PolicyAssertion> policies = sph.getSecureConversationTokens();
        if (!policies.isEmpty()) {
            return (PolicyAssertion) policies.get(0);
        }
        return null;
    }

    private SecurityPolicy getInboundXWSSecurityPolicy(Message msg) {
        SecurityPolicy mp = null;

        //Review : Will this return operation name in all cases , doclit,rpclit, wrap / non wrap ?
        WSDLBoundOperation operation = null;
        if (cachedOperation != null) {
            operation = cachedOperation;
        } else {
            operation = msg.getOperation(tubeConfig.getWSDLPort());
            if (operation == null) {
                operation = getWSDLOpFromAction();
            }
        }

        SecurityPolicyHolder sph = (SecurityPolicyHolder) inMessagePolicyMap.get(operation);
        //TODO: pass isTrustMessage Flag to this method later
        if (sph == null && (isTrustMessage() || isSCMessage)) {
            operation = getWSDLOpFromAction();
            sph = (SecurityPolicyHolder) inMessagePolicyMap.get(operation);
        }
        if (sph == null) {
            return null;
        }

        mp = sph.getMessagePolicy();

        return mp;
    }

    private SecurityPolicy getInboundFaultPolicy(SOAPMessage msg) {
        if (cachedOperation != null) {
            WSDLOperation operation = cachedOperation.getOperation();
            SecurityPolicyHolder sph = inMessagePolicyMap.get(cachedOperation);
            try {
                SOAPBody body = msg.getSOAPBody();
                NodeList nodes = body.getElementsByTagName("detail");
                if (nodes.getLength() == 0) {
                    nodes = body.getElementsByTagNameNS(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Detail");
                }
                if(nodes.getLength() == 0) {
                    nodes = body.getElementsByTagNameNS(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "detail");
                }
                if(nodes.getLength() == 0) {
                    return sph.getMessagePolicy();                    
                }
                if (nodes.getLength() > 0) {
                    Node node = nodes.item(0);
                    Node faultNode = node.getFirstChild();
                    while (faultNode != null && faultNode.getNodeType() != Node.ELEMENT_NODE)
                            faultNode = faultNode.getNextSibling();   //fix for bug #1487
                    
                    if (faultNode == null) {
                        return new MessagePolicy();
                    }
                    final String uri = faultNode.getNamespaceURI();
                    final QName faultDetail;
                    if (uri != null && uri.length() > 0) {
                        faultDetail = new QName(uri, faultNode.getLocalName());
                    } else {
                        faultDetail = new QName(faultNode.getLocalName());
                    }
                    WSDLFault fault = operation.getFault(faultDetail);                    
                    SecurityPolicyHolder faultPolicyHolder = sph.getFaultPolicy(fault);
                    SecurityPolicy faultPolicy = (faultPolicyHolder == null) ? new MessagePolicy() : faultPolicyHolder.getMessagePolicy();
                    return faultPolicy;
                }
            } catch (SOAPException sx) {
                //sx.printStackTrace();
                //log error
            }
        }
        return new MessagePolicy();

    }

    private boolean isTrustMessage() {
        if (wstVer.getIssueRequestAction().equals(action) ||
                wstVer.getIssueResponseAction().equals(action)) {
            return true;
        }
        return false;

    }

    private boolean isRMMessage() {
        return rmVer.isProtocolAction(action);
    }

    private boolean isMCMessage() {
        return mcVer.isProtocolAction(action);
    }

    private String getAction(Message msg) {
        if (addVer != null) {
            MessageHeaders hl = msg.getHeaders();
            String retVal = AddressingUtils.getAction(hl, addVer, tubeConfig.getBinding().getSOAPVersion());
            return retVal;
        }
        return "";

    }

    private SecurityPolicy getInboundXWSBootstrapPolicy(Token scAssertion) {
        if(scAssertion == null){
            return null;
        }
        return ((SCTokenWrapper) scAssertion).getMessagePolicy();
    }

    private boolean isSCMessage() {
        if (wsscVer.getSCTRequestAction().equals(action) ||
                wsscVer.getSCTResponseAction().equals(action) ||
                wsscVer.getSCTRenewRequestAction().equals(action) ||
                wsscVer.getSCTRenewResponseAction().equals(action)) {
            return true;
        }
        return false;
    }

    private boolean isSCCancel() {

        if (wsscVer.getSCTCancelResponseAction().equals(action) ||
                wsscVer.getSCTCancelRequestAction().equals(action)) {
            return true;
        }
        return false;
    }

    private String getAction(WSDLOperation operation) {
        if (!isClient) {
            return operation.getInput().getAction();
        } else {
            return operation.getOutput().getAction();
        }
    }

    private WSDLBoundOperation getWSDLOpFromAction() {
        Set<WSDLBoundOperation> keys = inMessagePolicyMap.keySet();
        for (WSDLBoundOperation wbo : keys) {
            WSDLOperation wo = wbo.getOperation();
            // WsaWSDLOperationExtension extensions = wo.getExtension(WsaWSDLOperationExtension.class);
            String confAction = getAction(wo);
            if (confAction != null && confAction.equals(action)) {
                return wbo;
            }
        }
        return null;
    }
}

