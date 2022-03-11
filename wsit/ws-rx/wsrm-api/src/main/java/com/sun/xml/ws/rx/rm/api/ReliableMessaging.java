/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.xml.ws.spi.WebServiceFeatureAnnotation;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebServiceFeatureAnnotation(id = ReliableMessagingFeature.ID, bean = ReliableMessagingFeature.class)
public @interface ReliableMessaging {
    /**
     * Specifies if this feature is enabled or disabled.
     */
    boolean enabled() default true;

    RmProtocolVersion version() default RmProtocolVersion.WSRM200702;
    long sequenceInactivityTimeout() default ReliableMessagingFeature.DEFAULT_SEQUENCE_INACTIVITY_TIMEOUT;
    long destinationBufferQuota() default ReliableMessagingFeature.DEFAULT_DESTINATION_BUFFER_QUOTA;
    boolean orderedDeliveryEnabled() default false;
    ReliableMessagingFeature.DeliveryAssurance deliveryAssurance() default ReliableMessagingFeature.DeliveryAssurance.EXACTLY_ONCE;
    ReliableMessagingFeature.SecurityBinding securityBinding() default ReliableMessagingFeature.SecurityBinding.NONE;
    boolean persistenceEnabled() default false;
    long sequenceManagerMaintenancePeriod() default ReliableMessagingFeature.DEFAULT_SEQUENCE_MANAGER_MAINTENANCE_PERIOD;
    long maxConcurrentSessions() default ReliableMessagingFeature.DEFAULT_MAX_CONCURRENT_SESSIONS;
}
