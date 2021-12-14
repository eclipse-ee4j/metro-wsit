/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.impl.policy.mls;

/**
 * Base class for all KeyBindings
 *
 */
public abstract class KeyBindingBase extends WSSPolicy {
     //added for policy integration
    public static final String INCLUDE_ONCE = "http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/Once".intern() ;
    public static final String INCLUDE_ONCE_VER2 = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/Once".intern() ;
    public static final String INCLUDE_NEVER = "http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/Never".intern();
    public static final String INCLUDE_NEVER_VER2 = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/Never".intern();
    public static final String INCLUDE_ALWAYS_TO_RECIPIENT = "http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/AlwaysToRecipient".intern();
    public static final String INCLUDE_ALWAYS="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy/IncludeToken/Always".intern();
    public static final String INCLUDE_ALWAYS_TO_RECIPIENT_VER2 = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/AlwaysToRecipient".intern();
    public static final String INCLUDE_ALWAYS_VER2 = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/Always".intern();
    //protected com.sun.xml.ws.security.policy.Token policyToken;
    protected boolean policyToken = false;
    protected String includeToken = INCLUDE_ALWAYS;

    /*public void setPolicyToken(com.sun.xml.ws.security.policy.Token tok) {
       // policyToken = tok;
    }*/
        
    protected String issuer;
    protected byte[] claims;
    protected String claimsDialect;
    
    public boolean policyTokenWasSet() {
        return policyToken;
    }

    public void setPolicyTokenFlag(boolean flag) {
        policyToken = flag;
    }
    
    public void setIncludeToken(String include){
        if (INCLUDE_ONCE.equals(include)) {
            throw new UnsupportedOperationException("IncludeToken Policy ONCE is not yet Supported");
        }
        this.includeToken = include;
        policyToken = true;
    }
    
    public String getIncludeToken(){
        return includeToken;
    }
    
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    
    public String getIssuer() {
        return issuer;
    }
    
    public void setClaims(byte[] claims) {
        this.claims = claims;
    }
    
    public byte[] getClaims() {
        return claims;
    }
}
