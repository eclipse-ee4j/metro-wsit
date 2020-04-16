/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * WSITClientAuthConfig.java
 *
 * Created on November 1, 2006, 11:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.security.secconv.WSSecureConversationException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ClientAuthContext;
import jakarta.xml.bind.JAXBElement;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;
import com.sun.xml.wss.provider.wsit.logging.LogStringsMessages;
import java.util.Collections;
import java.util.WeakHashMap;

/**
 *
 * @author kumar jayanti
 */
public class WSITClientAuthConfig implements ClientAuthConfig {

    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSIT_PVD_DOMAIN,
            LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE);
    private String layer = null;
    private String appContext = null;
    private CallbackHandler callbackHandler = null;    
    //private PolicyMap policyMap = null;
    private ReentrantReadWriteLock rwLock;
    private ReentrantReadWriteLock.ReadLock rLock;
    private ReentrantReadWriteLock.WriteLock wLock;
    private volatile boolean secEnabled;
    private Map<Integer, WSITClientAuthContext> tubetoClientAuthContextHash = Collections.synchronizedMap(new WeakHashMap<Integer, WSITClientAuthContext>());
    /** Creates a new instance of WSITClientAuthConfig */
    public WSITClientAuthConfig(String layer, String appContext, CallbackHandler callbackHandler) {
        this.layer = layer;
        this.appContext = appContext;
        this.callbackHandler = callbackHandler;
        this.rwLock = new ReentrantReadWriteLock(true);
        this.rLock = rwLock.readLock();
        this.wLock = rwLock.writeLock();
    }

    public ClientAuthContext getAuthContext(String operation, Subject subject, Map rawMap) throws AuthException {
        @SuppressWarnings("unchecked") Map<Object, Object> map = rawMap;

        PolicyMap pMap = (PolicyMap) map.get("POLICY");
        WSDLPort port = (WSDLPort) map.get("WSDL_MODEL");
        Object tubeOrPipe = map.get(PipeConstants.SECURITY_PIPE);
        Integer hashCode = (tubeOrPipe != null) ? tubeOrPipe.hashCode() : null;
        map.put(PipeConstants.AUTH_CONFIG, this);

        if (pMap == null || pMap.isEmpty()) {
            return null;
        }
        /*if ( hashCode == null) {
        //this is a cloned pipe
        log.log(Level.INFO, "called getAuthContext() of WsitClientAuthConfig");
        return clientAuthContext;
        }*/
        //now check if security is enabled
        //if the policy has changed due to redeploy recheck if security is enabled
        try {
            rLock.lock(); // acquire read lock
            if (!secEnabled || !tubetoClientAuthContextHash.containsKey( hashCode)) {
                rLock.unlock(); // must unlock read, before acquiring write lock
                wLock.lock(); // acquire write lock
                try {
                    if (!secEnabled || !tubetoClientAuthContextHash.containsKey( hashCode)) { //re-check
                        if (!WSITAuthConfigProvider.isSecurityEnabled(pMap, port)) {
                            return null;
                        }
                        secEnabled = true;
                    }
                } finally {
                    rLock.lock(); // reacquire read before releasing write lock
                    wLock.unlock(); //release write lock
                }
            }
        } finally {
            rLock.unlock(); // release read lock
        }
        WSITClientAuthContext clientAuthContext = null;
        this.rLock.lock();
        try {
            if (tubetoClientAuthContextHash.containsKey(hashCode)) {                
                clientAuthContext = (WSITClientAuthContext) tubetoClientAuthContextHash.get(hashCode);
            }
        } finally {
            this.rLock.unlock();
        }

        if (clientAuthContext == null) {
            this.wLock.lock();
            try {
                // recheck the precondition, since the rlock was released.                
                if (!tubetoClientAuthContextHash.containsKey( hashCode)) {
                    clientAuthContext = new WSITClientAuthContext(operation, subject, map, callbackHandler);
                    tubetoClientAuthContextHash.put( hashCode, clientAuthContext);
                }
            } finally {
                this.wLock.unlock();
            }
        }

        this.startSecureConversation(map, clientAuthContext);
        return clientAuthContext;
    }

    public String getMessageLayer() {
        return layer;
    }

    public String getAppContext() {
        return appContext;
    }

    public String getOperation(MessageInfo messageInfo) {
        return null;
    }

    public void refresh() {
    }

    public String getAuthContextID(MessageInfo messageInfo) {
        return null;
    }

    public boolean isProtected() {
        return true;
    }

    public ClientAuthContext cleanupAuthContext(Integer hashCode) {
        return this.tubetoClientAuthContextHash.remove( hashCode);
    }

    @SuppressWarnings("unchecked")
    private JAXBElement startSecureConversation(Map map, WSITClientAuthContext clientAuthContext) {
        //check if we need to start secure conversation
        JAXBElement ret = null;
        try {
            MessageInfo info = (MessageInfo) map.get("SECURITY_TOKEN");
            if (info != null) {
                Packet packet = (Packet) info.getMap().get(WSITAuthContextBase.REQ_PACKET);
                if (packet != null) {
                    if (clientAuthContext != null) {
                        ret = ((WSITClientAuthContext) clientAuthContext).startSecureConversation(packet);
                        //map.put("SECURITY_TOKEN", ret);
                        info.getMap().put("SECURITY_TOKEN", ret);
                    } else {
                        log.log(Level.SEVERE,
                                LogStringsMessages.WSITPVD_0024_NULL_CLIENT_AUTH_CONTEXT());
                        throw new WSSecureConversationException(
                                LogStringsMessages.WSITPVD_0024_NULL_CLIENT_AUTH_CONTEXT());
                    }
                } else {
                    log.log(Level.SEVERE, LogStringsMessages.WSITPVD_0025_NULL_PACKET());
                    throw new RuntimeException(LogStringsMessages.WSITPVD_0025_NULL_PACKET());
                }
            }
        } catch (WSSecureConversationException ex) {
            log.log(Level.SEVERE, LogStringsMessages.WSITPVD_0026_ERROR_STARTING_SC(), ex);
            throw new RuntimeException(LogStringsMessages.WSITPVD_0026_ERROR_STARTING_SC(), ex);
        }
        return ret;
    }
}
