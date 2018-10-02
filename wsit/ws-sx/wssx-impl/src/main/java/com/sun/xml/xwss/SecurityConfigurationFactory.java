/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xwss;

import java.io.InputStream;
import com.sun.xml.wss.XWSSecurityException;

/**
 * A Factory for creating an XWSSecurityConfiguration object(s). An XWSSecurityConfiguration object is used
 * by a JAXWS 2.0 Client to specify the client side security configuration.
 * A JAXWS client would specify the client side security configuration in the following manner
 * <PRE>
 *  FileInputStream f = new FileInputStream("./etc/client_security_config.xml");
 *  XWSSecurityConfiguration config = SecurityConfigurationFactory.newXWSSecurityConfiguration(f);
 *  ((BindingProvider)stub).getRequestContext().
                put(XWSSecurityConfiguration.MESSAGE_SECURITY_CONFIGURATION, config);
 * </PRE>
 *
 * @since JAXWS 2.0
 */

public class SecurityConfigurationFactory {

    /**
     * 
     * @param config XWSS Security Configuration.
     * @throws com.sun.xml.wss.XWSSecurityException is XWS-Security configuration file is not wellformed.
     */
    public static XWSSecurityConfiguration newXWSSecurityConfiguration(InputStream config) 
        throws XWSSecurityException {
        return new SecurityConfiguration(config);      
    }

}
