/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.wsit;

import javax.xml.namespace.QName;

/**
 * @author Alexey Stashok
 */
public final class TCPConstants {
    
    /** Prevents instantiation */
    private TCPConstants() {}
    
    public static final String TCPTRANSPORT_POLICY_NAMESPACE_URI = "http://java.sun.com/xml/ns/wsit/2006/09/policy/soaptcp/service";
    public static final QName TCPTRANSPORT_POLICY_ASSERTION = new QName(TCPTRANSPORT_POLICY_NAMESPACE_URI, "OptimizedTCPTransport");
    public static final QName TCPTRANSPORT_PORT_ATTRIBUTE = new QName("port");
    
    public static final String CLIENT_TRANSPORT_NS = "http://java.sun.com/xml/ns/wsit/2006/09/policy/transport/client";
    public static final QName SELECT_OPTIMAL_TRANSPORT_ASSERTION = new QName(CLIENT_TRANSPORT_NS, "AutomaticallySelectOptimalTransport");

    public static final String TCPTRANSPORT_CONNECTION_MANAGEMENT_NAMESPACE_URI = "http://java.sun.com/xml/ns/wsit/2006/09/policy/soaptcp";
    public static final QName TCPTRANSPORT_CONNECTION_MANAGEMENT_ASSERTION = new QName(TCPTRANSPORT_CONNECTION_MANAGEMENT_NAMESPACE_URI, "ConnectionManagement");
    public static final String TCPTRANSPORT_CONNECTION_MANAGEMENT_HIGH_WATERMARK_ATTR = "HighWatermark";
    public static final String TCPTRANSPORT_CONNECTION_MANAGEMENT_MAX_PARALLEL_CONNECTIONS_ATTR = "MaxParallelConnections";
    public static final String TCPTRANSPORT_CONNECTION_MANAGEMENT_NUMBER_TO_RECLAIM_ATTR = "NumberToReclaim";

}
