/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.connectioncache.spi.transport;

import com.sun.xml.ws.transport.tcp.connectioncache.impl.transport.InboundConnectionCacheBlockingImpl;
import com.sun.xml.ws.transport.tcp.connectioncache.impl.transport.OutboundConnectionCacheBlockingImpl;
import java.util.logging.Logger ;

/** A factory class for creating connections caches.
 * Note that a rather unusual syntax is needed for calling these methods:
 *
 * ConnectionCacheFactory.<V>makeXXXCache() 
 *
 * This is required because the type variable V is not used in the
 * parameters of the factory method (there are no parameters).
 */
public final class ConnectionCacheFactory {
    private ConnectionCacheFactory() {}

    public static <C extends Connection> OutboundConnectionCache<C>
    makeBlockingOutboundConnectionCache( String cacheType, int highWaterMark,
	int numberToReclaim, int maxParallelConnections, Logger logger ) {

	return new OutboundConnectionCacheBlockingImpl<C>( cacheType, highWaterMark,
	    numberToReclaim, maxParallelConnections, logger ) ;
    }

    public static <C extends Connection> InboundConnectionCache<C>
    makeBlockingInboundConnectionCache( String cacheType, int highWaterMark,
	int numberToReclaim, Logger logger ) {
	return new InboundConnectionCacheBlockingImpl<C>( cacheType,
	    highWaterMark, numberToReclaim, logger ) ;
    }
}
