/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import static com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.*;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
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
    long sequenceInactivityTimeout() default DEFAULT_SEQUENCE_INACTIVITY_TIMEOUT;
    long destinationBufferQuota() default DEFAULT_DESTINATION_BUFFER_QUOTA;
    boolean orderedDeliveryEnabled() default false;
    DeliveryAssurance deliveryAssurance() default DeliveryAssurance.EXACTLY_ONCE;
    SecurityBinding securityBinding() default SecurityBinding.NONE;
    boolean persistenceEnabled() default false;
    long sequenceManagerMaintenancePeriod() default DEFAULT_SEQUENCE_MANAGER_MAINTENANCE_PERIOD;
    long maxConcurrentSessions() default DEFAULT_MAX_CONCURRENT_SESSIONS;
}
