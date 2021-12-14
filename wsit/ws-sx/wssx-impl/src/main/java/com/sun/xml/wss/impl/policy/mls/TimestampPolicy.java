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
 * $Id: TimestampPolicy.java,v 1.2 2010-10-21 15:37:34 snajper Exp $
 */

package com.sun.xml.wss.impl.policy.mls;

import com.sun.xml.wss.impl.MessageConstants;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.sun.xml.wss.impl.PolicyTypeUtil;

/**
 * A policy representing a WSS Timestamp element.
 * Note: The TimestampPolicy is the only WSSPolicy element that does not contain a
 * concrete FeatureBinding and/or KeyBinding.
 */
public class TimestampPolicy extends WSSPolicy {
    
    /*
     * Feature Bindings
     * Key Bindings
     */
    
    private String creationTime     = MessageConstants._EMPTY;
    private String expirationTime   = MessageConstants._EMPTY;
    
    private long timeout = 300000;
    private long maxClockSkew = 300000;
    private long timestampFreshness = 300000;
    
    static SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'");
    
    /**
     *Default constructor
     */
    public TimestampPolicy() {
        setPolicyIdentifier(PolicyTypeUtil.TIMESTAMP_POLICY_TYPE);
    }
    
    /**
     * set the CreationTime for the timestamp in this TimestampPolicy
     */
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
    
    /**
     * If the current time on a receiving system is past the CreationTime of the timestamp plus the
     * timeout, then the timestamp is to be considered expired.
     * @param timeout the number of milliseconds after which the Timestamp in this
     * TimestampPolicy will expire.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    /**
     * set the maximum clock skew adjustment value (in milliseconds)
     * @param maxClockSkew the Maximum Clock Skew adjustment to be used
     * when validating received timestamps
     */
    public void setMaxClockSkew(long maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
    }
    
    /**
     * set the Timestamp Freshness Limit (in milliseconds) for this Timestamp
     * Timestamps received by a receiver with creation Times older than
     * the Timestamp Freshness Limit period are supposed to be rejected by the receiver.
     * @param timestampFreshness the Timestamp Freshness Limit (in milliseconds)
     */
    public void setTimestampFreshness(long timestampFreshness) {
        this.timestampFreshness = timestampFreshness;
    }
    
    /**
     * @return creationTime the creation time of the timestamp in this
     * TimestampPolicy if set, empty string otherwise
     */
    public String getCreationTime() {
        return this.creationTime;
    }
    
    /**
     * @return timeout the Timeout in milliseconds for this Timestamp
     */
    public long getTimeout() {
        return this.timeout;
    }
    
    /**
     * @return expirationTime the expiration time if set for this Timestamp, empty string otherwise
     */
    public String getExpirationTime() throws Exception {
        if (expirationTime.equals("") && timeout != 0 && !creationTime.equals("")) {
            try {
                synchronized(formatter) {
                    expirationTime = Long.toString(formatter.parse(creationTime).getTime() + timeout);
                }
            } catch (Exception e) {
                synchronized(formatter1) {
                    expirationTime =  Long.toString(formatter1.parse(creationTime).getTime() + timeout);
                            
                }
            }
        }
        
        return this.expirationTime;
    }
    
    /**
     * @param expirationTime the expiration time
     */
    public void setExpirationTime(String expirationTime) {
        this.expirationTime= expirationTime;
    }
    
    /**
     * @return maxClockSkew the maximum Clock Skew adjustment
     */
    public long getMaxClockSkew() {
        return this.maxClockSkew;
    }
    
    /**
     * @return timeStampFreshness limit
     */
    public long getTimestampFreshness() {
        return this.timestampFreshness;
    }
    
    /**
     * @param policy the policy to be compared for equality
     * @return true if the argument policy is equal to this
     */
    @Override
    public boolean equals(WSSPolicy policy) {
        
        boolean assrt = false;
        
        try {
            TimestampPolicy tPolicy = (TimestampPolicy) policy;
            boolean b1 = creationTime.equals("") ? true : creationTime.equalsIgnoreCase(tPolicy.getCreationTime());
            boolean b2 = getExpirationTime().equals("") ? true : getExpirationTime().equalsIgnoreCase(tPolicy.getExpirationTime());
            assrt = b1 && b2;
        } catch (Exception e) {}
        
        return assrt;
    }
    
    /*
     * Equality comparision ignoring the Targets
     * @param policy the policy to be compared for equality
     * @return true if the argument policy is equal to this
     */
    @Override
    public boolean equalsIgnoreTargets(WSSPolicy policy) {
        return equals(policy);
    }
    
    /**
     * Clone operator
     * @return clone of this policy
     */
    @Override
    public Object clone(){
        TimestampPolicy tPolicy = new TimestampPolicy();
        
        try {
            tPolicy.setTimeout(timeout);
            tPolicy.setCreationTime(creationTime);
            tPolicy.setExpirationTime(expirationTime);
            tPolicy.setMaxClockSkew(maxClockSkew);
            tPolicy.setTimestampFreshness(timestampFreshness);
        } catch (Exception e) {}
        
        return tPolicy;
    }
    
    /**
     * @return the type of the policy
     */
    @Override
    public String getType() {
        return PolicyTypeUtil.TIMESTAMP_POLICY_TYPE;
    }
    
}

