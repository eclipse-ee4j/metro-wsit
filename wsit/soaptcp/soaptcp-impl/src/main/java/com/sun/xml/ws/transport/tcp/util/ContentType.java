/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Stashok
 */
public final class ContentType {
    private String mimeType;
    private final Map<String, String> parameters = new HashMap<>(4);
    
    public ContentType() {
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public void parse(final String contentType) {
        parameters.clear();
        
        final int mimeDelim = contentType.indexOf(';');
        if (mimeDelim == -1) { // If the contentType doesn't have params
            mimeType = contentType.trim().toLowerCase();
            return;
        } else {
            mimeType = contentType.substring(0, mimeDelim).trim().toLowerCase();
        }
        
        int delim = mimeDelim + 1;
        // Scan ContentType string's params, decode them
        while(delim < contentType.length()) {
            int nextDelim = contentType.indexOf(';', delim);
            if (nextDelim == -1) nextDelim = contentType.length();
            
            int eqDelim = contentType.indexOf('=', delim);
            if (eqDelim == -1) eqDelim = nextDelim;
            
            final String key = contentType.substring(delim, eqDelim).trim();
            final String value = contentType.substring(eqDelim + 1, nextDelim).trim();
            parameters.put(key, value);
            
            delim = nextDelim + 1;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof ContentType) {
            ContentType ctToCompare = (ContentType) o;
            return mimeType.equals(ctToCompare.mimeType) && ctToCompare.parameters.equals(parameters);
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        return mimeType.hashCode() ^ parameters.hashCode();
    }
}
