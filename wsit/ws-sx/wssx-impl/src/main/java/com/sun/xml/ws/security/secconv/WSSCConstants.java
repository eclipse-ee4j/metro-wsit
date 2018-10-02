/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv;

/**
 * Common Constants pertaining to WS-SecureConversation
 * @author WS-Trust Implementation Team
 */
public class WSSCConstants {
    
    /** the SecureConversation namespace URI */
    public static final String WSC_NAMESPACE = "http://schemas.xmlsoap.org/ws/2005/02/sc";        
    
    /** the prefix to use for WS-SecureConversation */
    public static final String WSC_PREFIX = "wsc";
    
    /** URI for SCT token type */
    public static final String SECURITY_CONTEXT_TOKEN_TYPE = WSC_NAMESPACE + "/sct";        

    /** URI for DerivedKey token type */
    public static final String DERIVED_KEY_TOKEN_TYPE = WSC_NAMESPACE + "/dk";        
    
    /** SecurityContextToken Type String */
    public static final String SECURITY_CONTEXT_TOKEN = "SecurityContextToken";
    
    public static final String SECURITY_CONTEXT_ID = "Incomimg_SCT";
    
    /** Action URIs */
    public static final String REQUEST_SECURITY_CONTEXT_TOKEN_ACTION = "http://schemas.xmlsoap.org/ws/2005/02/trust/RST/SCT";    
    public static final String REQUEST_SECURITY_CONTEXT_TOKEN_RESPONSE_ACTION = "http://schemas.xmlsoap.org/ws/2005/02/trust/RSTR/SCT";    
    
    public static final String RENEW_SECURITY_CONTEXT_TOKEN_ACTION = "http://schemas.xmlsoap.org/ws/2005/02/trust/RST/SCT/Renew";
    public static final String RENEW_SECURITY_CONTEXT_TOKEN_RESPONSE_ACTION = "http://schemas.xmlsoap.org/ws/2005/02/trust/RSTR/SCT/Renew";
    
    public static final String CANCEL_SECURITY_CONTEXT_TOKEN_ACTION = "http://schemas.xmlsoap.org/ws/2005/02/trust/RST/SCT/Cancel";    
    public static final String CANCEL_SECURITY_CONTEXT_TOKEN_RESPONSE_ACTION = "http://schemas.xmlsoap.org/ws/2005/02/trust/RSTR/SCT/Cancel";            
    
}
