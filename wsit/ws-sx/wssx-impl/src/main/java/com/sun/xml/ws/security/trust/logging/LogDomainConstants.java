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
 * $Id: LogDomainConstants.java,v 1.2 2010-10-21 15:37:09 snajper Exp $
 */

package com.sun.xml.ws.security.trust.logging;

/**
 * @author Manveen Kaur
 *
 * This class defines a number of constants pertaining to Logging domains.
 */

public class LogDomainConstants {
    
    public static final String MODULE_TOP_LEVEL_DOMAIN =
            "com.sun.xml.ws.security.trust";
    
    public static final String TRUST_IMPL_DOMAIN = MODULE_TOP_LEVEL_DOMAIN;
    
    public static final String PACKAGE_ROOT = "com.sun.xml.ws.security.trust.logging";
    
    public static final String TRUST_IMPL_DOMAIN_BUNDLE =
            PACKAGE_ROOT + ".LogStrings";
        
}
