/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.common.endpoint;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages; 
import com.sun.xml.ws.tx.at.internal.XidImpl;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.tx.at.runtime.TransactionServices;
import com.sun.xml.ws.tx.at.WSATException;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.at.WSATXAResource;
import com.sun.xml.ws.tx.at.common.CoordinatorIF;
import com.sun.xml.ws.tx.at.common.WSATVersion;

import javax.transaction.xa.Xid;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceContext;

/**
 *
 * This is the common implementation for wsat10 and wsat11 Coordinators endpoints.
 */
public class Coordinator<T> implements CoordinatorIF<T> {
    private static final Logger LOGGER = Logger.getLogger(Coordinator.class);

    private WebServiceContext context;
    private WSATVersion<T> version;

    public Coordinator(WebServiceContext m_context, WSATVersion<T> m_version) {
        this.context = m_context;
        this.version = m_version;
    }

    /**
     * Prepared response
     * Get Xid and update status in order to notify.
     * If Xid does not exist this must be a recovery call
     * @param parameters Notification
     */
    public void preparedOperation(T parameters) {
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4509_PREPARED_OPERATION_ENTERED(parameters));
        Xid xidFromWebServiceContextHeaderList = getXid();
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4510_PREPARED_OPERATION(xidFromWebServiceContextHeaderList));
        if (!getWSATHelper().setDurableParticipantStatus(xidFromWebServiceContextHeaderList, WSATXAResource.PREPARED)) {
            //Xid does not exist so must/better be recovery
            replayOperation(parameters);
        }
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4511_PREPARED_OPERATION_EXITED(parameters));
    }

    /**
     * Aborted response
     * Get Xid and update status in order to notify.
     * @param parameters Notification
     */
    public void abortedOperation(T parameters) {
      if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4512_ABORTED_OPERATION_ENTERED(parameters));
        Xid xidFromWebServiceContextHeaderList = getXid();
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4513_ABORTED_OPERATION(xidFromWebServiceContextHeaderList));
        getWSATHelper().setDurableParticipantStatus(xidFromWebServiceContextHeaderList, WSATXAResource.ABORTED);
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4514_ABORTED_OPERATION_EXITED(parameters));
    }

    /**
     * ReadOnly response
     * Get Xid and update status in order to notify.
     * @param parameters Notification
     */
    public void readOnlyOperation(T parameters) {
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4515_READ_ONLY_OPERATION_ENTERED(parameters));
        Xid xidFromWebServiceContextHeaderList = getXid();
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4516_READ_ONLY_OPERATION(xidFromWebServiceContextHeaderList));
        getWSATHelper().setDurableParticipantStatus(xidFromWebServiceContextHeaderList, WSATXAResource.READONLY);
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4517_READ_ONLY_OPERATION_EXITED(parameters));
    }

    /**
     * Committed response
     * Get Xid and update status in order to notify.
     * @param parameters Notification
     */
    public void committedOperation(T parameters) {
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4518_COMMITTED_OPERATION_ENTERED(parameters));
        Xid xidFromWebServiceContextHeaderList = getXid();
        if(isDebugEnabled())  LOGGER.info(LocalizationMessages.WSAT_4519_COMMITTED_OPERATION(xidFromWebServiceContextHeaderList));
        getWSATHelper().setDurableParticipantStatus(xidFromWebServiceContextHeaderList, WSATXAResource.COMMITTED);
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4520_COMMITTED_OPERATION_EXITED(parameters));
    }

    /**
     * WS-AT 1.0 recovery operation
     * Get Xid and issue replay
     * @param parameters  Notification
     */
    public void replayOperation(T parameters) {
        if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4521_REPLAY_OPERATION_ENTERED(parameters));
        Xid xidFromWebServiceContextHeaderList = getXid();
        String wsatTid = getWSATHelper().getWSATTidFromWebServiceContextHeaderList(context);
        if(isDebugEnabled())  LOGGER.info(LocalizationMessages.WSAT_4522_REPLAY_OPERATION(xidFromWebServiceContextHeaderList));
        try {
            getTransactionServices().replayCompletion(
                    wsatTid, createWSATXAResourceForXidFromReplyTo(xidFromWebServiceContextHeaderList));
        } catch (WSATException e) {
            if(isDebugEnabled())
                LOGGER.severe(LocalizationMessages.WSAT_4523_REPLAY_OPERATION_SOAPEXCEPTION(xidFromWebServiceContextHeaderList), e);
            //there is no consequence, recovery reattempt should be (re)issued by subordinate
        }
            if(isDebugEnabled()) LOGGER.info(LocalizationMessages.WSAT_4514_ABORTED_OPERATION_EXITED(parameters));
    }

    /**
     * Return TransactionServicesImpl in order to issue replayCompletion
     * @return TransactionServices
     */
    protected TransactionServices getTransactionServices() {
        return WSATHelper.getTransactionServices();
    }

    /**
     * For recovery/replay create and return WSATResource from reply to
     * @param xid Xid
     * @return WSATXAResource
     */
     WSATXAResource createWSATXAResourceForXidFromReplyTo(Xid xid) {
        MessageHeaders headerList = (MessageHeaders) context.getMessageContext().get(
                        com.sun.xml.ws.developer.JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
        WSEndpointReference wsReplyTo = AddressingUtils.getReplyTo(headerList, AddressingVersion.W3C, SOAPVersion.SOAP_12);
        EndpointReference replyTo = wsReplyTo.toSpec();
        return new WSATXAResource(version.getVersion(), replyTo, xid, true);
    }

    /**
     * Get the Xid in the header from the WebServiceContext
     * @return Xid
     */
    Xid getXid() {
        Xid xid= getWSATHelper().getXidFromWebServiceContextHeaderList(context);
        String bqual = getWSATHelper().getBQualFromWebServiceContextHeaderList(context);
        return new XidImpl(xid.getFormatId(), xid.getGlobalTransactionId(), bqual.getBytes());
    }

    boolean isDebugEnabled() {
        return WSATHelper.isDebugEnabled();
    }

    /**
     * Return the WSATHelper singleton
     * @return WSATHelper for version
     */
    protected WSATHelper getWSATHelper() {
        return version.getWSATHelper();
    }

}
