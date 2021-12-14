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

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.internal.WSATGatewayRM;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.tx.at.WSATConstants;

import java.util.logging.Level;
import com.sun.xml.ws.tx.at.WSATHelper;

import com.sun.xml.ws.tx.at.common.TransactionImportManager;
import com.sun.xml.ws.tx.at.common.TransactionManagerImpl;
import com.sun.xml.ws.tx.at.internal.XidImpl;
import com.sun.xml.ws.tx.at.runtime.TransactionIdHelper;
import com.sun.xml.ws.tx.coord.common.WSATCoordinationContextBuilder;
import com.sun.xml.ws.tx.coord.common.WSCBuilderFactory;
import com.sun.xml.ws.tx.coord.common.types.CoordinationContextIF;

import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import javax.transaction.xa.Xid;


public class WSATClientHelper implements WSATClient {
    private volatile int counter = 0;
    private static final Logger LOGGER = Logger.getLogger(WSATClientHelper.class);

    /**
     * For outbound case, if transaction exists, suspend and store it and attach CoordinationContext to SOAP Header
     * For return of outbound case, if suspend transaction exists, resume it
     *
     * @return a list of Header that should be added the request message
     */
    @Override
    public List<Header> doHandleRequest(TransactionalAttribute transactionalAttribute, Map<String, Object> map) {
        try {
            if (TransactionManagerImpl.getInstance().getTransactionManager().getTransaction()==null) 
                return new ArrayList();
        } catch (SystemException e) {
            e.printStackTrace();
            return new ArrayList();
        }
        List<Header> addedHeaders = processTransactionalRequest(transactionalAttribute, map);
        return addedHeaders;
    }

    @Override
    public boolean doHandleResponse(Map<String, Object> map) {
       return resumeAndClearXidTxMap(map);
    }

    @Override
    public void doHandleException(Map<String, Object> map) {
          LOGGER.info(LocalizationMessages.WSAT_4569_INBOUND_APPLICATION_MESSAGE());
       resumeAndClearXidTxMap(map);
    }

    private boolean resumeAndClearXidTxMap(Map<String, Object> map) {
        if (WSATHelper.isDebugEnabled()) 
            LOGGER.info(LocalizationMessages.WSAT_4569_INBOUND_APPLICATION_MESSAGE());
        Xid xid = getWSATXidFromMap(map);
        if (xid != null) {
            WSATHelper.getInstance().removeFromXidToTransactionMap(xid);
        }
        Transaction transaction = getWSATTransactionFromMap(map);
        return !(transaction != null && !resume(transaction));
    }

   private Transaction getWSATTransactionFromMap(Map map) {
      Transaction transaction = (Transaction)map.get(WSATConstants.WSAT_TRANSACTION);
      return transaction;
   }

   private Xid getWSATXidFromMap(Map map) {
      Xid xid = (Xid)map.get(WSATConstants.WSAT_TRANSACTION_XID);
      return xid;
   }

   /**
     * Resume the transaction previous suspended by this handler.
     *
     * @return boolean if resume was successful
     */
   private boolean resume(Transaction transaction) {
       if (WSATHelper.isDebugEnabled())
           LOGGER.info(LocalizationMessages.WSAT_4570_WILL_RESUME_IN_CLIENT_SIDE_HANDLER(transaction, Thread.currentThread()));
       try {
           TransactionManagerImpl.getInstance().getTransactionManager().resume(transaction);
           if (WSATHelper.isDebugEnabled())
               LOGGER.info(LocalizationMessages.WSAT_4571_RESUMED_IN_CLIENT_SIDE_HANDLER(transaction, Thread.currentThread()));
           return true;
       } catch (InvalidTransactionException e) {
           if (WSATHelper.isDebugEnabled())
               LOGGER.severe(LocalizationMessages.WSAT_4572_INVALID_TRANSACTION_EXCEPTION_IN_CLIENT_SIDE_HANDLER(
                       transaction, Thread.currentThread()), e);
           try {
               transaction.setRollbackOnly();
           } catch (IllegalStateException | SystemException ex) {
               Logger.getLogger(WSATClientHelper.class).log(Level.SEVERE, null, ex);
               return false;
           }
           return false;
       } catch (SystemException e) {
           if (WSATHelper.isDebugEnabled())
               LOGGER.severe(LocalizationMessages.WSAT_4573_SYSTEM_EXCEPTION_IN_CLIENT_SIDE_HANDLER(
                       transaction, Thread.currentThread()), e);
           try {
               transaction.setRollbackOnly();
               return false;
           } catch (IllegalStateException | SystemException ex) {
               Logger.getLogger(WSATClientHelper.class).log(Level.SEVERE, null, ex);
               return false;
           }

       }
   }

    /**
     * Process ClientRequestInfo received for this outgoing request.
     * Determine if this transaction interceptor is interested in the operation.
     * Get the current transaction.  One should exist as we checked for it's presence before calling this method
     * If no transaction exists do nothing.
     * If one does exist create and add the transaction CoordinationContext.
     *
     * @return false if there are any issues with suspend or message header creation (namely SOAPException),
     *         true otherwise
     */
    private List<Header> processTransactionalRequest(
            TransactionalAttribute transactionalAttribute, Map<String, Object> map) {
        while (!WSATGatewayRM.isReadyForRuntime) {
            LOGGER.info("WS-AT recovery is enabled but WS-AT is not ready for runtime.  Processing WS-AT recovery log files...");
            WSATGatewayRM.getInstance().recover();
        }
        List<Header> headers = new ArrayList<>();
        String txId;
        byte[] activityId = WSATHelper.assignUUID().getBytes();
        LOGGER.info("WS-AT activityId:" + activityId);
        Xid xid = new XidImpl(1234, new String(System.currentTimeMillis() + "-" + counter++).getBytes(), new byte[]{});
        txId = TransactionIdHelper.getInstance().xid2wsatid(xid);
        long ttl = 0;
        ttl = TransactionImportManager.getInstance().getTransactionRemainingTimeout();
        if (WSATHelper.isDebugEnabled())
            LOGGER.info(LocalizationMessages.WSAT_4575_WSAT_INFO_IN_CLIENT_SIDE_HANDLER(
                    txId, ttl, "suspendedTransaction", Thread.currentThread()));
        if (WSATHelper.isDebugEnabled())
            LOGGER.info(LocalizationMessages.WSAT_4575_WSAT_INFO_IN_CLIENT_SIDE_HANDLER(
                    txId, ttl, "suspendedTransaction", Thread.currentThread()));
        WSCBuilderFactory builderFactory =
                WSCBuilderFactory.newInstance(transactionalAttribute.getVersion());
        WSATCoordinationContextBuilder builder  =
                builderFactory.newWSATCoordinationContextBuilder();
        CoordinationContextIF cc =
                builder.txId(txId).expires(ttl).soapVersion(transactionalAttribute.getSoapVersion()).mustUnderstand(true)
                        .build();
        Header coordinationHeader =
                Headers.create(cc.getJAXBRIContext(),cc.getDelegate());
        headers.add(coordinationHeader);
       if (WSATHelper.isDebugEnabled())
           LOGGER.info(LocalizationMessages.WSAT_4568_OUTBOUND_APPLICATION_MESSAGE_TRANSACTION_AFTER_ADDING_CONTEXT(
                                                                                                    "suspendedTransaction"));
        Transaction suspendedTransaction = suspend(map); //note suspension moved after context creation
        map.put(WSATConstants.WSAT_TRANSACTION_XID, xid);
        WSATHelper.getInstance().putToXidToTransactionMap(xid, suspendedTransaction);
        return headers;
    }

    /**
     * Suspend the transaction on the current thread which will be resumed upon return
     *
     * @return Transaction not null if suspend is successful
     */
    private Transaction suspend(Map<String, Object> map) {
        Transaction suspendedTransaction = null;
       try {
           suspendedTransaction = TransactionManagerImpl.getInstance().getTransactionManager().suspend();
           if (WSATHelper.isDebugEnabled())
               LOGGER.info(LocalizationMessages.WSAT_4577_ABOUT_TO_SUSPEND_IN_CLIENT_SIDE_HANDLER(
                   suspendedTransaction, Thread.currentThread()));
           map.put(WSATConstants.WSAT_TRANSACTION, suspendedTransaction);
           if (WSATHelper.isDebugEnabled())
               LOGGER.info(LocalizationMessages.WSAT_4578_SUSPENDED_IN_CLIENT_SIDE_HANDLER(
                   suspendedTransaction, Thread.currentThread()));
         } catch (SystemException e) {
            //tx should always be null here as suspend would either work or not
            return null;
         }
         return suspendedTransaction;
    }

}
