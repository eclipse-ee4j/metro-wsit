/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.misc;

import com.sun.xml.wss.NonceManager;
import com.sun.xml.wss.logging.LogDomainConstants;
import org.glassfish.gmbal.AMXMetadata;
import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedObject;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedObject
@Description("per-endpoint NonceManager")
@AMXMetadata(type = "WSNonceManager")
public class DefaultNonceManager extends NonceManager {

    private static final boolean USE_DAEMON_THREAD = true;
    private static final Timer nonceCleanupTimer = new Timer(USE_DAEMON_THREAD);
    
    // Nonce Cache
    private NonceCache nonceCache = null;

     /** logger */
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN, LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    public DefaultNonceManager() {
    }
    
    @ManagedAttribute // Only for monitoring
    private NonceCache getNonceCache() { 
        return nonceCache; 
    }

    @Override
    public boolean validateNonce(String nonce, String created) throws NonceException {
        if ((nonceCache == null) || ((nonceCache != null) && nonceCache.wasCanceled())) {
            initNonceCache(getMaxNonceAge());
        }
        //  check if the reclaimer Task is scheduled or not
        if (!nonceCache.isScheduled()) {
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE,
                        "About to Store a new Nonce, but Reclaimer not Scheduled, so scheduling one" + nonceCache);
            }
            setNonceCacheCleanup();
        }
        return nonceCache.validateAndCacheNonce(nonce, created);
    }
    
    private synchronized void setNonceCacheCleanup() {

        if (!nonceCache.isScheduled()) {
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Scheduling Nonce Reclaimer task...... for " + this + ":" + nonceCache);
            }
            nonceCleanupTimer.schedule(
                    nonceCache,
                    nonceCache.getMaxNonceAge(), // run it the first time after
                    nonceCache.getMaxNonceAge()); //repeat every
            nonceCache.scheduled(true);
        }
    }
    
    private synchronized void initNonceCache(long maxNonceAge) {

        if (nonceCache == null) {
            nonceCache = new NonceCache(maxNonceAge);
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Creating NonceCache for first time....." + nonceCache);
            }
        } else if (nonceCache.wasCanceled()) {
            nonceCache = new NonceCache(maxNonceAge);
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Re-creating NonceCache because it was canceled....." + nonceCache);
            }
        }
    }
}
