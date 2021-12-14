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
 * $Id: NonceCache.java,v 1.2 2010-10-21 15:37:30 snajper Exp $
 */
package com.sun.xml.wss.impl.misc;

import com.sun.xml.wss.NonceManager;
import com.sun.xml.wss.NonceManager.NonceException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/*
 * This class holds a Nonce Cache and is a TimerTask
 */
@ManagedData
public class NonceCache extends TimerTask {

    /** logger */
    protected static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    // Nonce Cache
    private Map<String, String> nonceCache = Collections.synchronizedMap(new HashMap<>());
    private Map<String, String> oldNonceCache = Collections.synchronizedMap(new HashMap<>());

    @ManagedAttribute // Only for monitoring
    private Map<String, String> getNonceCache() {
        return nonceCache;
    }

    @ManagedAttribute // Only for monitoring
    private Map<String, String> getOldNonceCache() {
        return oldNonceCache;
    }
    // default
    private long MAX_NONCE_AGE = MessageConstants.MAX_NONCE_AGE;
    // flag to indicate if this timertask is scheduled into the Timer queue
    private boolean scheduledFlag = false;
    private boolean canceledFlag = false;

    public NonceCache() {
    }

    public NonceCache(long maxNonceAge) {
        MAX_NONCE_AGE = maxNonceAge;
    }

    @SuppressWarnings("unchecked")
    public boolean validateAndCacheNonce(String nonce, String created) throws NonceException {
        if (nonceCache.containsKey(nonce) || oldNonceCache.containsKey(nonce)) {            
            log.log(Level.WARNING, LogStringsMessages.WSS_0815_NONCE_REPEATED_ERROR(nonce));
            throw new NonceManager.NonceException(LogStringsMessages.WSS_0815_NONCE_REPEATED_ERROR(nonce));
        }

        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Storing Nonce Value {0} into {1}", new Object[]{nonce, this});
        }

        nonceCache.put(nonce, created);
        return true;
    }

    @ManagedAttribute
    public boolean isScheduled() {
        return scheduledFlag;
    }

    public void scheduled(boolean flag) {
        scheduledFlag = flag;
    }

    @ManagedAttribute
    public boolean wasCanceled() {
        return canceledFlag;
    }

    @Override
    public void run() {

        if (nonceCache.isEmpty()) {
            cancel();
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Canceled Timer Task due to inactivity ...for {0}", this);
            }
            return;
        }

       removeExpired();
    }

    @Override
    public boolean cancel() {
        boolean ret = super.cancel();
        canceledFlag = true;
        oldNonceCache.clear();
        nonceCache.clear();

        return ret;
    }

    @ManagedAttribute
    public long getMaxNonceAge() {
        return MAX_NONCE_AGE;
    }

    public void removeExpired() {
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Clearing old Nonce values...for {0}", this);
        }

        oldNonceCache.clear();
        Map<String, String> temp = nonceCache;
        nonceCache = oldNonceCache;
        oldNonceCache = temp;
    }
}
