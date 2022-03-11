/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.internal;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.tx.at.WSATXAResource;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * Encapsulates remote WS-AT participants as a branch for this superior
 * transaction.
 */
public class BranchRecord implements Externalizable {
  private static final long serialVersionUID = -8663994789749988958L;
  private static final int VERSION = 1;

  private Xid globalXid;
  private Map<Xid, RegisteredResource> registeredResources;
  private String branchAliasSuffix = "BI_WSATGatewayRM"; //this should be different from the WSATGateway resource name prefix, and should keep it short.
  private boolean logged;
  private String txLogLocation;
  private static final Logger LOGGER = Logger.getLogger(BranchRecord.class);

    /**
   * Used for recovery (created by readObject) and Externalizable no-arg constructor
   */
  public BranchRecord() {
    this.registeredResources = new HashMap<>();
  }

  BranchRecord(Xid xid) {
    this.globalXid = xid;
    this.registeredResources = new HashMap<>();
  }

  /**
   * Adds the specified WS-AT resource to the gateway branch record.
   * @param xid Xid used for key
   * @param wsatResource WSATXAResource
   * @return RegisteredResource that contains (WSAT)XAResource provided
   */
  public synchronized RegisteredResource addSubordinate(Xid xid, WSATXAResource wsatResource) {
    debug("addSubordindate xid:"+xid+" wsatResource:"+wsatResource);
    RegisteredResource rr = new RegisteredResource(wsatResource);
    registeredResources.put(xid, rr);
    return rr;
  }

  /**
   * Returns the transaction branch name of the specified WS-AT resource in order to enlist the resource.
   * Each call will return a different value for the same wsatResource and
   *  so it should only be called once for each resource.
   * @param wsatResource WSATXAResource
   * @return String representing
   */
  synchronized String getBranchName(XAResource wsatResource) {
    int index = getResourceIndex(wsatResource);
    if (index == -1) {
      throw new IllegalStateException(
          "WS-AT resource not associated with transaction branch " + globalXid);
    }
    return index + branchAliasSuffix;
  }


    /**
     *  If persist of record was successfully logged
     * @param b boolean
     */
  void setLogged(boolean b) {
    this.logged = b;
  }

    /**
     * If persist of record was successfully logged
     * @return boolean
     */
  boolean isLogged() {
    return logged;
  }

  int prepare(Xid xid) throws XAException {
/*
    if (isPrimaryBranch(xid)) {
      // primary branch always returns RDONLY
      debug("prepare() xid=" + xid + " returning XA_RDONLY");
      return XAResource.XA_RDONLY;
    }
*/
    RegisteredResource rr = getRegisteredResource(xid);
    int vote = XAResource.XA_OK;
    try {
      vote = rr.prepare(xid);
    } catch (XAException xae) {
      switch(xae.errorCode) {
      case XAException.XA_RBROLLBACK:
        // throw rollback,  TM will call rollback
        throw xae;
//      case XAException.XA_HEURMIX:
//      case XAException.XA_HEURHAZ:
//        heuristic = true;
//        break;
      case XAException.XAER_NOTA:
        // possible timeout of subordinate, initiate rollback
        JTAHelper.throwXAException(XAException.XA_RBTIMEOUT, "Subordinate resource timeout.", xae);
        break;
      default:
        throw xae;
      }
    }

    return vote;
  }

  void rollback(Xid xid) throws XAException {
    if (isPrimaryBranch(xid)) {
      debug("rollback() xid=" + xid + " ignoring primary branch ");
    }
    RegisteredResource rr = getRegisteredResource(xid);
    try {
      rr.rollback(xid);
    } catch (XAException e) {
      switch (e.errorCode) {
      case XAException.XA_HEURMIX:
      case XAException.XA_HEURHAZ:
      case XAException.XA_HEURCOM:
        throw e;
      case XAException.XAER_NOTA:
        // ignore, assume completion
        break;
      default:
        throw e;
      }
    }
  }

  void commit(Xid xid, boolean onePhase) throws XAException {
    if (isPrimaryBranch(xid)) {
      debug("commit() xid=" + xid + " ignoring primary branch");
        //This happens in superior during migration recovery and so should not do a
        // JTAHelper.throwXAException(XAException.XAER_NOTA, "Primary branch RDONLY");
    }
    RegisteredResource rr = getRegisteredResource(xid);
    try {
      rr.commit(xid, onePhase);
    } catch (XAException e) {
      switch (e.errorCode) {
      case XAException.XA_HEURMIX:
      case XAException.XA_HEURHAZ:
      case XAException.XA_HEURRB:
        throw e;
      case XAException.XAER_NOTA:
        // ignore, assume completion
        break;
      default:
        throw e;
      }
    }
  }

  /**
   * Returns the branch index of the specified WS-AT resource
   *
   * @param wsatResource WSATXAResource
   * @return the branch index, or -1 if DNE.
   */
  private synchronized int getResourceIndex(XAResource wsatResource) {
    for (int i=0; i<registeredResources.size(); i++) {
      RegisteredResource rr = registeredResources.get(i);
      if (wsatResource.equals(rr.resource)) return i;
    }
    return -1;
  }

  XAResource exists(XAResource wsatResource) {
      int resourceIndex = getResourceIndex(wsatResource);
      return resourceIndex==-1?null:registeredResources.get(resourceIndex).resource;
  }

  private boolean isPrimaryBranch(Xid xid) {
    return Arrays.equals(xid.getBranchQualifier(), globalXid.getBranchQualifier());
  }

  /**
   * Returns the WS-AT resource branch index based from the value embedded in
   * the branch qualifier
   *
   * @param xid Xid of resource
   * @return the branch index, or -1 if bqual does not represent a WS-AT
   *         resource
   */
  private int getResourceIndex(Xid xid) {
    String bqual = new String(xid.getBranchQualifier());
    int endPos = bqual.indexOf(branchAliasSuffix);
    if (endPos == -1) return -1;
    String s = bqual.substring(0, endPos);
    return Integer.parseInt(s);
  }

  /**
   * Returns the WS-AT resource for the specified branch qualifier. Will never
   * return null.
   *
   * @throws XAException
   *           No branch information exists for the specified Xid branch
   *           qualifier.
   */
  private synchronized RegisteredResource getRegisteredResource(Xid xid) throws XAException {
    RegisteredResource registeredResource = registeredResources.get(xid);
    if (registeredResource == null) {
      JTAHelper.throwXAException(XAException.XAER_NOTA, "Xid=" + xid);
    }
    if (registeredResource.getBranchXid() != null) {
      boolean isRegisteredBranch =
              Arrays.equals(registeredResource.getBranchXid().getBranchQualifier(), xid.getBranchQualifier());
      if (!isRegisteredBranch) {
        if (WSATHelper.isDebugEnabled()) {
          byte[] branchQualifier = registeredResource.getBranchXid().getBranchQualifier();
          if (branchQualifier == null) branchQualifier = new byte[0];
          debug("WS-AT Branch registered branchId:\t[" + new String(branchQualifier) + "] ");
          branchQualifier = xid.getBranchQualifier();
          if (branchQualifier == null) branchQualifier = new byte[0];
          debug("WS-AT Branch branchId used to identify a registered resource, branchQual:" + new String(branchQualifier));
        }
        debug("prepare() xid=" + xid + " returning XA_RDONLY");
        JTAHelper.throwXAException(XAException.XAER_NOTA, "xid=" + xid);
      }
    }
    return registeredResource;
  }

  Xid getXid() {
    return globalXid;
  }

  synchronized Collection<Xid> getAllXids() {
    Collection<Xid> xids = new ArrayList<>();
    Iterator<RegisteredResource> resourceIterator = registeredResources.values().iterator();
    while(resourceIterator.hasNext()) {
      xids.add(resourceIterator.next().getBranchXid());
    }
    return xids;
  }

  // ensure that each registered resource has an associated Xid w/ bqual
  void assignBranchXid(Xid xid) {
    int index = getResourceIndex(xid);
    if (index == -1) return;
    RegisteredResource rr = registeredResources.get(index);
    if (rr == null) return;
    if (rr.getBranchXid() == null) rr.setBranchXid(xid);
  }

  public void cleanup() {
    // perform any necessary branch record cleanup, release resource, etc.
    // on WS-AT participant XAResource
  }

  synchronized boolean allResourcesCompleted() {
    for (int i=0, num=registeredResources.size(); i<num; i++) {
      RegisteredResource rr = registeredResources.get(i);
      if (!rr.isCompleted()) return false;
    }
    return true;
  }

    public void setTxLogLocation(String logLocation) {
        txLogLocation = logLocation;
    }

    public String getTxLogLocation() {
        return txLogLocation;
    }

  private void debug(String msg) {
    LOGGER.info(msg);
  }

    static class RegisteredResource implements Externalizable {
  	private static final long serialVersionUID = 601688150453719976L;
    private static final int STATE_ACTIVE = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_READONLY = 3;
    private static final int STATE_COMPLETED = 4;

    private WSATXAResource resource;
    private int vote=-1;
    private int state;
    private BranchXidImpl branchXid;

   /**
    * For Externalizable
    */
   public RegisteredResource() {
    }

    RegisteredResource(WSATXAResource wsatResource) {
      this.resource = wsatResource;
      this.state = STATE_ACTIVE;
    }

    private Xid getBranchXid() {
      if (branchXid == null) return null;
      return branchXid.getDelegate();
    }

    private void setBranchXid(Xid xid) {
        branchXid = new BranchXidImpl(new XidImpl(xid));
    }

   /**
    * Called during readExternal to recreate suborindate from log
    */
    private void setPrepared() {
      this.state = STATE_PREPARED;
    }

    XAResource getResource() {
      return resource;
    }

    boolean isCompleted() {
      return state == STATE_COMPLETED || state == STATE_READONLY;
    }

    int prepare(Xid xid) throws XAException {

      switch (state) {
      case STATE_PREPARED:
      case STATE_READONLY:
        // replay
        return vote;
      case STATE_COMPLETED:
        JTAHelper.throwXAException(XAException.XAER_INVAL, "Resource completed.");
        break;
      }

      try {
        vote = resource.prepare(xid);
        switch (vote) {
        case XAResource.XA_OK:
          state = STATE_PREPARED;
          break;
        case XAResource.XA_RDONLY:
          state = STATE_READONLY;
          break;
        }
      } catch (XAException xae) {
        switch (xae.errorCode) {
        case XAException.XA_RBCOMMFAIL:
        case XAException.XA_RBDEADLOCK:
        case XAException.XA_RBINTEGRITY:
        case XAException.XA_RBOTHER:
        case XAException.XA_RBPROTO:
        case XAException.XA_RBROLLBACK:
        case XAException.XA_RBTIMEOUT:
        case XAException.XA_RBTRANSIENT:
          // branch rolled back, state complete and rethrow
          state = STATE_COMPLETED;
          throw xae;
        case XAException.XAER_RMERR:
        case XAException.XAER_RMFAIL:
        case XAException.XAER_INVAL:
        case XAException.XAER_PROTO:
        case XAException.XAER_NOTA:
          // rethrow
          throw xae;
        default:
          throw xae;
        }
      }

      return vote;
    }

    void commit(Xid xid, boolean onePhase) throws XAException {
      switch (state) {
      case STATE_READONLY:
      case STATE_COMPLETED:
        // replay
        return;
      }

      try {
        resource.commit(xid, onePhase);
        state = STATE_COMPLETED;
      } catch (XAException xae) {
        switch (xae.errorCode) {
        case XAException.XA_HEURCOM:
          state = STATE_COMPLETED;
          break;
        case XAException.XA_HEURRB:
        case XAException.XA_HEURMIX:
        case XAException.XA_HEURHAZ:
          state = STATE_COMPLETED;
          throw xae;
        case XAException.XA_RBCOMMFAIL:
        case XAException.XA_RBDEADLOCK:
        case XAException.XA_RBINTEGRITY:
        case XAException.XA_RBOTHER:
        case XAException.XA_RBPROTO:
        case XAException.XA_RBROLLBACK:
        case XAException.XA_RBTIMEOUT:
        case XAException.XA_RBTRANSIENT:
          if (onePhase) {
            // branch rolled back, state complete and rethrow
            state = STATE_COMPLETED;
            throw xae;
          } else {
            // assertion, invalid errorCode for onePhase=false
            JTAHelper.throwXAException(XAException.XA_HEURHAZ, "Invalid rollback error code thrown for 2PC commit. ", xae);
          }
          break;
        case XAException.XAER_RMERR:
        case XAException.XAER_RMFAIL:
        case XAException.XAER_INVAL:
        case XAException.XAER_PROTO:
          // rethrow
          throw xae;
        case XAException.XAER_NOTA:
          // assume that branch commit has completed.
          state = STATE_COMPLETED;
          break;
        default:
          throw xae;
        }
      }
    }

    void rollback(Xid xid) throws XAException {

      switch (state) {
      case STATE_READONLY:
      case STATE_COMPLETED:
        // replay
        return;
      }

      try {
        resource.rollback(xid);
        state = STATE_COMPLETED;
      } catch (XAException xae) {
        switch (xae.errorCode) {
        case XAException.XA_HEURRB:
          state = STATE_COMPLETED;
          break;
        case XAException.XA_HEURCOM:
        case XAException.XA_HEURMIX:
        case XAException.XA_HEURHAZ:
          state = STATE_COMPLETED;
          throw xae;
        case XAException.XAER_RMERR:
        case XAException.XAER_RMFAIL:
        case XAException.XAER_INVAL:
        case XAException.XAER_PROTO:
          // rethrow
          throw xae;
        case XAException.XAER_NOTA:
          // assume that branch commit has completed.
          state = STATE_COMPLETED;
          break;
        default:
          throw xae;
        }
      }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      branchXid = new BranchXidImpl();
      branchXid.readExternal(in);
      resource = (WSATXAResource) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      resource.setXid(new XidImpl(resource.getXid()));
      new BranchXidImpl(resource.getXid()).writeExternal(out);
        try{
            out.writeObject(resource);       
        } catch(Exception e){
            e.printStackTrace();
        }
    }


  } // RegisteredResource

  //
  // Externalizable
  //

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {

    // Version
    out.writeInt(VERSION);

    // Global Xid
    out.writeInt(globalXid.getFormatId());

    byte[] gtrid = globalXid.getGlobalTransactionId();
    out.writeByte((byte)gtrid.length);
    out.write(gtrid);

    byte[] bqual = globalXid.getBranchQualifier();
    if (bqual == null) {
      out.writeByte((byte)-1);
    } else {
      out.writeByte((byte)bqual.length);
      out.write(bqual);
    }

    // RegisteredResources
    out.writeInt(registeredResources.size());
   /*
    for (int i=0; i<registeredResources.size(); i++) {
      RegisteredResource rr = registeredResources.get(i);
      rr.writeExternal(out);
    }
    */
    Iterator<RegisteredResource> resourceIterator = registeredResources.values().iterator();
    while(resourceIterator.hasNext()) {
        resourceIterator.next().writeExternal(out);
    }
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    // Version
    int version = in.readInt();
    if (version != VERSION) {
      throw new IOException("invalid OTSBranch version " + version);
    }

    // Global Xid
    int formatId = in.readInt();

    int len = in.readByte();
    byte[] gtrid = new byte[len];
    in.readFully(gtrid);

    len = in.readByte();
    byte[] bqual=null;
    if (len > -1) {
      bqual = new byte[len];
      in.readFully(bqual);
    }

    // globalXid = XIDFactory.createXID(formatId, gtrid, bqual);
    globalXid = new XidImpl(formatId, gtrid, bqual);

    // RegisteredResources
    int resourceNum = in.readInt();
    for (int i=0; i<resourceNum; i++) {

      RegisteredResource rr = new RegisteredResource();
      rr.readExternal(in);
      rr.setPrepared();
      registeredResources.put(globalXid, rr);
    }

    logged = true;
  }

  public String toString() {
    return "BranchRecord:globalXid=" + globalXid;
  }
}

