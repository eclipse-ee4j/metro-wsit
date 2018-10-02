/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.sun.xml.ws.transport.tcp.util.TCPConstants.*;
/**
 * SOAP/TCP connection cache settings
 * 
 * @author Alexey Stashok
 */
public class ConnectionManagementSettings {    
    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain);
    
    private static final int DEFAULT_VALUE = -1;

    private int highWatermark = DEFAULT_VALUE;
    private int maxParallelConnections = DEFAULT_VALUE;
    private int numberToReclaim = DEFAULT_VALUE;
    
    private static volatile ConnectionManagementSettingsHolder holder;
    
    public static ConnectionManagementSettingsHolder getSettingsHolder() {
        if (holder == null) {
            synchronized(ConnectionManagementSettings.class) {
                if (holder == null) {
                    if (!createDefaultHolder()) {
                        holder = new SystemPropsConnectionManagementSettingsHolder();
                    }
                }
            }
        }
        return holder;
    }
    
    public static void setSettingsHolder(ConnectionManagementSettingsHolder holder) {
        ConnectionManagementSettings.holder = holder;
    }
    
    // Client side constructor (outbound connection cache)
    public ConnectionManagementSettings(int highWatermark, 
            int maxParallelConnections, int numberToReclaim) {
        this.highWatermark = highWatermark != DEFAULT_VALUE ? 
            highWatermark : HIGH_WATER_MARK_CLIENT;
        this.maxParallelConnections = maxParallelConnections != DEFAULT_VALUE ? 
            maxParallelConnections : MAX_PARALLEL_CONNECTIONS_CLIENT;
        this.numberToReclaim = numberToReclaim != DEFAULT_VALUE ? 
            numberToReclaim : NUMBER_TO_RECLAIM_CLIENT;
    }
    
    // Server side constructor (inbound connection cache)
    public ConnectionManagementSettings(int highWatermark, int numberToReclaim) {
        this.highWatermark = highWatermark != DEFAULT_VALUE ? 
            highWatermark : HIGH_WATER_MARK_SERVER;
        this.maxParallelConnections = DEFAULT_VALUE;
        this.numberToReclaim = numberToReclaim != DEFAULT_VALUE ? 
            numberToReclaim : NUMBER_TO_RECLAIM_SERVER;
    }

    public int getHighWatermark() {
        return highWatermark;
    }
    
    public int getMaxParallelConnections() {
        return maxParallelConnections;
    }
    
    public int getNumberToReclaim() {
        return numberToReclaim;
    }
        
    /**
     * Method tries to load default connection settings holder (Policy implementation)
     * 
     * @return true, if policy based settings holder was initiated successfully,
     * false otherwise
     */
    private static boolean createDefaultHolder() {
        boolean isOk = true;
        try {
            Class<?> policyHolderClass =
                    Class.forName("com.sun.xml.ws.transport.tcp.wsit.PolicyConnectionManagementSettingsHolder");
            Method getSingltonMethod = policyHolderClass.getMethod("getInstance");
            holder = (ConnectionManagementSettingsHolder) getSingltonMethod.invoke(null);
            logger.log(Level.FINE, MessagesMessages.WSTCP_1150_CON_MNGMNT_SETTINGS_POLICY());
        } catch(Exception e) {
            logger.log(Level.FINE, MessagesMessages.WSTCP_1151_CON_MNGMNT_SETTINGS_SYST_PROPS());
            isOk = false;
        }

        return isOk;
    }

    /**
     * SOAP/TCP connection cache settings holder.
     * Contains client and server cache settings
     */
    public interface ConnectionManagementSettingsHolder {
        public ConnectionManagementSettings getClientSettings();
        public ConnectionManagementSettings getServerSettings();
    }
    
    /**
     * SOAP/TCP connection cache settings holder.
     * Implements holder, which gets connection settings from system properties.
     */
    private static class SystemPropsConnectionManagementSettingsHolder 
            implements ConnectionManagementSettingsHolder {
        private volatile ConnectionManagementSettings clientSettings;
        private volatile ConnectionManagementSettings serverSettings;
        
        public ConnectionManagementSettings getClientSettings() {
            if (clientSettings == null) {
                synchronized(this) {
                    if (clientSettings == null) {
                        clientSettings = createSettings(true);
                    }
                }
            }
            
            return clientSettings;
        }

        public ConnectionManagementSettings getServerSettings() {
            if (serverSettings == null) {
                synchronized(this) {
                    if (serverSettings == null) {
                        serverSettings = createSettings(false);
                    }
                }
            }
            
            return serverSettings;
        }
        
        private static ConnectionManagementSettings createSettings(boolean isClient) {
            int highWatermark = Integer.getInteger(TCPConstants.HIGH_WATER_MARK, 
                    DEFAULT_VALUE);
            
            int maxParallelConnections = Integer.getInteger(
                    TCPConstants.MAX_PARALLEL_CONNECTIONS, DEFAULT_VALUE);
            
            int numberToReclaim = Integer.getInteger(TCPConstants.NUMBER_TO_RECLAIM, 
                    DEFAULT_VALUE);
            
            
            ConnectionManagementSettings settings = null;
            if (isClient) {
                settings = new ConnectionManagementSettings(highWatermark,
                        maxParallelConnections, numberToReclaim);
            } else {
                settings = new ConnectionManagementSettings(highWatermark, 
                        numberToReclaim);
            }
            
            return settings;
        }
    }    
}
