/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.coord.common;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.tx.coord.common.client.RegistrationMessageBuilder;
import com.sun.xml.ws.tx.coord.common.client.RegistrationProxyBuilder;

public abstract class WSCBuilderFactory {

    public static WSCBuilderFactory newInstance(Transactional.Version version) {
        if (Transactional.Version.WSAT10 == version||Transactional.Version.DEFAULT == version)
            return new com.sun.xml.ws.tx.coord.v10.WSCBuilderFactoryImpl();
        else if (Transactional.Version.WSAT11 == version || Transactional.Version.WSAT12 == version) {
            return new com.sun.xml.ws.tx.coord.v11.WSCBuilderFactoryImpl();
        } else {
            throw new IllegalArgumentException(version + "is not a supported ws-at version");
        }
    }

    public static WSCBuilderFactory fromHeaders(MessageHeaders h) {
        WSCBuilderFactory builder = null;
        // FIXME: RJE -- remove cast
        HeaderList headers = (HeaderList) h;
        for (int i = 0; i < headers.size(); i++) {
            Header header =  headers.get(i);
            if(header.getLocalPart().equals(WSATConstants.COORDINATION_CONTEXT)){
                if(WSATConstants.WSAT10_NS_URI.equals(header.getNamespaceURI())){
                   builder = new com.sun.xml.ws.tx.coord.v10.WSCBuilderFactoryImpl();
                }else if(WSATConstants.WSAT11_NS_URI.equals(header.getNamespaceURI())){
                    builder = new com.sun.xml.ws.tx.coord.v11.WSCBuilderFactoryImpl();
                }
                if(builder!=null) {
                  headers.understood(i);
                  //builder.getWSATCoordinationContextBuilder().
                  return builder;
                }
            }
        }
        return null;
    }

    public abstract WSATCoordinationContextBuilder newWSATCoordinationContextBuilder();

    public abstract RegistrationProxyBuilder newRegistrationProxyBuilder();

    public abstract RegistrationMessageBuilder newWSATRegistrationRequestBuilder();


}
