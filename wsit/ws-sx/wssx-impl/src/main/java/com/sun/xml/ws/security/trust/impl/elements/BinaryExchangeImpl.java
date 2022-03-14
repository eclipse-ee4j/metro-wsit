/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: BinaryExchangeImpl.java,v 1.2 2010-10-21 15:36:54 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.elements;

import com.sun.xml.ws.security.trust.elements.BinaryExchange;
import com.sun.xml.ws.security.trust.impl.bindings.BinaryExchangeType;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.ws.security.trust.logging.LogDomainConstants;

import com.sun.istack.NotNull;

import com.sun.xml.ws.security.trust.logging.LogStringsMessages;

/**
 *
 * @author Manveen Kaur (manveen.kaur@sun.com).
 */

public class BinaryExchangeImpl extends BinaryExchangeType implements BinaryExchange {
    
    private static final Logger log =
            Logger.getLogger(
            LogDomainConstants.TRUST_IMPL_DOMAIN,
            LogDomainConstants.TRUST_IMPL_DOMAIN_BUNDLE);
    
    public BinaryExchangeImpl(String encodingType, String valueType, byte[] rawText) {
        setEncodingType(encodingType);
        setValueType(valueType);
        setRawValue(rawText);
    }
    
    public BinaryExchangeImpl(BinaryExchangeType bcType)throws RuntimeException{
        setEncodingType(bcType.getEncodingType());
        setValueType(bcType.getValueType());
        setValue(bcType.getValue());
    }
    
    public byte[] getRawValue() {
        try {
            return Base64.getMimeDecoder().decode(getTextValue());
        } catch (IllegalArgumentException de) {
            log.log(Level.SEVERE,
                    LogStringsMessages.WST_0020_ERROR_DECODING(getTextValue()), de);
            throw new RuntimeException(LogStringsMessages.WST_0020_ERROR_DECODING(getTextValue()), de);
        }
    }
    
    public String getTextValue() {
        return super.getValue();
    }
    
    public void setTextValue(@NotNull final String encodedText) {
        super.setValue(encodedText);
    }
    
    public final void setRawValue(@NotNull final byte[] rawText) {
        super.setValue(Base64.getMimeEncoder().encodeToString(rawText));
    }
    
}
