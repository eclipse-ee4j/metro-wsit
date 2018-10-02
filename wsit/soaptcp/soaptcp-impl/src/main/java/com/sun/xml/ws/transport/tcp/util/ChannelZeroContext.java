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

import com.sun.istack.NotNull;
import java.util.Arrays;

/**
 * @author Alexey Stashok
 */
public class ChannelZeroContext extends ChannelContext {

    static final ChannelSettings channelZeroSettings = new ChannelSettings(
            Arrays.asList(MimeTypeConstants.SOAP11, MimeTypeConstants.FAST_INFOSET_SOAP11),
            Arrays.asList(TCPConstants.CHARSET_PROPERTY, TCPConstants.TRANSPORT_SOAP_ACTION_PROPERTY),
            0, TCPConstants.SERVICE_CHANNEL_WS_NAME, 
            WSTCPURI.parse(TCPConstants.PROTOCOL_SCHEMA + "://somehost:8080/service"));
    
    
    public ChannelZeroContext(@NotNull final ConnectionSession connectionSession) {
        super(connectionSession, channelZeroSettings);
    }
    
}
