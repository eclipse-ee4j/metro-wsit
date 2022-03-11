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

import com.sun.istack.NotNull;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * @author Alexey Stashok
 */
public final class ChannelSettings {

    private List<String> negotiatedMimeTypes;

    private List<String> negotiatedParams;

    private int channelId;

    private QName wsServiceName;

    private WSTCPURI targetWSURI;

    public ChannelSettings() {
    }

    public ChannelSettings(@NotNull final List<String> negotiatedMimeTypes,
            @NotNull final List<String> negotiatedParams,
            final int channelId,
            final QName wsServiceName,
            final WSTCPURI targetWSURI) {
        this.negotiatedMimeTypes = negotiatedMimeTypes;
        this.negotiatedParams = negotiatedParams;
        this.channelId = channelId;
        this.wsServiceName = wsServiceName;
        this.targetWSURI = targetWSURI;
    }

    public @NotNull List<String> getNegotiatedMimeTypes() {
        return negotiatedMimeTypes;
    }

    public void setNegotiatedMimeTypes(@NotNull final List<String> negotiatedMimeTypes) {
        this.negotiatedMimeTypes = negotiatedMimeTypes;
    }

    public @NotNull List<String> getNegotiatedParams() {
        return negotiatedParams;
    }

    public void setNegotiatedParams(@NotNull final List<String> negotiatedParams) {
        this.negotiatedParams = negotiatedParams;
    }

    public @NotNull WSTCPURI getTargetWSURI() {
        return targetWSURI;
    }

    public void setTargetWSURI(@NotNull final WSTCPURI targetWSURI) {
        this.targetWSURI = targetWSURI;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(final int channelId) {
        this.channelId = channelId;
    }

    public @NotNull QName getWSServiceName() {
        return wsServiceName;
    }

    public void setWSServiceName(@NotNull final QName wsServiceName) {
        this.wsServiceName = wsServiceName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb = sb.append("TargetURI: ")
                .append(targetWSURI)
                .append(" wsServiceName: ")
                .append(wsServiceName)
                .append(" channelId: ")
                .append(channelId)
                .append(" negotiatedParams: ")
                .append(negotiatedParams)
                .append(" negotiatedMimeTypes: ")
                .append(negotiatedMimeTypes);

        return sb.toString();
    }
}
