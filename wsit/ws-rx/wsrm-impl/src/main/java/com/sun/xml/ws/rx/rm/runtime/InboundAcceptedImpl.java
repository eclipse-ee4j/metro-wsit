/*
 * Copyright (c) 2017, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.rx.rm.runtime;

import java.util.logging.Level;

import com.oracle.webservices.oracle_internal_api.rm.InboundAccepted;
import com.oracle.webservices.oracle_internal_api.rm.InboundAcceptedAcceptFailed;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.runtime.transaction.TransactionPropertySet;

public class InboundAcceptedImpl extends InboundAccepted {
    private static final Logger LOGGER = Logger.getLogger(InboundAcceptedImpl.class);
    private final JaxwsApplicationMessage request;
    private final RuntimeContext rc;
    private Boolean accepted;

    public InboundAcceptedImpl(JaxwsApplicationMessage request, RuntimeContext rc) {
        this.request = request;
        this.rc = rc;
    }

    @Override
    public void setAccepted(Boolean accept) throws InboundAcceptedAcceptFailed {

        if (accept == null) {
            throw new IllegalArgumentException("Found supplied accept Boolean null.");
        }

        accepted = accept;

        TransactionPropertySet ps =
                request.getPacket().getSatellite(TransactionPropertySet.class);
        boolean txOwned = (ps != null && ps.isTransactionOwned());

        if (accept) {
            rc.destinationMessageHandler.acknowledgeApplicationLayerDelivery(request);

            if (txOwned) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Transaction status before commit: " + rc.transactionHandler.getStatusAsString());
                }

                try {
                    rc.transactionHandler.commit();
                } catch (Throwable t) {
                    accepted = null;
                    throw new InboundAcceptedAcceptFailed("Not able to commit the TX.", t);
                }
            } else {
                //Do nothing as we don't own the TX
            }
        } else {//accept == false
            if (txOwned) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Transaction status before rollback: " + rc.transactionHandler.getStatusAsString());
                }
                rc.transactionHandler.rollback();
            } else {
                //Don't roll back as we don't own the TX but if active then mark for roll back
                if (rc.transactionHandler.userTransactionAvailable() &&
                        rc.transactionHandler.isActive()) {
                    rc.transactionHandler.setRollbackOnly();
                }
            }
        }
    }

    @Override
    public Boolean getAccepted() {
        return accepted;
    }

    @Override
    public String getRMSequenceId() {
        return (String)request.getPacket().invocationProperties.get(ServerTube.SEQUENCE_PROPERTY);
    }

    @Override
    public long getRMMessageNumber() {
        return (long) (Long)request.getPacket().invocationProperties.get(ServerTube.MESSAGE_NUMBER_PROPERTY);
    }
}
