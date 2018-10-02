/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebServiceFeatureAnnotation(id = PacketFilteringFeature.ID, bean = PacketFilteringFeature.class)
public @interface PacketFiltering {
    /**
     * Specifies if this feature is enabled or disabled.
     */
    boolean enabled() default true;

    /**
     * Property to determine the packet filters.
     */
    Class<? extends PacketFilter>[] filters() default {};
}

