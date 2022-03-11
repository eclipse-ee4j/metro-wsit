/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;


/**
 * Binding defines SignatureToken and Encryption Token used from initiator to recipient and from recipient to initiator.
 * @author K.Venugopal@sun.com
 */
public interface SymmetricBinding extends Binding{

    /**
     * returns the EncryptionToken
     * @return {@link Token}
     */
    Token getEncryptionToken();
    /**
     * returns token to be used for Signature operations
     * @return {@link Token}
     */
    Token getSignatureToken();
    /**
     * return token to be used for signature and encryption operations.
     * @return {@link Token}
     */
    Token getProtectionToken();
}
