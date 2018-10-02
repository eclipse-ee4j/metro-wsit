/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: WSTrustSOAPFaultException.java,v 1.2 2010-10-21 15:36:48 snajper Exp $
 */

package com.sun.xml.ws.security.trust;

import javax.xml.namespace.QName;

/**
 * Captures the SOAPFault that needs to be thrown by an STS when a processing error occurs
 * @author Kumar Jayanti
 */
public class WSTrustSOAPFaultException extends RuntimeException {
    
    public static final QName WS_TRUST_INVALID_REQUEST_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "InvalidRequest", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_FAILED_AUTHENTICATION_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "FailedAuthentication", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_REQUEST_FAILED_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "RequestFailed", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_INVALID_SECURITYTOKEN_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "InvalidSecurityToken", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_AUTHENTICATION_BAD_ELEMENTS_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "AuthenticationBadElements", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_EXPIRED_DATA_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "ExpiredData", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_INVALID_TIMERANGE_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "InvalidTimeRange", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_INVALID_SCOPE_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "InvalidScope", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_RENEW_NEEDED_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "RenewNeeded", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_UNABLE_TO_RENEW_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "UnableToRenew", WSTrustConstants.WST_PREFIX);
    public static final QName WS_TRUST_BAD_REQUEST_FAULT = new QName(WSTrustConstants.WST_NAMESPACE, "BadRequest", WSTrustConstants.WST_PREFIX);

    
    public static final String WS_TRUST_INVALID_REQUEST_FAULTSTRING = "The request was invalid or malformed";
    public static final String WS_TRUST_FAILED_AUTHENTICATION_FAULTSTRING = "Authentication Failed";
    public static final String WS_TRUST_REQUEST_FAILED_FAULTSTRING = "The specified request failed";
    public static final String WS_TRUST_INVALID_SECURITYTOKEN_FAULTSTRING = "Security Token has been Revoked";
    public static final String WS_TRUST_AUTHENTICATION_BAD_ELEMENTS_FAULTSTRING = "Insufficient Digest Elements";
    public static final String WS_TRUST_BAD_REQUEST_FAULTSTRING = "The specified RequestSecurityToken is not understood";
    public static final String WS_TRUST_EXPIRED_DATA_FAULTSTRING = "The request data is out-of-date";
    public static final String WS_TRUST_INVALID_TIMERANGE_FAULTSTRING = "The requested time range is invalid or unsupported";
    public static final String WS_TRUST_INVALID_SCOPE_FAULTSTRING = "The request scope is invalid or unsupported";
    public static final String WS_TRUST_RENEW_NEEDED_FAULTSTRING = "A renewable security token has expired";
    public static final String WS_TRUST_UNABLE_TO_RENEW_FAULTSTRING = "The requested renewal failed";
    
    

    private final QName faultCode;
    private final String faultString;
    
    /** 
     * Creates a new instance of WSTrustSOAPFaultException 
     */
    public WSTrustSOAPFaultException(String message, Throwable cause, QName faultCode, String faultString) {
        super(message,cause);
        this.faultCode = faultCode;
        this.faultString = faultString;
    }
    
    /**
     * Get the FaultString for this exception
     */
    public String getFaultString() {
        return faultString;
    }
    
    /**
     * Get the FaultCode (QName) for this exception
     */
    public QName getFaultCode() {
        return faultCode;
    }
    
}
