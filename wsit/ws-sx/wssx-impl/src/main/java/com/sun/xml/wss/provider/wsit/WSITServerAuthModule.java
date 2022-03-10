/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * WSITServerAuthModule.java
 *
 * Created on November 5, 2006, 8:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.api.message.Message;

import java.security.Principal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.security.auth.message.module.ServerAuthModule;
import jakarta.xml.soap.SOAPMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;
import com.sun.xml.wss.provider.wsit.logging.LogStringsMessages;

/**
 * @author kumar.jayanti
 */
public class WSITServerAuthModule implements ServerAuthModule {

    private static final Logger log = Logger.getLogger(
        LogDomainConstants.WSIT_PVD_DOMAIN,
        LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE
    );

    private Class<?>[] supported = new Class[2];

    /** Creates a new instance of WSITServerAuthModule */
    public WSITServerAuthModule() {
        supported[0] = SOAPMessage.class;
        supported[1] = Message.class;
    }


    /**
     * Creates a new instance of WSITServerAuthModule
     *
     * @param supported see {@link #getSupportedMessageTypes()}
     */
    public WSITServerAuthModule(final Class<?>... supported) {
        this.supported = supported;
    }


    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler,
        Map<String, Object> options) {
    }


    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return supported;
    }


    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) {
        return AuthStatus.SUCCESS;
    }


    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) {
        return AuthStatus.SUCCESS;
    }


    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        if (subject == null) {
            log.log(Level.SEVERE, LogStringsMessages.WSITPVD_0037_NULL_SUBJECT());
            throw new AuthException(LogStringsMessages.WSITPVD_0037_NULL_SUBJECT());
        }

        if (!subject.isReadOnly()) {
            return;
        }

        Set<Principal> principals = subject.getPrincipals();
        Set<?> privateCredentials = subject.getPrivateCredentials();
        Set<?> publicCredentials = subject.getPublicCredentials();

        try {
            principals.clear();
        } catch (UnsupportedOperationException uoe) {
            log.log(Level.SEVERE, LogStringsMessages.WSITPVD_0064_ERROR_CLEAN_SUBJECT(), uoe);
        }

        Iterator<?> pi = privateCredentials.iterator();
        while (pi.hasNext()) {
            try {
                Destroyable dstroyable = (Destroyable) pi.next();
                dstroyable.destroy();
            } catch (DestroyFailedException dfe) {
                log.log(Level.SEVERE, LogStringsMessages.WSITPVD_0064_ERROR_CLEAN_SUBJECT(), dfe);
            } catch (ClassCastException cce) {
                log.log(Level.SEVERE, LogStringsMessages.WSITPVD_0064_ERROR_CLEAN_SUBJECT(), cce);
            }
        }

        Iterator<?> qi = publicCredentials.iterator();
        while (qi.hasNext()) {
            try {
                Destroyable dstroyable = (Destroyable) qi.next();
                dstroyable.destroy();
            } catch (DestroyFailedException dfe) {
                log.log(Level.SEVERE, LogStringsMessages.WSITPVD_0064_ERROR_CLEAN_SUBJECT(), dfe);
            } catch (ClassCastException cce) {
                log.log(Level.SEVERE, LogStringsMessages.WSITPVD_0064_ERROR_CLEAN_SUBJECT(), cce);
            }
        }
    }

}
