/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.api;

import javax.xml.namespace.QName;

/**
 * Enumeration holding supported WS-MakeConnection protocol versions
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public enum McProtocolVersion {

    WSMC200702(
    "http://docs.oasis-open.org/ws-rx/wsmc/200702",
    "http://docs.oasis-open.org/ws-rx/wsmc/200702");

    /**
     * Namespaces
     */
    public final String protocolNamespaceUri;
    public final String policyNamespaceUri;
    /**
     * Action constants
     */
    public final String wsmcAction;
    public final String wsmcFaultAction;
    /**
     * Header names
     */
    public final QName messagePendingHeaderName;
    /**
     * Fault codes
     */
    public final QName unsupportedSelectionFaultCode;
    public final QName missingSelectionFaultCode;

    private McProtocolVersion(String protocolNamespaceUri, String policyNamespaceUri) {
        this.protocolNamespaceUri = protocolNamespaceUri;
        this.policyNamespaceUri = policyNamespaceUri;

        this.wsmcAction = protocolNamespaceUri + "/MakeConnection";
        this.wsmcFaultAction = protocolNamespaceUri + "/fault";

        this.messagePendingHeaderName = new QName(protocolNamespaceUri, "MessagePending");

        this.unsupportedSelectionFaultCode = new QName(protocolNamespaceUri, "UnsupportedSelection");
        this.missingSelectionFaultCode = new QName(protocolNamespaceUri, "MissingSelection");
    }

    /**
     * Provides a default reliable messaging version value.
     *
     * @return a default reliable messaging version value. Currently returns {@link #WSMC200702}.
     */
    public static McProtocolVersion getDefault() {
        return McProtocolVersion.WSMC200702; // if changed, update also MakeConnectionSupported annotation
    }

    /**
     * Determines if the tested string is a valid WS-Addressing action header value
     * that belongs to a WS-MakeConnection protocol message
     *
     * @param wsaAction WS-Addressing action string
     *
     * @return {@code true} in case the {@code wsaAction} parameter is a valid WS-Addressing
     *         action header value that belongs to a WS-MakeConnection protocol message
     */
    public boolean isProtocolAction(String wsaAction) {
        return (wsaAction != null) &&
               (wsmcAction.equals(wsaAction) ||
               isFault(wsaAction));
    }

    /**
     * Determines if the tested string is a valid WS-Addressing action header value
     * that belongs to a WS-MakeConnection protocol fault
     *
     * @param wsaAction WS-Addressing action string
     *
     * @return {@code true} in case the {@code wsaAction} parameter is a valid WS-Addressing
     *         action header value that belongs to a WS-MakeConnection protocol fault
     */
    public boolean isFault(String wsaAction) {
        return wsmcFaultAction.equals(wsaAction);
    }

}
