/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.mex;

import java.util.logging.Level;

/**
 * @author WS Development Team
 */
public class MetadataConstants {

    private static final String XMLSOAP_2004_09 =
        "http://schemas.xmlsoap.org/ws/2004/09/";

    public static final String GET_REQUEST = XMLSOAP_2004_09 + "transfer/Get";
    public static final String GET_RESPONSE =
        XMLSOAP_2004_09 + "transfer/GetResponse";
    public static final String GET_MDATA_REQUEST =
        XMLSOAP_2004_09 + "mex/GetMetadata/Request";

    public static final String MEX_NAMESPACE = XMLSOAP_2004_09 + "mex";
    public static final String MEX_PREFIX = "mex";

    // todo: get this from wsa api
    public static final String WSA_ANON =
        "http://www.w3.org/2005/08/addressing/anonymous";
    public static final String WSA_PREFIX = "wsa";

    public static final String SOAP_1_1 =
        "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String SOAP_1_2 =
        "http://www.w3.org/2003/05/soap-envelope";

    public static final String SCHEMA_DIALECT =
        "http://www.w3.org/2001/XMLSchema";
    public static final String WSDL_DIALECT =
        "http://schemas.xmlsoap.org/wsdl/";
    public static final String POLICY_DIALECT = XMLSOAP_2004_09 + "policy";

    /**
     * This is the logging level that is used for errors
     * that occur while retrieving metadata. May not need to
     * log as Level.SEVERE since some errors will be expected.
     * For instance, a soap 1.1 endpoint will return a version
     * mismatch fault when a soap 1.2 request is made.
     * <p>
     * Because this level may be changed as development continues,
     * we are storing it in one place.
     */
    public static final Level ERROR_LOG_LEVEL = Level.FINE;

}
