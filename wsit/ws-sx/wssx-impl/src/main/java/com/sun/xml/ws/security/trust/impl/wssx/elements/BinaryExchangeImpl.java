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
 * $Id: BinaryExchangeImpl.java,v 1.2 2010-10-21 15:37:04 snajper Exp $
 */

package com.sun.xml.ws.security.trust.impl.wssx.elements;

import com.sun.xml.ws.security.trust.elements.BinaryExchange;
import com.sun.xml.ws.security.trust.impl.wssx.bindings.BinaryExchangeType;

import java.util.Base64;

/**
 *
 * @author Manveen Kaur (manveen.kaur@sun.com).
 */

public class BinaryExchangeImpl extends BinaryExchangeType implements BinaryExchange {

    public BinaryExchangeImpl(String encodingType, String valueType, byte[] rawText) {
        setEncodingType(encodingType);
        setValueType(valueType);
        setRawValue(rawText);
    }

    public BinaryExchangeImpl(BinaryExchangeType bcType) {
        setEncodingType(bcType.getEncodingType());
        setValueType(bcType.getValueType());
        setValue(bcType.getValue());
    }

    @Override
    public byte[] getRawValue() {
        try {
            return Base64.getMimeDecoder().decode(getTextValue());
        } catch (IllegalArgumentException de) {
            throw new RuntimeException("Error while decoding " +
                    de.getMessage());
        }
    }

    @Override
    public String getTextValue() {
        return super.getValue();
    }

    @Override
    public void setTextValue(String encodedText) {
        super.setValue(encodedText);
    }

    @Override
    public void setRawValue(byte[] rawText) {
        super.setValue(Base64.getMimeEncoder().encodeToString(rawText));
    }

}
