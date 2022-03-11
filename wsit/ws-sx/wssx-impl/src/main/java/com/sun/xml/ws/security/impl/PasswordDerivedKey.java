/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 *
 * @author suresh Created:22-Dec-2008.
 */
package com.sun.xml.ws.security.impl;


import java.io.UnsupportedEncodingException;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Random;

import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.ws.security.opt.crypto.dsig.internal.HmacSHA1;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.misc.SecurityUtil;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;


public class PasswordDerivedKey {

    private byte[] salt=null;
    private final int keylength = 160;
    private byte[] sign = null;

    private byte[] generateRandomSaltof15Bytes() {

        Random random = new Random();
        byte[] randomSalt = new byte[15];
        random.nextBytes(randomSalt);
        return randomSalt;
    }

    private void generate16ByteSalt() {
        salt = new byte[16];
        salt[0] = 0;
        byte[] temp = generateRandomSaltof15Bytes();
        System.arraycopy(temp, 0, salt, 1, 15);
    }

    public  byte[] generate160BitKey(String password, int iteration, byte[] reqsalt) {

        String saltencode = Base64.encode(reqsalt);

        byte[] keyof160bits = new byte[20];
        byte[] temp = password.getBytes();
        byte[] temp1 = saltencode.getBytes();
        byte[] input = new byte[temp1.length + temp.length];

        System.arraycopy(temp, 0, input, 0, temp.length);
        System.arraycopy(temp1, 0, input, temp.length, temp1.length);

        MessageDigest md = null;

        try {
            md = java.security.MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        md.reset();
        md.update(input);
        keyof160bits = md.digest();

        for (int i = 2; i <= iteration; i++) {
            md.reset();
            md.update(keyof160bits);
            keyof160bits = md.digest();

        }
        return keyof160bits;

    }
    public SecretKey generate16ByteKeyforEncryption(byte[] keyof20Bytes){
        byte[] keyof16Bytes = new byte[16];
        System.arraycopy(keyof20Bytes, 0, keyof16Bytes, 0, 16);
        AuthenticationTokenPolicy.UsernameTokenBinding untBinding = new AuthenticationTokenPolicy.UsernameTokenBinding();
        untBinding.setSecretKey(keyof16Bytes);
        SecretKey sKey = untBinding.getSecretKey(SecurityUtil.getSecretKeyAlgorithm(MessageConstants.AES_BLOCK_ENCRYPTION_128));
        //untBinding.setSecretKey(sKey);
        return sKey;
    }

    public SecretKey generateDerivedKeyforEncryption(String password, String algorithm, int iteration)
            throws UnsupportedEncodingException {

        SecretKey keySpec = null;
        byte[] reqsalt = new byte[16];
        byte[] keyof128length = new byte[16];
        byte[] keyof160bits = new byte[20];
        if (salt == null) {
            salt=new byte[16];
            generate16ByteSalt();

        }
        reqsalt[0] = 02;
        System.arraycopy(salt, 1, reqsalt, 1, 15);

        keyof160bits = generate160BitKey(password, iteration, reqsalt);
        System.arraycopy(keyof160bits, 0, keyof128length, 0, 16);

        if (testAlgorithm(algorithm)) {
            keySpec = new SecretKeySpec(keyof128length, algorithm);
        } else {
            throw new RuntimeException("This Derived Key procedure doesnot support " +algorithm);
        }
        return keySpec;


    }

    public byte[] generateMAC(byte[] data, String password, int iteration)
            throws InvalidKeyException, SignatureException, UnsupportedEncodingException {

        SecretKey keySpec = null;
        byte[] reqsalt = new byte[16];
        byte[] keyof160bits = new byte[20];
        if (salt == null) {
            salt=new byte[16];
            generate16ByteSalt();
        }
        reqsalt[0] = 01;
        System.arraycopy(salt, 1, reqsalt, 1, 15);
        keyof160bits = generate160BitKey(password, iteration, reqsalt);
        keySpec = new SecretKeySpec(keyof160bits, "AES");

        HmacSHA1 mac = new HmacSHA1();
        mac.init(keySpec, keylength);
        mac.update(data);

        return mac.sign();

    }

    public byte[] get16ByteSalt() {
        generate16ByteSalt();
        return salt;
    }

    public SecretKey verifyEncryptionKey(String password, int iterate, byte[] receivedSalt) throws UnsupportedEncodingException {

        byte[] keyof160bits = new byte[20];
        receivedSalt[0]=02;
        keyof160bits = generate160BitKey(password, iterate, receivedSalt);
        byte[] keyof128length = new byte[16];
        System.arraycopy(keyof160bits, 0, keyof128length, 0, 16);
        return new SecretKeySpec(keyof128length, "AES");

    }

    public boolean verifyMACSignature(byte[] receivedSignature,byte[] data,String password, int iterate, byte[] receivedsalt) throws UnsupportedEncodingException, InvalidKeyException, SignatureException {

        receivedsalt[0]=01;
        byte[] keyof160bits = generate160BitKey(password, iterate, receivedsalt);
        SecretKey keySpec = new SecretKeySpec(keyof160bits, "AES");

        HmacSHA1 mac = new HmacSHA1();
        mac.init(keySpec, keylength);
        mac.update(data);

        byte[] signature = mac.sign();
        return MessageDigest.isEqual(receivedSignature,signature);

    }


    public boolean testAlgorithm(String algo) {

        return algo.equalsIgnoreCase("AES") || algo.equalsIgnoreCase("Aes128") || algo.startsWith("A") || algo.startsWith("a");
    }
}
