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
