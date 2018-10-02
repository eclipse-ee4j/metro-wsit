/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.servicechannel.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "closeChannel", namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "closeChannel", namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/")
public class CloseChannel {

    @XmlElement(name = "channelId", namespace = "")
    private int channelId;

    /**
     * 
     * @return
     *     returns int
     */
    public int getChannelId() {
        return this.channelId;
    }

    /**
     * 
     * @param channelId
     *     the value for the channelId property
     */
    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

}
