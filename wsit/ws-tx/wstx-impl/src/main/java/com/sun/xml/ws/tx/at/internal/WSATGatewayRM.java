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

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.localization.LocalizationMessages;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.at.WSATXAResource;
import com.sun.xml.ws.tx.at.common.TransactionImportManager;
import com.sun.xml.ws.tx.dev.WSATRuntimeConfig;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

import jakarta.transaction.*;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import jakarta.xml.ws.WebServiceException;


/**
 * Gateway XAResource for managing outbound WS-AT transaction branches.
 */
public class WSATGatewayRM implements XAResource, WSATRuntimeConfig.RecoveryEventListener {
  private static final Logger LOGGER = Logger.getLogger(WSATGatewayRM.class);
  private static final String WSAT = "wsat";
  private static final String OUTBOUND = "outbound";
  private static final String INBOUND = "inbound";

  private static WSATGatewayRM singleton;
  static private String resourceRegistrationName; // JTA resource registration name
  static private Map<Xid, BranchRecord> branches; // xid to Branch
  static List<Xid> pendingXids; // collection of Xids
  private final Object currentXidLock = new Object();
  private Xid currentXid;
  static boolean isReadyForRecovery = false;
  public static boolean isReadyForRuntime = false;
  public static  String txlogdir;
  static String txlogdirInbound;
  private static String txlogdirOutbound;
    static boolean isStoreInit = false;
  private volatile int counter = 0;
  private Map<Xid,Xid> activityXidToInternalXidMap = new HashMap<>();
  private Map<Xid,Xid> internalXidToActivityXidMap = new HashMap<>();

    // package access for test instantiation only, this is a singleton
  WSATGatewayRM(String serverName) {
    resourceRegistrationName = "RM_NAME_PREFIX" + serverName;
    branches = Collections.synchronizedMap(new HashMap<>());
    pendingXids = Collections.synchronizedList(new ArrayList<>());
    singleton = this;
  }

  /**
   * called by transaction services for enlistment and used by HA delegation
   */
  public static synchronized WSATGatewayRM getInstance() {
    if(singleton==null) {
        create("server");
    }
    return singleton;
  }

  /**
   * Called during tube/web service init
   */
  public static synchronized WSATGatewayRM create() {
    return create("server");    
  }

    /**
     * Called as part of WSATTransactionService start
     * @param serverName this server's name
     * @return the WSATGatewayRM singleton that WSATTransactionService will call stop on during stop/shutdown
     */
  private static synchronized WSATGatewayRM create(String serverName)
  {
    if (singleton == null) {
        new WSATGatewayRM(serverName);
        isReadyForRecovery = setupRecovery();
    }
    return singleton;
  }

    private static boolean setupRecovery() {
        if (!WSATRuntimeConfig.getInstance().isWSATRecoveryEnabled()) return true;
        TransactionImportManager.registerRecoveryResourceHandler(singleton);
        WSATRuntimeConfig.getInstance().setWSATRecoveryEventListener(singleton);
        setTxLogDirs();
        try {
            initStore();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Called for create of WSATGatewayRM
     */
   static void initStore() throws Exception {
        if (isStoreInit) return;
        if (WSATHelper.isDebugEnabled()) debug("WSATGatewayRM.initStore path:"+txlogdirInbound);
        createFile(txlogdirInbound, true);
        if (WSATHelper.isDebugEnabled()) debug("WSATGatewayRM.initStore path:"+txlogdirOutbound);
        createFile(txlogdirOutbound, true);
        isStoreInit = true;
    }


    static private File createFile(String logFilePath, boolean isDir) throws Exception {
        File file = new File(logFilePath);
        if (!file.exists()) {
            if (isDir && !file.mkdirs()) {
                throw new Exception("Could not create directory : " + file.getAbsolutePath());
            } else if (!isDir) {
                try {
                    file.createNewFile();
                } catch (IOException ioe) {
                    Exception storeEx = new Exception("Could not create file : " + file.getAbsolutePath());
                    storeEx.initCause(ioe);
                    throw storeEx;
                }
            }
        }
        return file;
    }

    /**
     * Called for XAResource.recover
     */
   void recoverPendingBranches(String outboundRecoveryDir, String inboundRecoveryDir) {
    if (WSATHelper.isDebugEnabled()) debug("recoverPendingBranches outbound directory:"+outboundRecoveryDir);
    FileInputStream fis;
    ObjectInputStream in;
      File[] files = new File(outboundRecoveryDir).listFiles();
      if(files!=null) for (int i=0;i<files.length;i++) {
       try {
        fis = new FileInputStream(files[i]);
        in = new ObjectInputStream(fis);
        BranchRecord branch = (BranchRecord) in.readObject();
        branch.setTxLogLocation(files[i].getCanonicalPath());
        branches.put(branch.getXid(), branch);
        pendingXids.addAll(branch.getAllXids());
        in.close();
       } catch (Throwable e) {
            throw new WebServiceException("Failure while recovering WS-AT transaction logs outbound file:"+files[i], e);
       }
      }
    if (WSATHelper.isDebugEnabled()) debug("recoverPendingBranches inbound directory:"+inboundRecoveryDir);
     fis = null;
     in = null;
     files = new File(inboundRecoveryDir).listFiles();
     if(files!=null) for (int i=0;i<files.length;i++) {
       try {
        fis = new FileInputStream(files[i]);
        in = new ObjectInputStream(fis);
        ForeignRecoveryContext frc = (ForeignRecoveryContext) in.readObject();
        frc.setTxLogLocation(files[i].getCanonicalPath());
        frc.setRecovered();
        ForeignRecoveryContextManager.getInstance().add(frc);
        in.close();
       } catch (Throwable e) {
            throw new WebServiceException("Failure while recovering WS-AT transaction logs inbound file:"+files[i], e);
       }
      }
  }

  /**
   * Enlist a foreign WS-AT resource in the current transaction. It is
   * assumed that the XAResource parameter wraps a WS-AT endpoint. Invoked in
   * the outbound case.
   *
   * @param xid The current, superior transaction id.
   * @param wsatResource The foreign WS-AT resource.
   * @throws SystemException from enlistResource
   * @throws RollbackException from enlistResource
   * @throws IllegalStateException from enlistResource
   * @return Xid xid
   */
  public Xid registerWSATResource(Xid xid, XAResource wsatResource, Transaction tx)
      throws IllegalStateException, RollbackException, SystemException {
    // enlist each WSAT resource, specifically each endpoint, as a separate branch alias
    if (tx == null)
        throw new IllegalStateException("Transaction " + tx + " does not exist, wsatResource=" + wsatResource);
    Xid xidFromActivityMap = activityXidToInternalXidMap.get(xid);
    BranchRecord branch;
    if(xidFromActivityMap!=null) {
        branch = getBranch(xidFromActivityMap);
        WSATXAResource resource = (WSATXAResource) branch.exists(wsatResource);
        if (resource!=null) return resource.getXid();
    }
    // enlist primary, read-only branch (ensures 2PC)
    tx.enlistResource(new WSATNoOpXAResource()); 
    synchronized(currentXidLock) {
      tx.enlistResource(new WSATGatewayRMPeerRecoveryDelegate());
      // this is again due to changing xid in GF
      ((WSATXAResource)wsatResource).setXid(currentXid);
      branch = getBranch(currentXid);
      branch.addSubordinate(currentXid, ((WSATXAResource)wsatResource));
      activityXidToInternalXidMap.put(xid, currentXid);
      internalXidToActivityXidMap.put(currentXid, xid);
      if (WSATHelper.isDebugEnabled())
        debug("registerWSATResource() xid=" + currentXid);
    }
    return currentXid;
  }

    /**
     * Implementation of Subordinate/ServerXAResource called in reaction to registerWSATResource enlistResource call
     * This should be the only use/patch of this method
     * NOTE: lock on currentBQual must be obtained before calling this method as it is in
     * @param xid Xid
     * @param flags flags
     * @throws XAException xaException
     */
  @Override
  public void start(Xid xid, int flags) throws XAException {
    currentXid = xid;
    debug("start currentXid:"+currentXid+" xid:"+xid);
    if (WSATHelper.isDebugEnabled())
        debug("start() xid=" + xid + ", flags=" + flags);
    switch (flags) {
    case XAResource.TMNOFLAGS:
      getOrCreateBranch(xid);
      break;
    case XAResource.TMRESUME:
    case XAResource.TMJOIN:
      BranchRecord branch = getBranch(xid);
      if (branch == null) {
        JTAHelper.throwXAException(XAException.XAER_NOTA, "Attempt to resume xid "
            + xid + " that is not in SUSPENDED state.");
      }
      break;
    case XAResource.TMFAIL:
      // should initiate branch rollback, but RMERR will cause a rollback retry
      JTAHelper.throwXAException(XAException.XAER_RMERR,
          "error while attempting to rollback branch" + resourceRegistrationName);
      break;
    default:
      throw new IllegalArgumentException("invalid flag:" + flags);
    }
  }

  @Override
  public void end(Xid xid, int flags) throws XAException {
    if (WSATHelper.isDebugEnabled())
        debug("end() xid=" + xid + ", flags=" + flags);
    BranchRecord branch = getBranch(xid);
    if (branch == null) {
      JTAHelper.throwXAException(XAException.XAER_NOTA,
          "end: no branch info for " + xid);
    }
  }

  @Override
  public int prepare(Xid xid) throws XAException {
    if (WSATHelper.isDebugEnabled()) debug("prepare() xid=" + xid);
    purgeActivityAndInternalXidMapEntries(xid);
    BranchRecord branch = getBranch(xid);
    if (WSATHelper.isDebugEnabled()) debug("prepare() xid=" + xid+" branch="+branch);
    if (branch == null) {
      JTAHelper.throwXAException(XAException.XAER_NOTA, "prepare: no branch info for " + xid);
    }
    if (WSATHelper.isDebugEnabled()) debug("prepare() xid=" + xid);
    persistBranchIfNecessary(branch);
    int vote;
    try {
        vote = branch.prepare(xid);
    } catch (XAException xae) {
        deleteBranchIfNecessary(branch);
        throw xae;
    }
    if (vote == XAResource.XA_RDONLY) deleteBranchIfNecessary(branch);
    return vote;
  }

    private void purgeActivityAndInternalXidMapEntries(Xid xid) {
        Xid activityXid = internalXidToActivityXidMap.remove(xid);
        if (activityXid!=null) activityXidToInternalXidMap.remove(activityXid);
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
    if (WSATHelper.isDebugEnabled()) debug("commit() xid=" + xid);
    BranchRecord branch = getBranch(xid);
    if (branch == null) {
      JTAHelper.throwXAException(XAException.XAER_NOTA, "commit: no branch information for xid:" + xid);
    }
    try {
      branch.commit(xid, onePhase);
    } finally {
      deleteBranchIfNecessary(branch);
    }
  }

  @Override
  public void rollback(Xid xid) throws XAException {
    if (WSATHelper.isDebugEnabled()) debug("rollback() xid=" + xid);
    purgeActivityAndInternalXidMapEntries(xid);
    BranchRecord branch = getBranch(xid);
    if (branch == null) {
      JTAHelper.throwXAException(XAException.XAER_NOTA,
          "rollback: no branch info for " + xid);
    }
    try {
      branch.rollback(xid);
    } finally {
      deleteBranchIfNecessary(branch);
    }
  }

    /**
     * Used for lazy/automatic-recovery="false"
     */
    public void recover() {
        try {
            recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);
        } catch (XAException e) {
            e.printStackTrace();  
        }
    }

    /**
     * Call for local recover/server specified by null instance value
     *
     * @return Xid[] indoubt Xids
     */
    @Override
    public Xid[] recover(int flag) throws XAException {
        return recover(flag, null);
    }

    /**
     * Call for delegated recovery specified by non-null instance
     *
     * @return Xid[] indoubt Xids
     */
    public Xid[] recover(int flag, String instance) throws XAException {
        if (WSATHelper.isDebugEnabled()) debug("recover() flag=" + flag);
        if(!isReadyForRecovery) throw new XAException("recover call on WS-AT gateway failed due to failed initialization");
        boolean isDelegated = instance != null;
        if (isDelegated) {
      String delegatedtxlogdir = WSATGatewayRM.txlogdir + File.separator + ".." + File.separator + ".." +
                    File.separator + instance + File.separator + WSAT + File.separator;
       debug("delegatedtxlogdir in recover is" + delegatedtxlogdir);           
            String delegatedtxlogdirOutbound = delegatedtxlogdir + OUTBOUND + File.separator;
            String delegatedtxlogdirInbound = delegatedtxlogdir + INBOUND + File.separator;
            if (WSATHelper.isDebugEnabled()) debug("recover() for delegate flag=" + flag +
                    " delegatedtxlogdirOutbound:" + delegatedtxlogdirOutbound +
                    ", delegatedtxlogdirInbound:" + delegatedtxlogdirInbound);
            singleton.recoverPendingBranches(delegatedtxlogdirOutbound, delegatedtxlogdirInbound);
        } else if(!isReadyForRuntime){
            try {
                initStore();
            } catch (Exception e) {
                XAException xaEx = new XAException("WSATGatewayRM recover call failed due to StoreException:" + e);
                xaEx.errorCode = XAException.XAER_RMFAIL;
                xaEx.initCause(e);
                throw xaEx;
            }
            if (WSATHelper.isDebugEnabled()) debug("recover() for this server flag=" + flag +
                    " txlogdirOutbound:" + txlogdirOutbound +
                    ",txlogdirInbound:" + txlogdirInbound);
            singleton.recoverPendingBranches(txlogdirOutbound, txlogdirInbound);
            isReadyForRuntime = true;
        }
        // return all pending Xids on first call, empty array otherwise
        if ((flag & XAResource.TMSTARTRSCAN) != 0) {
            if (WSATHelper.isDebugEnabled()) debug("WSAT recover(" + flag + ") returning " + pendingXids);
            Xid[] xids = pendingXids.toArray(new Xid[0]);
            return xids;
        }
        if (WSATHelper.isDebugEnabled()) debug("recover() returning empty array");
        return new Xid[0];
    }

    static void setTxLogDirs() {
        txlogdir = getTxLogDir();
         debug("txlogdir is" + txlogdir);
         String wstxlogdir = txlogdir;
        File f = new File(txlogdir);
        wstxlogdir = f.getParent(); 
       debug("wstxlogdir is" + wstxlogdir);
        txlogdirInbound =
                wstxlogdir + File.separator + WSAT + File.separator + INBOUND + File.separator;
        txlogdirOutbound =
                wstxlogdir + File.separator + WSAT + File.separator + OUTBOUND + File.separator;
    }

    static String getTxLogDir() {
     //   return TransactionImportManager.getInstance().getTxLogLocation();
        return WSATRuntimeConfig.getInstance().getTxLogLocation();
    }

    @Override
    public void forget(Xid xid) throws XAException {
    if (WSATHelper.isDebugEnabled()) debug("forget() xid=" + xid);
    BranchRecord branch = getBranch(xid);
    if (branch == null) JTAHelper.throwXAException(XAException.XAER_NOTA, "forget: no branch info for " + xid);
    deleteBranchIfNecessary(branch);
  }

    /**
     * Not applicable
     * @return int -1 as not applicable
     */
  @Override
  public int getTransactionTimeout() {
    return -1;
  }

    /**
     * Not applicable
     * @param seconds int
     * @return boolean always false as not applicable
     */
  @Override
  public boolean setTransactionTimeout(int seconds) {
    return false;
  }

    /**
     * There is only one WSATGatewayRM per server for active transactions and isSameRM should not be called for
     *  any migrated WSATGatewayRM instances
     * @param xares XAResource
     * @return boolean if is same RM which in this WSATGatewayRM case means means the same instance
     */
  @Override
  public boolean isSameRM(XAResource xares) {
    if (!(xares instanceof WSATGatewayRM)) return false;
    WSATGatewayRM oxares = (WSATGatewayRM) xares;
    return this.equals(oxares);
  }


    /**
     * Return true as WSATGatewayRM is always available, health should not change.
     * @return boolean
     */
  public boolean detectedUnavailable() {
    return true;
  }

    /**
     * Always returns TMSUCCESS, avoids unnecessary suspend
     * @return int TMSUCCESS delist flag
     */
  public int getDelistFlag() {
    return TMSUCCESS;
  }

    /**
     * Called from registerWSATResource to gate enlist and again from start with Xid with branchqual
     * therefore it is important that branchqual is not in equality check as we'll have two branchrecords
     * @param xid Xid
     * @return  BranchRecord branchRecord
     */
  private synchronized BranchRecord getOrCreateBranch(Xid xid) {
    BranchRecord branch = getBranch(xid);
    if (branch == null) {
      branch = new BranchRecord(xid);
      branches.put(xid, branch);
    }
    return branch;
  }

  private synchronized BranchRecord getBranch(Xid xid) {
    BranchRecord branch = branches.get(xid);
    if (branch != null && xid.getBranchQualifier() != null) branch.assignBranchXid(xid);
    return branch;
  }

  private void delete(BranchRecord branch)  {
    releaseBranchRecord(branch);
    branch.cleanup();
  }

    /**
     * Called after prepare in order to persist branch record.
     * @param branch BranchRecord
     * @throws IOException from log write
     */
  private void persistBranchRecord(BranchRecord branch) throws IOException {
    if(!WSATRuntimeConfig.getInstance().isWSATRecoveryEnabled()) return;
    if (WSATHelper.isDebugEnabled()) debug("persist branch record " + branch);
    FileOutputStream fos;
    ObjectOutputStream out;
    String logLocation = txlogdirOutbound + File.separator + System.currentTimeMillis() + "-" + counter++;
    branch.setTxLogLocation(logLocation);
    fos = new FileOutputStream(logLocation);
    out = new ObjectOutputStream(fos);
    out.writeObject(branch);
    out.close();
    fos.flush();
    branch.setLogged(true);
  }

    /**
     * Called after rollback, commit, and forget in order to delete branch record.
     * @param branch BranchRecord
     */
  private void releaseBranchRecord(BranchRecord branch) {
    String logLocation = branch.getTxLogLocation();
    if (WSATHelper.isDebugEnabled()) debug("release branch record:" + branch + " logLocation:" + logLocation);
    new File(logLocation).delete();
    branch.setLogged(false);
  }

  void persistBranchIfNecessary(BranchRecord branch) throws XAException {
    try {
      synchronized(branch) {
        if (!branch.isLogged()) {
          persistBranchRecord(branch);
          pendingXids.addAll(branch.getAllXids());
        }
      }
    } catch(IOException pse) {
      debug("error persisting branch " + branch + ": " + pse);
      LOGGER.severe(LocalizationMessages.WSAT_4500_ERROR_PERSISTING_BRANCH_RECORD(branch.toString()), pse);
      JTAHelper.throwXAException(XAException.XAER_RMERR, "Error persisting branch " + branch, pse);
    }
  }

  private void deleteBranchIfNecessary(BranchRecord branch) throws XAException {
    boolean deleted = false;
    try {
      synchronized (branch) {
        branches.remove(branch.getXid());
        pendingXids.removeAll(branch.getAllXids());
        if (branch.isLogged()) {
          delete(branch);
          deleted = true;
        }
      }
    } catch(Exception pse) {
      debug("error deleting branch record " + branch + ": " + pse);
      LOGGER.severe(LocalizationMessages.WSAT_4501_ERROR_DELETING_BRANCH_RECORD(branch.toString()), pse);
      JTAHelper.throwXAException(XAException.XAER_RMERR, "Error deleting branch record " + branch, pse);
    }
  }

    //RecoveryListener implementation
    @Override
    public void beforeRecovery(boolean delegated, String instance) {
        debug("afterRecovery called, delegated:" + delegated + " instance:" + instance);
        if (!delegated) {
            return;
        }
        TransactionImportManager.registerRecoveryResourceHandler(
                new WSATGatewayRMPeerRecoveryDelegate(instance));
    }

    @Override
    public void afterRecovery(boolean success, boolean delegated, String instance) {
        debug("afterRecovery called, success:" + success + " delegated:" + delegated + " instance:" + instance);
    }

  private static void debug(String msg) {
    if (WSATHelper.isDebugEnabled()) {
        Logger.getLogger(WSATGatewayRM.class).log(Level.INFO, msg);
    }
  }


  private static final class BranchObjectHandler {
    private static final int VERSION = 1;

    public Object readObject(ObjectInput in) throws ClassNotFoundException, IOException {
      int version = in.readInt();
      if (version != VERSION)
          throw new IOException("Stream corrupted.  Invalid WS-AT gateway branch version: " + version);
      BranchRecord branch = new BranchRecord();
      branch.readExternal(in);
      if (WSATHelper.isDebugEnabled()) debug("read WS-AT branch " + branch);
      return branch;
    }

    public void writeObject(ObjectOutput out, Object o) throws IOException {
      if (!(o instanceof BranchRecord))
          throw new IOException("Cannot serialize class of type: " + (o == null ? null : o.getClass().toString()) );
      out.writeInt(VERSION);
      BranchRecord branch = (BranchRecord) o;
      branch.writeExternal(out);
      if (WSATHelper.isDebugEnabled()) debug("serialized WS-AT branch " + branch);
    }
  }
}

