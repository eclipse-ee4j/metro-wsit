/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

/**
 * Represents Asymmetric Token information to be used for Signature and Encryption
 * by the client and the service. If the message pattern requires multiple messages,
 * this binding defines that the Initiator Token is used for the message signature
 * from initiator to the recipient, and for encryption from recipient to initiator.
 * The Recipient Token is used for encryption from initiator to recipient, and for
 * the message signature from recipient to initiator. This interface represents normalized
 * AsymmetricBinding security policy assertion as shown below.
 *
 * <pre>{@code
 *  <xmp>
 *      <sp:AsymmetricBinding ... >
 *              <wsp:Policy>
 *                  <sp:InitiatorToken>
 *                      <wsp:Policy> ... </wsp:Policy>
 *                  </sp:InitiatorToken>
 *                  <sp:RecipientToken>
 *                      <wsp:Policy> ... </wsp:Policy>
 *                  </sp:RecipientToken>
 *                  <sp:AlgorithmSuite ... >
 *                      ...
 *                  </sp:AlgorithmSuite>
 *                  <sp:Layout ... > ... </sp:Layout> ?
 *                  <sp:IncludeTimestamp ... /> ?
 *                  <sp:EncryptBeforeSigning ... /> ?
 *                  <sp:EncryptSignature ... /> ?
 *                  <sp:ProtectTokens ... /> ?
 *                  <sp:OnlySignEntireHeadersAndBody ... /> ?
 *                      ...
 *             </wsp:Policy>
 *          ...
 *      </sp:AsymmetricBinding>
 *
 *  </xmp>
 * }</pre>
 *
 * @author K.Venugopal@sun.com
 */
public interface AsymmetricBinding extends Binding{
   
    /**
     * returns Recipient token
     * @return {@link X509Token}
     */
    public Token getRecipientToken();
   
    /**
     * returns Recipient token
     * @return {@link X509Token}
     */
    public Token getRecipientSignatureToken();

     /**
     * returns Recipient token
     * @return {@link X509Token}
     */
    public Token getRecipientEncryptionToken();

    /**
     * returns Initiator token
     * @return {@link X509Token}
     */
    public Token getInitiatorToken();

    /**
     * returns Initiator token
     * @return {@link X509Token}
     */
    public Token getInitiatorSignatureToken();

    /**
     * returns Initiator token
     * @return {@link X509Token}
     */
    public Token getInitiatorEncryptionToken();
}
