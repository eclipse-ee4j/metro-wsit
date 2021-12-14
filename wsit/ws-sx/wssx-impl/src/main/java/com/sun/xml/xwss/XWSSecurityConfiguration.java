/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xwss;

/**
 * An XWSSecurityConfiguration object is used
 * by a JAXWS 2.0 Client to specify the client side security configuration.
 * @since JAXWS 2.0
 * @see SecurityConfigurationFactory
 */

public interface XWSSecurityConfiguration {

    String MESSAGE_SECURITY_CONFIGURATION =
        "com.sun.xml.ws.security.configuration";

}
