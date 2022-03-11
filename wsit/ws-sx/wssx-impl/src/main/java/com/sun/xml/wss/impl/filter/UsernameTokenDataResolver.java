/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.filter;

import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.ws.security.impl.PasswordDerivedKey;
import com.sun.xml.ws.security.secext10.AttributedString;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.FilterProcessingContext;

import com.sun.xml.wss.impl.misc.Base64;
import com.sun.xml.wss.impl.policy.mls.EncryptionPolicy;
import com.sun.xml.wss.logging.LogDomainConstants;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.wss.impl.policy.mls.SignaturePolicy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy;
import com.sun.xml.wss.impl.policy.mls.AuthenticationTokenPolicy.UsernameTokenBinding;
import com.sun.xml.ws.security.opt.impl.tokens.UsernameToken;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.impl.filter.LogStringsMessages;
/**
 *
 * @author suresh
 */
public class UsernameTokenDataResolver {

    private static final Logger log = Logger.getLogger(
            LogDomainConstants.IMPL_FILTER_DOMAIN,
            LogDomainConstants.IMPL_FILTER_DOMAIN_BUNDLE);
     /**
      * sets the values of Salt, Iterations , username for UsernameToken,
      * generates 160 bit key for signature and sets it in UsernameToken Binding
      * @param context FilterProcessingContext
      * @param unToken UsernameToken
      * @param policy SignaturePolicy
      * @param untBinding UsernameTokenBinding
      * @param firstByte int
      * @return untBinding  UsernameTokenBinding
      */
     public static UsernameTokenBinding setSaltandIterationsforUsernameToken(
            FilterProcessingContext context, UsernameToken unToken,
            SignaturePolicy policy,UsernameTokenBinding untBinding, int firstByte) throws XWSSecurityException, UnsupportedEncodingException {
            //Sets Salt and Iterations in UsernameToken;
            int iterations ;
            if(context.getiterationsForPDK() != 0){
                iterations = context.getiterationsForPDK();
            }else {
                iterations = MessageConstants.DEFAULT_VALUEOF_ITERATIONS;
            }
            if(iterations < 1000){
                iterations = MessageConstants.DEFAULT_VALUEOF_ITERATIONS;
            }
            byte[] macSignature = null;
            PasswordDerivedKey pdk = new PasswordDerivedKey();
            //Setting username in unToken ;
            String userName = unToken.getUsernameValue();
            if (userName == null || "".equals(userName)) {
                userName = context.getSecurityEnvironment().getUsername(context.getExtraneousProperties());
            }
            if (userName == null || "".equals(userName)) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_1409_INVALID_USERNAME_TOKEN());
                throw new XWSSecurityException("Username has not been set");
            }
            unToken.setUsernameValue(userName);
            String password = untBinding.getPassword();
            if (!untBinding.hasNoPassword() && (password == null || "".equals(password))) {
                password = context.getSecurityEnvironment().getPassword(context.getExtraneousProperties());
            }
            if (!untBinding.hasNoPassword()) {
                if (password == null) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_1424_INVALID_USERNAME_TOKEN());
                    throw new XWSSecurityException("Password for the username has not been set");
                }
            }
            //Setting iterations in UsernameToken;
            AttributedString as =  new AttributedString();
            String iterate = Integer.toString(iterations);
            as.setValue(iterate);
            unToken.setIteration(as);

            byte[] salt = null;
            if (unToken.getSalt() == null) {
                 //Setting Salt in UsernameToken ;
                salt = pdk.get16ByteSalt();
                AttributedString aString = new AttributedString();
                aString.setValue(Base64.encode(salt));
                unToken.setSalt(aString);
            } else {
                //Retrieving the salt already there in unToken;
                String decodeString = unToken.getSalt().getValue();
                String  iter = unToken.getIteration().getValue();
                iterations = Integer.parseInt(iter);
                try {
                    salt = Base64.decode(decodeString);
                } catch (Base64DecodingException ex) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_1426_BASE_64_DECODING_ERROR(), ex);
                    throw new UnsupportedEncodingException("error while decoding the salt in username token");
                }
            }
            //salt[0] = MessageConstants.VALUE_FOR_SIGNATURE;
            salt[0] = (byte) firstByte;
         macSignature = pdk.generate160BitKey(password, iterations, salt);
         untBinding.setSecretKey(macSignature);
        return untBinding;
    }
    /**
     * sets the values of salt, iterations and username in UsernameToken,
     * generates 128 bit key for encryption and sets it in username token binding
     * @param context FilterProcessingContext
     * @param unToken UsernameToken
     * @param policy EncryptionPolicy
     * @param untBinding UsernameTokenBinding
     * @return untBinding  AuthenticationTokenPolicy.UsernameTokenBinding
     */
    public static AuthenticationTokenPolicy.UsernameTokenBinding setSaltandIterationsforUsernameToken(
    FilterProcessingContext context, UsernameToken unToken,
    EncryptionPolicy policy,UsernameTokenBinding untBinding) throws XWSSecurityException, UnsupportedEncodingException {
        //Setting Iterations for UsernameToken ;
        int iterations;
        if (context.getiterationsForPDK() != 0) {
            iterations = context.getiterationsForPDK();
        } else {
            iterations = MessageConstants.DEFAULT_VALUEOF_ITERATIONS;
        }
        if(iterations < 1000){
                iterations = MessageConstants.DEFAULT_VALUEOF_ITERATIONS;
            }
        byte[] keyof128bits = new byte[16];
        byte[] encSignature = null;
        //Setting username for UsernameToken ;
        String userName = unToken.getUsernameValue();
        if (userName == null || "".equals(userName)) {
            userName = context.getSecurityEnvironment().getUsername(context.getExtraneousProperties());
        }
        if (userName == null || "".equals(userName)) {
            log.log(Level.SEVERE, LogStringsMessages.WSS_1409_INVALID_USERNAME_TOKEN());
            throw new XWSSecurityException("Username has not been set");
        }
        unToken.setUsernameValue(userName);
        //Retrieving password ;
        String password = untBinding.getPassword();
        if (!untBinding.hasNoPassword() && (password == null || "".equals(password))) {
            password = context.getSecurityEnvironment().getPassword(context.getExtraneousProperties());
        }
        if (!untBinding.hasNoPassword()) {
            if (password == null) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_1424_INVALID_USERNAME_TOKEN());
                throw new XWSSecurityException("Password for the username has not been set");
            }
        }
        //Setting iterations in UsernameToken;
        AttributedString as =  new AttributedString();
        String iterate = Integer.toString(iterations);
        as.setValue(iterate);
        unToken.setIteration(as);
        PasswordDerivedKey pdk = new PasswordDerivedKey();
        byte[] salt = null;
        if (unToken.getSalt() == null) {
            // Setting the Salt in unToken first time;
            salt = pdk.get16ByteSalt();
            AttributedString atbs =  new AttributedString();
            atbs.setValue(Base64.encode(salt));
            unToken.setSalt(atbs);
        } else {
            //Retrieving the salt already there in unToken;
            String decodeString = unToken.getSalt().getValue();
            String  iter = unToken.getIteration().getValue();
            iterations = Integer.parseInt(iter);
            try {
                salt = Base64.decode(decodeString);
            } catch (Base64DecodingException ex) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_1426_BASE_64_DECODING_ERROR(), ex);
                throw new UnsupportedEncodingException("error while decoding the salt in username token");
            }
        }
        salt[0] = MessageConstants.VALUE_FOR_ENCRYPTION;
        encSignature = pdk.generate160BitKey(password, iterations, salt);
        for (int i = 0; i < 16; i++) {
            keyof128bits[i] = encSignature[i];
        }
        untBinding.setSecretKey(keyof128bits);
        return untBinding;
  }
  /**
   * sets username and password in usernametoken
   * @param context FilterProcessingContext
   * @param token com.sun.xml.wss.core.UsernameToken
   * @param unToken UsernameToken
   * @param policy AuthenticationTokenPolicy
   * @return UsernameTokenBinding
   */
   // currently we are not using this method
   public static AuthenticationTokenPolicy.UsernameTokenBinding resolveUsernameToken(
            FilterProcessingContext context, com.sun.xml.wss.core.UsernameToken token, UsernameToken unToken,
            AuthenticationTokenPolicy policy) throws XWSSecurityException {

        UsernameTokenBinding userNamePolicy =
                (UsernameTokenBinding) policy.getFeatureBinding();

            String userName = userNamePolicy.getUsername();
            String password = userNamePolicy.getPassword();

            if (userName == null || "".equals(userName)) {
                userName = context.getSecurityEnvironment().getUsername(context.getExtraneousProperties());
            }
            if (userName == null || "".equals(userName)) {
                log.log(Level.SEVERE, LogStringsMessages.WSS_1409_INVALID_USERNAME_TOKEN());
                throw new XWSSecurityException("Username has not been set");
            }
            if (token != null) {
            token.setUsername(userName);
            } else {
            unToken.setUsernameValue(userName);
            }
            if (!userNamePolicy.hasNoPassword() && (password == null || "".equals(password))) {
                password = context.getSecurityEnvironment().getPassword(context.getExtraneousProperties());
            }
            if (!userNamePolicy.hasNoPassword()) {
                if (password == null) {
                    log.log(Level.SEVERE, LogStringsMessages.WSS_1424_INVALID_USERNAME_TOKEN());
                    throw new XWSSecurityException("Password for the username has not been set");
                }
                if (token != null) {
                token.setPassword(password);
                } else {
                unToken.setPasswordValue(password);
                }
            }
        return userNamePolicy;
    }
}


