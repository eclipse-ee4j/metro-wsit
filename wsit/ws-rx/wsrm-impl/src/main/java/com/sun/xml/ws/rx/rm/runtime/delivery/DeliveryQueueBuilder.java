/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.delivery;

import com.sun.istack.NotNull;
import com.sun.xml.ws.rx.rm.runtime.RmConfiguration;
import com.sun.xml.ws.rx.rm.runtime.delivery.Postman.Callback;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public final class DeliveryQueueBuilder {
    
    private final @NotNull RmConfiguration configuration;
    private final @NotNull Postman postman;
    private final @NotNull Postman.Callback deliveryCallback;

    private Sequence sequence;

    public static DeliveryQueueBuilder getBuilder(@NotNull RmConfiguration configuration, @NotNull Postman postman, @NotNull Callback deliveryCallback) {
        return new DeliveryQueueBuilder(configuration, postman, deliveryCallback);
    }

    private DeliveryQueueBuilder(@NotNull RmConfiguration configuration, @NotNull Postman postman, @NotNull Callback deliveryCallback) {
        assert configuration != null;
        assert postman != null;
        assert deliveryCallback != null;

        this.configuration = configuration;
        this.postman = postman;
        this.deliveryCallback = deliveryCallback;
    }

    public void sequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public DeliveryQueue build() {
        if (configuration.getRmFeature().isOrderedDeliveryEnabled()) {
            boolean rejectOutOfOrderMessages = configuration.getRmFeature().isRejectOutOfOrderMessagesEnabled();
            return new InOrderDeliveryQueue(postman, deliveryCallback, sequence, configuration.getRmFeature().getDestinationBufferQuota(), rejectOutOfOrderMessages);
        } else {
            return new SimpleDeliveryQueue(postman, deliveryCallback);
        }
    }
}
