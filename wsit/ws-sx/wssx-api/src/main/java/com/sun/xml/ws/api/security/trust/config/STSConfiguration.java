/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust.config;

import javax.security.auth.callback.CallbackHandler;

import java.util.Map;

/** This interface contains the attributes for configuring an STS.
 *
 * @author Jiandong Guo
 */
public interface STSConfiguration {


    /**
     * Gets the implementation class of <code>WSTrustContract</code> for this STS.
     *
     * @return class name
     */
    String getType();

    /**
     *  Get the Issuer for the STS which is a unique string identifing the STS.
     *
     */
    String getIssuer();

    /**
     *  Retruns true if the issued tokens from this STS must be encrypted.
     *
     */
    boolean getEncryptIssuedToken();

    /**
     *  Retruns true if the issued keys from this STS must be encrypted.
     *
     */
    boolean getEncryptIssuedKey();

    long getIssuedTokenTimeout();

    /**
     *  Set <code>CallbackHandler</code> for handling certificates for the
     *  service provider and keys for the STS.
     *
     */
    void setCallbackHandler(CallbackHandler callbackHandler);

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is any object.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly.
     *
     *
     * @return
     *     always non-null
     */
    Map<String, Object> getOtherOptions();

    /**
     *  Get <code>CallbackHandler</code> for handling certificates for the
     *  service provider and keys for the STS.
     *
     */
    CallbackHandler getCallbackHandler();

   // void addTokenGenerator(IssuedTokenGenerator tokenGen, String tokenType);

    //IssuedTokenGenerator getTokenGenerator(String tokenType);

    /**
     *  Add <code>TrustMetadata</code> for the service provider as identified by the given
     *  end point.
     */
    void addTrustSPMetadata(TrustSPMetadata data, String spEndpoint);

    /**
     *  Get <code>TrustMetadata</code> for the service provider as identified by the given
     *  end point.
     */
    TrustSPMetadata getTrustSPMetadata(String spEndpoint);
}
