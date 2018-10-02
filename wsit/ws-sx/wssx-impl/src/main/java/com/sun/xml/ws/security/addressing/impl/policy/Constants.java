/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.addressing.impl.policy;

import java.util.logging.Logger;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class Constants {
    
    public static final String ADDRESSING_POLICY_DOMAIN = "javax.enterprise.resource.xml.webservices.addressing.policy";
    public static final String ADDRESSING_POLICY_PACKAGE_ROOT = "com.sun.xml.ws.addressing.impl.policy";
    public static final String ADDRESSING_POLICY_DOMAIN_BUNDLE = ADDRESSING_POLICY_PACKAGE_ROOT + ".Localization";
    public static final Logger logger = Logger.getLogger(Constants.ADDRESSING_POLICY_DOMAIN,Constants.ADDRESSING_POLICY_DOMAIN_BUNDLE);

}
