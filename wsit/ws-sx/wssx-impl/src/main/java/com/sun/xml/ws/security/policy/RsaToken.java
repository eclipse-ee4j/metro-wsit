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

/**
 * RsaToken should be used with SecurityPolicy submission namespace (2005/07) and a namespace of
 * http://schemas.microsoft.com/ws/2005/07/securitypolicy. It should be replaced with KeyValueToken
 * for SecurityPolicy 1.2
 *
 * @author ashutosh.shahi@sun.com
 */
public interface RsaToken extends Token{
    
}
