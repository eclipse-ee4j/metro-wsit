/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.security.auth.callback.CallbackHandler;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.ClientAuthConfig;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.xml.ws.WebServiceException;

/**
 *
 * @author kumar.jayanti
 */
public class WSITAuthConfigProvider implements AuthConfigProvider {

    private final WeakHashMap<String, ClientAuthConfig> clientConfigMap = new WeakHashMap<>();
    private final WeakHashMap<String, ServerAuthConfig> serverConfigMap = new WeakHashMap<>();

    private final ReentrantReadWriteLock rwLock;
    private final ReentrantReadWriteLock.ReadLock rLock;
    private final ReentrantReadWriteLock.WriteLock wLock;

    /** Creates a new instance of WSITAuthConfigProvider */
    public WSITAuthConfigProvider(Map<String, String> props, AuthConfigFactory factory) {
        if (factory != null) {
            factory.registerConfigProvider(this, "SOAP", null, "WSIT AuthConfigProvider");
        }
        this.rwLock = new ReentrantReadWriteLock(true);
        this.rLock = rwLock.readLock();
        this.wLock = rwLock.writeLock();
    }

    @Override
    public  ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler callbackHandler) {
        ClientAuthConfig clientConfig;
        this.rLock.lock();
        try {
            clientConfig = this.clientConfigMap.get(appContext);
            if (clientConfig != null) {
                return clientConfig;
            }
        } finally {
            this.rLock.unlock();
        }
        // make sure you don't hold the rlock when you request the wlock
        // or you will encounter dealock
        this.wLock.lock();
        try {
            clientConfig = new WSITClientAuthConfig(layer, appContext, callbackHandler);
            this.clientConfigMap.put(appContext, clientConfig);
            return clientConfig;
        } finally {
            this.wLock.unlock();
        }
    }

    @Override
    public  ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler callbackHandler) {
        ServerAuthConfig serverConfig;
        this.rLock.lock();
        try {
            serverConfig = this.serverConfigMap.get(appContext);
            if (serverConfig != null) {
                return serverConfig;
            }
        } finally {
            this.rLock.unlock();
        }
        // make sure you don't hold the rlock when you request the wlock
        // or you will encounter dealock
        this.wLock.lock();
        try {
            serverConfig = new WSITServerAuthConfig(layer, appContext, callbackHandler);
            this.serverConfigMap.put(appContext,serverConfig);
            return serverConfig;
        } finally {
            this.wLock.unlock();
        }
    }

    @Override
    public void refresh() {
    }

    /**
     * Checks to see whether WS-Security is enabled or not.
     *
     * @param policyMap policy map for {@code this} assembler
     * @param wsdlPort wsdl:port
     * @return true if Security is enabled, false otherwise
     */

    public static boolean isSecurityEnabled(PolicyMap policyMap, WSDLPort wsdlPort) {
        if (policyMap == null || wsdlPort == null) {
            return false;
        }

        try {
            PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(wsdlPort.getOwner().getName(),
                    wsdlPort.getName());
            Policy policy = policyMap.getEndpointEffectivePolicy(endpointKey);

            if ((policy != null) &&
                    (policy.contains(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri) ||
                        policy.contains(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)||
                        policy.contains(SecurityPolicyVersion.SECURITYPOLICY200512.namespaceUri))) {
                return true;
            }

            for (WSDLBoundOperation wbo : wsdlPort.getBinding().getBindingOperations()) {
                PolicyMapKey operationKey = PolicyMap.createWsdlOperationScopeKey(wsdlPort.getOwner().getName(),
                        wsdlPort.getName(),
                        wbo.getName());
                policy = policyMap.getOperationEffectivePolicy(operationKey);
                if ((policy != null) &&
                       (policy.contains(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri) ||
                            policy.contains(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri))) {
                    return true;
                }

                policy = policyMap.getInputMessageEffectivePolicy(operationKey);
                if ((policy != null) &&
                        (policy.contains(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri) ||
                            policy.contains(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri))) {
                    return true;
                }

                policy = policyMap.getOutputMessageEffectivePolicy(operationKey);
                if ((policy != null) &&
                        (policy.contains(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri) ||
                            policy.contains(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri))) {
                    return true;
                }

                policy = policyMap.getFaultMessageEffectivePolicy(operationKey);
                if ((policy != null) &&
                        (policy.contains(SecurityPolicyVersion.SECURITYPOLICY200507.namespaceUri) ||
                            policy.contains(SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri))) {
                    return true;
                }
            }
        } catch (PolicyException e) {
            throw new WebServiceException(e);
        }

        return false;
    }

}
