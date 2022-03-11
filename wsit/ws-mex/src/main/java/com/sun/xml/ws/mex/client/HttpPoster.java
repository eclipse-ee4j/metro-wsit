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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import com.sun.xml.ws.mex.MessagesMessages;
import com.sun.xml.ws.mex.MetadataConstants;

/**
 * Class that handles making the HTTP POST request
 * to a service.
 */
public class HttpPoster {

    private static final Logger logger =
        Logger.getLogger(HttpPoster.class.getName());

    /**
     * Makes the request to the service. It is expected that this
     * method may throw IOException several times before metadata
     * is returned successfully.
     *
     * @param request A String containing the xml that
     *     will be the payload of the message.
     * @param address Address of the service.
     * @return The java.io.InputStream returned by the http
     *     url connection.
     */
    InputStream post(final String request, final String address,
        final String contentType) throws IOException {

        final URL url = new URL(address);
        final HttpURLConnection conn = createConnection(url);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("SOAPAction", "\"" + MetadataConstants.GET_REQUEST + "\"");

        final Writer writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(request);
        writer.flush();

        try {
            return conn.getInputStream();
        } catch (IOException ioe) {
            outputErrorStream(conn);

            // this exception is caught within the mex code and is logged there
            throw ioe;
        } finally {
            writer.close();
        }
    }

    // This method is simply for debugging/error output
    private void outputErrorStream(final HttpURLConnection conn) {
        final InputStream error = conn.getErrorStream();
        if (error != null) {
            final BufferedReader reader = new BufferedReader(
                new InputStreamReader(error));
            try {
                if (logger.isLoggable(MetadataConstants.ERROR_LOG_LEVEL)) {
                    logger.log(MetadataConstants.ERROR_LOG_LEVEL,
                        MessagesMessages.MEX_0010_ERROR_FROM_SERVER());
                    String line = reader.readLine();
                    while (line != null) {
                        logger.log(MetadataConstants.ERROR_LOG_LEVEL, line);
                        line = reader.readLine();
                    }
                    logger.log(MetadataConstants.ERROR_LOG_LEVEL,
                        MessagesMessages.MEX_0011_ERROR_FROM_SERVER_END());
                }
            } catch (IOException ioe) {
                // This exception has no more impact.
                logger.log(MetadataConstants.ERROR_LOG_LEVEL,
                    MessagesMessages.MEX_0012_READING_ERROR_STREAM_FAILURE(),
                    ioe);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // This exception has no more impact.
                    logger.log(MetadataConstants.ERROR_LOG_LEVEL,
                        MessagesMessages.MEX_0013_CLOSING_ERROR_STREAM_FAILURE(),
                        ex);
                }
            }
        }
    }

    /**
     * This method is called by ServiceDescriptorImpl when a
     * metadata response contains a mex location element. The
     * location element contains an address of a metadata document
     * that can be retrieved with an HTTP GET call.
     *
     * @param address The address of the document.
     * @return The java.io.InputStream returned by the http
     *     url connection.
     */
    public InputStream makeGetCall(final String address) throws IOException {
        final URL url = new URL(address);
        final HttpURLConnection conn = createConnection(url);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded"); // taken from wsimport
        try {
            return conn.getInputStream();
        } catch (IOException ioe) {
            outputErrorStream(conn);

            // this exception is caught within the mex code and is logged there
            throw ioe;
        }
    }

    /*
     * This method creates an http url connection and sets the
     * hostname verifier on it if it's an ssl connection.
     */
    private HttpURLConnection createConnection(final URL url)
        throws IOException {

        return (HttpURLConnection) url.openConnection();
    }

}
