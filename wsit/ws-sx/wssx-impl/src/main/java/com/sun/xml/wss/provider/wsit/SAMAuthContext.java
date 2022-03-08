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
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.config.ServerAuthContext;
import jakarta.security.auth.message.module.ServerAuthModule;

import java.util.Collections;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;


/**
 * Required wrapper for custom {@link ServerAuthModule}
 *
 * @author David Matejcek
 */
public class SAMAuthContext implements ServerAuthContext {

    private final ServerAuthModule serverAuthModule;

    /**
     * Creates instance of this class and calls
     * {@link ServerAuthModule#initialize(jakarta.security.auth.message.MessagePolicy, jakarta.security.auth.message.MessagePolicy, CallbackHandler, java.util.Map)}
     *
     * @param handler
     * @param serverAuthModule
     * @throws AuthException
     */
    public SAMAuthContext(final CallbackHandler handler, final ServerAuthModule serverAuthModule) throws AuthException {
        this.serverAuthModule = serverAuthModule;
        this.serverAuthModule.initialize(null, null, handler, Collections.<String, Object> emptyMap());
    }


    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
        throws AuthException {
        return serverAuthModule.secureResponse(messageInfo, serviceSubject);
    }


    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        serverAuthModule.cleanSubject(messageInfo, subject);
    }
}
