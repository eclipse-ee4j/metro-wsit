/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.mc.dev;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.PropertySet;
import com.sun.xml.ws.api.message.Packet;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class AdditionalResponses extends PropertySet {
    public static final String ADDITIONAL_RESPONSE_QUEUE = "com.sun.xml.ws.rx.mc.api.AditionalResponses.ADDITIONAL_RESPONSE_QUEUE";

    private final Queue<Packet> additionalResponsePacketQueue;

    public AdditionalResponses() {
        additionalResponsePacketQueue = new LinkedList<Packet>();
    }

    public static PropertyMap getMODEL() {
        return MODEL;
    }

    @Property(ADDITIONAL_RESPONSE_QUEUE)
    public @NotNull Queue<Packet> getAdditionalResponsePacketQueue() {
        return additionalResponsePacketQueue;
    }

    // The next lines implement the required PropertySet contract as described
    // in the PropertySet.getPropertyMap() javadoc

    private static final PropertyMap MODEL;

    static {
        MODEL = parse(AdditionalResponses.class, MethodHandles.lookup());
    }

    @Override
    protected PropertyMap getPropertyMap() {
        return MODEL;
    }
}
