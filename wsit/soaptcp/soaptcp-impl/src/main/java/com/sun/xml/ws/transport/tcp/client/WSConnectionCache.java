/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.client;

import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;

/**
 * @author Alexey Stashok
 */
public class WSConnectionCache {
//    private static final Logger logger = Logger.getLogger(
//            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain + ".client");
//    
//    // map contains all connection sessions to certain destination
//    private final Map<Integer, Set<ConnectionSession>> allDstAddress2connectionSession;
//    
//    // map contains available connection sessions to certain destination (MAX_CHANNELS is not reached)
//    private final Map<Integer, ConcurrentLinkedQueue<ConnectionSession>> availableDstAddress2connectionSession;
//    
//    // set of locked connections, which are in use
//    private final Map<ConnectionSession, Thread> lockedConnections;
//    
//    public WSConnectionCache() {
//        allDstAddress2connectionSession = new HashMap<Integer, Set<ConnectionSession>>();
//        availableDstAddress2connectionSession = new HashMap<Integer, ConcurrentLinkedQueue<ConnectionSession>>();
//        lockedConnections = new HashMap<ConnectionSession, Thread>();
//    }
//    
//    public void registerConnectionSession(@NotNull final ConnectionSession connectionSession, final int dstAddrHashKey) {
//        ConcurrentLinkedQueue<ConnectionSession> availableConnectionSessions = availableDstAddress2connectionSession.get(dstAddrHashKey);
//        Set<ConnectionSession> allConnectionSessions = allDstAddress2connectionSession.get(dstAddrHashKey);
//        synchronized(this) {
//            //check if there is a record for such destination address
//            if (allConnectionSessions == null) {
//                allConnectionSessions = new HashSet<ConnectionSession>();
//                allDstAddress2connectionSession.put(dstAddrHashKey, allConnectionSessions);
//                availableConnectionSessions = new ConcurrentLinkedQueue<ConnectionSession>();
//                availableDstAddress2connectionSession.put(dstAddrHashKey, availableConnectionSessions);
//            }
//        }
//        availableConnectionSessions.offer(connectionSession);
//        allConnectionSessions.add(connectionSession);
//    }
//    
//    /**
//     * Get all active sessions for given destination host:port
//     */
//    public @NotNull Set<ConnectionSession> getAllConnectionsByAddr(final int dstAddrHashKey) {
//        final Set<ConnectionSession> allConnectionSessions = allDstAddress2connectionSession.get(dstAddrHashKey);
//        return allConnectionSessions != null ? allConnectionSessions : Collections.<ConnectionSession>emptySet();
//    }
//    
//    /**
//     * Get session, where it is available to create one more virtual connection
//     */
//    public @Nullable ConnectionSession pollAvailableConnectionByAddr(final int dstAddrHashKey) {
//        final ConcurrentLinkedQueue<ConnectionSession> availableConnectionSessions = availableDstAddress2connectionSession.get(dstAddrHashKey);
//        return availableConnectionSessions != null ? availableConnectionSessions.poll() : null;
//    }
//    
//    /**
//     * Put back session to available session list
//     */
//    public void offerAvailableConnectionByAddr(@NotNull final ConnectionSession connectionSession, final int dstAddrHashKey) {
//        final ConcurrentLinkedQueue<ConnectionSession> availableConnectionSessions = availableDstAddress2connectionSession.get(dstAddrHashKey);
//        availableConnectionSessions.offer(connectionSession);
//    }
//    
//    /**
//     * Destroy connection session
//     */
//    public void removeConnectionSession(final @NotNull ConnectionSession tcpConnectionSession) {
//        final int addressHashKey = tcpConnectionSession.getDstAddressHashKey();
//        final Set<ConnectionSession> allConnectionSessions = allDstAddress2connectionSession.get(addressHashKey);
//        
//        // method is called before ConnectionSession was registered in cache
//        if (allConnectionSessions != null) {
//            final ConcurrentLinkedQueue<ConnectionSession> availableConnectionSessions = availableDstAddress2connectionSession.get(addressHashKey);
//            
//            synchronized(tcpConnectionSession) {
//                // remove session from all and available lists
//                allConnectionSessions.remove(tcpConnectionSession);
//                availableConnectionSessions.remove(tcpConnectionSession);
//                
//                unlockConnection(tcpConnectionSession);
//                tcpConnectionSession.notifyAll();
//            }
//        }
//    }
//    
//    public void lockConnection(final @NotNull ConnectionSession tcpConnectionSession) throws InterruptedException, SessionAbortedException {
//        logger.log(Level.FINEST, MessagesMessages.WSTCP_1020_CONNECTION_CACHE_ENTER());
//        final Thread lockedThread = lockedConnections.get(tcpConnectionSession);
//        if (Thread.currentThread().equals(lockedThread)) return;
//        
//        synchronized(tcpConnectionSession) {
//            logger.log(Level.FINEST, MessagesMessages.WSTCP_1021_CONNECTION_CACHE_SYNC());
//            while(lockedConnections.containsKey(tcpConnectionSession)) {
//                tcpConnectionSession.wait();
//            }
//            
//            // check whether session was aborted?
//            final Set<ConnectionSession> allConnectionSessions = allDstAddress2connectionSession.get(tcpConnectionSession.getDstAddressHashKey());
//            if (allConnectionSessions.contains(tcpConnectionSession)) {
//                logger.log(Level.FINEST, MessagesMessages.WSTCP_1022_CONNECTION_CACHE_LOCK());
//                lockedConnections.put(tcpConnectionSession, Thread.currentThread());
//            } else {
//                logger.log(Level.FINEST, MessagesMessages.WSTCP_1023_CONNECTION_CACHE_SESSION_ABORTED());
//                throw new SessionAbortedException();
//            }
//        }
//    }
//    
//    public void unlockConnection(final @NotNull ConnectionSession tcpConnectionSession) {
//        synchronized(tcpConnectionSession) {
//            lockedConnections.remove(tcpConnectionSession);
//            tcpConnectionSession.notify();
//        }
//    }
}
