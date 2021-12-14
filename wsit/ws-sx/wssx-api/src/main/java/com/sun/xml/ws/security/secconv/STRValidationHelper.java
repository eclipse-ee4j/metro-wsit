/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.secconv;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;

/**
 * This interface is used by the RM server side to validate the SecurityTokenReference.
 */
public interface STRValidationHelper {
    /**
     * Get the active security token used by the specified packet for signing and encrypting the message.
     *   
     * @return The reference URI to the security token context
     */
    String getSecurityContextTokenId(Packet packet);
    
    /**
     * Get the security token reference URI from the specified wsse:SecurityTokenReference element.
     * 
     * @return The reference URI to the SecurityTokenReference
     */
    String extractSecurityTokenId(SecurityTokenReferenceType str) throws Exception;
}
