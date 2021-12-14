/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.security.impl.policyconv.SecurityPolicyHolder;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 *
 * Holds all the Translated info for one PolicyAlternative
 */
public class PolicyAlternativeHolder {

    protected static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSIT_PVD_DOMAIN,
            LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE);    
    private HashMap<WSDLBoundOperation, SecurityPolicyHolder> outMessagePolicyMap = null;
    private HashMap<WSDLBoundOperation, SecurityPolicyHolder> inMessagePolicyMap = null;
    private HashMap<String, SecurityPolicyHolder> outProtocolPM = null;
    private HashMap<String, SecurityPolicyHolder> inProtocolPM = null;
//TODO:POLALT in future all of these can be per-alternative
//    private boolean hasIssuedTokens = false;
//    private boolean hasSecureConversation = false;
//    private boolean hasReliableMessaging = false;
//    private boolean hasMakeConnection = false;
//    private boolean hasKerberosToken = false;
//    protected AlgorithmSuite bindingLevelAlgSuite = null;
//    private AlgorithmSuite bootStrapAlgoSuite;
    protected Policy bpMSP = null;
    protected SecurityPolicyVersion spVersion;
    private String uuid;

    public PolicyAlternativeHolder(AssertionSet assertions, SecurityPolicyVersion sv, Policy bpMSP) {
        //this.alternative = assertions;
        this.spVersion = sv;
        this.bpMSP = bpMSP;
        uuid = UUID.randomUUID().toString();
        this.inMessagePolicyMap = new HashMap<>();
        this.outMessagePolicyMap = new HashMap<>();
        this.inProtocolPM = new HashMap<>();
        this.outProtocolPM = new HashMap<>();
    }

    public void putToOutMessagePolicyMap(WSDLBoundOperation op, SecurityPolicyHolder sh) {
        this.outMessagePolicyMap.put(op, sh);
    }

    public SecurityPolicyHolder getFromOutMessagePolicyMap(WSDLBoundOperation op) {
        return this.outMessagePolicyMap.get(op);
    }

    public void putToInMessagePolicyMap(WSDLBoundOperation op, SecurityPolicyHolder sh) {
        this.inMessagePolicyMap.put(op, sh);
    }

    public SecurityPolicyHolder getFromInMessagePolicyMap(WSDLBoundOperation op) {
        return this.inMessagePolicyMap.get(op);
    }

    public void putToOutProtocolPolicyMap(String protocol, SecurityPolicyHolder sh) {
        this.outProtocolPM.put(protocol, sh);
    }

    public SecurityPolicyHolder getFromOutProtocolPolicyMap(String protocol) {
        return this.outProtocolPM.get(protocol);
    }

    public void putToInProtocolPolicyMap(String protocol, SecurityPolicyHolder sh) {
        this.inProtocolPM.put(protocol, sh);
    }

    public SecurityPolicyHolder getFromInProtocolPolicyMap(String protocol) {
        return this.inProtocolPM.get(protocol);
    }

    /**
     * @return the uuid, a unique ID to identify the PolicyAlternative
     *         for use by the Security Runtime
     */
    public String getId() {
        return uuid;
    }

    /**
     * @return the outMessagePolicyMap
     */
    public HashMap<WSDLBoundOperation, SecurityPolicyHolder> getOutMessagePolicyMap() {
        return outMessagePolicyMap;
    }

    /**
     * @return the inMessagePolicyMap
     */
    public HashMap<WSDLBoundOperation, SecurityPolicyHolder> getInMessagePolicyMap() {
        return inMessagePolicyMap;
    }

    /**
     * @return the outProtocolPM
     */
    public HashMap<String, SecurityPolicyHolder> getOutProtocolPM() {
        return outProtocolPM;
    }

    /**
     * @return the inProtocolPM
     */
    public HashMap<String, SecurityPolicyHolder> getInProtocolPM() {
        return inProtocolPM;
    }
}
