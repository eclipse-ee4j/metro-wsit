/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: SignatureKeyCallback.java,v 1.2 2010-10-21 15:37:24 snajper Exp $
 */

package com.sun.xml.wss.impl.callback;

import javax.security.auth.callback.Callback;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;


/**
 * CallBack implementation for signature key.
 *
 * @author XWS-Security Team
 */
public class SignatureKeyCallback extends XWSSCallback implements Callback {

    public interface Request {
    }

    private Request request;

    public SignatureKeyCallback(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    /**
     * A CallbackHandler handling an instance of this request should make
     * sure that a private key and a corresponding X.509 certificate must
     * be set on the request.
     */
    public static abstract class PrivKeyCertRequest implements Request {

        PrivateKey privateKey;

        X509Certificate certificate;

        /**
         * Set the Private Key used for Signature Calculation.
         *
         * @param privateKey <code>java.security.PrivateKey</code> representing the
         * PrivateKey to be used for Signature value calculation.
         *
         */
        public void setPrivateKey(PrivateKey privateKey) {
            this.privateKey = privateKey;
        }

        /**
         * Get the PrivateKey stored in this Request.
         *
         * @return <code>java.security.PrivateKey</code> - PrivateKey to be used for 
         * Signature value calculation.
         */
        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        /**
         * Set the X509Certificate used for Signature verification.
         *
         * @param certificate <code>java.security.X509Certificate</code> to be 
         * used for Signature Verification.
         *
         */
        public void setX509Certificate(X509Certificate certificate) {
            this.certificate = certificate;
        }

        /**
         * Get the X509Certificate stored in this Request.
         *
         * @return <code>java.security.X509Certificate</code> - X509Certificate
         * to be used for Signature Verification.
         */
        public X509Certificate getX509Certificate() {
            return certificate;
        }
    }

    /**
     * A Callback initialized with this request should be handled if there's
     * some default private key to be used for signing.
     */
    public static class DefaultPrivKeyCertRequest
        extends PrivKeyCertRequest {
    }

    /**
     * A Callback initialized with this request should be handled if the
     * private key to be used for signing is mapped to some alias.
     */
    public static class AliasPrivKeyCertRequest extends PrivKeyCertRequest {

        private String alias;

        /**
         * Constructor.
         *
         * @param alias <code>java.lang.String</code> representing the alias of
         * the PrivateKey to be used for Signature calculation.
         */
        public AliasPrivKeyCertRequest(String alias) {
            this.alias = alias;
        }

        /**
         * Get the alias stored in this Request.
         *
         * @return <code>java.lang.String</code> representing the alias of the PrivateKey
         * to be used for Signature calculation.
         */
        public String getAlias() {
            return alias;
        }
    }

    /**
     * A Callback initialized with this request should be handled if the
     * private key to be used for signing is to be retrieved given the PublicKey
     */
    public static class PublicKeyBasedPrivKeyCertRequest extends PrivKeyCertRequest {

        private PublicKey pk;

        /**
         * Constructor.
         *
         * @param publicKey <code>java.security.PublicKey</code>.
         */
        public PublicKeyBasedPrivKeyCertRequest(PublicKey publicKey) {
            this.pk = publicKey;
        }

        /**
         * Get the PublicKey stored in this Request.
         *
         * @return <code>java.security.PublicKey</code>.
         */
        public PublicKey getPublicKey() {
            return pk;
        }
    }
}
