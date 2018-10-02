/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust;

import javax.security.auth.Subject;

/**
 * <p>
 * This interface is a plugin for authorization services to a Security Token Service (STS).
 * The authorization service determines if a requestor can be issued an token to access the target 
 * service. The usual services mechanism is used to find implementing class
 * of <code>STSAuthorizationProvider</code>.
 * </p>
 @author Jiandong Guo
 */
public interface STSAuthorizationProvider {
    
    /**
     * Returns true if the requestor identified by the <code>Subject</code> can access the the target
     * service.
     * @param subject The <code>Subject</code> contgaining authentication information and context of the 
     *                authenticated requestor.
     * @param appliesTo Identifying target service(s) 
     * @param tokenType Type of token to be issued.
     * @param keyType Type of key to be issued
     * @return true ot false.
     */  
    boolean isAuthorized(Subject subject, String appliesTo, String tokenType, String keyType);
}
