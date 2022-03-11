/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.tube;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.api.tx.at.TransactionalFeature;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WSATClientTube extends AbstractFilterTubeImpl implements WSATConstants {
    WSBinding m_wsbinding;
    WSATClient m_wsatClientHelper = new WSATClientHelper();
    private TransactionalFeature m_transactionalFeature;
    private WSDLPort m_port;

    public WSATClientTube(Tube next, ClientTubelineAssemblyContext context, TransactionalFeature feature) {  //for tube
        super(next);
        m_wsbinding = context.getBinding();
        m_transactionalFeature = feature;
        m_port = context.getWsdlPort();
    }

    private WSATClientTube(WSATClientTube that, TubeCloner cloner) {
        super(that, cloner);
        this.m_wsbinding = that.m_wsbinding;
        m_transactionalFeature = that.m_transactionalFeature;
        m_port =that.m_port;
    }


    public Set<QName> getHeaders() {
        return new HashSet<>();
    }

    @Override
    @NotNull
    public NextAction processRequest(@NotNull Packet request) {
        try{
            doProcessRequest(request);
        } catch (Exception e){
            e.printStackTrace();
        }
        return super.processRequest(request);
    }

    private void doProcessRequest(Packet request) {
        TransactionalAttribute transactionalAttribute =
                WSATTubeHelper.getTransactionalAttribute(m_transactionalFeature, request, m_port);
        transactionalAttribute.setSoapVersion(m_wsbinding.getSOAPVersion());
        List<Header> addedHeaders = m_wsatClientHelper.doHandleRequest(transactionalAttribute, request.invocationProperties);
        if (addedHeaders != null) {
            for (Header header : addedHeaders) {
                request.getMessage().getHeaders().add(header);
            }
        }
    }


    @Override
    @NotNull
    public NextAction processResponse(@NotNull Packet response) {
        m_wsatClientHelper.doHandleResponse(response.invocationProperties);
        return super.processResponse(response);
    }


    @Override
    @NotNull
    public NextAction processException(Throwable t) {
        Map<String, Object> map = com.sun.xml.ws.api.pipe.Fiber.current().getPacket().invocationProperties;
        m_wsatClientHelper.doHandleResponse(map);
        return super.processException(t);   
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        return new WSATClientTube(this, cloner);
    }
}
