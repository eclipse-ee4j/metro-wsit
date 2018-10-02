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
 * $Id: ModuleOptions.java,v 1.2 2010-10-21 15:37:47 snajper Exp $
 */

package com.sun.xml.wss.provider;

public interface ModuleOptions {
     public static final String SECURITY_CONFIGURATION_FILE = "security.config";
     public static final String ALIASES = "aliases";
     public static final String PASSWORDS = "keypasswords"; 
     public static final String DEBUG = "debug";
     public static final String SIGNING_KEY_ALIAS = "signature.key.alias";
     public static final String ENCRYPTION_KEY_ALIAS = "encryption.key.alias";
     public static final String DYNAMIC_USERNAME_PASSWORD = "dynamic.username.password";
}
