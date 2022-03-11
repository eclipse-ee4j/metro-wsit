/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 Eclipse Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.security.auth.message.config.ServerAuthContext;
import jakarta.security.auth.message.module.ServerAuthModule;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;


/**
 * Required wrapper for custom {@link ServerAuthModule}
 *
 * @author David Matejcek
 */
public class SAMAuthConfig implements ServerAuthConfig {

    private final String layer;
    private final String appContext;
    private final CallbackHandler handler;
    private final ServerAuthModule serverAuthModule;


    /**
     * @param layer - usually SOAP or HttpServlet
     */
    public SAMAuthConfig(String layer, String appContext, CallbackHandler handler, ServerAuthModule serverAuthModule) {
        this.layer = layer;
        this.appContext = appContext;
        this.handler = handler;
        this.serverAuthModule = serverAuthModule;
    }


    @Override
    public String getMessageLayer() {
        return layer;
    }


    @Override
    public String getAppContext() {
        return appContext;
    }


    @Override
    public String getAuthContextID(MessageInfo messageInfo) {
        return appContext;
    }


    @Override
    public void refresh() {
    }


    @Override
    public boolean isProtected() {
        return false;
    }


    @Override
    public ServerAuthContext getAuthContext(String authContextID, Subject serviceSubject,
        Map<String, Object> properties) throws AuthException {
        return new SAMAuthContext(handler, serverAuthModule);
    }
}
