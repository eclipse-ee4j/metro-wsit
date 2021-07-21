/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.security.trust;

import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;

/**
 * <p>
 * This interface is a plugin for attrinute services to a Security Token Service (STS).
 * An attribute service provides the attributes about a requestor. The attributes are 
 * included in the issued toekn for the requestor using with the target servicce for 
 * authentication and authorization purpose. The usual services mechanism is used to find implementing class
 * of <code>STSAttributeProvider</code>.
 * </p>
 @author Jiandong Guo
 */

public interface STSAttributeProvider {
    public static final String NAME_IDENTIFIER = "NameID";
    
    /**
     * Returns the map of claimed attributes of the requestor apply to the targeted service.
     * @param subject The <code>Subject</code> contgaining authentication information and context of the 
     *                authenticated requestor.
     * @param appliesTo Identifying target service(s) 
     * @param tokenType Type of token to be issued which will contain these attributes.
     * @param claims Identifying the attributes of the requestor claimed by the target service.
     * @return map of attribut key and values. The key of the map is a <code>QName</code> contains the key name the the name space 
     *         for the key. The value of the map is a <code>List</code> of <code>String</code> contains
     *         a list of the values. One particular value with the requestor 
     *         identity to be in the issued token with key name<code>NAME_IDENTIFIER</code> must be in the map.
     */  
    Map<QName, List<String>> getClaimedAttributes(Subject subject, String appliesTo, String tokenType, Claims claims);
}
