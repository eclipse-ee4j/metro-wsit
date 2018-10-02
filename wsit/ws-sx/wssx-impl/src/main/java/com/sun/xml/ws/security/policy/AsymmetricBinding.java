/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * <pre>
 *  &lt;xmp&gt;
 *      &lt;sp:AsymmetricBinding ... &gt;
 *              &lt;wsp:Policy&gt;
 *                  &lt;sp:InitiatorToken&gt;
 *                      &lt;wsp:Policy&gt; ... &lt;/wsp:Policy&gt;
 *                  &lt;/sp:InitiatorToken&gt;
 *                  &lt;sp:RecipientToken&gt;
 *                      &lt;wsp:Policy&gt; ... &lt;/wsp:Policy&gt;
 *                  &lt;/sp:RecipientToken&gt;
 *                  &lt;sp:AlgorithmSuite ... &gt;
 *                      ...
 *                  &lt;/sp:AlgorithmSuite&gt;
 *                  &lt;sp:Layout ... &gt; ... &lt;/sp:Layout&gt; ?
 *                  &lt;sp:IncludeTimestamp ... /&gt; ?
 *                  &lt;sp:EncryptBeforeSigning ... /&gt; ?
 *                  &lt;sp:EncryptSignature ... /&gt; ?
 *                  &lt;sp:ProtectTokens ... /&gt; ?
 *                  &lt;sp:OnlySignEntireHeadersAndBody ... /&gt; ?
 *                      ...
 *             &lt;/wsp:Policy&gt;
 *          ...
 *      &lt;/sp:AsymmetricBinding&gt;
 *
 *  &lt;/xmp&gt;
 * </pre>
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
