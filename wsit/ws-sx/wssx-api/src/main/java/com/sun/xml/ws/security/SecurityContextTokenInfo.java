/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security;

import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

import java.util.Date;
import java.io.Serializable;
import java.util.Set;


/**
 * The </code>SecurityContextTokenInfo</code> class represents security parameters
 * which will be saved in the <code>Session</code> object so that whenever the endpoint
 * crashes the security negotiations can be resumed from its original state and no new 
 * negotiations need to be done.
 *
 */
@ManagedData(name="SecurityContextTokenInfo")
@Description("Security parameters")
public interface SecurityContextTokenInfo extends Serializable {

    @ManagedAttribute
    @Description("Identifier")
    String getIdentifier();

    void setIdentifier(String identifier);

    @ManagedAttribute
    @Description("External identifier")
    String getExternalId();

    void setExternalId(String externalId);
    
    String getInstance();

    void setInstance(String instance);

    @ManagedAttribute    
    @Description("Secret")
    byte[] getSecret();

    byte[] getInstanceSecret(String instance);

    void addInstance(String instance, byte[] key);

    @ManagedAttribute
    @Description("Creation time")
    Date getCreationTime();

    void setCreationTime(Date creationTime);

    @ManagedAttribute
    @Description("Expiration time")
    Date getExpirationTime();

    void setExpirationTime(Date expirationTime);
    
    Set getInstanceKeys();

    @ManagedAttribute
    @Description("Issued token context")
    IssuedTokenContext getIssuedTokenContext();

    IssuedTokenContext getIssuedTokenContext(SecurityTokenReference reference);
        
}
