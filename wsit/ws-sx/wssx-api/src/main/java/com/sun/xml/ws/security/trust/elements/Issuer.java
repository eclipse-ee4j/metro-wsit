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
 * $Id: Issuer.java,v 1.2 2010-10-21 15:35:40 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import javax.xml.ws.EndpointReference;

/**
 * Specifies the issuer of the security token that is presented
 * in the message. The element's type is an endpoint reference as defined
 * in WS-Addressing.
 *
 * @author WS-Trust Implementation Team
 */
public interface Issuer {

    /**
     * Get the endpoint reference of the issuer.
     */
    EndpointReference getEndpointReference();

   /**
     * Set the endpoint reference of the issuer.
     */
    void setEndpointReference(EndpointReference endpointReference);

}
