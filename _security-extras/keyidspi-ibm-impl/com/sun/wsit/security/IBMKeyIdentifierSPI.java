/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.wsit.security;

import com.ibm.security.util.DerInputStream;
import com.ibm.security.util.DerValue;
import com.ibm.security.x509.KeyIdentifier;
import com.sun.xml.wss.core.reference.KeyIdentifierSPI;

import java.io.IOException;
import java.security.cert.X509Certificate;

public class IBMKeyIdentifierSPI extends KeyIdentifierSPI {

    public IBMKeyIdentifierSPI() {
    }

    public byte[] getSubjectKeyIdentifier(X509Certificate cert) throws KeyIdentifierSPIException {
        byte[] subjectKeyIdentifier =
                cert.getExtensionValue(SUBJECT_KEY_IDENTIFIER_OID);
        if (subjectKeyIdentifier == null) {
            return null;
        }

        try {
            KeyIdentifier keyId = null;

            DerValue derVal = new DerValue(
                    new DerInputStream(subjectKeyIdentifier).getOctetString());

            keyId = new KeyIdentifier(derVal.getOctetString());
            return keyId.getIdentifier();
        } catch (NoClassDefFoundError ncde) {
            // TODO X509 Token profile states that only the contents of the
            // OCTET STRING should be returned, excluding the "prefix"
            byte[] dest = new byte[subjectKeyIdentifier.length - 4];
            System.arraycopy(
                    subjectKeyIdentifier, 4, dest, 0, subjectKeyIdentifier.length - 4);
            return dest;

        } catch (Exception e) {
            //log exception
            throw new KeyIdentifierSPIException(e);
        }

    }
}
