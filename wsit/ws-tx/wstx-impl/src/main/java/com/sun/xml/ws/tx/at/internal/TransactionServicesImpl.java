/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.internal;

import com.sun.xml.ws.tx.at.WSATConstants;
import com.sun.xml.ws.tx.at.runtime.TransactionServices;
import com.sun.xml.ws.tx.at.WSATException;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.at.common.TransactionImportManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.sun.istack.logging.Logger;

import jakarta.transaction.*;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import jakarta.xml.ws.EndpointReference;

public class TransactionServicesImpl implements TransactionServices {

    private static TransactionServices INSTANCE;
    private static Logger LOGGER = Logger.getLogger(TransactionServicesImpl.class);

    //this is used to track registrations and avoid dupe registration
    static List<Xid> importedXids = new ArrayList<>();


    public static TransactionServices getInstance() {
        if(INSTANCE==null) INSTANCE = new TransactionServicesImpl();
        return INSTANCE;
    }

   private TransactionServicesImpl() {  //todo use MaintenanceTaskExecutor
     ForeignRecoveryContextManager.getInstance().start();
   }

   @Override
   public byte[] getGlobalTransactionId() {
       return new byte[]{'a'};
   }

    /**
     * Remove Xid from importedXids.  This is safe anytime after two-phase completion begins as importedXids only
     *  serves to avoid duplicate registration and it is a protocol error (ie bug)
     *  if registration should take place after 2pc begins.
     * @param xid Xid
     */
   private void removeFromImportedXids(Xid xid) {
       importedXids.remove(xid);
   }

   @Override
   public Xid enlistResource(XAResource resource, Xid xid)
          throws WSATException {
    WSATGatewayRM wsatgw = WSATGatewayRM.getInstance();
    if (wsatgw == null) throw new WSATException("WS-AT gateway not deployed.");
    Transaction transaction = WSATHelper.getInstance().getFromXidToTransactionMap(xid);
    try {
       return wsatgw.registerWSATResource(xid, resource, transaction);
    } catch (IllegalStateException | SystemException | RollbackException e) {
      throw new WSATException(e);
    }
   }

   @Override
   public void registerSynchronization(Synchronization synchronization, Xid xid) {
        debug("regsync");
    }

    //returns null if not infected previously or xid if it has
    @Override
    public Xid importTransaction(int timeout, byte[] tId) {
        final XidImpl xidImpl = new XidImpl(tId);
        if(importedXids.contains(xidImpl)) return xidImpl;
        TransactionImportManager.getInstance().recreate(xidImpl, timeout);
        importedXids.add(xidImpl);
        return null;
    }

    @Override
    public String prepare(byte[] tId) throws WSATException {
        debug("prepare:" + new String(tId));
        final XidImpl xidImpl = new XidImpl(tId);
        removeFromImportedXids(xidImpl);
        ForeignRecoveryContextManager.getInstance().persist(xidImpl);
        int vote;
        try {
            vote = TransactionImportManager.getInstance().getXATerminator().prepare(xidImpl);
        } catch (XAException ex) {
            LOGGER.warning(ex.getMessage() + " errorcode:" + ex.errorCode, ex);
            throw new WSATException(ex.getMessage() + " errorcode:" + ex.errorCode, ex);
        } finally {
            TransactionImportManager.getInstance().release(xidImpl); //prepare does an implicit import/recreate  
        }
        if(vote==XAResource.XA_RDONLY) {
            debug("deleting record due to readonly reply from prepare for txid:" + new String(tId));
            ForeignRecoveryContextManager.getInstance().delete(xidImpl);
        }
        return vote==XAResource.XA_OK?WSATConstants.PREPARED:WSATConstants.READONLY;
    }

    @Override
    public void commit(byte[] tId) throws WSATException {
        debug("commit:" + new String(tId));
        final XidImpl xidImpl = new XidImpl(tId);
        try {
            TransactionImportManager.getInstance().getXATerminator().commit(xidImpl, false);
            ForeignRecoveryContextManager.getInstance().delete(xidImpl);
        } catch (XAException ex) {
            LOGGER.warning(ex.getMessage() + " errorcode:" + ex.errorCode, ex);
            throw new WSATException(ex.getMessage() + " errorcode:" + ex.errorCode, ex);
        } finally {
            TransactionImportManager.getInstance().release(xidImpl);
        }
    }

    @Override
    public void rollback(byte[] tId) throws WSATException {
        debug("rollback:" + new String(tId));
        final XidImpl xidImpl = new XidImpl(tId);
        removeFromImportedXids(xidImpl);
        try {
            TransactionImportManager.getInstance().getXATerminator().rollback(xidImpl);
            ForeignRecoveryContextManager.getInstance().delete(xidImpl);
        } catch (XAException ex) {
            LOGGER.warning(ex.getMessage() + " errorcode:" + ex.errorCode, ex);
            if(ex.errorCode == XAException.XAER_NOTA || ex.errorCode == XAException.XAER_PROTO)
                ForeignRecoveryContextManager.getInstance().delete(xidImpl);
            throw new WSATException(ex.getMessage() + " errorcode:" + ex.errorCode, ex);
        } finally {
            TransactionImportManager.getInstance().release(xidImpl);
        }
    }

    @Override
    public void replayCompletion(String tId, XAResource xaResource) throws WSATException {
        debug("replayCompletion tid:" + tId + " xaResource:" + xaResource);
        final XidImpl xid = new XidImpl(tId.getBytes());
        ForeignRecoveryContext foreignRecoveryContext =
                ForeignRecoveryContextManager.getInstance().getForeignRecoveryContext(xid);
        if (WSATHelper.isDebugEnabled())
            debug("replayCompletion() tid=" + tId + " xid=" + xid + " foreignRecoveryContext=" + foreignRecoveryContext);
        if (foreignRecoveryContext == null) {
            try {
                xaResource.rollback(xid);
                ForeignRecoveryContextManager.getInstance().delete(xid);
            } catch (XAException xae) {
                debug("replayCompletion() tid=" + tId + " (" + xid + "), XAException ("
                        + JTAHelper.xaErrorCodeToString(xae.errorCode, false) + ") rolling back imported transaction: " + xae);
                throw new WSATException("XAException on rollback of subordinate in response to replayCompletion for "
                        + xid + "(tid=" + tId + ")", xae);
            }
        }
        // if the transaction exists, then a completion retry for the resource will
        // occur in due time.  There isn't a mechanism currently to speed
        // up the retry in order to respond immediately.
    }

    @Override
    public EndpointReference getParentReference(Xid xid) {
      debug("getParentReference xid:"+xid);
      if (xid == null) throw new IllegalArgumentException("No subordinate transaction parent reference as xid is null");
      ForeignRecoveryContext foreignRecoveryContext =
              ForeignRecoveryContextManager.getInstance().getForeignRecoveryContext(xid);
      if (foreignRecoveryContext == null)
          throw new AssertionError("No recovery context associated with transaction " + xid);
      return foreignRecoveryContext.getEndpointReference();
    }

    private void debug(String msg){
        LOGGER.log(Level.INFO, msg);
    }

}
