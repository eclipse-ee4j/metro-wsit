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
 * $Id: LogDomainConstants.java,v 1.2 2010-10-21 15:37:46 snajper Exp $
 */

package com.sun.xml.wss.logging;
import java.util.logging.Logger;

/**
 * @author XWS-Security Team
 *
 * This interface defines a number of constants pertaining to Logging domains.
 */

public interface LogDomainConstants {
    
    public static final String MODULE_TOP_LEVEL_DOMAIN =
            "javax.enterprise.resource.xml.webservices.security";
    
    public static final String WSS_API_DOMAIN = MODULE_TOP_LEVEL_DOMAIN;
    
    public static String CONFIGURATION_DOMAIN = MODULE_TOP_LEVEL_DOMAIN;
    
    public static String FILTER_DOMAIN = MODULE_TOP_LEVEL_DOMAIN;
    
    public static final String PACKAGE_ROOT = "com.sun.xml.wss.logging";
    
    public static final String WSS_API_DOMAIN_BUNDLE =
            PACKAGE_ROOT + ".LogStrings";
    
    public static final String FILTER_DOMAIN_BUNDLE =
            PACKAGE_ROOT + ".LogStrings";
    
    public static final String CONFIGURATION_DOMAIN_BUNDLE =
            PACKAGE_ROOT + ".LogStrings";
    
    public static final String SAML_API_DOMAIN =
            MODULE_TOP_LEVEL_DOMAIN + ".saml";
    
    public static final String SAML_API_DOMAIN_BUNDLE =
            PACKAGE_ROOT+".saml" + ".LogStrings";
    
    public static final String MISC_API_DOMAIN_BUNDLE =
            PACKAGE_ROOT+".misc" + ".LogStrings";
    
    public static final String IMPL_DOMAIN =
            PACKAGE_ROOT + ".impl";
    
    public static final String IMPL_DOMAIN_BUNDLE = PACKAGE_ROOT + ".LogStrings";
    public static final String IMPL_SIGNATURE_DOMAIN= IMPL_DOMAIN+".dsig";
    public static final String IMPL_SIGNATURE_DOMAIN_BUNDLE =IMPL_SIGNATURE_DOMAIN + ".LogStrings";

    public static final String IMPL_MISC_DOMAIN= IMPL_DOMAIN+".misc";
    public static final String IMPL_MISC_DOMAIN_BUNDLE = IMPL_MISC_DOMAIN+ ".LogStrings";
    
    public static final String IMPL_CRYPTO_DOMAIN= IMPL_DOMAIN+".crypto";
    public static final String IMPL_CRYPTO_DOMAIN_BUNDLE = IMPL_CRYPTO_DOMAIN+ ".LogStrings";
    
    public static final String IMPL_CANON_DOMAIN= IMPL_DOMAIN+".c14n";
    public static final String IMPL_CANON_DOMAIN_BUNDLE = IMPL_CANON_DOMAIN+ ".LogStrings";

    public static final String IMPL_CONFIG_DOMAIN= IMPL_DOMAIN+".configuration";
    public static final String IMPL_CONFIG_DOMAIN_BUNDLE = IMPL_CONFIG_DOMAIN+ ".LogStrings";

    public static final String IMPL_FILTER_DOMAIN= IMPL_DOMAIN+".filter";
    public static final String IMPL_FILTER_DOMAIN_BUNDLE = IMPL_FILTER_DOMAIN+ ".LogStrings";
    
    public static final String IMPL_OPT_DOMAIN = IMPL_DOMAIN+".opt";
    public static final String IMPL_OPT_DOMAIN_BUNDLE = IMPL_OPT_DOMAIN+ ".LogStrings";
    
    public static final String IMPL_OPT_SIGNATURE_DOMAIN = IMPL_OPT_DOMAIN+".signature";
    public static final String IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE = IMPL_OPT_SIGNATURE_DOMAIN+ ".LogStrings";
    
    public static final String IMPL_OPT_CRYPTO_DOMAIN = IMPL_OPT_DOMAIN+".crypto";
    public static final String IMPL_OPT_CRYPTO_DOMAIN_BUNDLE = IMPL_OPT_CRYPTO_DOMAIN+ ".LogStrings";
    
    public static final String IMPL_OPT_TOKEN_DOMAIN = IMPL_OPT_DOMAIN+".token";
    public static final String IMPL_OPT_TOKEN_DOMAIN_BUNDLE = IMPL_OPT_TOKEN_DOMAIN+ ".LogStrings";
    
    public static final Logger CRYPTO_IMPL_LOGGER =  Logger.getLogger(IMPL_CRYPTO_DOMAIN,
            IMPL_CRYPTO_DOMAIN_BUNDLE);

}
