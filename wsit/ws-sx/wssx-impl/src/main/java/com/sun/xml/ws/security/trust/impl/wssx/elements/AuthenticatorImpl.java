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
 * $Id: AuthenticatorImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.trust.elements.Authenticator;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.AuthenticatorType;

import java.util.Base64;
import java.util.List;

/**
 * Provides verification (authentication) of a computed hash.
 *
 * @author Manveen Kaur
 */

public class AuthenticatorImpl extends AuthenticatorType implements Authenticator {

    public AuthenticatorImpl() {
        // empty constructor
    }

    public AuthenticatorImpl(AuthenticatorType aType) {
        //ToDo
    }

    public AuthenticatorImpl(byte[] hash) {
        setRawCombinedHash(hash);
    }

    @Override
    public List<Object> getAny() {
        return super.getAny();
    }

    @Override
    public byte[] getRawCombinedHash() {
        return getCombinedHash();
    }

    @Override
    public void setRawCombinedHash(byte[] rawCombinedHash) {
        setCombinedHash(rawCombinedHash);
    }

    @Override
    public String getTextCombinedHash() {
        return Base64.getMimeEncoder().encodeToString(getRawCombinedHash());
    }

    @Override
    public void setTextCombinedHash(String encodedCombinedHash) {
        try {
            setRawCombinedHash(Base64.getMimeDecoder().decode(encodedCombinedHash));
        } catch (IllegalArgumentException de) {
            throw new RuntimeException("Error while decoding " +
                    de.getMessage());
        }
    }

}
