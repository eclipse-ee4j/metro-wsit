/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
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
     String SECURITY_CONFIGURATION_FILE = "security.config";
     String ALIASES = "aliases";
     String PASSWORDS = "keypasswords";
     String DEBUG = "debug";
     String SIGNING_KEY_ALIAS = "signature.key.alias";
     String ENCRYPTION_KEY_ALIAS = "encryption.key.alias";
     String DYNAMIC_USERNAME_PASSWORD = "dynamic.username.password";
}
