/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.jaxws.impl;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.policy.PolicyMap;

/**
 * {@link TubeConfiguration} for servers.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ServerTubeConfiguration extends TubeConfiguration {
    private final WSEndpoint endpoint;

    public ServerTubeConfiguration(PolicyMap policy, WSDLPort wsdlModel, WSEndpoint endpoint) {
        super(policy, wsdlModel);
        this.endpoint = endpoint;
    }

    /**
     * Gets the {@link WSEndpoint} for which the pipeline is being created.
     *
     * <p>
     * {@link WSEndpoint} provides information about the surrounding environment,
     * such as access to the application server.
     *
     * @return always non-null same object.
     */
    public WSEndpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public WSBinding getBinding() {
        return endpoint.getBinding();
    }
}
