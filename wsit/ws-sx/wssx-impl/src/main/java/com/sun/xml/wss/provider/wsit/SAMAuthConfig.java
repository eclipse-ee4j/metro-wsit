/*
 * Copyright (c) 2022 Eclipse Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
     * @param appContext
     * @param handler
     * @param serverAuthModule
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
