/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * WSITServerAuthConfig.java
 *
 * Created on November 1, 2006, 11:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.policy.PolicyMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author kumar jayanti
 */
public class WSITServerAuthConfig implements ServerAuthConfig {

    private String layer = null;
    private String appContext = null;
    private CallbackHandler callbackHandler = null;
    private WSITServerAuthContext serverAuthContext = null;
    private PolicyMap policyMap = null;
    private boolean secEnabled;
    private ReentrantReadWriteLock rwLock;
    private ReentrantReadWriteLock.ReadLock rLock;
    private ReentrantReadWriteLock.WriteLock wLock;

    /** Creates a new instance of WSITServerAuthConfig */
    public WSITServerAuthConfig(String layer, String appContext, CallbackHandler callbackHandler) {
        this.layer = layer;
        this.appContext = appContext;
        this.callbackHandler = callbackHandler;
        this.rwLock = new ReentrantReadWriteLock(true);
        this.rLock = rwLock.readLock();
        this.wLock = rwLock.writeLock();
    }

    public ServerAuthContext getAuthContext(String operation, Subject subject, Map rawMap) throws AuthException {
        @SuppressWarnings("unchecked") Map<Object, Object> map = rawMap;
        PolicyMap pMap = (PolicyMap) map.get("POLICY");
        WSDLPort port = (WSDLPort) map.get("WSDL_MODEL");
        if (pMap == null || pMap.isEmpty()) {
            //TODO: log warning here if pMap == null
            return null;
        }

        //check if security is enabled
        //if policy has changed due to redeploy, check if security is enabled
        try {
            rLock.lock(); // acquire read lock
            if (!secEnabled || policyMap != pMap) {
                rLock.unlock(); // must unlock read, before acquiring write lock
                wLock.lock(); // acquire write lock
                try {
                    if (!secEnabled || policyMap != pMap) { //re-check
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

        this.rLock.lock();
        try {
            if (serverAuthContext != null) {
                //return the cached one only if the same policyMap was passed in
                if (policyMap == pMap) {
                    return serverAuthContext;
                }
            }
        } finally {
            this.rLock.unlock();
        }
        // make sure you don't hold the rlock when you request the wlock
        // or you will encounter dealock
        this.wLock.lock();
        try {
            // recheck the precondition, since the rlock was released.
            if ((serverAuthContext == null) || (policyMap != pMap)) {
                serverAuthContext = new WSITServerAuthContext(operation, subject, map, callbackHandler);
                policyMap = pMap;
            }
            return serverAuthContext;
        } finally {
            this.wLock.unlock();
        }
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
}
