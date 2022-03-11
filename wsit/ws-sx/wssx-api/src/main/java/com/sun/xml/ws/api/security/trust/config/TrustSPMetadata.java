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

import java.util.Map;

/**
 * <p>
 * This interface captures metadata of a service provider.
 * </p>
 @author Jiandong Guo
 */
public interface TrustSPMetadata{

     /**
     * Gets the alias for the certificate of the service provider.
     *
     * @return the cert alias of the service provider
     */
    String getCertAlias();

    /**
     * Gets the token type for the  service provider.
     *
     * @return the token type of the service provider
     */
    String getTokenType();

    /**
     * Gets the key type for the  service provider.
     *
     * @return the key type of the service provider
     */
    String getKeyType();

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
}
