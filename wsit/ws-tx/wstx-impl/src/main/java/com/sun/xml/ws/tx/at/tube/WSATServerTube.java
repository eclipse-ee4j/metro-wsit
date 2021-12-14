/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.tube;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.api.tx.at.TransactionalFeature;



/**
 * Typical inbound message:
 * <p>
 * {@code
 * <s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope" xmlns:a="http://www.w3.org/2005/08/addressing"><s:Header><a:Action s:mustUnderstand="1">http://tem
 * puri.org/IService/GetData</a:Action><a:MessageID>urn:uuid:353ec55b-3e04-4e13-9471-9652858f7680</a:MessageID><a:ReplyTo><a:Address>http://www.w3.org/2005/08/addr
 * essing/anonymous</a:Address></a:ReplyTo>
 * 
 * <CoordinationContext s:mustUnderstand="1" xmlns="http://schemas.xmlsoap.org/ws/2004/10/wscoor" xmlns:mstx="http://schema
 * s.microsoft.com/ws/2006/02/transactions"><wscoor:Identifier xmlns:wscoor="http://schemas.xmlsoap.org/ws/2004/10/wscoor">urn:uuid:79c06523-2392-45d7-9b66-8cc06d0
 * 07d2d</wscoor:Identifier><Expires>599552</Expires><CoordinationType>http://schemas.xmlsoap.org/ws/2004/10/wsat</CoordinationType>
 * 
 * <RegistrationService><Address x
 * mlns="http://schemas.xmlsoap.org/ws/2004/08/addressing">https://pparkins-us:453/WsatService/Registration/Coordinator/</Address>
 * 
 * <ReferenceParameters xmlns="http:
 * //schemas.xmlsoap.org/ws/2004/08/addressing"><mstx:RegisterInfo><mstx:LocalTransactionId>79c06523-2392-45d7-9b66-8cc06d007d2d</mstx:LocalTransactionId></mstx:Re
 * gisterInfo></ReferenceParameters>
 * 
 * </RegistrationService>
 * 
 * <mstx:IsolationLevel>0</mstx:IsolationLevel><mstx:LocalTransactionId>79c06523-2392-45d7-9b66-8cc06d007d2d
 * </mstx:LocalTransactionId><PropagationToken xmlns="http://schemas.microsoft.com/ws/2006/02/tx/oletx">AQAAAAMAAAAjZcB5kiPXRZtmjMBtAH0tAAAQAAAAAACIAAAAAMToedzE6Hk
 * 0W6xnBOupAC/M+Xk0W6xnUOypANwmcAFYCxcAlOupAGZjYThlYTc3LTYwYjQtNGEwNS1hODI0LWUxM2NjYjQ3MzVjYQABAAALAAAAZM1kzSEAAABQUEFSS0lOUy1VUwAYAAAAUABQAEEAUgBLAEkATgBTAC0AVQB
 * TAAAAAQAAAAEAAAAeAAAAdGlwOi8vcHBhcmtpbnMtdXMubG9jYWxkb21haW4vAAA=</PropagationToken>
 * </CoordinationContext>
 * 
 * <a:To s:mustUnderstand="1">http://localhost:7001/Hello
 * TXWeb/DataService</a:To></s:Header><s:Body><GetData xmlns="http://tempuri.org/"><value>1</value></GetData></s:Body></s:Envelope>--------------------
 * }
 */

public class WSATServerTube extends AbstractFilterTubeImpl implements WSATConstants {
    private static final String  WSATATTRIBUTE = ".wsee.wsat.attribute";
    ServerTubelineAssemblyContext m_context;    
    private WSDLPort m_port;
    private TransactionalFeature m_transactionalFeature;
    WSATServer m_wsatServerHelper = new WSATServerHelper();

    public WSATServerTube(Tube next, ServerTubelineAssemblyContext context, TransactionalFeature feature) {  //for tube
        super(next);
        m_context = context;
        m_port = context.getWsdlPort();
        m_transactionalFeature = feature;
    }

    public WSATServerTube(WSATServerTube that, TubeCloner cloner) {
        super(that,cloner);
        this.m_context = that.m_context;
        this.m_port = that.m_port;
        m_transactionalFeature = that.m_transactionalFeature;
    }


    @Override
    public
    @NotNull
    NextAction processRequest(Packet request) {
      TransactionalAttribute tx = WSATTubeHelper.getTransactionalAttribute(m_transactionalFeature, request, m_port);
      tx.setSoapVersion(m_context.getEndpoint().getBinding().getSOAPVersion());
      request.invocationProperties.put(WSATATTRIBUTE, tx);
      MessageHeaders headers = request.getMessage().getHeaders();
      m_wsatServerHelper.doHandleRequest(headers, tx);
      return super.processRequest(request);
    }

    @Override
    public
    @NotNull
    NextAction processResponse(Packet response) {
        TransactionalAttribute tx = (TransactionalAttribute) response.invocationProperties.get(WSATATTRIBUTE);
        m_wsatServerHelper.doHandleResponse(tx);
        return super.processResponse(response);
    }

    @Override
    public
    @NotNull
    NextAction processException(Throwable t) {
        m_wsatServerHelper.doHandleException(t);
        return super.processException(t);
    }

    @Override
    public void preDestroy() {
        super.preDestroy();
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        return new WSATServerTube(this, cloner);
    }


    NextAction doProcessResponse(Packet request) {
        return super.processResponse(request);
    }

}
