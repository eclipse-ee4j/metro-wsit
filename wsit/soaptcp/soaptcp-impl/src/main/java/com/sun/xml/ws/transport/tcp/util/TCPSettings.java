/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

/**
 * @author Alexey Stashok
 */
public final class TCPSettings {
    private static final TCPSettings instance = new TCPSettings();
    private static final String ENCODING_MODE_PROPERTY = "com.sun.xml.ws.transport.tcp.encodingMode";
    private static final String OUTPUT_BUFFER_GROWING_PROPERTY = "com.sun.xml.ws.transport.tcp.output.bufferGrow";
    private static final String OUTPUT_BUFFER_GROWING_LIMIT_PROPERTY = "com.sun.xml.ws.transport.tcp.output.bufferGrowLimit";

    private EncodingMode encodingMode;

    // Output buffer growing settings
    private boolean isOutputBufferGrow;
    private int outputBufferGrowLimit;

    public enum EncodingMode {
        XML,
        FI_STATELESS,
        FI_STATEFUL,
        FI_ALL
    }

    private TCPSettings() {
        gatherSettings();
    }

    public static TCPSettings getInstance() {
        return instance;
    }

    public EncodingMode getEncodingMode() {
        return encodingMode;
    }

    public boolean isOutputBufferGrow() {
        return isOutputBufferGrow;
    }

    public int getOutputBufferGrowLimit() {
        return outputBufferGrowLimit;
    }

    private void gatherSettings() {
        if (System.getProperty(ENCODING_MODE_PROPERTY) != null){
            final String encodingModeS = System.getProperty(ENCODING_MODE_PROPERTY);
            if ("xml".equalsIgnoreCase(encodingModeS)) {
                encodingMode = EncodingMode.XML;
            } else if ("FIStateless".equalsIgnoreCase(encodingModeS)) {
                encodingMode = EncodingMode.FI_STATELESS;
            } else {
                encodingMode = EncodingMode.FI_STATEFUL;
            }
        } else {
            encodingMode = EncodingMode.FI_STATEFUL;
        }

        // True, if property does not exist or set to true
        isOutputBufferGrow =
                System.getProperty(OUTPUT_BUFFER_GROWING_PROPERTY) == null ||
                    Boolean.getBoolean(OUTPUT_BUFFER_GROWING_PROPERTY);

        outputBufferGrowLimit = Integer.getInteger(
                OUTPUT_BUFFER_GROWING_LIMIT_PROPERTY, 65536);
    }
}
