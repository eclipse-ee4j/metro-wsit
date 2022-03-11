/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.connectioncache.impl.transport;

import com.sun.xml.ws.transport.tcp.connectioncache.spi.concurrent.ConcurrentQueue;
import com.sun.xml.ws.transport.tcp.connectioncache.spi.transport.Connection;
import com.sun.xml.ws.transport.tcp.connectioncache.spi.transport.ConnectionCache;
import java.util.logging.Logger ;
import java.util.logging.Level ;


public abstract class ConnectionCacheBase<C extends Connection>
    implements ConnectionCache<C> {

    // A name for this instance, provided for convenience.
    private final String cacheType ;

    // Log to this logger if FINER is enabled.
    protected final Logger logger ;

    // Configuration data
    // XXX we may want this data to be dynamically re-configurable
    private final int highWaterMark ;        // Maximum number of
                        // connections before we start
                        // closing idle connections
    private final int numberToReclaim ;        // How many connections to
                        // reclaim at once

    // MUST be initialized in a subclass
    protected ConcurrentQueue<C> reclaimableConnections = null ;

    protected boolean debug() {
    return logger.isLoggable( Level.FINER ) ;
    }

    @Override
    public final String getCacheType() {
    return cacheType ;
    }

    @Override
    public final int numberToReclaim() {
    return numberToReclaim ;
    }

    @Override
    public final int highWaterMark() {
    return highWaterMark ;
    }

    // The name of this class, which is implemented in the subclass.
    // I could derive this from this.getClass().getClassName(), but
    // this is easier.
    protected abstract String thisClassName() ;

    ConnectionCacheBase( final String cacheType,
    final int highWaterMark, final int numberToReclaim,
    final Logger logger ) {

    if (cacheType == null)
        throw new IllegalArgumentException( "cacheType must not be null" ) ;

    if (highWaterMark < 0)
        throw new IllegalArgumentException( "highWaterMark must be non-negative" ) ;

    if (numberToReclaim < 1)
        throw new IllegalArgumentException( "numberToReclaim must be at least 1" ) ;

    if (logger == null)
        throw new IllegalArgumentException( "logger must not be null" ) ;

    this.cacheType = cacheType ;
    this.logger = logger ;
    this.highWaterMark = highWaterMark ;
    this.numberToReclaim = numberToReclaim ;
    }

    protected final void dprint(final String msg) {
    logger.finer(thisClassName() + msg);
    }

    public String toString() {
    return thisClassName() + "["
        + getCacheType() + "]";
    }

    public void dprintStatistics() {
    dprint( ".stats:"
           + " idle=" + numberOfIdleConnections()
           + " reclaimable=" + numberOfReclaimableConnections()
           + " busy=" + numberOfBusyConnections()
           + " total=" + numberOfConnections()
           + " ("
           + highWaterMark() + "/"
           + numberToReclaim()
           + ")");
    }

    /** Reclaim some idle cached connections.  Will never
     * close a connection that is busy.
     */
    protected boolean reclaim() {
    if (debug())
        dprint( ".reclaim: starting" ) ;

    int ctr = 0 ;
    while (ctr < numberToReclaim()) {
        C candidate = reclaimableConnections.poll() ;
        if (candidate == null)
        // If we have closed all idle connections, we must stop
        // reclaiming.
        break ;

        if (debug())
        dprint( ".reclaim: closing connection " + candidate ) ;

        try {
        close( candidate ) ;
        } catch (RuntimeException exc) {
        if (debug())
            dprint( ".reclaim: caught exception on close: " + exc ) ;
        throw exc ;
        }

        ctr++ ;
    }

    if (debug())
        dprint( ".reclaim: reclaimed " + ctr + " connection(s)" ) ;

    return ctr > 0 ;
    }
}
