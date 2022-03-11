/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.opt.crypto.dsig.internal;

import java.security.Key;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * An implementation of the HMAC-SHA1 (RFC 2104)
 *
 * @author Joyce Leung
 */

public class HmacSHA1 {


    private static final int SHA1_BLOCK = 64;        // 512 bit block in SHA-1
    private byte[] key_opad;

    //private boolean initialized = false;
    //private Key key;
    private MessageDigest digest;
    private int byte_length;

    /**
     * Initialize with the key
     *
     * @param key a Hmac key
     * @param length output length in byte. length should be &gt; 0 for a
     * specified length or -1 for unspecified length (length of the signed output)
     * @exception InvalidKeyException if key is null
     */
    public void init(Key key, int length) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("The key should not be null");
        }
        try {
            this.digest = MessageDigest.getInstance("SHA1");
            initialize(key);
        } catch (NoSuchAlgorithmException nsae) {
            // FIXME: should throw some other exception instead of
            //        InvalidKeyException
            throw new InvalidKeyException("SHA1 not supported");
        }
        if(length > 0 ) {
            this.byte_length = length / 8;
        }
        else {
            byte_length = -1;
        }
        //initialized = true;
    }

    /**
     * update the engine with data
     *
     * @param data information to be signed or verified
     */
    public void update(byte[] data) {
        this.digest.update(data);
    }
    public void update(byte data) {
        this.digest.update(data);
    }
    public void update(byte[] data, int offset, int len) {
        this.digest.update(data, offset, len);
    }

    /**
     * Signs the data
     */
    public byte[] sign() throws SignatureException {

        if (byte_length == 0) {
            throw new SignatureException
        ("length should be -1 or greater than zero, but is " + byte_length);
        }

        byte[] value = this.digest.digest();

        this.digest.reset();
        this.digest.update(this.key_opad);
        this.digest.update(value);
        byte[] result = this.digest.digest();

        if (byte_length > 0 && result.length > byte_length) {
            byte[] truncated = new byte[byte_length];
            System.arraycopy(result, 0, truncated, 0, byte_length);
            result = truncated;
        }
        return result;
    }

    /**
     * Verifies the signature
     *
     * @param signature the signature to be verified
     */
    public boolean verify(byte[] signature) throws SignatureException {
        return MessageDigest.isEqual(signature, this.sign());
    }

    private void initialize(Key key) {
        byte[] rawKey = key.getEncoded();
        byte[] normalizedKey = new byte[SHA1_BLOCK];
        if (rawKey.length > SHA1_BLOCK) {
            this.digest.reset();
            rawKey = this.digest.digest(rawKey);
        }
        System.arraycopy(rawKey, 0, normalizedKey, 0, rawKey.length);
        for (int i = rawKey.length;  i < SHA1_BLOCK;  i ++) {
            normalizedKey[i] = 0;
        }
        byte[] key_ipad = new byte[SHA1_BLOCK];
        key_opad = new byte[SHA1_BLOCK];
        for (int i = 0;  i < SHA1_BLOCK;  i ++) {
            key_ipad[i] = (byte)(normalizedKey[i] ^ 0x36);
            key_opad[i] = (byte)(normalizedKey[i] ^ 0x5c);
        }

        this.digest.reset();
        this.digest.update(key_ipad);
    }
}
