/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.callback;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.security.auth.callback.Callback;

import com.sun.xml.wss.impl.misc.Base64;
import org.apache.xml.security.exceptions.Base64DecodingException;
import com.sun.xml.wss.RealmAuthenticationAdapter;

/**
 * This Callback is intended for Username-Password validation.
 * A validator that implements the PasswordValidator interface
 * should be set on the callback by the callback handler.
 *
 * <p>Note: A validator for WSS Digested Username-Password is provided
 * as part of this callback.
 *
 * @author XWS-Security Team
 */
public class PasswordValidationCallback extends XWSSCallback implements Callback {

    private Request  request;
    private boolean result = false;
    private PasswordValidator validator;
    private RealmAuthenticationAdapter authenticator = null;

    public PasswordValidationCallback(Request request) {
        this.request = request;
    }

    public boolean getResult() {
        try {
            if (validator != null)
                result = validator.validate(request);
        }catch (ClassCastException e){
            throw e;
        }catch (Exception e) {
            return false;
        }
        return result;
    }

    public Request getRequest() {
        return request;
    }

    /**
     * This method must be invoked by the CallbackHandler while handling
     * this callback.
     */
    public void setValidator(PasswordValidator validator) {
        this.validator = validator;
        if (this.validator instanceof ValidatorExtension) {
            ((ValidatorExtension)this.validator).setRuntimeProperties(this.getRuntimeProperties());
        }
    }

    public PasswordValidator getValidator() {
        return this.validator;
    }

    public void setRealmAuthentcationAdapter(RealmAuthenticationAdapter adapter) {
        this.authenticator = adapter;
    }

    public RealmAuthenticationAdapter getRealmAuthenticationAdapter() {
        return this.authenticator;
    }

    public interface Request {
    }

    /**
     * Represents a validation request when the password in the username token
     * is in plain text.
     */
    public static class PlainTextPasswordRequest implements Request {

        private String password;
        private String userName;

        /**
         * Constructor.
         *
         * @param userName <code>java.lang.String</code> representation of User name.
         * @param password <code>java.lang.String</code> representation of password.
         */
        public PlainTextPasswordRequest(String userName, String password) {
            this.password = password;
            this.userName = userName;
        }

        /**
         * Get the username stored in this Request.
         *
         * @return <code>java.lang.String</code> representation of username.
         */
        public String getUsername() {
            return userName;
        }

        /**
         * Get the password stored in the Request.
         *
         * @return <code>java.lang.String</code> representation of password.
         */
        public String getPassword() {
            return password;
        }

    }

    /**
     * Represents a validation request when the password in the username token
     * is in digested form.
     */
    public static class DigestPasswordRequest implements Request {

        private String password;
        private String userName;
        private String digest;
        private String nonce;
        private String created;

        /**
         * Constructor.
         *
         * @param userName <code>java.lang.String</code> representing Username.
         * @param digest <code>java.lang.String</code> Base64 encoded form of Digested Password.
         * @param nonce <code>java.lang.String</code> representation of unique Nonce
         * used for calculating Digested password.
         * @param created <code>java.security.String</code> representation of created time
         * used for password digest calculation.
         *
         */
        public DigestPasswordRequest(
            String userName,
            String digest,
            String nonce,
            String created) {

            this.userName = userName;
            this.digest = digest;
            this.nonce = nonce;
            this.created = created;
        }

        /**
         * This method must be invoked by the CallbackHandler while handling
         * Callback initialized with DigestPasswordRequest.
         */
        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return userName;
        }

        public String getDigest() {
            return digest;
        }

        public String getNonce() {
            return nonce;
        }

        public String getCreated() {
            return created;
        }

    }

    public static class DerivedKeyPasswordRequest implements Request {
        private String userName;
        private String password;
        private String created;

        public DerivedKeyPasswordRequest(
            String userName
            //String created
            ) {
            this.userName = userName;
            //this.created = created;
        }
        public void setPassword(String password){
            this.password = password;
        }
        public String getPassword() {
            return password;
        }
        public String getUsername() {
            return userName;
        }
        public void setUsername(String name){
            this.userName = name;
        }
        public String getCreated() {
            return created;
        }
    }
    /**
     * Interface for validating password.
     */
    public interface PasswordValidator {

        /**
         * @param request PasswordValidationRequest
         * @return true if password validation succeeds else false
         */
        boolean validate(Request request) throws PasswordValidationException;
    }

    /**
     * Implements WSS digest Password Validation.
     * The method to compute password digest is described in http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0.pdf
     */
    public static class DigestPasswordValidator implements PasswordValidator {

        public DigestPasswordValidator() {}

         @Override
         public boolean validate(Request request) throws PasswordValidationException {

             DigestPasswordRequest req = (DigestPasswordRequest)request;
             String passwd = req.getPassword();
             String nonce = req.getNonce();
             String created = req.getCreated();
             String passwordDigest = req.getDigest();
             //String username = req.getUsername();

             if (null == passwd)
               return false;
              byte[] decodedNonce = null;
              if (null != nonce) {
                  try {
                      decodedNonce = Base64.decode(nonce);
                  } catch (Base64DecodingException bde) {
                      throw new PasswordValidationException(bde);
                  }
              }
              String utf8String = "";
              if (created != null) {
                  utf8String += created;
              }
              utf8String += passwd;
              byte[] utf8Bytes;
             utf8Bytes = utf8String.getBytes(StandardCharsets.UTF_8);

             byte[] bytesToHash;
              if (decodedNonce != null) {
                  bytesToHash = new byte[utf8Bytes.length + decodedNonce.length];
                  System.arraycopy(decodedNonce, 0, bytesToHash, 0, decodedNonce.length);
                  System.arraycopy(utf8Bytes, 0, bytesToHash, decodedNonce.length, utf8Bytes.length);
              } else {
                  bytesToHash = utf8Bytes;
              }
              byte[] hash;
              try {
                  MessageDigest sha = MessageDigest.getInstance("SHA-1");
                  hash = sha.digest(bytesToHash);
              } catch (Exception e) {
                  throw new PasswordValidationException(
                      "Password Digest could not be created" + e);
              }
              return (passwordDigest.equals(Base64.encode(hash)));
         }

    }

    public abstract static class WsitDigestPasswordValidator extends DigestPasswordValidator {
        protected WsitDigestPasswordValidator() {}
        public abstract void setPassword(Request request);
    }

    public abstract static class DerivedKeyPasswordValidator implements PasswordValidator {
        protected DerivedKeyPasswordValidator() {}
        public abstract void setPassword(Request request);
      }

    public static class PasswordValidationException extends Exception {

        private static final long serialVersionUID = 431043579458710413L;

        public PasswordValidationException(String message) {
            super(message);
        }

        public PasswordValidationException(String message, Throwable cause) {
            super(message, cause);
        }

        public PasswordValidationException(Throwable cause) {
            super(cause);
        }
    }
}
