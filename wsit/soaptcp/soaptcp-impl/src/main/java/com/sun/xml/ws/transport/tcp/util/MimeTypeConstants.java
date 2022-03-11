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

import com.sun.xml.ws.encoding.fastinfoset.FastInfosetMIMETypes;

/**
 *
 * @author Alexey Stashok
 */
public final class MimeTypeConstants {
    public static final String SOAP11 = "text/xml";
    public static final String SOAP12 = "application/soap+xml";

    public static final String MTOM = "multipart/related";

    public static final String FAST_INFOSET_SOAP11 = FastInfosetMIMETypes.SOAP_11;
    public static final String FAST_INFOSET_SOAP12 = FastInfosetMIMETypes.SOAP_12;

    public static final String FAST_INFOSET_STATEFUL_SOAP11 = FastInfosetMIMETypes.STATEFUL_SOAP_11;
    public static final String FAST_INFOSET_STATEFUL_SOAP12 = FastInfosetMIMETypes.STATEFUL_SOAP_12;
}
