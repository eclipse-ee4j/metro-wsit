/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import java.util.HashMap;


import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.assembler.metro.ServerPipelineHook;
import com.sun.xml.ws.policy.PolicyMap;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;

/**
 * This is used by JAXWSContainer to return proper 196 security and
 *  app server monitoring pipes to the StandAlonePipeAssembler and 
 *  TangoPipeAssembler
 */
public class ServerPipeCreator extends ServerPipelineHook {
     
    public ServerPipeCreator(){
    }

    @Override
    public Pipe createSecurityPipe(PolicyMap map, SEIModel sei,
            WSDLPort port, WSEndpoint owner, Pipe tail) {

        HashMap<String, Object> props = new HashMap<>();

        boolean httpBinding = BindingID.XML_HTTP.equals(owner.getBinding().getBindingId());
        props.put(PipeConstants.POLICY, map);
        props.put(PipeConstants.SEI_MODEL, sei);
        props.put(PipeConstants.WSDL_MODEL, port);
        props.put(PipeConstants.ENDPOINT, owner);
        props.put(PipeConstants.NEXT_PIPE, tail);
        props.put(PipeConstants.CONTAINER, owner.getContainer());
        return new ServerSecurityPipe(props, tail, httpBinding);
    }
   
    @Override
    public 
    @NotNull
    Tube createSecurityTube(ServerTubelineAssemblyContext context) {

        HashMap<String, Object> props = new HashMap<>();
        boolean httpBinding = BindingID.XML_HTTP.equals(context.getEndpoint().getBinding().getBindingId());
        props.put(PipeConstants.POLICY, context.getPolicyMap());
        props.put(PipeConstants.SEI_MODEL, context.getSEIModel());
        props.put(PipeConstants.WSDL_MODEL, context.getWsdlPort());
        props.put(PipeConstants.ENDPOINT, context.getEndpoint());
        //props.put(PipeConstants.NEXT_PIPE,context.getAdaptedTubelineHead());
        props.put(PipeConstants.NEXT_TUBE, context.getTubelineHead());
        props.put(PipeConstants.CONTAINER, context.getEndpoint().getContainer());
        //TODO: Convert GF security pipes to TUBE(s).
        ServerSecurityTube serverTube = new ServerSecurityTube(props, context.getTubelineHead(), httpBinding);
        return serverTube;
    }
}
