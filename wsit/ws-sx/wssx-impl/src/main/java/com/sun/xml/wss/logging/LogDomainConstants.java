/*
 * Copyright (c) 2010, 2022 Oracle and/or its affiliates. All rights reserved.
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

    String MODULE_TOP_LEVEL_DOMAIN =
            "javax.enterprise.resource.xml.webservices.security";

    String WSS_API_DOMAIN = MODULE_TOP_LEVEL_DOMAIN;

    String CONFIGURATION_DOMAIN = MODULE_TOP_LEVEL_DOMAIN;

    String FILTER_DOMAIN = MODULE_TOP_LEVEL_DOMAIN;

    String PACKAGE_ROOT = "com.sun.xml.wss.logging";

    String WSS_API_DOMAIN_BUNDLE =
            PACKAGE_ROOT + ".LogStrings";

    String FILTER_DOMAIN_BUNDLE =
            PACKAGE_ROOT + ".LogStrings";

    String CONFIGURATION_DOMAIN_BUNDLE =
            PACKAGE_ROOT + ".LogStrings";

    String SAML_API_DOMAIN =
            MODULE_TOP_LEVEL_DOMAIN + ".saml";

    String SAML_API_DOMAIN_BUNDLE =
            PACKAGE_ROOT+".saml" + ".LogStrings";

    String MISC_API_DOMAIN_BUNDLE =
            PACKAGE_ROOT+".misc" + ".LogStrings";

    String IMPL_DOMAIN =
            PACKAGE_ROOT + ".impl";

    String IMPL_DOMAIN_BUNDLE = PACKAGE_ROOT + ".LogStrings";
    String IMPL_SIGNATURE_DOMAIN= IMPL_DOMAIN+".dsig";
    String IMPL_SIGNATURE_DOMAIN_BUNDLE =IMPL_SIGNATURE_DOMAIN + ".LogStrings";

    String IMPL_MISC_DOMAIN= IMPL_DOMAIN+".misc";
    String IMPL_MISC_DOMAIN_BUNDLE = IMPL_MISC_DOMAIN+ ".LogStrings";

    String IMPL_CRYPTO_DOMAIN= IMPL_DOMAIN+".crypto";
    String IMPL_CRYPTO_DOMAIN_BUNDLE = IMPL_CRYPTO_DOMAIN+ ".LogStrings";

    String IMPL_CANON_DOMAIN= IMPL_DOMAIN+".c14n";
    String IMPL_CANON_DOMAIN_BUNDLE = IMPL_CANON_DOMAIN+ ".LogStrings";

    String IMPL_CONFIG_DOMAIN= IMPL_DOMAIN+".configuration";
    String IMPL_CONFIG_DOMAIN_BUNDLE = IMPL_CONFIG_DOMAIN+ ".LogStrings";

    String IMPL_FILTER_DOMAIN= IMPL_DOMAIN+".filter";
    String IMPL_FILTER_DOMAIN_BUNDLE = IMPL_FILTER_DOMAIN+ ".LogStrings";

    String IMPL_OPT_DOMAIN = IMPL_DOMAIN+".opt";
    String IMPL_OPT_DOMAIN_BUNDLE = IMPL_OPT_DOMAIN+ ".LogStrings";

    String IMPL_OPT_SIGNATURE_DOMAIN = IMPL_OPT_DOMAIN+".signature";
    String IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE = IMPL_OPT_SIGNATURE_DOMAIN+ ".LogStrings";

    String IMPL_OPT_CRYPTO_DOMAIN = IMPL_OPT_DOMAIN+".crypto";
    String IMPL_OPT_CRYPTO_DOMAIN_BUNDLE = IMPL_OPT_CRYPTO_DOMAIN+ ".LogStrings";

    String IMPL_OPT_TOKEN_DOMAIN = IMPL_OPT_DOMAIN+".token";
    String IMPL_OPT_TOKEN_DOMAIN_BUNDLE = IMPL_OPT_TOKEN_DOMAIN+ ".LogStrings";

    Logger CRYPTO_IMPL_LOGGER =  Logger.getLogger(IMPL_CRYPTO_DOMAIN,
            IMPL_CRYPTO_DOMAIN_BUNDLE);

}
