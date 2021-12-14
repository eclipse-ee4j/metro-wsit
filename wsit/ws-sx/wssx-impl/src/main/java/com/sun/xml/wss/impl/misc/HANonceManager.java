/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.xml.wss.impl.misc;

import com.sun.xml.ws.api.ha.HaInfo;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.commons.ha.HaContext;
import com.sun.xml.ws.commons.ha.StickyKey;
import com.sun.xml.wss.NonceManager;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.glassfish.ha.store.api.BackingStore;
import org.glassfish.ha.store.api.BackingStoreConfiguration;
import org.glassfish.ha.store.api.BackingStoreException;
import org.glassfish.ha.store.api.BackingStoreFactory;

/**
 *
 * @author suresh
 */
public class HANonceManager extends NonceManager {

    private Long maxNonceAge;
    private BackingStore<StickyKey, HAPojo> backingStore = null;
    private NonceCache localCache;
    private final ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    //private boolean isScheduled = false;
    public HANonceManager(final long maxNonceAge) {
        this.maxNonceAge = maxNonceAge;

        try {
            final BackingStoreConfiguration<StickyKey, HANonceManager.HAPojo> bsConfig =
                    HighAvailabilityProvider.INSTANCE.initBackingStoreConfiguration("HANonceManagerStore", StickyKey.class, HANonceManager.HAPojo.class);
            //maxNonceAge is in milliseconds so convert it into seconds
            bsConfig.getVendorSpecificSettings().put("max.idle.timeout.in.seconds", maxNonceAge / 1000L);
            //bsConfig.getVendorSpecificSettings().put("local.caching", true);
            //bsConfig.setClassLoader(this.getClass().getClassLoader());
            //not sure whether this statement required or not ?
            bsConfig.getVendorSpecificSettings().put(BackingStoreConfiguration.START_GMS, true);
            final BackingStoreFactory bsFactory = HighAvailabilityProvider.INSTANCE.getBackingStoreFactory(HighAvailabilityProvider.StoreType.IN_MEMORY);
            backingStore = bsFactory.createBackingStore(bsConfig);
            localCache = new NonceCache(maxNonceAge);
            singleThreadScheduledExecutor.scheduleAtFixedRate(new nonceCleanupTask(), this.maxNonceAge, this.maxNonceAge, TimeUnit.MILLISECONDS);
        } catch (BackingStoreException ex) {
            LOGGER.log(Level.SEVERE, LogStringsMessages.WSS_0826_ERROR_INITIALIZE_BACKINGSTORE(), ex);
        }
     }

    public HANonceManager(BackingStore<StickyKey, HAPojo> backingStore, final long maxNonceAge) {
        this.backingStore = backingStore;
        this.maxNonceAge = maxNonceAge;
        singleThreadScheduledExecutor.scheduleAtFixedRate(new nonceCleanupTask(), this.maxNonceAge, this.maxNonceAge, TimeUnit.MILLISECONDS);
    }


    @Override
    public boolean validateNonce(String nonce, String created) throws NonceException {
        //need to eagerly start the NonceCleanupTask otherwise in a cluster mode if the
        //first request goes to one instance and second request goes to another one after
        //MAX_NONCE_AGE the second instance would detect this as a replay since its NonceCleanup Task
        //never executed.
        //if(!isScheduled){
        //    singleThreadScheduledExecutor.scheduleAtFixedRate(new nonceCleanupTask(), maxNonceAge, maxNonceAge, TimeUnit.MILLISECONDS);
        //    isScheduled = true;
        //}
        //first check in local NonceCache.
        boolean isnewNonce = localCache.validateAndCacheNonce(nonce, created);
        byte[] data = created.getBytes();
        HAPojo pojo = new HAPojo();
        pojo.setData(data);
        try {
            HAPojo value = null;
            try {
                value = HighAvailabilityProvider.loadFrom(backingStore, new StickyKey(nonce), null);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, " exception during load command ", ex);
            }
            if (value != null) {              
                final String message = "Nonce Repeated : Nonce Cache already contains the nonce value :" + nonce;
                LOGGER.log(Level.WARNING, LogStringsMessages.WSS_0815_NONCE_REPEATED_ERROR(nonce));
                throw new NonceManager.NonceException(message);
            } else {
                HaInfo haInfo = HaContext.currentHaInfo();
                if (haInfo != null) {
                    HaContext.udpateReplicaInstance(HighAvailabilityProvider.saveTo(backingStore, new StickyKey(nonce, haInfo.getKey()), pojo, true));
                } else {
                    final StickyKey stickyKey = new StickyKey(nonce);
                    final String replicaId = HighAvailabilityProvider.saveTo(backingStore, stickyKey, pojo, true);
                    HaContext.updateHaInfo(new HaInfo(stickyKey.getHashKey(), replicaId, false));
                }

                LOGGER.log(Level.INFO, " nonce {0} saved ", nonce);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, LogStringsMessages.WSS_0825_ERROR_VALIDATE_NONCE(), ex);
            return false;
        }
        return true;    
    }

    public class nonceCleanupTask implements Runnable {

        @Override
        public void run() {
            try {
                //clear local nonce cache
                localCache.removeExpired();
                if (backingStore.size() <= 0) {
                    return;
                }
                int removed = backingStore.removeExpired(maxNonceAge);
                LOGGER.log(Level.INFO, " removed {0} expired entries from backing store ",removed);
            } catch (BackingStoreException ex) {
                LOGGER.log(Level.SEVERE, LogStringsMessages.WSS_0827_ERROR_REMOVING_EXPIRED_ENTRIES(), ex);
            }
        }
    }

    public void remove(String key) throws BackingStoreException{
        backingStore.remove(new StickyKey(key));
    }

    static public class HAPojo implements Serializable {

        private static final long serialVersionUID = 5214186833541531653L;
        byte[] data;

        public void setData(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return this.data;
        }
        public String toString() {
            if (data == null) {
                return "";
            }
            return new String(data);
        }
    }
}
