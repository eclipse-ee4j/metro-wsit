/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.policy.PolicyMap;

/**
 * {@link TubeConfiguration} for client.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ClientTubeConfiguration extends TubeConfiguration {
    private final WSBinding binding;

    public ClientTubeConfiguration(PolicyMap policy, WSDLPort wsdlPort, WSBinding binding) {
        super(policy, wsdlPort);
        this.binding = binding;
    }

    public WSBinding getBinding() {
        return binding;
    }
}
