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
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.ClientAuthConfig;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.security.auth.message.module.ServerAuthModule;

import javax.security.auth.callback.CallbackHandler;


/**
 * Required wrapper for custom {@link ServerAuthModule}.
 *
 * @author David Matejcek
 */
public class SAMConfigProvider implements AuthConfigProvider {

    private final ServerAuthModule serverAuthModule;


    /**
     * @param serverAuthModule - this module does all the authentication,
     */
    public SAMConfigProvider(final ServerAuthModule serverAuthModule) {
        this.serverAuthModule = serverAuthModule;
    }

    @Override
    public ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler handler)
        throws AuthException {
        return null;
    }


    @Override
    public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler handler)
        throws AuthException {
        return new SAMAuthConfig(layer, appContext, handler, this.serverAuthModule);
    }


    @Override
    public void refresh() {
    }
}
