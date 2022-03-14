/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: AuthenticatorImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.elements.Authenticator;
import com.sun.xml.ws.security.trust.impl.bindings.AuthenticatorType;


import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;

/**
 * Provides verification (authentication) of a computed hash.
 *
 * @author Manveen Kaur
 */

public class AuthenticatorImpl extends AuthenticatorType implements Authenticator {

    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);

    public AuthenticatorImpl() {
        // empty constructor
    }

    public AuthenticatorImpl(AuthenticatorType aType) throws RuntimeException{
        //ToDo
    }

    public AuthenticatorImpl(byte[] hash) {
        setRawCombinedHash(hash);
    }

    @Override
    public byte[] getRawCombinedHash() {
        return getCombinedHash();
    }

    @Override
    public final void setRawCombinedHash(final byte[] rawCombinedHash) {
        setCombinedHash(rawCombinedHash);
    }

    @Override
    public String getTextCombinedHash() {
        return Base64.getMimeEncoder().encodeToString(getRawCombinedHash());
    }

    @Override
    public void setTextCombinedHash(final String encodedCombinedHash) {
        try {
            setRawCombinedHash(Base64.getMimeDecoder().decode(encodedCombinedHash));
        } catch (IllegalArgumentException de) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0020_ERROR_DECODING(encodedCombinedHash), de);
            throw new RuntimeException(LogStringsMessages.WST_0020_ERROR_DECODING(encodedCombinedHash) , de);
        }
    }

}
