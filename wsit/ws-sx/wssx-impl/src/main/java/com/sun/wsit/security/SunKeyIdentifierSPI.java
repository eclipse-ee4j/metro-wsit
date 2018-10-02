/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.wsit.security;

import java.io.IOException;
import java.security.cert.X509Certificate;
/**
 *
 * @author sk112103
 */
public class SunKeyIdentifierSPI extends com.sun.xml.wss.core.reference.KeyIdentifierSPI {
    
    /** Creates a new instance of SunKeyIdentifierSPI */
    public SunKeyIdentifierSPI() {
    }

    public byte[] getSubjectKeyIdentifier(X509Certificate cert) throws KeyIdentifierSPIException {
        byte[] subjectKeyIdentifier =
                cert.getExtensionValue(SUBJECT_KEY_IDENTIFIER_OID);
        if (subjectKeyIdentifier == null)
            return null;
        
        try {
            sun.security.x509.KeyIdentifier keyId = null;
            
            sun.security.util.DerValue derVal = new sun.security.util.DerValue(
                    new sun.security.util.DerInputStream(subjectKeyIdentifier).getOctetString());
            
            keyId = new sun.security.x509.KeyIdentifier(derVal.getOctetString());
            return keyId.getIdentifier();
        } catch (NoClassDefFoundError ncde) {
            // TODO X509 Token profile states that only the contents of the
            // OCTET STRING should be returned, excluding the "prefix"
            byte[] dest = new byte[subjectKeyIdentifier.length-4];
            System.arraycopy(
                    subjectKeyIdentifier, 4, dest, 0, subjectKeyIdentifier.length-4);
            return dest;
            
        } catch (IOException e) {
            //log exception
            throw new KeyIdentifierSPIException(e);
        }

    }
    
    
}
