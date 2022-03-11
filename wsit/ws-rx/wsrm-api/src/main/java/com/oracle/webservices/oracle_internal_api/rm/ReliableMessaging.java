/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

// GENERATED CODE.  DO NOT EDIT.
// GENERATED FROM reliable-messaging-internap-api-properties.xml

package com.oracle.webservices.oracle_internal_api.rm;

import jakarta.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
  * Configure WS-ReliableMessaging.
  */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id=ReliableMessagingFeature.ID, bean=ReliableMessagingFeature.class)
public @interface ReliableMessaging
{
    /**
     *
         Enable this feature.  Defaults to true.
     */
    boolean enabled() default true;

    /**
     *
       Specifies the period (in milliseconds) of a sequence manager maintenance task execution.
       Default value: 60000
     */
    long sequenceManagerMaintenancePeriod() default 60000;

    /**
     *
       Specifies how many concurrently active RM sessions (measured based on
       inbound RM sequences) the sequence manager dedicated to the WS Endpoint
       accepts before starting to refuse new requests for sequence creation.
       Default value: 100
     */
    int maxConcurrentSession() default 100;

    /**
     *
       When an XA TX is used to link the RMD with the user's ProviderRequest, this specifies the timeout value in seconds.
       The default value of 0 says to use the system default.
     */
    int userTransactionTimeout() default 0;
}
