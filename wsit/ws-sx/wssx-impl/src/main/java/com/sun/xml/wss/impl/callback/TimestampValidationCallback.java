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
 * TimestampValidationCallback.java
 *
 * Created on July 12, 2005, 12:54 AM
 *
 * This callback is intended for Timestamp validation.
 * A validator that implements the TimestampValidator interface should 
 * set on the callback by callback handler.
 */

package com.sun.xml.wss.impl.callback;

import javax.security.auth.callback.Callback;

/**
 *
 * @author abhijit.das@Sun.COM
 */
public class TimestampValidationCallback extends XWSSCallback implements Callback {
    
    private Request request;
    private TimestampValidator validator;
    
    
    /** Creates a new instance of TimestampValidationCallback */
    public TimestampValidationCallback(Request request) {
        this.request = request;
    }
    
    public void getResult() throws TimestampValidationException {
        if (validator == null) {
            throw new TimestampValidationException("A Required TimestampValidator object was not set by the CallbackHandler");
        }
        validator.validate(request);
    }
    
    /**
     * The CallbackHandler handling this callbacl should set the validator.
     *
     */
    public void setValidator(TimestampValidator validator) {
        this.validator = validator;
         if (this.validator instanceof ValidatorExtension) {
            ((ValidatorExtension)this.validator).setRuntimeProperties(this.getRuntimeProperties());
        }
    }
    
    public interface Request {
        
    }
    
    public static class UTCTimestampRequest implements Request {
        private String created;
        private String expired;
        private long maxClockSkew = 0;
        private long timestampFreshnessLimit = 0;
        
        private boolean isUsernameToken = false;
    
    
        /**
         * Set it to true if the Created Timestamp present inside 
         * UsernameToken needs to be validated.
         *
         */
        public void isUsernameToken(boolean isUsernameToken) {
            this.isUsernameToken = true;
        }
    
    
        /** 
         * Check if the Timestamp Created value is coming from UsernameToken 
         * @return true if Created is inside UsernameToken else false
         */
        public boolean isUsernameToken() {
            return isUsernameToken;
        }
        
        
        /**
         * Constructor.
         *
         * @param created <code>java.lang.String</code> representaion of Creation time.
         * @param expired <code>java.lang.String</code> representation of Expiration time.
         * @param maxClockSkew representing the max time difference between sender's
         * system time and receiver's system time in milliseconds.
         * @param timestampFreshnessLimit representing the maximum time interval for nonce 
         * cache removal.
         *
         */
        public UTCTimestampRequest(String created, 
                String expired, 
                long maxClockSkew,
                long timestampFreshnessLimit) {
            this.created = created;
            this.expired = expired;
            this.maxClockSkew = maxClockSkew;
            this.timestampFreshnessLimit = timestampFreshnessLimit;
        }
        
        public String getCreated() {
            return created;
        }
        
        public String getExpired() {
            return expired;
        }
        
        public long getMaxClockSkew() {
            return maxClockSkew;
        }
        
        public long getTimestampFreshnessLimit() {
            return timestampFreshnessLimit;
        }
    }
    
    public static class TimestampValidationException extends Exception {

        private static final long serialVersionUID = 5390126265884591759L;

        public TimestampValidationException(String message) {
            super(message);
        }

        public TimestampValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    
        public TimestampValidationException(Throwable cause) {
            super(cause);
        }
    }
    
    
    public interface TimestampValidator {
        /** 
         * Timestamp validation method.
         *
         * @throws TimestampValidationException if validation does not succeed.
         */
        void validate(Request request) throws TimestampValidationException;
    }
}
