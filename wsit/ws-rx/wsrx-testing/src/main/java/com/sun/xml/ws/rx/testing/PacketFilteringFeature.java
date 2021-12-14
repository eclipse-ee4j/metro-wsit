/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.testing;

import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jakarta.xml.ws.WebServiceFeature;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
@ManagedData
public final class PacketFilteringFeature extends WebServiceFeature {

    public static final String ID = "com.sun.xml.ws.rm.runtime.testing.PacketFilteringFeature";
    //
    private static final Logger LOGGER = Logger.getLogger(PacketFilteringFeature.class);
    //
    private final List<Class<? extends PacketFilter>> filterClasses;

    public PacketFilteringFeature() {
        // this constructor is here just to satisfy JAX-WS specification requirements
        this.filterClasses = Collections.emptyList();
        this.enabled = true;
    }

    public PacketFilteringFeature(boolean enabled) {
        // this constructor is here just to satisfy JAX-WS specification requirements
        this.filterClasses = Collections.emptyList();
        this.enabled = enabled;
    }

    public PacketFilteringFeature(Class<? extends PacketFilter>... filterClasses) {
        this(true, filterClasses);
    }

    @FeatureConstructor({"enabled", "filters"})
    public PacketFilteringFeature(boolean enabled, Class<? extends PacketFilter>... filterClasses) {
        this.enabled = enabled;
        if (filterClasses != null && filterClasses.length > 0) {
            this.filterClasses = Collections.unmodifiableList(Arrays.asList(filterClasses));
        } else {
            this.filterClasses = Collections.emptyList();
        }
    }

    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    List<PacketFilter> createFilters(RuntimeContext context) {
        List<PacketFilter> filters = new ArrayList<>(filterClasses.size());
        
        for (Class<? extends PacketFilter> filterClass : filterClasses) {
            try {
                final PacketFilter filter = filterClass.newInstance();
                filter.configure(context);
                filters.add(filter);
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.warning("Error instantiating packet filter of class [" + filterClass.getName() + "]", ex);
            }
        }

        return filters;
    }

    @ManagedAttribute
    boolean hasFilters() {
        return !filterClasses.isEmpty();
    }
}
