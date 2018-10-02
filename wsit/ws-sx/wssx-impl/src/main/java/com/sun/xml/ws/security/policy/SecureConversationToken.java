/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.policy;

import com.sun.xml.ws.policy.NestedPolicy;
import java.util.Set;



/**
 * This interface represents requirement for Security Context Token defined in WS-SecureConversation 1.0
 * @author K.Venugopal@sun.com
 */
public interface SecureConversationToken extends Token {

    /**
     * returns a {@link java.util.Iterator } over the token reference types to be used.
     * @return either REQUIRE_EXTERNAL_URI_REFERENCE
     */
    public Set getTokenRefernceTypes();
    
    /**
     * returns true if RequiredDerivedKey element is present under SecureConversationToken
     * @return true if RequireDerviedKeys element is present under SecureConversationToken or false.
     */
    public boolean isRequireDerivedKeys();
   
    /**
     * returns true if isMustNotSendCancel element is present under SecureConversationToken
     * @return true if isMustNotSendCancel element is present under SecureConversationToken or false.
     */
    public boolean isMustNotSendCancel();
    
    /**
     * returns true if isMustNotSendRenew element is present under SecureConversationToken
     * @return true if isMustNotSendRenew element is present under SecureConversationToken or false.
     */
    public boolean isMustNotSendRenew();
    
    /**
     * returns the type of the token.
     * @return one of SC10_SECURITYCONTEXT_TOKEN
     */
    public String getTokenType();
    
    /**
     * returns the issuer for the SecureConversation token.
     * @return returns the issuer
     */
    public Issuer getIssuer();
    
    /**
     * 
     * @return the issuer name for SecureConversation token
     */
    public IssuerName getIssuerName();
    
    /**
     * 
     * @return Claims
     */ 
    public Claims getClaims();
  
    /**
     * returns {@link com.sun.xml.ws.policy.Policy } which represents Bootstrap Policy
     * @return {@link com.sun.xml.ws.policy.Policy }
     */
    public NestedPolicy getBootstrapPolicy();
   
}
