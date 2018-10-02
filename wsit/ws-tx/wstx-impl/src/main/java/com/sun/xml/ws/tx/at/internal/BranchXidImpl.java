/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.internal;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import javax.transaction.xa.Xid;

/**
 * Xid implementation used for persisting branch state.
 * Wrapper over  XidImpl to override semantics of hashCode and equals
 */
public class BranchXidImpl implements Xid, Externalizable {

  private Xid delegate;
  
  public BranchXidImpl() {
  }

  public BranchXidImpl(Xid xid) {
    this.delegate = xid;
  }
  
  public byte[] getBranchQualifier() {
    return delegate.getBranchQualifier();
  }

  public int getFormatId() {
    return delegate.getFormatId();
  }

  public byte[] getGlobalTransactionId() {
    return delegate.getGlobalTransactionId();
  }

  public Xid getDelegate() {
    return delegate;
  }
  
  // 
  // Object
  //
  
  public boolean equals(Object o) { 
    if (!(o instanceof Xid)) return false;
    Xid that = (Xid) o;
        final boolean formatId = getFormatId() == that.getFormatId();
        final boolean txid = Arrays.equals(getGlobalTransactionId(), that.getGlobalTransactionId());
        final boolean bqual = Arrays.equals(getBranchQualifier(), that.getBranchQualifier());
    return formatId
        && txid
        && bqual;
  }
  
  public int hashCode() {
    return delegate.hashCode();
  }
  
  public String toString() {
    return "BranchXidImpl:" + delegate.toString();
  }
  
  //
  // Externalizable
  //
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    delegate = (Xid) in.readObject();
  }

  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(delegate);
  }

}
