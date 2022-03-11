/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.security.trust.util;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;

public class TrustNamespacePrefixMapper extends NamespacePrefixMapper {


    @Override
    public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix) {
        // I want this namespace to be mapped to "xsi"
        if( "http://www.w3.org/2001/XMLSchema-instance".equals(namespaceUri) ) {
            return "xsi";
        }

        // I want the namespace foo to be the default namespace.
        if( "http://schemas.xmlsoap.org/ws/2005/02/trust".equals(namespaceUri) ) {
            return "wst";
        }

        if( "http://docs.oasis-open.org/ws-sx/ws-trust/200512".equals(namespaceUri) ) {
            return "trust";
        }

        if( "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".equals(namespaceUri) ) {
            return "wsu";
        }

        if( "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd".equals(namespaceUri) ) {
            return "wsse";
        }

        if( "http://schemas.xmlsoap.org/ws/2005/02/sc".equals(namespaceUri) ) {
            return "wssc";
        }

        if( "http://docs.oasis-open.org/ws-sx/ws-secureconversation/200512".equals(namespaceUri) ) {
            return "sc";
        }

        if( "http://schemas.xmlsoap.org/ws/2004/09/policy".equals(namespaceUri) ) {
            return "wsp";
        }

        if( "http://www.w3.org/2005/08/addressing".equals(namespaceUri) ) {
            return "wsa";
        }

        // otherwise I don't care. Just use the default suggestion, whatever it may be.
        return suggestion;
    }
}


