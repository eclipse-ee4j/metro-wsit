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
 * $Id: PrefixNamespaceMappingCallback.java,v 1.2 2010-10-21 15:37:24 snajper Exp $
 */

package com.sun.xml.wss.impl.callback;

import javax.security.auth.callback.Callback;
import java.util.Properties;

/**
 * Note: This callback has been deprecated and disabled.
 * <P>
 * This callback is an optional callback that can be handled by an
 * implementation of CallbackHandler to register any prefix versus
 * namespace-uri mappings that the developer wants to make use of in the
 * security configuration.
 *
 * <p>Note: The following prefix-namespace mappings are supported by default
 * and hence do not require to be registered.
 *
 * <ul>
 * <li>env       : http://schemas.xmlsoap.org/soap/envelope/ </li>
 * <li>S         : http://schemas.xmlsoap.org/soap/envelope/ </li>
 * <li>SOAP-ENV  : http://schemas.xmlsoap.org/soap/envelope/ </li>
 * <li>ds        : http://www.w3.org/2000/09/xmldsig# </li>
 * <li>xenc      : http://www.w3.org/2001/04/xmlenc# </li>
 * <li>wsse      : http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd </li>
 * <li>wsu       : http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd </li>
 * <li>saml      : urn:oasis:names:tc:SAML:1.0:assertion </li>
 * </ul>
 *
 * @deprecated : since XWS 2.0 EA
 */
@Deprecated
public class PrefixNamespaceMappingCallback extends XWSSCallback implements Callback {

    private Properties prefixNamespaceMappings = null;

    public PrefixNamespaceMappingCallback() {}

    /**
     * Set the prefix:namespace-uri mappings to be registered
     *
     * @param mappings the <code>Properties</code> to be registered
     */
    public void setMappings(Properties mappings) {
        prefixNamespaceMappings = mappings;
    }

    /**
     * @return the prefix:namespace-uri mappings
     */
    public Properties getMappings() {
        return prefixNamespaceMappings;
    }
}
