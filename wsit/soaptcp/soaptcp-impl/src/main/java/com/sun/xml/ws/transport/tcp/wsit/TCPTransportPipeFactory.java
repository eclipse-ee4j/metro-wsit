/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.transport.tcp.client.ServiceChannelTransportPipe;
import com.sun.xml.ws.transport.tcp.client.TCPTransportPipe;
import com.sun.xml.ws.transport.tcp.util.TCPConstants;
import com.sun.xml.ws.transport.tcp.servicechannel.stubs.ServiceChannelWSImplService;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceException;

/**
 * @author Alexey Stashok
 */
public class TCPTransportPipeFactory extends com.sun.xml.ws.transport.tcp.client.TCPTransportPipeFactory {
    private static final QName serviceChannelServiceName = new ServiceChannelWSImplService().getServiceName();

    @Override
    public Tube doCreate(ClientTubeAssemblerContext context) {
        return doCreate(context, true);
    }

    public static Tube doCreate(@NotNull final ClientTubeAssemblerContext context, final boolean checkSchema) {
        if (checkSchema && !TCPConstants.PROTOCOL_SCHEMA.equalsIgnoreCase(context.getAddress().getURI().getScheme())) {
            return null;
        }
        
        initializeConnectionManagement(context.getWsdlModel());
        if (context.getService().getServiceName().equals(serviceChannelServiceName)) {
            return new ServiceChannelTransportPipe(context);
        }
        
        return new TCPTransportPipe(context);
    }    
    
    /**
     * Sets the client ConnectionManagement settings, which are passed via cliend
     * side policies for ServiceChannelWS
     */
    private static void initializeConnectionManagement(WSDLPort port) {
        PolicyConnectionManagementSettingsHolder instance = 
                PolicyConnectionManagementSettingsHolder.getInstance();
        
        if (instance.clientSettings == null) {
            synchronized(instance) {
                if (instance.clientSettings == null) {
                    instance.clientSettings = 
                            PolicyConnectionManagementSettingsHolder.createSettingsInstance(port);
                }
            }
        }
    }
    
    private static int retrieveCustomTCPPort(WSDLPort port) {
        try {
            WSDLModel model = port.getBinding().getOwner();
            PolicyMap policyMap = model.getPolicyMap();
            if (policyMap != null) {
                PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(port.getOwner().getName(), port.getName());
                Policy policy = policyMap.getEndpointEffectivePolicy(endpointKey);

                if (policy != null && policy.contains(com.sun.xml.ws.transport.tcp.wsit.TCPConstants.TCPTRANSPORT_POLICY_ASSERTION)) {
                    /* if client set to choose optimal transport and server has TCP transport policy
                    then need to check server side policy "enabled" attribute*/
                    for (AssertionSet assertionSet : policy) {
                        for (PolicyAssertion assertion : assertionSet) {
                            if (assertion.getName().equals(com.sun.xml.ws.transport.tcp.wsit.TCPConstants.TCPTRANSPORT_POLICY_ASSERTION)) {
                                String value = assertion.getAttributeValue(new QName("port"));
                                if (value == null) {
                                    return -1;
                                }
                                value = value.trim();

                                try {
                                    return Integer.parseInt(value);
                                } catch(NumberFormatException e) {
                                }
                                
                                return -1;
                            }
                        }
                    }
                }
            }

            return -1;
        } catch (PolicyException e) {
            throw new WebServiceException(e);
        }
    }
}
