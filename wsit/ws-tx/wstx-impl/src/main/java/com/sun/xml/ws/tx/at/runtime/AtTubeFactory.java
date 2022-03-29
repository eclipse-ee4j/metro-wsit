/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.runtime;

import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.PipelineAssembler;
import com.sun.xml.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.api.tx.at.TransactionalFeature;
import com.sun.xml.ws.tx.at.internal.WSATGatewayRM;
import com.sun.xml.ws.tx.at.tube.WSATClientTube;
import com.sun.xml.ws.tx.at.tube.WSATServerTube;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebServiceException;

public final class AtTubeFactory implements TubeFactory {

    private static final String WSAT_SOAP_NSURI = "http://schemas.xmlsoap.org/ws/2004/10/wsat";
    private static final QName AT_ALWAYS_CAPABILITY = new QName(WSAT_SOAP_NSURI, "ATAlwaysCapability");
    private static final QName AT_ASSERTION = new QName(WSAT_SOAP_NSURI, "ATAssertion");

    /**
     * Default constructor.
     */
    public AtTubeFactory() {}

    /**
     * Adds TX tube to the client-side tubeline, depending on whether TX is enabled or not.
     *
     * @param context wsit client tubeline assembler context
     * @return new tail of the client-side tubeline
     */
    @Override
    public Tube createTube(ClientTubelineAssemblyContext context) {
        final TransactionalFeature feature = context.getBinding().getFeature(TransactionalFeature.class);
        if (isWSATPolicyEnabled(context.getPolicyMap(), context.getWsdlPort(), false)
                || (feature != null && feature.isEnabled())) { //todo add the case where policy is enabled but annotation is NEVER
            WSATGatewayRM.create();
            return new WSATClientTube(context.getTubelineHead(), context, feature);
        } else {
            return context.getTubelineHead();
        }
    }

    /**
     * Adds TX tube to the service-side tubeline, depending on whether TX is enabled or not.
     *
     * @param context wsit service tubeline assembler context
     * @return new head of the service-side tubeline
     */
    @Override
    public Tube createTube(ServerTubelineAssemblyContext context) {
        final TransactionalFeature feature = context.getEndpoint().getBinding().getFeature(TransactionalFeature.class);
        if (isWSATPolicyEnabled(context.getPolicyMap(), context.getWsdlPort(), true)
                || (feature != null && feature.isEnabled())) { //todo add the case where policy is enabled but annotation is NEVER
            WSATGatewayRM.create();
            return new WSATServerTube(context.getTubelineHead(), context, feature);
        } else {
            return context.getTubelineHead();
        }
    }


    /**
     * Checks to see whether WS-Atomic Transactions are enabled or not.
     *
     * @param policyMap policy map for {@link AtTubeFactory} assembler
     * @param wsdlPort the WSDLPort object
     * @param isServerSide true if this method is being called from {@link PipelineAssembler#createServer(ServerPipeAssemblerContext)}
     * @return true if Transactions is enabled, false otherwise
     */
    private boolean isWSATPolicyEnabled(PolicyMap policyMap, WSDLPort wsdlPort, boolean isServerSide) {
        if (policyMap == null || wsdlPort == null /* TODO : fix missing util method || !Util.isJTAAvailable()*/) {
            // false for standalone WSIT client or WSIT Service in Tomcat
            return false;
        }
        try {
            PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(wsdlPort.getOwner().getName(), wsdlPort.getName());
            Policy policy = policyMap.getEndpointEffectivePolicy(endpointKey);
            for (WSDLBoundOperation wbo : wsdlPort.getBinding().getBindingOperations()) {
                PolicyMapKey operationKey = PolicyMap.createWsdlOperationScopeKey(wsdlPort.getOwner().getName(), wsdlPort.getName(), wbo.getName());
                policy = policyMap.getOperationEffectivePolicy(operationKey);
                if (policy != null) {
                    // look for ATAlwaysCapable on the server side
                    if ((isServerSide) && (policy.contains(AT_ALWAYS_CAPABILITY))) {
                        return true;
                    }
                    // look for ATAssertion in both client and server
                    if (policy.contains(AT_ASSERTION)) {
                        return true;
                    }
                }
            }
        } catch (PolicyException e) {
            throw new WebServiceException(e);
        }
        return false;
    }
}
