/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.rx.mc.api.MakeConnectionSupportedFeature;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import org.glassfish.gmbal.ManagedObjectManager;

/**
 * Common base for WS-RX technology configuration
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public interface RxConfiguration {
    String ACK_REQUESTED_HEADER_SET = RxConfiguration.class.getName() + ".ACK_REQUESTED_HEADER_SET";
    
    /**
     * @see ReliableMessagingFeature
     */
    boolean isReliableMessagingEnabled();
    
    /**
     * @see MakeConnectionSupportedFeature
     */
    boolean isMakeConnectionSupportEnabled();

    /**
     * Provides information about the SOAP protocol version used on the endpoint.
     * 
     * @return the SOAP protocol version used on the RM-enabled endpoint
     */
    SOAPVersion getSoapVersion();

    /**
     * Provides information about the WS-Addressing protocol version used on the endpoint.
     * 
     * @return the WS-Addressing protocol version used on the RM-enabled endpoint
     */
    AddressingVersion getAddressingVersion();
    
    /**
     * Provides information if the port, which this configuration belongs to, has 
     * any request/response operations.
     *
     * @return {@code true} in case the port has any request/response operations; {@code false} otherwise
     */
    boolean requestResponseOperationsDetected();
    
    /**
     * Returns GMBAL/JMX manager
     *
     * @return GMBAL/JMX manager. May return null.
     */
    ManagedObjectManager getManagedObjectManager();
}
