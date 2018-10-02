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
 * $Id: WSTrustClientContract.java,v 1.2 2010-10-21 15:36:48 snajper Exp $
 */

package com.sun.xml.ws.security.trust;

import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.xml.ws.policy.impl.bindings.AppliesTo;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.trust.elements.BaseSTSRequest;
import com.sun.xml.ws.security.trust.elements.BaseSTSResponse;
import com.sun.xml.ws.security.trust.elements.RequestSecurityTokenResponse;

import java.net.URI;

/**
 * The Contract to be used by the Trust-Plugin on the Client Side.
 * TODO: Need to refine this....
 * @author root
 */
public interface WSTrustClientContract {
   
   /**
    * Handle an RSTR returned by the Issuer and update Token information into the
    * IssuedTokenContext.
    */
   public void handleRSTR(
           BaseSTSRequest request, BaseSTSResponse response, IssuedTokenContext context) throws WSTrustException;
   
   /**
    * Handle an RSTR returned by the Issuer and Respond to the Challenge
    * 
    */
   public BaseSTSResponse handleRSTRForNegotiatedExchange(
           BaseSTSRequest rst, BaseSTSResponse rstr, IssuedTokenContext context) throws WSTrustException;
   
   /**
    * Create an RSTR for a client initiated IssuedTokenContext establishment, 
    * for example a Client Initiated WS-SecureConversation context.
    * 
    */
   public BaseSTSResponse createRSTRForClientInitiatedIssuedTokenContext(AppliesTo scopes,IssuedTokenContext context) throws WSTrustException;
    
   /**
    * Contains Challenge
    * @return true if the RSTR contains a SignChallenge/BinaryExchange or
    *  some other custom challenge recognized by this implementation.
    */
   boolean containsChallenge(RequestSecurityTokenResponse rstr);
   
   /**
    * Return the &lt;wst:ComputedKey&gt; URI if any inside the RSTR, null otherwise
    */
   URI getComputedKeyAlgorithmFromProofToken(RequestSecurityTokenResponse rstr);
}
