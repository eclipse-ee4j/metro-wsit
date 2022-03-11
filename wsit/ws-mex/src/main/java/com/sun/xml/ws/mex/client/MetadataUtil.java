/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.mex.MetadataConstants;
import com.sun.xml.ws.mex.client.MetadataClient.Protocol;

/**
 * Class for making mex Get requests (which are the same
 * as ws-transfer Get requests). Currently only http requests
 * are supported.
 */
public class MetadataUtil {

    // the transport-specific code is (mostly) here
    private final HttpPoster postClient;

    private static final Logger logger =
        Logger.getLogger(MetadataUtil.class.getName());

    public MetadataUtil() {
        postClient = new HttpPoster();
    }

    /**
     * Make a mex/wxf request to a server.
     *
     * @param address The address to query for metadata.
     * @return The full response from the server.
     */
    InputStream getMetadata(final String address,
        final Protocol protocol) throws IOException {

        final String request = getMexWsdlRequest(address, protocol);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Request message:\n" + request + "\n");
        }
        String contentType = "application/soap+xml"; // soap 1.2
        if (protocol == Protocol.SOAP_1_1) {
            contentType = "text/xml; charset=\"utf-8\"";
        }
        return postClient.post(request, address, contentType);
    }

    private String getMexWsdlRequest(final String address,
        final Protocol protocol) {

        // start with soap 1.2
        String soapPrefix = "s12";
        String soapNamespace = MetadataConstants.SOAP_1_2;
        if (protocol == Protocol.SOAP_1_1) {
            soapPrefix = "soap-env";
            soapNamespace = MetadataConstants.SOAP_1_1;
        }
        return "<" + soapPrefix + ":Envelope " +
            "xmlns:" + soapPrefix + "='" + soapNamespace + "' " +
            "xmlns:" + MetadataConstants.WSA_PREFIX + "='" + AddressingVersion.W3C.nsUri + "'>" +
            "<" + soapPrefix + ":Header>" +
            "<" + MetadataConstants.WSA_PREFIX + ":Action>" +
                MetadataConstants.GET_REQUEST +
            "</" + MetadataConstants.WSA_PREFIX + ":Action>" +
            "<" + MetadataConstants.WSA_PREFIX + ":To>" + address + "</" + MetadataConstants.WSA_PREFIX + ":To>" +
            "<" + MetadataConstants.WSA_PREFIX + ":ReplyTo><" + MetadataConstants.WSA_PREFIX + ":Address>" +
                MetadataConstants.WSA_ANON +
            "</" + MetadataConstants.WSA_PREFIX + ":Address></" + MetadataConstants.WSA_PREFIX + ":ReplyTo>" +
            "<" + MetadataConstants.WSA_PREFIX + ":MessageID>" +
            "uuid:778b135f-3fdf-44b2-b53e-ebaab7441e40" +
            "</" + MetadataConstants.WSA_PREFIX + ":MessageID>" +
            "</" + soapPrefix + ":Header>" +
            "<" + soapPrefix + ":Body/>" +
            "</" + soapPrefix + ":Envelope>";
    }

}

