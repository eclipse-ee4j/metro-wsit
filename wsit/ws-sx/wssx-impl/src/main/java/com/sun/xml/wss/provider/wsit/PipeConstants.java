/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

/**
 * This pipe is used to do client side security for app server
 */
public interface PipeConstants {

    static final String BINDING = "BINDING";
    static final String CLIENT_SUBJECT = "CLIENT_SUBJECT";
    static final String ENDPOINT = "ENDPOINT";
    static final String ENDPOINT_ADDRESS = "ENDPOINT_ADDRESS";
    static final String NEXT_PIPE = "NEXT_PIPE";
    static final String NEXT_TUBE = "NEXT_TUBE";
    static final String POLICY = "POLICY";
    static final String SEI_MODEL = "SEI_MODEL";
    static final String SECURITY_TOKEN = "SECURITY_TOKEN";
    static final String SECURITY_PIPE = "SECURITY_PIPE";
    static final String SERVER_SUBJECT = "SERVER_SUBJECT";
    static final String SERVICE = "SERVICE";
    static final String SERVICE_REF = "SERVICE_REF";
    static final String SOAP_LAYER = "SOAP";
    static final String SERVICE_ENDPOINT = "SERVICE_ENDPOINT";
    static final String WSDL_MODEL = "WSDL_MODEL";
    static final String WSDL_SERVICE = "WSDL_SERVICE";
    static final String CONTAINER = "CONTAINER";
    static final String AUTH_CONFIG="AUTH_CONFIG";
    static final String WRAPPED_CONTEXT="WRAPPED_CONTEXT";
    static final String ASSEMBLER_CONTEXT = "ASSEMBLER_CONTEXT";
    static final String SERVER_CERT="SERVER_CERT";
}
