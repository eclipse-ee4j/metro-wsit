/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.wsit.security;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * @author sk112103
 */
public class SunKeyIdentifierSPI extends com.sun.xml.wss.core.reference.KeyIdentifierSPI {

    /**
     * total header length
     */
    private static final byte HEADER_LENGTH = 4;

    /**
     * length of a header value
     */
    private static final byte HEADER_FIELD_LENGTH = 1;

    /**
     * position (0-based) of the header field that contains the expected length of the actual value in byte
     */
    private static final byte HEADER_FIELD_VALUE_LENGTH = 3;

    /**
     * Creates a new instance of SunKeyIdentifierSPI
     */
    public SunKeyIdentifierSPI() {
    }

    @Override
    public byte[] getSubjectKeyIdentifier(X509Certificate cert) throws KeyIdentifierSPIException {
        byte[] subjectKeyIdentifier = cert.getExtensionValue(SUBJECT_KEY_IDENTIFIER_OID);
        if (subjectKeyIdentifier == null) {
            return null;
        }

        try {
            if (subjectKeyIdentifier.length < HEADER_LENGTH) {
                throw new IllegalArgumentException("subjectKeyIdentifier too short, header is missing");
            }

            final int valueLength = (new BigInteger(
                    Arrays.copyOfRange(subjectKeyIdentifier, HEADER_FIELD_VALUE_LENGTH, HEADER_FIELD_VALUE_LENGTH + HEADER_FIELD_LENGTH)))
                    .intValue();
            final int expectedTotalLength = HEADER_LENGTH + valueLength;
            if (subjectKeyIdentifier.length < expectedTotalLength) {
                throw new IllegalArgumentException(String.format("subjectKeyIdentifier too short (expected=%d, actual=%d)", expectedTotalLength, subjectKeyIdentifier.length));
            } else if (subjectKeyIdentifier.length > expectedTotalLength) {
                throw new IllegalArgumentException(String.format("subjectKeyIdentifier too big (expected=%d, actual=%d)", expectedTotalLength, subjectKeyIdentifier.length));
            }
            return Arrays.copyOfRange(subjectKeyIdentifier, HEADER_LENGTH, HEADER_LENGTH + valueLength);

        } catch (Exception e) {
            //log exception
            throw new KeyIdentifierSPIException(e);
        }
    }
}
