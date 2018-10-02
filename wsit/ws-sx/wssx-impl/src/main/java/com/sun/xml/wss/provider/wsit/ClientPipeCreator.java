/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.xml.ws.assembler.dev.ClientPipelineHook;
import com.sun.xml.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.ws.policy.PolicyMap;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;

/**
 * This is used by WSClientContainer to return proper 196 security pipe
 * to the StandAlonePipeAssembler and TangoPipeAssembler
 */
public class ClientPipeCreator extends ClientPipelineHook {
        
    public ClientPipeCreator(){
    }

    
    @Override
    public Pipe createSecurityPipe(PolicyMap map, 
            ClientPipeAssemblerContext ctxt, Pipe tail) {
        HashMap<Object, Object> propBag = new HashMap<Object, Object>();
        propBag.put(PipeConstants.POLICY, map);
        propBag.put(PipeConstants.WSDL_MODEL, ctxt.getWsdlModel());
        propBag.put(PipeConstants.SERVICE, ctxt.getService());
        propBag.put(PipeConstants.BINDING, ctxt.getBinding());
        propBag.put(PipeConstants.ENDPOINT_ADDRESS, ctxt.getAddress());
    	propBag.put(PipeConstants.NEXT_PIPE,tail);
        propBag.put(PipeConstants.CONTAINER,ctxt.getContainer());
        propBag.put(PipeConstants.ASSEMBLER_CONTEXT, ctxt);
        ClientSecurityPipe ret = new ClientSecurityPipe(propBag, tail);
        return ret;
    }
    
    
    @Override
    public @NotNull Tube createSecurityTube(ClientTubelineAssemblyContext context) {
        HashMap<Object, Object> propBag = new HashMap<Object, Object>();
        propBag.put(PipeConstants.POLICY, context.getPolicyMap());
        propBag.put(PipeConstants.WSDL_MODEL, context.getWrappedContext().getWsdlModel());
        propBag.put(PipeConstants.SERVICE, context.getService());
        propBag.put(PipeConstants.BINDING, context.getBinding());
        propBag.put(PipeConstants.ENDPOINT_ADDRESS, context.getAddress());
//        propBag.put(PipeConstants.NEXT_PIPE,context.getAdaptedTubelineHead());
        propBag.put(PipeConstants.NEXT_TUBE, context.getTubelineHead());
        propBag.put(PipeConstants.CONTAINER, context.getContainer());
        propBag.put(PipeConstants.WRAPPED_CONTEXT, context.getWrappedContext());
        propBag.put(PipeConstants.ASSEMBLER_CONTEXT, context);
        ClientSecurityTube ret = new ClientSecurityTube(propBag, context.getTubelineHead());
        return ret;
    }
    
}
