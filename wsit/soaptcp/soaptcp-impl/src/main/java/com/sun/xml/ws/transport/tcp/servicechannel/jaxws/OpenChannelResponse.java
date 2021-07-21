/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.servicechannel.jaxws;

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "openChannelResponse", namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "openChannelResponse", namespace = "http://servicechannel.tcp.transport.ws.xml.sun.com/", propOrder = {
    "channelId",
    "negotiatedMimeTypes",
    "negotiatedParams"
})
public class OpenChannelResponse {

    @XmlElement(name = "channelId", namespace = "")
    private int channelId;
    @XmlElement(name = "negotiatedMimeTypes", namespace = "", required=true)
    private List<String> negotiatedMimeTypes;
    @XmlElement(name = "negotiatedParams", namespace = "")
    private List<String> negotiatedParams;

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
     *     the value for the _return property
     */
    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    /**
     *
     * @return
     *     returns List
     */
    public List<String> getNegotiatedMimeTypes() {
        return this.negotiatedMimeTypes;
    }

    /**
     *
     * @param negotiatedMimeTypes
     *     the value for the negotiatedMimeTypes property
     */
    public void setNegotiatedMimeTypes(List<String> negotiatedMimeTypes) {
        this.negotiatedMimeTypes = negotiatedMimeTypes;
    }

    /**
     *
     * @return
     *     returns List
     */
    public List<String> getNegotiatedParams() {
        return this.negotiatedParams;
    }

    /**
     *
     * @param negotiatedParams
     *     the value for the negotiatedParams property
     */
    public void setNegotiatedParams(List<String> negotiatedParams) {
        this.negotiatedParams = negotiatedParams;
    }

}
