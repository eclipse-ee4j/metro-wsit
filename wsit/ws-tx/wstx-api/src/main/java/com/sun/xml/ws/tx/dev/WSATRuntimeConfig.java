/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.dev;

import com.sun.istack.logging.Logger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class WSATRuntimeConfig {
    private static final Logger LOGGER = Logger.getLogger(WSATRuntimeConfig.class);
    private static final Lock DATA_LOCK = new ReentrantLock();

    public static final class Initializer {

        private Initializer() {
            // do nothing
        }

        public Initializer domainName(String value) {
            WSATRuntimeConfig.domainName = value;

            return this;
        }

        public Initializer hostName(String value) {
            WSATRuntimeConfig.hostName = value;

            return this;
        }

        public Initializer httpPort(String value) {
            if (value != null && value.trim().length() > 0) {
                WSATRuntimeConfig.httpPort = Integer.parseInt(value.trim());
            } else {
                LOGGER.config(String.format("Could not set HTTP port value to '%1s'. Rolling back to default: %2d", value, WSATRuntimeConfig.httpPort));
            }

            return this;
        }

        public Initializer httpsPort(String value) {
            if (value != null && value.trim().length() > 0) {
                WSATRuntimeConfig.httpsPort = Integer.parseInt(value.trim());
            } else {
                LOGGER.config(String.format("Could not set HTTPS port value to '%1s'. Rolling back to default: %2d", value, WSATRuntimeConfig.httpsPort));
        }

            return this;
        }

        public Initializer txLogLocation(final TxlogLocationProvider provider) {
            WSATRuntimeConfig.txLogLocationProvider = provider;

            return this;
        }

        public Initializer enableWsatRecovery(boolean value) {
            WSATRuntimeConfig.isWsatRecoveryEnabled = value;

            return this;
        }

        public Initializer enableWsatSsl(boolean value) {
            WSATRuntimeConfig.isWsatSslEnabled = value;

            return this;
        }

        public Initializer enableRollbackOnFailedPrepare(boolean value) {
            WSATRuntimeConfig.isRollbackOnFailedPrepare = value;

            return this;
        }

        public void done() {
            DATA_LOCK.unlock();
        }
    }
    private static WSATRuntimeConfig instance;
    private static boolean isWsatRecoveryEnabled = Boolean.valueOf(System.getProperty("wsat.recovery.enabled", "true"));
    private static TxlogLocationProvider txLogLocationProvider;
    private static boolean isWsatSslEnabled = Boolean.valueOf(System.getProperty("wsat.ssl.enabled", "false"));
    private static boolean isRollbackOnFailedPrepare = Boolean.valueOf(System.getProperty("wsat.rollback.on.failed.prepare", "true"));
    private static String domainName = "domain1";
    private static String hostName = "localhost";
    private static int httpPort = 8080;
    private static int httpsPort = 8181;
    private static RecoveryEventListener wsatRecoveryEventListener;

    private WSATRuntimeConfig() {
        // do nothing
    }

    public static Initializer initializer() {
        DATA_LOCK.lock();

        return new Initializer();
    }

    public static WSATRuntimeConfig getInstance() {
        DATA_LOCK.lock();
        try {
            if (instance == null) {
                instance = new WSATRuntimeConfig();
            }
            return instance;
        } finally {
            DATA_LOCK.unlock();
        }
    }

    /**
     * Is WS-AT recovery and therefore WS-AT transaction logging enabled
     */
    public boolean isWSATRecoveryEnabled() {
        return isWsatRecoveryEnabled;
    }

    /**
     * Return Protocol, host, and port String
     * @return for example "https://localhost:8181/"
     */
    public String getHostAndPort() {
        return isWsatSslEnabled ? "https://" + hostName + ":" + httpsPort : "http://" + hostName + ":" + httpPort;
    }

    /**
     * Returns the current domain name as provided by the container
     * @return the current domain name as provided by the container
     */
    public static String getDomainName() {
        return domainName;
    }

    /**
     * Returns the current instance/host name as provided by the container
     * @return container the current instance/host name as provided by the container
     */
    public static String getHostName() {
        return hostName;
    }

    /**
     * Returns the current HTTP name as used by the container
     * @return the current HTTP name as used by the container
     */
    public static int getHttpPort() {
        return httpPort;
    }

    /**
     * the current HTTPS name as used by the container
     * @return the current HTTPS name as used by the container
     */
    public static int getHttpsPort() {
        return httpsPort;
    }

    /**
     * Return the underlying transaction log location
     * @return String directory
     */
    public String getTxLogLocation() {
        return (txLogLocationProvider == null) ? null : txLogLocationProvider.getTxLogLocation();
    }

    public boolean isRollbackOnFailedPrepare() {
        return isRollbackOnFailedPrepare;
    }

    public void setWSATRecoveryEventListener(RecoveryEventListener WSATRecoveryEventListener) {
        wsatRecoveryEventListener = WSATRecoveryEventListener;
    }

    public interface TxlogLocationProvider {

        /**
         * Returns current value of the underlying transaction log location
         * @return transaction log directory path string
         */
        String getTxLogLocation();
    }

    public interface RecoveryEventListener {

        /**
         * Indicate to the listener that recovery for a specific instance is about to start.
         * @param delegated identifies whether it is part of a delegated transaction recovery
         * @param instance the instance name for which transaction recovery is performed, null if unknown
         */
        void beforeRecovery(boolean delegated, String instance);

        /**
         * Indicate to the listener that recovery is over.
         * @param success <code>true</code> if the recovery operation finished successfully
         * @param delegated identifies whether it is part of a delegated transaction recovery
         * @param instance the instance name for which transaction recovery is performed, null if unknown
         */
        void afterRecovery(boolean success, boolean delegated, String instance);
    }

    //do NOT make this static
    public class WSATRecoveryEventListener implements RecoveryEventListener {

        @Override
        public void beforeRecovery(boolean delegated, String instance) {
            if(wsatRecoveryEventListener!=null) wsatRecoveryEventListener.beforeRecovery(delegated, instance);
        }

        @Override
        public void afterRecovery(boolean success, boolean delegated, String instance) {
            if(wsatRecoveryEventListener!=null) wsatRecoveryEventListener.afterRecovery(success, delegated, instance);
        }
    }
}
