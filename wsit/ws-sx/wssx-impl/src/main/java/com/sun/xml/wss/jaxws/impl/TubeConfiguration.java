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
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubelineAssembler;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.policy.PolicyMap;

import javax.xml.ws.Dispatch;

/**
 * Entry point to the various configuration information
 * necessary for constructing {@link Tube}s.
 *
 * <p>
 * This object is created by a {@link TubelineAssembler} and
 * passed as a constructor parameter to most pipes,
 * so that they can access configuration information.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class TubeConfiguration {
    private final PolicyMap policy;
    private final WSDLPort wsdlPort;

    TubeConfiguration(PolicyMap policy, WSDLPort wsdlPort) {
        this.policy = policy;
        this.wsdlPort = wsdlPort;
    }

    /**
     * Gets the {@link PolicyMap} that represents
     * the policy information applicable to the current pipeline.
     *
     * @return always non-null same object.
     */
    public PolicyMap getPolicyMap() {
        return policy;
    }

    /**
     * Gets the {@link WSDLPort} that represents
     * the WSDL information about the port for which
     * a pipeline is created.
     *
     * <p>
     * This model is present only when the client
     * provided WSDL to the JAX-WS runtime in some means
     * (such as indirectly through SEI or through {@link Dispatch}.)
     *
     * <p>
     * JAX-WS allows modes of operations where no WSDL
     * is available for the current pipeline, and in which
     * case this model is not present.
     *
     * @return null if this model is not present.
     *         If non-null, it's always the same object.
     */
    public WSDLPort getWSDLPort() {
        return wsdlPort;
    }

    /**
     * Gets the applicable {@link WSBinding} for this pipeline.
     *
     * @return always non-null.
     */
    public abstract WSBinding getBinding();
}
