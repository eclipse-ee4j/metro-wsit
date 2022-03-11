/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

import javax.xml.namespace.QName;

/**
 * @author Alexey Stashok
 */
public final class TCPConstants {
    public static final String UTF8 = "UTF-8";

    public static final String CHARSET_PROPERTY = "charset";
    public static final String CONTENT_TYPE_PROPERTY = "Content-Type";
    public static final String SOAP_ACTION_PROPERTY = "action";
    public static final String TRANSPORT_SOAP_ACTION_PROPERTY = "SOAPAction";

    public static final int OK = 0;
    public static final int ONE_WAY = 1;
    public static final int ERROR = 2;

    // Max string length for SOAP/TCP content parameter value
    public static final int MAX_PARAM_VALUE_LENGTH = 1024;

    // Error codes
    public static final int CRITICAL_ERROR = 0;
    public static final int NON_CRITICAL_ERROR = 1;

    //Critical error sub-codes
    public static final int MALFORMED_FRAME_ERROR = 0;
    public static final int UNKNOWN_MESSAGE_ID = 1;
    public static final int INCORRECT_MESSAGE_FRAME_SEQ = 2;
    public static final int INTERLEAVED_MESSAGE_FRAME_SEQ = 3;
    public static final int UNKNOWN_REQUEST_RESPONSE_PATTERN = 4;

    //Non-critical error sub-codes
    public static final int GENERAL_CHANNEL_ERROR = 0;
    public static final int UNKNOWN_CHANNEL_ID = 1;
    public static final int UNKNOWN_CONTENT_ID = 2;
    public static final int UNKNOWN_PARAMETER_ID = 3;

    //ConnectionManagement Service error codes
    public static final int WS_NOT_FOUND_ERROR = 1;
    public static final int TOO_MANY_SESSIONS = 2;
    public static final int TOO_MANY_CHANNELS = 3;
    public static final int TOO_MANY_CHANNELS_FOR_SESSION = 4;


    /** ByteBuffer settings for FramedBufferInputStream and FramedBufferOutputStream */
    public static final int DEFAULT_FRAME_SIZE = 4096;
    public static final boolean DEFAULT_USE_DIRECT_BUFFER = false;

    /** Name of property in MessageContext, which hold tcp connection context */
    public static final String CHANNEL_CONTEXT = "channelContext";
    public static final String TCP_SESSION = "tcpSession";

    /** Name of Service pipeline attribute in client's connection session */
    public static final String SERVICE_PIPELINE_ATTR_NAME = "ServicePipeline";

    /** Service Channel WS endpoint path */
    public static final String SERVICE_CHANNEL_URL_PATTERN = "/servicechannel";
    public static final String SERVICE_CHANNEL_CONTEXT_PATH = "/service";


    /** Number of tries client will use to send message */
    public static final int CLIENT_MAX_FAIL_TRIES = 5;

    /** Attribute names for ServiceWS invocation */
    public static final String ADAPTER_REGISTRY = "AdapterRegistry";

    /** SOAP/TCP protocol schema */
    public static final String PROTOCOL_SCHEMA = "vnd.sun.ws.tcp";

    /** SOAP/TCP logging domain root */
    public static final String LoggingDomain = "com.sun.metro.transport.tcp";

    /** Connection management property names*/
    public static final String HIGH_WATER_MARK = "high-water-mark";
    public static final String MAX_PARALLEL_CONNECTIONS = "max-parallel-connections";
    public static final String NUMBER_TO_RECLAIM = "number-to-reclaim";

    /** Connection management settings default values */
    public static final int HIGH_WATER_MARK_SERVER = 1500;
    public static final int NUMBER_TO_RECLAIM_SERVER = 1;

    public static final int HIGH_WATER_MARK_CLIENT = 1500;
    public static final int NUMBER_TO_RECLAIM_CLIENT = 1;
    public static final int MAX_PARALLEL_CONNECTIONS_CLIENT = 5;

    /** Service Channel web service: Service and Port names*/
    public static final QName SERVICE_CHANNEL_WS_NAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "ServiceChannelWSImplService");
    public static final QName SERVICE_CHANNEL_WS_PORT_NAME = new QName("http://servicechannel.tcp.transport.ws.xml.sun.com/", "ServiceChannelWSImplPort");

}
