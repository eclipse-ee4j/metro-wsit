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

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.common.TransactionImportManager;
import com.sun.xml.ws.tx.at.internal.WSATGatewayRM;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.tx.at.internal.XidImpl;
import com.sun.xml.ws.tx.at.runtime.TransactionIdHelper;
import com.sun.xml.ws.tx.at.internal.ForeignRecoveryContext;
import com.sun.xml.ws.tx.at.internal.ForeignRecoveryContextManager;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.tx.at.common.TransactionManagerImpl;
import com.sun.xml.ws.tx.coord.common.CoordinationContextBuilder;
import com.sun.xml.ws.tx.coord.common.RegistrationIF;
import com.sun.xml.ws.tx.coord.common.WSCBuilderFactory;
import com.sun.xml.ws.tx.coord.common.client.RegistrationMessageBuilder;
import com.sun.xml.ws.tx.coord.common.client.RegistrationProxyBuilder;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterResponseType;
import com.sun.xml.ws.tx.coord.common.types.BaseRegisterType;
import com.sun.xml.ws.tx.coord.common.types.CoordinationContextIF;

import javax.transaction.xa.Xid;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceException;

public class WSATServerHelper implements WSATServer {
    private static final Logger LOGGER = Logger.getLogger(WSATServerHelper.class);
    Xid xidToResume; //todo should not rely on tube member vars, use context map instead

    @Override
    public void doHandleRequest(MessageHeaders headers, TransactionalAttribute tx) {
        if (WSATHelper.isDebugEnabled())
            debug("processRequest MessageHeaders:" + headers + " TransactionalAttribute:" + tx + " isEnabled:" + tx.isEnabled());
        CoordinationContextBuilder ccBuilder = CoordinationContextBuilder.headers(headers, tx.getVersion());
        if (ccBuilder != null) {
            while(!WSATGatewayRM.isReadyForRuntime) {
                debug("WS-AT recovery is enabled but WS-AT is not ready for runtime.  Processing WS-AT recovery log files...");
                WSATGatewayRM.getInstance().recover();
            }
            xidToResume = processIncomingTransaction(ccBuilder);
        } else {
            if (tx.isRequired()) throw new WebServiceException("transaction context is required to be inflowed");
        }
    }

    @Override
    public void doHandleResponse(TransactionalAttribute transactionalAttribute) {
        if(xidToResume!=null) {
            debug("doHandleResponse about to suspend " + xidToResume);
            TransactionImportManager.getInstance().release(xidToResume);
        }
    }

    @Override
    public void doHandleException(Throwable throwable) {
        if(xidToResume!=null) {
            debug("doHandleException about to suspend " + xidToResume + " Exception:" + throwable);
            TransactionImportManager.getInstance().release(xidToResume);
        }
    }

    /**
     * builder can not be null.
     * //ref params
     * //"Identifier in registerOperation is null" wscoor:InvalidState if omitted
     * ReferenceParameters referenceParameters = registrationCoordinatorEndpointReference.getReferenceParameters();
     * List<Object> list = referenceParameters.getElements();
     * for (Object aList : list) header.addChildElement((SOAPElement) aList);
     * //Request messages
     * //    MUST include a wsa:MessageID header.
     * //    MUST include a wsa:ReplyTo header.
     * @param builder CoordinationContextBuilder
     */
    private Xid processIncomingTransaction(CoordinationContextBuilder builder) {
        if(WSATHelper.isDebugEnabled()) debug("in processingIncomingTransaction builder:"+builder);
        //we either need to fast suspend immediately and resume after register as we are doing or move this after register
        CoordinationContextIF cc = builder.buildFromHeader();
        long timeout = cc.getExpires().getValue();
        String tid = cc.getIdentifier().getValue().replace("urn:","").replaceAll("uuid:","");
        boolean isRegistered = false;
        Xid foreignXid = null; //serves as a boolean
        try {
          foreignXid = WSATHelper.getTransactionServices().importTransaction((int) timeout, tid.getBytes());
          if(foreignXid!=null) isRegistered = true;
          if(!isRegistered) {
              foreignXid = new XidImpl(tid.getBytes());
              register(builder, cc, foreignXid, timeout, tid);
          }
        } catch (Exception e) {
            if(foreignXid!=null) {
                TransactionImportManager.getInstance().release(foreignXid);
            } else {
                debug("in processingIncomingTransaction WSATException foreignXid is null");
            }
            throw new WebServiceException(e);
        }
        return foreignXid;
    }

    private void register(
            CoordinationContextBuilder builder, CoordinationContextIF cc,
            Xid foreignXid, long timeout, String participantId)
    {
        participantId = TransactionIdHelper.getInstance().xid2wsatid(foreignXid);
        Transactional.Version version = builder.getVersion();
        WSCBuilderFactory factory = WSCBuilderFactory.newInstance(version);
        RegistrationMessageBuilder rrBuilder = factory.newWSATRegistrationRequestBuilder();
        BaseRegisterType registerType = rrBuilder.durable(true).txId(participantId).routing().build();
        RegistrationProxyBuilder proxyBuilder = factory.newRegistrationProxyBuilder();
        proxyBuilder.
                to(cc.getRegistrationService()).
                txIdForReference(participantId).
                timeout(timeout);
        RegistrationIF proxyIF = proxyBuilder.build();
        BaseRegisterResponseType registerResponseType = proxyIF.registerOperation(registerType);
        if(WSATHelper.isDebugEnabled()) debug("Return from registerOperation call:"+registerResponseType);
        if (registerResponseType != null){
            EndpointReference epr = registerResponseType.getCoordinatorProtocolService();
            ForeignRecoveryContext frc =
                    ForeignRecoveryContextManager.getInstance().addAndGetForeignRecoveryContextForTidByteArray(
                            foreignXid);
            frc.setEndpointReference(epr,builder.getVersion());
            TransactionManagerImpl.getInstance().putResource(
                    WSATConstants.TXPROP_WSAT_FOREIGN_RECOVERY_CONTEXT, frc);
        } else {
            log("Sending fault. Context refused registerResponseType is null (this may be due to request timeout)");
            throw new WebServiceException(
                    "Sending fault. Context refused registerResponseType is null (this may be due to request timeout)");
        }
    }

    public void log(String message) {
        LOGGER.info(LocalizationMessages.WSAT_4612_WSAT_SERVERHELPER(message));
    }

    private void debug(String message) {
        LOGGER.info(message);
    }
}
