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
 * $Id: RequestedReference.java,v 1.2 2010-10-21 15:35:41 snajper Exp $
 */

package com.sun.xml.ws.security.trust.elements;

import com.sun.xml.ws.security.trust.elements.str.SecurityTokenReference;


/**
 *
 * The trust spec talks about RequestedAttachedReference,
 * RequestedUnAttachedReference (in section 6.2)
 * Base class for these two above.
 * 
 * @author WS-Trust Implementation Team.
 */
public abstract interface RequestedReference {
    /**
     * Gets the value of the securityTokenReference property.
     * 
     * @return {@link SecurityTokenReference }
     *     
     */
    SecurityTokenReference getSTR();

    /**
     * Sets the value of the securityTokenReference property.
     * 
     * @param value {@link SecurityTokenReference }
     *     
     */
    void setSTR(SecurityTokenReference value);
    
}
