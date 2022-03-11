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

import com.sun.xml.ws.transport.tcp.connectioncache.spi.concurrent.ConcurrentQueueFactory;
import com.sun.xml.ws.transport.tcp.connectioncache.spi.transport.Connection;
import java.util.logging.Logger ;

abstract class ConnectionCacheBlockingBase<C extends Connection>
        extends ConnectionCacheBase<C> {

    protected int totalBusy ;    // Number of busy connections
    protected int totalIdle ;    // Number of idle connections

    ConnectionCacheBlockingBase( String cacheType, int highWaterMark,
            int numberToReclaim, Logger logger ) {

        super( cacheType, highWaterMark, numberToReclaim, logger ) ;

        this.totalBusy = 0 ;
        this.totalIdle = 0 ;

        this.reclaimableConnections =
                ConcurrentQueueFactory.<C>makeConcurrentQueue() ;
    }

    @Override
    public synchronized long numberOfConnections() {
        return totalIdle + totalBusy ;
    }

    @Override
    public synchronized long numberOfIdleConnections() {
        return totalIdle ;
    }

    @Override
    public synchronized long numberOfBusyConnections() {
        return totalBusy ;
    }

    @Override
    public synchronized long numberOfReclaimableConnections() {
        return reclaimableConnections.size() ;
    }
}

