/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.jaxws.impl;

import com.sun.xml.ws.api.policy.ModelTranslator;
import com.sun.xml.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.rx.mc.api.McProtocolVersion;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * TODO: Make this configurable
 * @author K.Venugopal@sun.com
 */
public class RMPolicyResolver {

    SecurityPolicyVersion spVersion;
    RmProtocolVersion rmVersion;
    McProtocolVersion mcVersion;
    boolean encrypt = false;

    /** Creates a new instance of RMPolicyResolver */
    public RMPolicyResolver() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY200507;
        rmVersion = RmProtocolVersion.WSRM200502;
        mcVersion = McProtocolVersion.WSMC200702;
    }

    public RMPolicyResolver(SecurityPolicyVersion spVersion, RmProtocolVersion rmVersion) {
        this.spVersion = spVersion;
        this.rmVersion = rmVersion;
        mcVersion = McProtocolVersion.WSMC200702;
    }

    public RMPolicyResolver(SecurityPolicyVersion spVersion, RmProtocolVersion rmVersion, McProtocolVersion mcVersion, boolean encrypt) {
        this.spVersion = spVersion;
        this.rmVersion = rmVersion;
        this.mcVersion = mcVersion;
        this.encrypt = encrypt;
    }

    public Policy getOperationLevelPolicy() throws PolicyException{
        PolicySourceModel model;
        try {
            String rmMessagePolicy = encrypt ? "rm-msglevel-policy-encrypt.xml" : "rm-msglevel-policy.xml";
            if(SecurityPolicyVersion.SECURITYPOLICY12NS == spVersion && RmProtocolVersion.WSRM200502 == rmVersion){
                rmMessagePolicy = "rm-msglevel-policy-sp12.xml";
            }else if(SecurityPolicyVersion.SECURITYPOLICY12NS == spVersion && (RmProtocolVersion.WSRM200702  == rmVersion )){
                rmMessagePolicy = encrypt ? "rm-msglevel-policy-sx-encrypt.xml" :"rm-msglevel-policy-sx.xml";
            }else if(SecurityPolicyVersion.SECURITYPOLICY200507 == spVersion && (RmProtocolVersion.WSRM200702 == rmVersion )){
                rmMessagePolicy = "rm-msglevel-policy-sx-sp10.xml";
            }
            model = unmarshalPolicy("com/sun/xml/ws/security/impl/policyconv/" + rmMessagePolicy);
        }catch (IOException ex) {
            throw new PolicyException(ex);
        }
        Policy mbp = ModelTranslator.getTranslator().translate(model);
        return mbp;
    }

    private PolicySourceModel unmarshalPolicy(String resource) throws PolicyException, IOException {
        Reader reader = getResourceReader(resource);
        PolicySourceModel model = ModelUnmarshaller.getUnmarshaller().unmarshalModel(reader);
        reader.close();
        return model;
    }

    private Reader getResourceReader(String resourceName) {
        return new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
    }
}
