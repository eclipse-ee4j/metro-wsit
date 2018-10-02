/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.wsit;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.transport.tcp.resources.MessagesMessages;
import com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelCreator;
import com.sun.xml.ws.transport.tcp.servicechannel.ServiceChannelWSImpl;
import com.sun.xml.ws.transport.tcp.util.ConnectionManagementSettings;
import com.sun.xml.ws.transport.tcp.util.ConnectionManagementSettings.ConnectionManagementSettingsHolder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

/**
 * SOAP/TCP connection cache settings holder.
 * Implements holder, which gets connection settings from 
 * correspondent WS policy map.
 *
 * @author Alexey Stashok
 */
public class PolicyConnectionManagementSettingsHolder
        implements ConnectionManagementSettingsHolder {

    private static final int DEFAULT_VALUE = -1;

    private static final Logger logger = Logger.getLogger(
            com.sun.xml.ws.transport.tcp.util.TCPConstants.LoggingDomain);

    volatile ConnectionManagementSettings clientSettings;
    volatile ConnectionManagementSettings serverSettings;
    
    private static final PolicyConnectionManagementSettingsHolder instance = 
            new PolicyConnectionManagementSettingsHolder();
    
    public static PolicyConnectionManagementSettingsHolder getInstance() {
        return instance;
    }
    
    public ConnectionManagementSettings getClientSettings() {
        return clientSettings;
    }

    public ConnectionManagementSettings getServerSettings() {
        if (serverSettings == null) {
            synchronized (this) {
                if (serverSettings == null) {
                    WSEndpoint<ServiceChannelWSImpl> endpoint = ServiceChannelCreator.getServiceChannelEndpointInstance();
                    serverSettings = createSettingsInstance(endpoint.getPort());
                }
            }
        }

        return serverSettings;
    }

    
    @NotNull
    static ConnectionManagementSettings createSettingsInstance(final @NotNull WSDLPort port) {
        try {
            WSDLModel model = port.getBinding().getOwner();
            PolicyMap policyMap = model.getPolicyMap();
            if (policyMap != null) {
                PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(com.sun.xml.ws.transport.tcp.util.TCPConstants.SERVICE_CHANNEL_WS_NAME,
                        com.sun.xml.ws.transport.tcp.util.TCPConstants.SERVICE_CHANNEL_WS_PORT_NAME);
                Policy policy = policyMap.getEndpointEffectivePolicy(endpointKey);
                if (policy != null && policy.contains(TCPConstants.TCPTRANSPORT_CONNECTION_MANAGEMENT_ASSERTION)) {
                    for ( AssertionSet assertionSet : policy) {
                        for ( PolicyAssertion assertion : assertionSet) {
                            if (assertion.getName().equals(TCPConstants.TCPTRANSPORT_CONNECTION_MANAGEMENT_ASSERTION)) {
                                int highWatermark = getAssertionAttrValue(assertion, TCPConstants.TCPTRANSPORT_CONNECTION_MANAGEMENT_HIGH_WATERMARK_ATTR);
                                int maxParallelConnections = getAssertionAttrValue(assertion, TCPConstants.TCPTRANSPORT_CONNECTION_MANAGEMENT_MAX_PARALLEL_CONNECTIONS_ATTR);
                                int numberToReclaim = getAssertionAttrValue(assertion, TCPConstants.TCPTRANSPORT_CONNECTION_MANAGEMENT_NUMBER_TO_RECLAIM_ATTR);

                                if (logger.isLoggable(Level.FINE)) {
                                    logger.log(Level.FINE,
                                            MessagesMessages.WSTCP_1130_CONNECTION_MNGMNT_SETTINGS_LOADED(
                                            highWatermark,
                                            maxParallelConnections,
                                            numberToReclaim));
                                }
                                return new ConnectionManagementSettings(highWatermark, maxParallelConnections, numberToReclaim);
                            }
                        }
                    }
                }
            }
        } catch ( Exception ex) {
        }

        return new ConnectionManagementSettings(DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_VALUE);

    }

    private static int getAssertionAttrValue(PolicyAssertion assertion, String attrName) {
        String strValue = assertion.getAttributeValue(new QName(attrName));
        if (strValue != null) {
            strValue = strValue.trim();
            return Integer.parseInt(strValue);
        }

        return DEFAULT_VALUE;
    }
}
