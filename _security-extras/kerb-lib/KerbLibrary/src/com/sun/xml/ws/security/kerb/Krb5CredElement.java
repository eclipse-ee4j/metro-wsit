/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.kerb;

import org.ietf.jgss.*;
import sun.security.jgss.spi.*;
import sun.security.krb5.*;
import java.security.Provider;

/**
 * Provides type safety for Krb5 credential elements.
 *
 * @author Mayank Upadhyay
 * @version 1.8, 11/17/05
 * @since 1.4
 */
interface Krb5CredElement 
    extends GSSCredentialSpi {
}
