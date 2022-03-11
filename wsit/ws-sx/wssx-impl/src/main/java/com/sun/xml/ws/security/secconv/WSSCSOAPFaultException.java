/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv;

import javax.xml.namespace.QName;

import com.sun.xml.ws.security.trust.WSTrustSOAPFaultException;

/**
 * Captures the SOAPFault that needs to be thrown by an Security Context Token Service when a
 * processing error occurs
 */
public class WSSCSOAPFaultException extends WSTrustSOAPFaultException {

    public static final QName WS_SC_BAD_CONTEXT_TOKEN_FAULT = new QName(WSSCConstants.WSC_NAMESPACE, "BadContextToken", WSSCConstants.WSC_PREFIX);
    public static final QName WS_SC_UNSUPPORTED_CONTEXT_TOKEN_FAULT = new QName(WSSCConstants.WSC_NAMESPACE, "UnsupportedContextToken", WSSCConstants.WSC_PREFIX);
    public static final QName WS_SC_UNKNOWN_DERIVATION_SOURCE_FAULT = new QName(WSSCConstants.WSC_NAMESPACE, "UnknownDerivationSource", WSSCConstants.WSC_PREFIX);
    public static final QName WS_SC_RENED_NEEDED_FAULT = new QName(WSSCConstants.WSC_NAMESPACE, "RenewNeeded", WSSCConstants.WSC_PREFIX);
    public static final QName WS_SC_UNABLE_TO_RENEW_FAULT = new QName(WSSCConstants.WSC_NAMESPACE, "UnableToRenew", WSSCConstants.WSC_PREFIX);

    public static final String WS_SC_BAD_CONTEXT_TOKEN_FAULTSTRING = "The requested context elements are insufficient or unsupported";
    public static final String WS_SC_UNSUPPORTED_CONTEXT_TOKEN_FAULTSTRING = "Not all of the values associated with the SCT are supported";
    public static final String WS_SC_UNKNOWN_DERIVATION_SOURCE_FAULTSTRING = "The specified source for the derivation is unknown";
    public static final String WS_SC_RENED_NEEDED_FAULTSTRING = "The provided context token has expired";
    public static final String WS_SC_UNABLE_TO_RENEW_FAULTSTRING = "The specified context token could not be renewed";
    private static final long serialVersionUID = 6580856296180287519L;

    /**
     * Creates a new instance of WSSCSOAPFaultException
     */
    public WSSCSOAPFaultException(String message, Throwable cause, QName faultCode, String faultString) {
        super(message,cause, faultCode, faultString);
    }
}
