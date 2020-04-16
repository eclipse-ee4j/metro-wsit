/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.api.transport.tcp;

import com.sun.xml.ws.api.transport.tcp.SelectOptimalTransportFeature.Transport;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.xml.ws.spi.WebServiceFeatureAnnotation;

/**
 * OptimizedTransport annotation
 *
 * @author Alexey Stashok
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebServiceFeatureAnnotation(id = SelectOptimalTransportFeature.ID, bean = SelectOptimalTransportFeature.class)
public @interface SelectOptimalTransport {
    /**
     * Specifies if this feature is enabled or disabled.
     */
    boolean enabled() default true;

    Transport transport() default Transport.TCP;
}
