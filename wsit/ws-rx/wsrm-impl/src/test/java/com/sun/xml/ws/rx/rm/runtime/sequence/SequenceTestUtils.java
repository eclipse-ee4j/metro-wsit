/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.rx.rm.runtime.RmRuntimeVersion;
import com.sun.xml.ws.rx.rm.runtime.ApplicationMessage;
import com.sun.xml.ws.rx.rm.runtime.RmConfiguration;
import com.sun.xml.ws.rx.rm.runtime.RuntimeContext;
import com.sun.xml.ws.rx.rm.runtime.delivery.DeliveryQueueBuilder;
import com.sun.xml.ws.rx.rm.runtime.delivery.Postman;
import com.sun.xml.ws.rx.rm.runtime.delivery.PostmanPool;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.gmbal.ManagedObjectManager;

/**
 *
 */
final class SequenceTestUtils  {
    private SequenceTestUtils() {}

    static RmConfiguration getConfiguration() {
        final ReliableMessagingFeature rmf = new ReliableMessagingFeatureBuilder(RmProtocolVersion.WSRM200702).build();

        return new RmConfiguration() {

            @Override
            public boolean isReliableMessagingEnabled() {
                return true;
            }

            @Override
            public boolean isMakeConnectionSupportEnabled() {
                return false;
            }

            @Override
            public SOAPVersion getSoapVersion() {
                return SOAPVersion.SOAP_12;
            }

            @Override
            public AddressingVersion getAddressingVersion() {
                return AddressingVersion.W3C;
            }

            @Override
            public boolean requestResponseOperationsDetected() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public ReliableMessagingFeature getRmFeature() {
                return rmf;
            }
            @Override
            public ManagedObjectManager getManagedObjectManager() {
                return null;
            }

            @Override
            public RmRuntimeVersion getRuntimeVersion() {
                return RmRuntimeVersion.WSRM200702;
            }

            @Override
            public com.oracle.webservices.oracle_internal_api.rm.ReliableMessagingFeature getInternalRmFeature() {
                // TODO Auto-generated method stub
                return null;
            }

       };
    }

    static DeliveryQueueBuilder getDeliveryQueueBuilder() {


        return DeliveryQueueBuilder.getBuilder(getConfiguration(), PostmanPool.INSTANCE.getPostman(), new Postman.Callback() {

            @Override
            public void deliver(ApplicationMessage message) {
            }

            @Override
            public RuntimeContext getRuntimeContext() {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }

    static List<Sequence.AckRange> createAckRanges(long... msgNumbers) {
        List<Sequence.AckRange> ackList = new LinkedList<>();

        if (msgNumbers.length > 0) {
            long lower = msgNumbers[0];
            long upper = msgNumbers[0] - 1;
            for (long number : msgNumbers) {
                if (number == upper + 1) {
                    upper = number;
                } else {
                    ackList.add(new Sequence.AckRange(lower, upper));
                    lower = upper = number;
                }
            }
            ackList.add(new Sequence.AckRange(lower, upper));
        }
        return ackList;
    }

}
