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
 * BinarySecurityToken.java
 *
 * Created on 11 October, 2007, 3:46 PM
 */

package com.sun.xml.ws.security.opt.api.keyinfo;

import java.security.cert.X509Certificate;

/**
 * Represents binary-formatted X509 security tokens
 * @author ashutosh.shahi@sun.com
 */
public interface X509BinarySecurityToken extends BinarySecurityToken{
    
    X509Certificate getCertificate();
}
