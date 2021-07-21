/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.assembler.metro;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.policy.PolicyMap;

/**
 * @author Arun Gupta
 */
public class ServerPipelineHook extends com.sun.xml.ws.api.server.ServerPipelineHook {
    /**
     * Called during the server-side pipeline construction process once to allow a
     * container to register a pipe for security on the service endpoint.
     *
     * This pipe will be injected to a point very close to the transport, allowing
     * it to do some security operations.
     *
     * @param policyMap {@link PolicyMap} holding policies for a scope
     * @param seiModel abstraction of server-side SEI
     * @param wsdlModel abstraction of wsdl:port
     * @param owner instance of deployed service
     * @param tail
     *      Head of the partially constructed pipeline. If the implementation
     *      wishes to add new pipes, it should do so by extending
     *      {@link com.sun.xml.ws.api.pipe.helper.AbstractFilterPipeImpl} and making sure that this {@link Pipe}
     *      eventually processes messages.
     *
     * @return
     *      The default implementation just returns <code>tail</code>, which means
     *      no additional pipe is inserted. If the implementation adds
     *      new pipes, return the new head pipe.
     */
    public @NotNull Pipe createSecurityPipe(@Nullable PolicyMap policyMap, @Nullable SEIModel seiModel, @Nullable WSDLPort wsdlModel, @NotNull WSEndpoint owner, @NotNull Pipe tail) {
        return tail;
    }

    /**
     * Called during the server-side tubeline construction process once to allow a
     * container to register a tube for security on the service endpoint.
     *
     * This tube will be injected to a point very close to the transport, allowing
     * it to do some security operations.
     * <p>
     * If the implementation wishes to add new tubes, it should do so by extending
     * {@link com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl} and making sure that this {@link Tube}
     * eventually processes messages.
     *
     * @param context
     *      Represents abstraction of policy map, tubeline head, SEI, WSDL abstraction etc. Context can be used
     *      whether add a new tube to the head or not.     
     *
     * @return
     *      The default implementation just returns <code>tail</code>, which means
     *      no additional tube is inserted. If the implementation adds
     *      new tubes, return the new head tube.
     */
    public @NotNull Tube createSecurityTube(ServerTubelineAssemblyContext context) {
        return context.getTubelineHead();
    }    
}
