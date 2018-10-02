/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.tube;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.api.tx.at.TransactionalFeature;

public class WSATTubeHelper {   
    private static final Logger LOGGER = Logger.getLogger(WSATTubeHelper.class);

    public static TransactionalAttribute getTransactionalAttribute(TransactionalFeature feature, Packet packet, WSDLPort port) {
        if (feature == null) {
            feature =
                    new TransactionalFeature(true, Transactional.TransactionFlowType.SUPPORTS, Transactional.Version.DEFAULT);
        }
        //a dynamic service client can be created without a wsdl.
        if (port == null) {
            boolean isEnabled = feature.isEnabled() && Transactional.TransactionFlowType.NEVER != feature.getFlowType();
            boolean isRequired = Transactional.TransactionFlowType.MANDATORY == feature.getFlowType();
            if (WSATHelper.isDebugEnabled()) {
                debug("no wsdl port found, the effective transaction attribute is: enabled(" + isEnabled + "),required(" + isRequired + "), version(" + feature.getVersion() + ").");
            }
            return new TransactionalAttribute(isEnabled, isRequired, feature.getVersion());
        }
        WSDLBoundOperation wsdlBoundOperation = packet.getMessage().getOperation(port);
        if (wsdlBoundOperation != null
                && wsdlBoundOperation.getOperation() != null
                && !wsdlBoundOperation.getOperation().isOneWay()) {
            String opName = wsdlBoundOperation.getName().getLocalPart();
            boolean isEnabled = feature.isEnabled(opName)
                    && Transactional.TransactionFlowType.NEVER != feature.getFlowType(opName);
            boolean isRequired = Transactional.TransactionFlowType.MANDATORY == feature.getFlowType(opName);

            if (WSATHelper.isDebugEnabled()) {
                debug("the effective transaction attribute for operation' " + opName + "' is : enabled(" + isEnabled + "),required(" + isRequired + "), version(" + feature.getVersion() + ").");
            }
            return new TransactionalAttribute(isEnabled, isRequired, feature.getVersion());
        }
        if (WSATHelper.isDebugEnabled()) {
            debug("no twoway operation found for this request, the effective transaction attribute is disabled.");
        }
        return new TransactionalAttribute(false, false, Transactional.Version.DEFAULT);
    }

    private static void debug(String message) {
        LOGGER.info(message);
    }
}
