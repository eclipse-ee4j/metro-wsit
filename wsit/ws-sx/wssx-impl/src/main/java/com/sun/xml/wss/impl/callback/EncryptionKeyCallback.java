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
 * $Id: EncryptionKeyCallback.java,v 1.2 2010-10-21 15:37:24 snajper Exp $
 */

package com.sun.xml.wss.impl.callback;

import javax.security.auth.callback.Callback;

import java.security.cert.X509Certificate;
import java.security.PublicKey;

import javax.crypto.SecretKey;


/**
 * CallBack implementation for encryption key.
 *
 * @author XWS-Security Team
 */
public class EncryptionKeyCallback extends XWSSCallback implements Callback {

    public static interface Request {
    }

    private Request request;

    public EncryptionKeyCallback(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    /**
     * A CallbackHandler handling an instance of this request should make
     * sure that an X.509 certificate must be set on the request.
     */
    public static abstract class X509CertificateRequest implements Request {

        X509Certificate certificate;

        public void setX509Certificate(X509Certificate certificate) {
            this.certificate = certificate;
        }

        public X509Certificate getX509Certificate() {
            return certificate;
        }
    }

    /**
     * A Callback initialized with this request should be handled if there's
     * some default X.509 certificate to be used for encryption.
     */
    public static class DefaultX509CertificateRequest
        extends X509CertificateRequest {
    }

    /**
     * A Callback initialized with this request should be handled if the
     * X.509 certificate to be used for encryption is mapped to some alias.
     */
    public static class AliasX509CertificateRequest
        extends X509CertificateRequest {

        private String alias;

        /**
         * Constructor.
         *
         * @param alias <code>String</code> representing the alias of the X509Certificate.
         *
         */
        public AliasX509CertificateRequest(String alias) {
            this.alias = alias;
        }

        /**
         * Get the alias stored in this Request.
         *
         * @return <code>java.lang.String</code>
         */
        public String getAlias() {
            return alias;
        }
    }

     /**
     * A CallbackHandler handling an instance of this request should make
     * sure that a symmetric key must be set on the request.
     */
    public static abstract class SymmetricKeyRequest implements Request {

        SecretKey symmetricKey;

        /**
         * Constructor.
         *
         * @param symmetricKey <code>javax.crypto.SecretKey</code> representing the
         * SymmetricKey to be used for Encryption.
         */
        public void setSymmetricKey(SecretKey symmetricKey) {
            this.symmetricKey = symmetricKey;
        }

        /**
         * Get the SymmetricKey stored in this Request.
         *
         * @return <code>javax.crypto.SecretKey</code>.
         *
         */
        public SecretKey getSymmetricKey() {
            return symmetricKey;
        }
    }

    /**
     * A CallbackHandler handling an instance of this request should make
     * sure that a symmetric key alias must be set on the request.
     */
    public static class AliasSymmetricKeyRequest
        extends SymmetricKeyRequest {

        private String alias;

        /**
         * Constructor.
         *
         * @param alias <code>java.lang.String</code> representing the alias of the
         * SymmetricKey to be used for Encryption.
         */
        public AliasSymmetricKeyRequest(String alias) {
            this.alias = alias;
        }

        /**
         * Get the alias stored in this Request.
         *
         * @return <code>java.lang.String</code> - alias of the SymmetricKey
         */
        public String getAlias() {
            return alias;
        }
    }

    /*Request for an X.509 certificate given the Public Key
     * This is an optional request and need not be handled
     * by the handler.
     *
     * The runtime makes a callback with this request to obtain
     * the certificate corresponding to the PublicKey. 
     */
    public static class PublicKeyBasedRequest
        extends X509CertificateRequest {
                                                                                                  
        PublicKey pubKey = null;
           
        /**
         * Constructor.
         *
         * @param pk <code>java.security.PublicKey</code> representing the PublicKey
         * to be used for Encryption.
         */
        public PublicKeyBasedRequest(PublicKey pk) {
            pubKey = pk;
        }
                                     
        
        /**
         * Get the PublicKey stored in this Request.
         *
         * @return <code>java.security.PublicKey</code>
         */
        public PublicKey getPublicKey() {
            return pubKey;
        }
                                                                                                  
    }

}
